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
package boomerang.scope.soot.sparse.aliasaware;

import boomerang.scope.Pair;
import boomerang.scope.Val;
import boomerang.scope.soot.sparse.SootAdapter;
import boomerang.scope.soot.sparse.SparseCFGBuilder;
import com.google.common.graph.MutableGraph;
import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.*;
import soot.toolkits.graph.BriefUnitGraph;
import soot.toolkits.graph.DirectedGraph;
import sparse.SparseAliasingCFG;
import sparse.eval.SparseCFGQueryLog;

/** Value is the type of DFF */
public class AliasAwareSparseCFGBuilder extends SparseCFGBuilder {

  private Deque<Value> backwardWorklist;
  private Deque<Value> forwardWorklist;
  private Map<Value, Unit> definitions;
  private Map<Value, LinkedHashSet<Unit>> valueToUnits;
  private Map<Value, Pair<Value, Unit>> valueKillebyValuedAt;
  private Type queryVarType;
  private SootMethod currentMethod;
  private Unit queryStmt;
  private String activePass;
  private int lastNodeIndex = -1;

  private static final Logger LOGGER = Logger.getLogger(AliasAwareSparseCFGBuilder.class.getName());

  private final boolean enableExceptions;
  private final boolean ignoreAfterQuery;

  public AliasAwareSparseCFGBuilder(boolean enableExceptions, boolean ignoreAfterQuery) {
    this.enableExceptions = enableExceptions;
    this.ignoreAfterQuery = ignoreAfterQuery;
    init();
  }

  private void init() {
    backwardWorklist = new ArrayDeque<>();
    forwardWorklist = new ArrayDeque<>();
    definitions = new HashMap<>();
    valueToUnits = new HashMap<>();
    valueKillebyValuedAt = new HashMap<>();
  }

  public SparseAliasingCFG buildSparseCFG(
      Val initialQueryVar, SootMethod m, Val queryVar, Unit queryStmt, SparseCFGQueryLog queryLog) {
    currentMethod = m;
    this.queryStmt = queryStmt;
    queryVarType = SootAdapter.getTypeOfVal(initialQueryVar);

    DirectedGraph<Unit> unitGraph = new BriefUnitGraph(m.getActiveBody());

    Unit head = getHead(unitGraph);

    MutableGraph<Unit> mCFG = numberStmtsAndConvertToMutableGraph(unitGraph);

    int initialStmtCount = mCFG.nodes().size();
    // if (isDebugTarget()) {
    // LOGGER.info(m.getName() + " original");
    // logCFG(LOGGER, mCFG);
    // }
    //    if (!m.getName().equals("main")) {

    findStmtsToKeep(mCFG, SootAdapter.asValue(queryVar), queryStmt);

    List<Unit> tails = unitGraph.getTails();
    for (Unit tail : tails) {
      sparsify(mCFG, head, tail, queryStmt);
    }
    // if (isDebugTarget()) {
    // LOGGER.info(m.getName() + " aa-sparse");
    // logCFG(LOGGER, mCFG);
    // }
    //    }
    int finalStmtCount = mCFG.nodes().size();
    queryLog.setInitialStmtCount(initialStmtCount);
    queryLog.setFinalStmtCount(finalStmtCount);
    return new SparseAliasingCFG(queryVar, mCFG, queryStmt, valueToUnits.keySet(), unitToNumber);
  }

  private void sparsify(MutableGraph<Unit> mCFG, Unit head, Unit tail, Unit queryStmt) {
    Iterator<Unit> iter = getBFSIterator(mCFG, head);
    Set<Unit> stmsToRemove = new HashSet<>();
    while (iter.hasNext()) {
      Unit unit = iter.next();
      if (!isControlStmt(unit)
          && !existInValueToUnits(unit)
          && !unit.equals(queryStmt)
          && !unit.equals(head)
          && !unit.equals(tail)
          && !isTargetCallSite(unit)) {
        stmsToRemove.add(unit);
      }
    }
    for (Unit unit : stmsToRemove) {
      removeStmt(mCFG, unit);
    }
  }

  private boolean isTargetCallSite(Unit unit) {
    if (unit instanceof Stmt) {
      Stmt stmt = (Stmt) unit;
      if (stmt.containsInvokeExpr()) {
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        List<Value> args = invokeExpr.getArgs();
        for (Value d : valueToUnits.keySet()) {
          // v as arg
          for (Value arg : args) {
            if (d.equals(arg)) {
              return true;
            }
          }
          // v as base v.m()
          if (isInvokeBase(d, invokeExpr)) {
            return true;
          }
        }
      }
    }
    return false;
  }

