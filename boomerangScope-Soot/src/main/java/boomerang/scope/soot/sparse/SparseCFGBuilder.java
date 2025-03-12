/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.soot.sparse;

import com.google.common.graph.GraphBuilder;
import com.google.common.graph.MutableGraph;
import com.google.common.graph.Traverser;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.internal.*;
import soot.toolkits.graph.DirectedGraph;

public class SparseCFGBuilder {

  private static final Logger LOGGER = Logger.getLogger(SparseCFGBuilder.class.getName());

  protected Map<Unit, Integer> unitToNumber = new HashMap<>();

  public MutableGraph<Unit> numberStmtsAndConvertToMutableGraph(DirectedGraph<Unit> rawGraph) {
    MutableGraph<Unit> mGraph = GraphBuilder.directed().build();
    List<Unit> heads = rawGraph.getHeads();
    for (Unit head : heads) {
      convertToMutableGraph(rawGraph, head, mGraph, 0);
    }
    return mGraph;
  }

  public void convertToMutableGraph(
      DirectedGraph<Unit> graph, Unit curr, MutableGraph<Unit> mutableGraph, int depth) {
    Integer num = unitToNumber.get(curr);
    if (num == null || num < depth) {
      unitToNumber.put(curr, depth);
    }
    depth++;
    List<Unit> succsOf = graph.getSuccsOf(curr);
    for (Unit succ : succsOf) {
      if (!mutableGraph.hasEdgeConnecting(curr, succ) && !curr.equals(succ)) {
        mutableGraph.putEdge(curr, succ);
        convertToMutableGraph(graph, succ, mutableGraph, depth);
      }
    }
  }

  /**
   * Boomerang uses BriefUnitGraph and chooses nonidentitiy stmt as the head
   *
   * @param graph
   * @return
   */
  public Unit getHead(DirectedGraph<Unit> graph) {
    List<Unit> heads = graph.getHeads();
    List<Unit> res = new ArrayList<>();
    for (Unit head : heads) {
      if (head instanceof IdentityStmt || graph.getSuccsOf(head).isEmpty()) {
        continue;
      }
      res.add(head);
    }
    if (res.size() > 1) {
      LOGGER.warning("Multiple heads!");
    }
    return res.get(0);
  }

  public Iterator<Unit> getBFSIterator(MutableGraph<Unit> graph, Unit head) {
    Traverser<Unit> traverser = Traverser.forGraph(graph);
    return traverser.breadthFirst(head).iterator();
  }

  public void logCFG(Logger logger, MutableGraph<Unit> graph) {
    logger.info(
        graph.nodes().stream()
            .map(Objects::toString)
            .collect(Collectors.joining(System.lineSeparator())));
  }

  public boolean isControlStmt(Unit stmt) {
    if (stmt instanceof JIfStmt
        || stmt instanceof JNopStmt
        || stmt instanceof JGotoStmt
        || stmt instanceof JReturnStmt
        || stmt instanceof JReturnVoidStmt) {
      return true;
    }
    //      JIdentityStmt id = (JIdentityStmt) stmt;
    //      if (id.getRightOp() instanceof JCaughtExceptionRef) {
    //      }
    return stmt instanceof JIdentityStmt;
  }

  public void removeStmt(MutableGraph<Unit> mCFG, Unit unit) {
    Set<Unit> preds = mCFG.predecessors(unit);
    List<Unit> tmpPreds = new ArrayList<>();
    preds.forEach(e -> tmpPreds.add(e));
    Set<Unit> succs = mCFG.successors(unit);
    List<Unit> tmpSuccs = new ArrayList<>();
    succs.forEach(e -> tmpSuccs.add(e));
    if (tmpPreds.size() == 1 && tmpSuccs.size() == 1) {
      mCFG.removeNode(unit);
      mCFG.putEdge(tmpPreds.get(0), tmpSuccs.get(0));
    }
  }
}
