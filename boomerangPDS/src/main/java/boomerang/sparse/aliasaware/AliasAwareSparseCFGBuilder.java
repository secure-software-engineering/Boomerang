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
package boomerang.sparse.aliasaware;

import boomerang.scope.IArrayRef;
import boomerang.scope.InstanceFieldVal;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.sparse.AbstractSparseCFGBuilder;
import boomerang.sparse.SparseAliasingCFG;
import boomerang.sparse.eval.SparseCFGQueryLog;
import com.google.common.collect.ImmutableSet;
import com.google.common.graph.Traverser;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Keeps the statements that define or use the query variable and the values it may alias with.
 * Starting at the query statement, the builder searches backwards for the definition of the query
 * variable and forwards for its uses. Values that may alias with the query variable (e.g. the
 * right-hand side of its definition, bases of fields and arguments of the same type) are tracked in
 * the same way until no new values are found.
 */
public class AliasAwareSparseCFGBuilder extends AbstractSparseCFGBuilder {

  private static final Logger LOGGER = LoggerFactory.getLogger(AliasAwareSparseCFGBuilder.class);
  private static final String THROWABLE_TYPE = "java.lang.Throwable";

  /**
   * Result of the backward search for a definition. The statement is null if the value is a field
   * that is defined outside the method.
   */
  private record Definition(@Nullable Statement stmt) {
    static final Definition DEFINED_OUTSIDE = new Definition(null);
  }

  private final boolean ignoreAfterQuery;
  private final Type queryVarType;

  // State of the current seed
  private final Deque<Val> backwardWorklist = new ArrayDeque<>();
  private final Deque<Val> forwardWorklist = new ArrayDeque<>();
  private final Map<Val, Statement> definitions = new HashMap<>();
  private final Map<Val, Pair<Val, Statement>> valueKilledByValueAt = new HashMap<>();
  private Statement queryStmt;

  /** The statements from which the query statement is reachable (including itself) */
  private Set<Statement> stmtsBeforeQuery = Set.of();

  // Results of all seeds
  private final Set<Statement> seedStmts = new HashSet<>();
  private final Set<Statement> trackedStmts = new HashSet<>();
  private final Set<Val> trackedVals = new LinkedHashSet<>();

  /**
   * @param method the method to build the graph for
   * @param requiredStmts statements that must remain in the graph
   * @param initialQueryVar the variable of the backward query, its type is used to find relevant
   *     fields and arguments
   * @param ignoreAfterQuery whether the search for uses stops at the seed statements
   */
  public AliasAwareSparseCFGBuilder(
      Method method,
      Collection<Statement> requiredStmts,
      Val initialQueryVar,
      boolean ignoreAfterQuery) {
    super(method, requiredStmts);
    this.queryVarType = initialQueryVar.getType();
    this.ignoreAfterQuery = ignoreAfterQuery;
  }

  /**
   * Keeps the statements of a value and the values it may alias with. The state of the search is
   * independent of previously added seeds, the kept statements are accumulated.
   *
   * @param queryVar the value whose statements are kept
   * @param queryStmt the statement the propagation of the value is at
   * @return false if the statement is not part of the method's graph
   */
  public boolean addSeed(Val queryVar, Statement queryStmt) {
    if (!graph.nodes().contains(queryStmt)) {
      return false;
    }

    backwardWorklist.clear();
    forwardWorklist.clear();
    definitions.clear();
    valueKilledByValueAt.clear();
    this.queryStmt = queryStmt;
    this.stmtsBeforeQuery =
        ImmutableSet.copyOf(Traverser.forGraph(graph::predecessors).breadthFirst(queryStmt));

    seedStmts.add(queryStmt);
    findStmtsToKeep(queryVar);
    return true;
  }

  /**
   * Removes the statements that are not relevant for the added seeds.
   *
   * @param val the value that is reported as the graph's value
   * @param queryLog log to record statistics
   * @return the sparse graph
   */
  public SparseAliasingCFG build(Val val, SparseCFGQueryLog queryLog) {
    int initialStmtCount = getStatementCount();
    closeTrackedVals();
    sparsify();

    queryLog.setInitialStmtCount(initialStmtCount);
    queryLog.setFinalStmtCount(getStatementCount());
    return new SparseAliasingCFG(method, val, queryStmt, graph, Set.copyOf(trackedVals)::contains);
  }

  @Override
  protected boolean keep(Statement stmt) {
    return trackedStmts.contains(stmt)
        || seedStmts.contains(stmt)
        || isTargetCallSite(stmt)
        || mentionsTrackedVal(stmt);
  }