  private boolean existInValueToUnits(Unit stmt) {
    for (Set<Unit> units : valueToUnits.values()) {
      for (Unit unit : units) {
        if (stmt.equals(unit)) {
          return true;
        }
      }
    }
    return false;
  }

  private void findStmtsToKeep(MutableGraph<Unit> mCFG, Value queryVar, Unit queryStmt) {
    activePass = "findBackwardDef";
    Unit def = findBackwardDefForValue(mCFG, queryStmt, queryVar, new HashSet<>(), false);
    putToValueToUnits(queryVar, def);
    activePass = "backwardPass";
    backwardPass(mCFG, def);
    activePass = "forwardPass";
    forwardPass(mCFG, queryStmt);
    activePass = "findStmtsForFieldBase";
    findStmtsForFieldBase(mCFG, queryStmt);
    activePass = "findStmtsAfterKill";
    findStmtsAfterKill(mCFG, queryStmt);
  }

  private void findStmtsForFieldBase(MutableGraph<Unit> mCFG, Unit queryStmt) {
    while (!backwardWorklist.isEmpty()) {
      backwardPass(mCFG, queryStmt);
      while (!forwardWorklist.isEmpty()) {
        forwardPass(mCFG, queryStmt);
      }
    }
  }

  private void findStmtsAfterKill(MutableGraph<Unit> mCFG, Unit queryStmt) {
    Unit def;
    while (!backwardWorklist.isEmpty()) {
      Value val = popBackwardStack();
      Unit killedAt = null;
      Collection<Pair<Value, Unit>> killers = valueKillebyValuedAt.values();
      for (Pair<Value, Unit> killer : killers) {
        if (killer.getX().equals(val)) {
          killedAt = killer.getY();
          break;
        }
      }
      if (killedAt != null) {
        def = findBackwardDefForValue(mCFG, killedAt, val, new HashSet<>(), false);
        putToValueToUnits(val, def);
      } else {
        throw new RuntimeException("if a val is killed there must be a killer");
      }
    }
    forwardPass(mCFG, queryStmt);
  }

  private void backwardPass(MutableGraph<Unit> mCFG, Unit def) {
    while (!backwardWorklist.isEmpty()) {
      Value val = popBackwardStack();
      Unit existingDef = definitions.get(val);
      Unit newDef;
      if (existingDef != null) {
        newDef = findBackwardDefForValue(mCFG, existingDef, val, new HashSet<>(), true);
      } else {
        newDef = findBackwardDefForValue(mCFG, def, val, new HashSet<>(), false);
      }
      def = newDef != null && !(newDef instanceof DefinedOutside) ? newDef : def;
      if (def != null) {
        putToValueToUnits(val, def);
      }
    }
  }

  private void forwardPass(MutableGraph<Unit> mCFG, Unit queryStmt) {
    while (!forwardWorklist.isEmpty()) {
      Value val = popForwardStack();
      Unit defStmt = definitions.get(val);
      putToValueToUnits(val, defStmt);
      Set<Unit> unitsForVal =
          findForwardDefUseForValue(mCFG, defStmt, val, queryStmt, new HashSet<>());
      for (Unit unit : unitsForVal) {
        putToValueToUnits(val, unit);
      }
    }
  }

  private boolean isDebugTarget() {
    return false;
  }

  private void logStackOp(Value val, String op) {
    if (isDebugTarget()) {
      System.out.println(activePass + ": " + op + ": " + val);
    }
  }

  private void pushToForwardStack(Value val) {
    logStackOp(val, "Forw push");
    forwardWorklist.push(val);
  }

  private void pushToBackwardStack(Value val) {
    logStackOp(val, "Back push");
    backwardWorklist.push(val);
  }

  private Value popBackwardStack() {
    Value val = backwardWorklist.pop();
    logStackOp(val, "Back pop");
    return val;
  }

  private Value popForwardStack() {
    Value val = forwardWorklist.pop();
    logStackOp(val, "Forw pop");
    return val;
  }

  private void putToValueToUnits(Value val, Unit stmt) {
    if (valueToUnits.containsKey(val)) {
      Set<Unit> units = valueToUnits.get(val);
      units.add(stmt);
    } else {
      LinkedHashSet<Unit> units = new LinkedHashSet<>();
      units.add(stmt);
      valueToUnits.put(val, units);
    }
    if (isDebugTarget()) {
      System.out.println("putToValueToUnits:");
      for (Value k : valueToUnits.keySet()) {
        System.out.print(k + ": ");
        System.out.println(
            valueToUnits.get(k).stream().map(Objects::toString).collect(Collectors.joining("---")));
      }
    }
  }

