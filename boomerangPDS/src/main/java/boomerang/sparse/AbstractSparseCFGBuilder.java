/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.sparse;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import com.google.common.graph.ElementOrder;
import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Base class for the builders of sparse control flow graphs. A builder copies the control flow
 * graph of a method into a mutable graph, decides which statements to keep and removes the other
 * statements by connecting their predecessor with their successor. Builders hold state for a single
 * graph, i.e. a new builder has to be used for each graph.
 */
public abstract class AbstractSparseCFGBuilder {

  private static final String OBJECT_TYPE = "java.lang.Object";

  protected final Method method;
  protected final Collection<Statement> heads;
  protected final Collection<Statement> tails;
  protected final MutableGraph<Statement> graph;
  private final Set<Statement> requiredStmts;

  /**
   * @param method the method to build the graph for
   * @param requiredStmts statements that must remain in the graph, independent of the strategy
   */
  protected AbstractSparseCFGBuilder(Method method, Collection<Statement> requiredStmts) {
    this.method = method;
    this.requiredStmts = new HashSet<>(requiredStmts);

    ControlFlowGraph cfg = method.getControlFlowGraph();
    this.heads = new LinkedHashSet<>(cfg.getStartPoints());
    this.tails = new LinkedHashSet<>(cfg.getEndPoints());
    this.graph = buildGraph(cfg);
  }

  private static MutableGraph<Statement> buildGraph(ControlFlowGraph cfg) {
    MutableGraph<Statement> graph =
        GraphBuilder.directed()
            .allowsSelfLoops(false)
            .nodeOrder(ElementOrder.insertion())
            .incidentEdgeOrder(ElementOrder.stable())
            .build();

    for (Statement stmt : cfg.getStatements()) {
      graph.addNode(stmt);
    }
    for (Statement stmt : cfg.getStatements()) {
      for (Statement succ : cfg.getSuccsOf(stmt)) {
        if (!stmt.equals(succ)) {
          graph.putEdge(stmt, succ);
        }
      }
    }
    return graph;
  }

  /**
   * @return the statements reachable from the heads in breadth-first order
   */
  protected Iterable<Statement> breadthFirst() {
    List<Statement> roots = new ArrayList<>();
    for (Statement head : heads) {
      if (graph.nodes().contains(head)) {
        roots.add(head);
      }
    }
    return Traverser.forGraph(graph).breadthFirst(roots);
  }

  /**
   * Removes all statements that are reachable from the heads and are neither control statements,
   * heads, tails, required statements, return sites nor kept by {@link #keep(Statement)}. A
   * statement is only removed if it has a single predecessor and a single successor that differ
   * from each other.
   */
  protected void sparsify() {
    // One pass per tail, as removing a statement may enable the removal of others
    int passes = Math.max(1, tails.size());
    for (int i = 0; i < passes; i++) {
      List<Statement> stmtsToRemove = new ArrayList<>();
      for (Statement stmt : breadthFirst()) {
        if (!isControlStmt(stmt)
            && !heads.contains(stmt)
            && !tails.contains(stmt)
            && !requiredStmts.contains(stmt)
            && !isReturnSite(stmt)
            && !keep(stmt)) {
          stmtsToRemove.add(stmt);
        }
      }
      for (Statement stmt : stmtsToRemove) {
        removeStmt(stmt);
      }
    }
  }

  /**
   * Return sites, i.e. the successors of call statements, are kept, such that the edges from a call
   * statement to its return sites are the same as in the original control flow graph. The solvers
   * create these edges independent of the propagated value (e.g. for unbalanced returns from a
   * callee), so the edges of value specific sparse graphs would not match otherwise.
   */
  private boolean isReturnSite(Statement stmt) {
    for (Statement pred : method.getControlFlowGraph().getPredsOf(stmt)) {
      if (pred.containsInvokeExpr()) {
        return true;
      }
    }
    return false;
  }

  /**
   * @param stmt a statement reachable from the heads of the graph
   * @return true if the statement has to remain in the sparse graph
   */
  protected abstract boolean keep(Statement stmt);

  private void removeStmt(Statement stmt) {
    Set<Statement> preds = graph.predecessors(stmt);
    Set<Statement> succs = graph.successors(stmt);
    if (preds.size() != 1 || succs.size() != 1) {
      return;
    }

    Statement pred = preds.iterator().next();
    Statement succ = succs.iterator().next();
    if (pred.equals(succ)) {
      return;
    }

    graph.removeNode(stmt);
    graph.putEdge(pred, succ);
  }

  /**
   * Control statements are always kept, because they determine the structure of the graph. This
   * includes all statements that neither assign a value nor call a method (e.g. nop, goto, switch,
   * throw, monitor statements).
   */
  protected static boolean isControlStmt(Statement stmt) {
    return stmt.isIdentityStmt()
        || stmt.isIfStmt()
        || stmt.isReturnStmt()
        || (!stmt.isAssignStmt() && !stmt.containsInvokeExpr());
  }

  /**
   * @return true if a value of one type may be stored in a variable of the other type
   */
  protected static boolean mayAlias(Type type1, Type type2) {
    return type1.isAssignableTo(type2) || type2.isAssignableTo(type1);
  }

  /**
   * Compares two types for equality. Since some frameworks only provide imprecise types for local
   * variables (e.g. Opal types reference locals as {@code java.lang.Object}), {@code
   * java.lang.Object} is considered equal to all types.
   */
  protected static boolean mayBeSameType(Type type1, Type type2) {
    return type1.equals(type2)
        || OBJECT_TYPE.equals(type1.toString())
        || OBJECT_TYPE.equals(type2.toString());
  }

  protected int getStatementCount() {
    return graph.nodes().size();
  }
}