  /**
   * The graph is only used for the tracked values, so it has to contain all statements at which
   * their data flow facts may change: definitions (kills, e.g. if a framework reuses a local for
   * several allocations), uses, field and array accesses with the value as base, calls with the
   * value as base or argument and returns of the value.
   */
  private boolean mentionsTrackedVal(Statement stmt) {
    for (Val val : trackedVals) {
      if (mentions(stmt, val)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Boomerang propagates values together with field and array accesses, e.g. the fact l.[l3, l4]
   * continues as $s.[l4] after $s = l.l3. Thus, all values that occur in a statement together with
   * a tracked value (copies, field and array accesses, calls and their results, returns) are
   * tracked as well, until a fixpoint is reached.
   */
  private void closeTrackedVals() {
    boolean changed = true;
    while (changed) {
      changed = false;
      for (Statement stmt : graph.nodes()) {
        if (mentionsTrackedVal(stmt)) {
          changed |= trackedVals.addAll(valuesOf(stmt));
        }
      }
    }
  }

  private static Set<Val> valuesOf(Statement stmt) {
    Set<Val> vals = new LinkedHashSet<>();
    if (stmt.isAssignStmt()) {
      addWithBase(vals, stmt.getLeftOp());
      Val rightOp = stmt.getRightOp();
      addWithBase(vals, rightOp.isCast() ? rightOp.getCastOp() : rightOp);
    }
    if (stmt.containsInvokeExpr()) {
      InvokeExpr invokeExpr = stmt.getInvokeExpr();
      if (invokeExpr.isInstanceInvokeExpr()) {
        vals.add(invokeExpr.getBase());
      }
      vals.addAll(invokeExpr.getArgs());
    }
    if (stmt.isReturnStmt()) {
      vals.add(stmt.getReturnOp());
    }
    if (stmt.isPhiStatement()) {
      vals.addAll(stmt.getPhiVals());
    }
    return vals;
  }

  private static void addWithBase(Set<Val> vals, Val op) {
    vals.add(op);
    if (op instanceof InstanceFieldVal) {
      vals.add(((InstanceFieldVal) op).getBase());
    } else if (op.isArrayRef()) {
      vals.add(op.getArrayBase().getBase());
    }
  }

  private static boolean mentions(Statement stmt, Val val) {
    if (stmt.isAssignStmt()) {
      Val rightOp = stmt.getRightOp();
      if (rightOp.isCast()) {
        rightOp = rightOp.getCastOp();
      }
      if (isOrHasBase(stmt.getLeftOp(), val) || isOrHasBase(rightOp, val)) {
        return true;
      }
      if (stmt.isInstanceOfStatement(val)) {
        return true;
      }
    }
    if (stmt.isPhiStatement() && stmt.getPhiVals().contains(val)) {
      return true;
    }
    if (stmt.containsInvokeExpr() && stmt.isParameter(val)) {
      return true;
    }
    return stmt.isReturnStmt() && stmt.getReturnOp().equals(val);
  }

  private static boolean isOrHasBase(Val op, Val val) {
    if (op.equals(val)) {
      return true;
    }
    if (op instanceof InstanceFieldVal) {
      return ((InstanceFieldVal) op).getBase().equals(val);
    }
    if (op.isArrayRef()) {
      return op.getArrayBase().getBase().equals(val);
    }
    return false;
  }

  private boolean isTargetCallSite(Statement stmt) {
    if (!stmt.containsInvokeExpr()) {
      return false;
    }

    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    for (Val d : trackedVals) {
      // v as arg
      for (Val arg : invokeExpr.getArgs()) {
        if (d.equals(arg)) {
          return true;
        }
      }
      // v as base v.m()
      if (isInvokeBase(d, invokeExpr)) {
        return true;
      }
    }
    return false;
  }

  private void findStmtsToKeep(Val queryVar) {
    Definition def = findBackwardDefForValue(queryStmt, queryVar, false);
    track(queryVar, def);
    backwardPass(def);
    forwardPass();
    findStmtsForFieldBase();
    findStmtsAfterKill();
  }

  private void findStmtsForFieldBase() {
    while (!backwardWorklist.isEmpty()) {
      backwardPass(new Definition(queryStmt));
      while (!forwardWorklist.isEmpty()) {
        forwardPass();
      }
    }
  }

  private void findStmtsAfterKill() {
    while (!backwardWorklist.isEmpty()) {
      Val val = backwardWorklist.pop();
      Statement killedAt = null;
      for (Pair<Val, Statement> killer : valueKilledByValueAt.values()) {
        if (killer.getX().equals(val)) {
          killedAt = killer.getY();
          break;
        }
      }

      if (killedAt != null) {
        track(val, findBackwardDefForValue(killedAt, val, false));
      } else {
        LOGGER.warn("Value {} is not killed in {}", val, method);
      }
    }
    forwardPass();
  }

  private void backwardPass(@Nullable Definition def) {
    while (!backwardWorklist.isEmpty()) {
      Val val = backwardWorklist.pop();
      Statement existingDef = definitions.get(val);
      Definition newDef;
      if (existingDef != null) {
        newDef = findBackwardDefForValue(existingDef, val, true);
      } else {
        newDef = findBackwardDefForValue(def == null ? null : def.stmt(), val, false);
      }

      if (newDef != null && newDef.stmt() != null) {
        def = newDef;
      }
      if (def != null) {
        track(val, def);
      }
    }
  }

  private void forwardPass() {
    while (!forwardWorklist.isEmpty()) {
      Val val = forwardWorklist.pop();
      Statement defStmt = definitions.get(val);
      track(val, defStmt);
      if (defStmt != null) {
        for (Statement stmt : findForwardDefUseForValue(defStmt, val)) {
          track(val, stmt);
        }
      }
    }
  }

  private void track(Val val, @Nullable Definition def) {
    track(val, def == null ? null : def.stmt());
  }

  /** Tracks the value and keeps the statement (if not null) */
  private void track(Val val, @Nullable Statement stmt) {
    trackedVals.add(val);
    if (stmt != null) {
      trackedStmts.add(stmt);
    }
  }

  /**
   * Searches the definition of a value by a DFS along the predecessors of a statement.
   *
   * @param start the statement to start at
   * @param queryVar the value whose definition is searched
   * @param skipStart whether the start statement is already known to be a definition
   * @return the definition, {@link Definition#DEFINED_OUTSIDE} if the value is an instance field
   *     without definition and null if no definition is found otherwise
   */
  private @Nullable Definition findBackwardDefForValue(
      @Nullable Statement start, Val queryVar, boolean skipStart) {
    if (start == null) {
      return null;
    }

    Set<Statement> visited = new HashSet<>();
    visited.add(start);
    if (!skipStart && isDefOfValue(start, queryVar)) {
      return new Definition(start);
    }

    // Fields are defined outside the method once the search reaches the first statement without
    // unvisited predecessors
    boolean isField = queryVar instanceof InstanceFieldVal;

    Deque<Iterator<Statement>> stack = new ArrayDeque<>();
    stack.push(graph.predecessors(start).iterator());
    while (!stack.isEmpty()) {
      Statement pred = nextUnvisited(stack.peek(), visited);
      if (pred == null) {
        stack.pop();
        if (isField) {
          return Definition.DEFINED_OUTSIDE;
        }
        continue;
      }

      visited.add(pred);
      if (isDefOfValue(pred, queryVar)) {
        return new Definition(pred);
      }
      stack.push(graph.predecessors(pred).iterator());
    }
    return isField ? Definition.DEFINED_OUTSIDE : null;
  }

  private static @Nullable Statement nextUnvisited(
      Iterator<Statement> iterator, Set<Statement> visited) {
    while (iterator.hasNext()) {
      Statement next = iterator.next();
      if (!visited.contains(next)) {
        return next;
      }
    }
    return null;
  }

  private boolean isDefOfValue(Statement stmt, Val queryVar) {
    if (stmt.isAssignStmt()) {
      Val leftOp = stmt.getLeftOp();
      Val rightOp = stmt.getRightOp();
      if (rightOp.isCast()) {
        rightOp = rightOp.getCastOp();
      }

      if (queryVar.equals(leftOp)
          || equalsFieldRef(leftOp, queryVar)
          || equalsQueryBase(leftOp, queryVar)
          || equalsArrayItem(leftOp, queryVar)
          || equalsFieldType(leftOp, queryVar)) {
        if (isAllocOrMethodAssignment(stmt, queryVar)) {
          forwardWorklist.push(queryVar);
          InvokeExpr invokeExpr = stmt.containsInvokeExpr() ? stmt.getInvokeExpr() : null;
          backwardWorklist.addAll(getInvokeBaseAndParams(invokeExpr, queryVar));
        } else {
          if (rightOp instanceof InstanceFieldVal) {
            Val base = ((InstanceFieldVal) rightOp).getBase();
            if (equalsFieldRef(rightOp, queryVar) && base.equals(leftOp)) {
              // recursion, e.g. n = n.next
              backwardWorklist.push(leftOp);
              definitions.put(leftOp, stmt);
              forwardWorklist.push(rightOp);
              definitions.put(rightOp, stmt);
              return false;
            }
            backwardWorklist.push(base);
          }
          backwardWorklist.push(rightOp);
        }
        definitions.put(queryVar, stmt);
        return true;
      }
    } else if (isIdentityDefinition(stmt, queryVar)) {
      // we treat it like an allocation
      forwardWorklist.push(queryVar);
      definitions.put(queryVar, stmt);
      return true;
    }
    return false;
  }

  /**
   * The scope does not expose the local that is defined by an identity statement. Identity
   * statements define the this local, the parameter locals and the locals of caught exceptions, so
   * they are considered as definition for these locals.
   */
  private boolean isIdentityDefinition(Statement stmt, Val queryVar) {
    if (!stmt.isIdentityStmt() || !queryVar.isLocal()) {
      return false;
    }

    if (stmt.isCatchStmt()) {
      return isThrowable(queryVar.getType());
    }
    return queryVar.isThisLocal() || method.getParameterLocals().contains(queryVar);
  }

  private static boolean isThrowable(Type type) {
    try {
      return type.isSubtypeOf(THROWABLE_TYPE);
    } catch (RuntimeException e) {
      return true;
    }
  }

  private static boolean equalsFieldRef(Val op, Val queryVar) {
    if (op instanceof InstanceFieldVal && queryVar instanceof InstanceFieldVal) {
      InstanceFieldVal opField = (InstanceFieldVal) op;
      InstanceFieldVal queryField = (InstanceFieldVal) queryVar;
      return queryField.getBase().equals(opField.getBase())
          && queryField.getField().equals(opField.getField());
    }
    if (op instanceof StaticFieldVal && queryVar instanceof StaticFieldVal) {
      return ((StaticFieldVal) op).getField().equals(((StaticFieldVal) queryVar).getField());
    }
    return false;
  }

  private static boolean equalsArrayItem(Val op, Val queryVar) {
    if (op.isArrayRef() && queryVar.isArrayRef()) {
      IArrayRef opArray = op.getArrayBase();
      IArrayRef queryArray = queryVar.getArrayBase();
      return queryArray.getBase().equals(opArray.getBase())
          && queryArray.getIndexExpr().equals(opArray.getIndexExpr());
    }
    return false;
  }

  /**
   * Ideally, the search stops at the query statement because of flow sensitivity, i.e. at
   * statements from which the query statement is not reachable. However, Boomerang may require the
   * whole method.
   */
  private boolean shouldStopSearch(Statement currentStmt) {
    if (!ignoreAfterQuery) {
      return false;
    }
    return currentStmt.equals(queryStmt) || !stmtsBeforeQuery.contains(currentStmt);
  }

  /**
   * Collects the statements that use or redefine a value by a DFS along the successors of its
   * definition, skipping the statements at which the search stops.
   */
  private Set<Statement> findForwardDefUseForValue(Statement head, Val queryVar) {
    Set<Statement> stmtsToKeep = new LinkedHashSet<>();
    Set<Statement> visited = new HashSet<>();
    visited.add(head);

    Deque<Iterator<Statement>> stack = new ArrayDeque<>();
    stack.push(graph.successors(head).iterator());
    while (!stack.isEmpty()) {
      Iterator<Statement> succs = stack.peek();
      if (!succs.hasNext()) {
        stack.pop();
        continue;
      }

      Statement succ = succs.next();
      if (shouldStopSearch(succ)) {
        // do not process statements after the query statement
        continue;
      }
      if (!valueKilledByValueAt.containsKey(queryVar)
          && keepContainingStmtsForward(succ, queryVar)) {
        stmtsToKeep.add(succ);
      }
      if (visited.add(succ)) {
        stack.push(graph.successors(succ).iterator());
      }
    }
    return stmtsToKeep;
  }

  private boolean keepContainingStmtsForward(Statement stmt, Val queryVar) {
    if (stmt.isAssignStmt()) {
      Val leftOp = stmt.getLeftOp();
      Val rightOp = stmt.getRightOp();
      if (rightOp.isCast()) {
        rightOp = rightOp.getCastOp();
      }

      if (queryVar.equals(rightOp)
          || equalsFieldRef(rightOp, queryVar)
          || equalsQueryBase(rightOp, queryVar)
          || equalsFieldType(rightOp, queryVar)
          || equalsArrayItem(rightOp, queryVar)
          || equalsInitialFieldType(rightOp, queryVar)) {
        if (!leftOp.equals(queryVar)) {
          // otherwise it would create a loop
          forwardWorklist.push(leftOp);
          definitions.put(leftOp, stmt);
        }
        if (leftOp instanceof InstanceFieldVal) {
          // we need to find how the base was created too
          backwardWorklist.push(((InstanceFieldVal) leftOp).getBase());
        }
        return true;
      } else if (queryVar.equals(leftOp)
          || equalsFieldRef(leftOp, queryVar)
          || equalsQueryBase(leftOp, queryVar)
          || equalsArrayItem(leftOp, queryVar)) {
        // kill: need to find the allocation of the killer object
        valueKilledByValueAt.put(queryVar, new Pair<>(rightOp, stmt));
        backwardWorklist.push(rightOp);
        return true;
      } else if (equalsInitialFieldType(leftOp, queryVar)) {
        backwardWorklist.push(rightOp);
        return true;
      }
    }
    return keepInvokeForValue(stmt, queryVar);
  }

  /**
   * The query variable is a field reference and its base equals the operand, e.g. for the query
   * variable box.f and the statement box2 = box
   */
  private static boolean equalsQueryBase(Val op, Val queryVar) {
    if (queryVar instanceof InstanceFieldVal) {
      return ((InstanceFieldVal) queryVar).getBase().equals(op);
    }
    return false;
  }

  private boolean equalsInitialFieldType(Val op, Val queryVar) {
    if (op instanceof InstanceFieldVal) {
      Val base = ((InstanceFieldVal) op).getBase();
      return base.equals(queryVar) && mayBeSameType(op.getType(), queryVarType);
    }
    return false;
  }

  /**
   * The operand is a field of the query variable with the same type, e.g. for the query variable
   * this and the operand this.n. this.n is kept because it may be used for a recursive field store.
   */
  private static boolean equalsFieldType(Val op, Val queryVar) {
    if (op instanceof InstanceFieldVal) {
      Val base = ((InstanceFieldVal) op).getBase();
      return base.equals(queryVar) && mayBeSameType(op.getType(), queryVar.getType());
    }
    return false;
  }

  private Collection<Val> getInvokeBaseAndParams(@Nullable InvokeExpr invokeExpr, Val d) {
    if (invokeExpr == null) {
      return Collections.emptySet();
    }

    Set<Val> otherArgs = new LinkedHashSet<>();
    for (Val arg : invokeExpr.getArgs()) {
      // Mirrors the original condition: (arg != d && type(arg) == type(d)) || type(arg) == type(q)
      if ((!arg.equals(d) && mayBeSameType(arg.getType(), d.getType()))
          || mayBeSameType(arg.getType(), queryVarType)) {
        otherArgs.add(arg);
      }
    }
    if (invokeExpr.isInstanceInvokeExpr()) {
      otherArgs.add(invokeExpr.getBase());
    }
    return otherArgs;
  }

  private boolean keepInvokeForValue(Statement stmt, Val d) {
    if (!stmt.containsInvokeExpr()) {
      return false;
    }

    InvokeExpr invokeExpr = stmt.getInvokeExpr();
    List<Val> args = invokeExpr.getArgs();
    // v as arg
    for (Val arg : args) {
      if (d.equals(arg)) {
        for (Val otherArg : args) {
          // other args of the same type can cause aliasing, so we need to find their allocation
          // sites
          if (!otherArg.equals(d)
              && mayBeSameType(otherArg.getType(), d.getType())
              && !definitions.containsKey(otherArg)) {
            backwardWorklist.push(otherArg);
          }
        }
        return true;
      }
    }
    // v as base v.m()
    if (isInvokeBase(d, invokeExpr)) {
      getInvokeBaseAndParams(invokeExpr, d).forEach(backwardWorklist::push);
      return true;
    }
    return false;
  }

  private static boolean isInvokeBase(Val d, InvokeExpr invokeExpr) {
    return invokeExpr.isInstanceInvokeExpr() && d.equals(invokeExpr.getBase());
  }

  private static boolean isAllocOrMethodAssignment(Statement stmt, Val d) {
    Val rightOp = stmt.getRightOp();
    if (rightOp.isNewExpr()) {
      return mayAlias(rightOp.getNewExprType(), d.getType());
    }
    return stmt.containsInvokeExpr();
  }
}