  private Unit findBackwardDefForValue(
      MutableGraph<Unit> mCFG, Unit tail, Value queryVar, Set<Unit> visited, boolean existingDef) {
    if (tail == null) {
      return null;
    }
    visited.add(tail);
    if (!existingDef && isDefOfValue(tail, queryVar)) {
      return tail;
    }
    Set<Unit> preds = mCFG.predecessors(tail);
    for (Unit pred : preds) {
      if (!visited.contains(pred)) {
        Unit u = findBackwardDefForValue(mCFG, pred, queryVar, visited, false);
        if (u == null) {
          continue;
        } else {
          return u;
        }
      }
    }
    if (queryVar instanceof JInstanceFieldRef) {
      return new DefinedOutside(queryVar);
    } else {
      return null; // throw new RuntimeException("No def found for value:" + queryVar);
    }
  }

  private boolean isDefOfValue(Unit unit, Value queryVar) {
    if (unit instanceof JAssignStmt) {
      JAssignStmt stmt = (JAssignStmt) unit;
      Value leftOp = stmt.getLeftOp();
      Value rightOp = stmt.getRightOp();
      // handle Casts (Not mentioned)
      if (rightOp instanceof JCastExpr) {
        JCastExpr cast = (JCastExpr) rightOp;
        rightOp = cast.getOp();
      }
      if (queryVar.equals(leftOp)
          || equalsFieldRef(leftOp, queryVar)
          || equalsQueryBase(leftOp, queryVar)
          || equalsArrayItem(leftOp, queryVar)
          || equalsFieldType(leftOp, queryVar)) {
        if (isAllocOrMethodAssignment(stmt, queryVar)) {
          pushToForwardStack(queryVar);
          Set<Value> params = getInvokeBaseAndParams(rightOp, queryVar);
          params.forEach(backwardWorklist::add);
        } else {
          if (equalsFieldRef(rightOp, queryVar)
              && rightOp instanceof JInstanceFieldRef) { // recursion
            Value base = ((JInstanceFieldRef) rightOp).getBase();
            if (base.equals(leftOp)) {
              pushToBackwardStack(leftOp);
              definitions.put(leftOp, unit);
              pushToForwardStack(rightOp);
              definitions.put(rightOp, unit);
              return false;
            }
          }
          if (rightOp instanceof JInstanceFieldRef) {
            Value base = ((JInstanceFieldRef) rightOp).getBase();
            pushToBackwardStack(base);
            // return true;
          }
          pushToBackwardStack(rightOp);
        }
        definitions.put(queryVar, unit);
        return true;
      }
    } else if (unit instanceof JIdentityStmt) {
      JIdentityStmt stmt = (JIdentityStmt) unit;
      Value leftOp = stmt.getLeftOp();
      if (queryVar.equals(leftOp)
          || equalsFieldRef(leftOp, queryVar)
          || equalsArrayItem(leftOp, queryVar)) {
        // we treat it like alloc
        pushToForwardStack(queryVar);
        definitions.put(queryVar, unit);
        return true;
      }
    }
    return false;
  }

  private boolean equalsFieldRef(Value op, Value queryVar) {
    if (op instanceof JInstanceFieldRef && queryVar instanceof JInstanceFieldRef) {
      return ((JInstanceFieldRef) queryVar).getBase().equals(((JInstanceFieldRef) op).getBase())
          && ((JInstanceFieldRef) queryVar).getField().equals(((JInstanceFieldRef) op).getField());
    } else if (op instanceof StaticFieldRef && queryVar instanceof StaticFieldRef) {
      return ((StaticFieldRef) op).getFieldRef().equals(((StaticFieldRef) queryVar).getFieldRef());
    }
    return false;
  }

  private boolean equalsArrayItem(Value op, Value queryVar) {
    if (op instanceof JArrayRef && queryVar instanceof JArrayRef) {
      return ((JArrayRef) queryVar).getBase().equals(((JArrayRef) op).getBase())
          && ((JArrayRef) queryVar).getIndex().equals(((JArrayRef) op).getIndex());
    }
    return false;
  }

  private int getLastNodeIndex() {
    if (lastNodeIndex == -1) {
      lastNodeIndex =
          Collections.max(unitToNumber.entrySet(), Comparator.comparingInt(Map.Entry::getValue))
              .getValue();
    }
    return lastNodeIndex;
  }

  /**
   * Idealy it makes sense to stop searching for stmts after the query stmt, because of flow
   * sensitivity. But boomerang apparently sometimes need the whole method.
   *
   * @param currentStmt
   * @param queryStmt
   * @return
   */
  private boolean shouldStopSearch(Unit currentStmt, Unit queryStmt) {
    if (ignoreAfterQuery) {
      return unitToNumber.get(currentStmt) >= unitToNumber.get(queryStmt);
    } else {
      return unitToNumber.get(currentStmt) >= getLastNodeIndex();
    }
  }

  private Set<Unit> findForwardDefUseForValue(
      MutableGraph<Unit> mCFG, Unit head, Value queryVar, Unit queryStmt, Set<Unit> visited) {
    visited.add(head);
    Set<Unit> stmtsToKeep = new HashSet<>();
    Set<Unit> succs = mCFG.successors(head);
    for (Unit succ : succs) {
      if (shouldStopSearch(succ, queryStmt)) {
        // do not process after queryStmt
        return stmtsToKeep;
      }
      if (!valueKillebyValuedAt.containsKey(queryVar)
          && keepContainingStmtsForward(succ, queryVar)) {
        stmtsToKeep.add(succ);
      }
      if (!visited.contains(succ)) {
        Set<Unit> ret = findForwardDefUseForValue(mCFG, succ, queryVar, queryStmt, visited);
        stmtsToKeep.addAll(ret);
      }
    }
    return stmtsToKeep;
  }

  /**
   * @param unit
   * @param queryVar
   * @return
   */
  private boolean keepContainingStmtsForward(Unit unit, Value queryVar) {
    if (unit instanceof JAssignStmt) {
      JAssignStmt stmt = (JAssignStmt) unit;
      Value leftOp = stmt.getLeftOp();
      Value rightOp = stmt.getRightOp();

      // handle Casts (Not mentioned)
      if (rightOp instanceof JCastExpr) {
        JCastExpr cast = (JCastExpr) rightOp;
        rightOp = cast.getOp();
      }
      if (queryVar.equals(rightOp)
          || equalsFieldRef(rightOp, queryVar)
          || equalsQueryBase(rightOp, queryVar)
          || equalsFieldType(rightOp, queryVar)
          || equalsArrayItem(rightOp, queryVar)
          || equalsInitialFieldType(rightOp, queryVar)) {
        if (!leftOp.equals(queryVar)) { // oth it would create a loop
          pushToForwardStack(leftOp);
          definitions.put(leftOp, unit);
        }
        if (leftOp instanceof JInstanceFieldRef) {
          // we need to find how the base was created too
          Value base = ((JInstanceFieldRef) leftOp).getBase();
          pushToBackwardStack(base);
        }
        return true;
      } else if (queryVar.equals(leftOp)
          || equalsFieldRef(leftOp, queryVar)
          || equalsQueryBase(leftOp, queryVar)
          || equalsArrayItem(leftOp, queryVar)) {
        // kill: need to find the alloc of the killer object
        valueKillebyValuedAt.put(queryVar, new Pair<>(rightOp, unit));
        pushToBackwardStack(rightOp);
        return true;
      } else if (equalsInitialFieldType(leftOp, queryVar)) {
        pushToBackwardStack(rightOp);
        return true;
      }
    }
    return keepInvokeForValue(unit, queryVar);
  }

  /**
   * queryVar is a field reference, and its base equals current rightOp e.g.: queryVar: box.f box2 =
   * box we want to find this
   *
   * @param rightOp
   * @param queryVar
   * @return
   */
  private boolean equalsQueryBase(Value rightOp, Value queryVar) {
    if (queryVar instanceof JInstanceFieldRef) {
      Value queryBase = ((JInstanceFieldRef) queryVar).getBase();
      return queryBase.equals(rightOp);
    }
    return false;
  }

  private boolean equalsInitialFieldType(Value op, Value queryVar) {
    if (op instanceof JInstanceFieldRef) {
      Value base = ((JInstanceFieldRef) op).getBase();
      Type fieldType = ((JInstanceFieldRef) op).getField().getType();
      return base.equals(queryVar) && fieldType.equals(queryVarType);
    }
    return false;
  }

  /**
   * queryVar: this leftOp: this.N and N is of the same type as this we want to keep this.N because
   * it might be used for recursive field store
   *
   * @param op
   * @param queryVar
   * @return
   */
  private boolean equalsFieldType(Value op, Value queryVar) {
    if (op instanceof JInstanceFieldRef) {
      Value base = ((JInstanceFieldRef) op).getBase();
      Type fieldType = ((JInstanceFieldRef) op).getField().getType();
      return base.equals(queryVar) && fieldType.equals(queryVar.getType());
    }
    return false;
  }

  /*
  private boolean isAllocOrMethodAssignment(JAssignStmt stmt, Value d) {
    Value rightOp = stmt.getRightOp();
    if (rightOp instanceof JNewExpr) {
      JNewExpr newExpr = (JNewExpr) rightOp;
      Type type = newExpr.getType();
      if (type.equals(d.getType())) {
        return true;
      }
    } else if (rightOp instanceof JSpecialInvokeExpr
            || rightOp instanceof JStaticInvokeExpr
            || rightOp instanceof JVirtualInvokeExpr
            || rightOp instanceof JInterfaceInvokeExpr
            || rightOp instanceof JDynamicInvokeExpr) {
      return true;
    }
    return false;
  }*/

  private Set<Value> getInvokeBaseAndParams(Value invoke, Value d) {
    Set<Value> otherArgs = new HashSet<>();
    if (invoke instanceof AbstractInvokeExpr) {
      List<Value> args = ((AbstractInvokeExpr) invoke).getArgs();
      for (Value arg : args) {
        if (!arg.equals(d) && (arg.getType().equals(d.getType()))
            || arg.getType().equals(queryVarType)) {
          otherArgs.add(arg);
        }
      }
    }
    if (invoke instanceof AbstractInstanceInvokeExpr) {
      Value base = ((AbstractInstanceInvokeExpr) invoke).getBase();
      otherArgs.add(base);
    }
    return otherArgs;
  }

  private boolean keepInvokeForValue(Unit unit, Value d) {
    if (unit instanceof Stmt) {
      Stmt stmt = (Stmt) unit;
      if (stmt.containsInvokeExpr()) {
        InvokeExpr invokeExpr = stmt.getInvokeExpr();
        List<Value> args = invokeExpr.getArgs();
        // v as arg
        // TODO: check this looks absurd
        for (Value arg : args) {
          if (d.equals(arg)) {
            for (Value otherArg : args) {
              if (!otherArg.equals(d) && otherArg.getType().equals(d.getType())) {
                // other args of the same type can cause aliasing so we need to find their alloc
                // sites
                if (!definitions.containsKey(otherArg)) {
                  pushToBackwardStack(otherArg);
                }
              }
            }
            return true;
          }
        }
        // v as base v.m()
        if (isInvokeBase(d, invokeExpr)) {
          Set<Value> params = getInvokeBaseAndParams(invokeExpr, d);
          params.forEach(backwardWorklist::push);
          return true;
        }
      }
    }
    return false;
  }

  private boolean isInvokeBase(Value d, InvokeExpr invokeExpr) {
    if (invokeExpr instanceof JVirtualInvokeExpr) {
      Value base = ((JVirtualInvokeExpr) invokeExpr).getBase();
      return d.equals(base);
    } else if (invokeExpr instanceof JSpecialInvokeExpr) {
      Value base = ((JSpecialInvokeExpr) invokeExpr).getBase();
      return d.equals(base);
    }
    return false;
  }

  private boolean isAllocOrMethodAssignment(JAssignStmt stmt, Value d) {
    Value rightOp = stmt.getRightOp();
    if (rightOp instanceof JNewExpr) {
      JNewExpr newExpr = (JNewExpr) rightOp;
      Type type = newExpr.getType();
      return type.equals(d.getType());
    } else
      return rightOp instanceof JSpecialInvokeExpr
          || rightOp instanceof JStaticInvokeExpr
          || rightOp instanceof JVirtualInvokeExpr
          || rightOp instanceof JInterfaceInvokeExpr
          || rightOp instanceof JDynamicInvokeExpr;
  }

  /**
   * DFS traverse Original Graph and keep all the stmts
   *
   * @param curr
   * @param graph
   * @param cfg
   */
  private void buildCompleteCFG(Unit curr, DirectedGraph<Unit> graph, SparseAliasingCFG cfg) {
    List<Unit> succs = graph.getSuccsOf(curr);
    if (succs == null || succs.isEmpty()) {
      return;
    }
    for (Unit succ : succs) {
      if (cfg.addEdge(curr, succ)) {
        buildCompleteCFG(succ, graph, cfg);
      }
    }
  }
}
