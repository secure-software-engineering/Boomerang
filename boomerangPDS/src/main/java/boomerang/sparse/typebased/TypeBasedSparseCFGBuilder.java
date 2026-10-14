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
package boomerang.sparse.typebased;

import boomerang.scope.InstanceFieldVal;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.sparse.AbstractSparseCFGBuilder;
import boomerang.sparse.SparseAliasingCFG;
import boomerang.sparse.eval.SparseCFGQueryLog;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.Nullable;

/**
 * Keeps the statements that involve values whose type may alias with the type of the query
 * variable. If such a value is stored in a field of a container object (or passed to a method of
 * one), the statements involving the container type are kept as well.
 */
public class TypeBasedSparseCFGBuilder extends AbstractSparseCFGBuilder {

  private final Deque<Type> typeWorklist = new ArrayDeque<>();
  private final Set<Type> containerTypes = new HashSet<>();
  private final Set<Statement> stmtsToKeep = new HashSet<>();

  public TypeBasedSparseCFGBuilder(Method method, Collection<Statement> requiredStmts) {
    super(method, requiredStmts);
  }

  /**
   * @param queryVar the variable the graph is built for
   * @param trackedTypes the types whose statements are kept, additional container types are added
   *     while building the graph
   * @param seedStmts the statements the propagation is at, they are kept in the graph
   * @param queryLog log to record statistics
   * @return the sparse graph or null if a seed statement is not part of the method's graph
   */
  public @Nullable SparseAliasingCFG build(
      Val queryVar,
      Collection<Type> trackedTypes,
      Collection<Statement> seedStmts,
      SparseCFGQueryLog queryLog) {
    if (!graph.nodes().containsAll(seedStmts)) {
      return null;
    }

    int initialStmtCount = getStatementCount();

    for (Type type : trackedTypes) {
      if (containerTypes.add(type)) {
        typeWorklist.push(type);
      }
    }
    while (!typeWorklist.isEmpty()) {
      findStmtsToKeep(typeWorklist.pop());
    }
    stmtsToKeep.addAll(seedStmts);

    sparsify();

    queryLog.setInitialStmtCount(initialStmtCount);
    queryLog.setFinalStmtCount(getStatementCount());
    Statement queryStmt = seedStmts.iterator().next();
    Set<Type> types = Set.copyOf(containerTypes);
    return new SparseAliasingCFG(
        method, queryVar, queryStmt, graph, val -> tracksType(types, val.getType()));
  }

  private static boolean tracksType(Set<Type> types, Type type) {
    for (Type tracked : types) {
      if (mayAlias(type, tracked)) {
        return true;
      }
    }
    return false;
  }

  /**
   * @return the tracked types including the container types found while building the graph
   */
  public Set<Type> getTrackedTypes() {
    return Collections.unmodifiableSet(containerTypes);
  }

  @Override
  protected boolean keep(Statement stmt) {
    return stmtsToKeep.contains(stmt);
  }

  private void findStmtsToKeep(Type queryVarType) {
    for (Statement stmt : breadthFirst()) {
      if (keepStmt(stmt, queryVarType)) {
        stmtsToKeep.add(stmt);
      }
    }
  }

  private boolean keepStmt(Statement stmt, Type queryVarType) {
    // In case of Container.f = base.m(f_type), keep both base and Container as container types
    boolean keep = false;
    if (stmt.containsInvokeExpr()) {
      InvokeExpr invokeExpr = stmt.getInvokeExpr();
      // v as arg
      for (Val arg : invokeExpr.getArgs()) {
        if (isFieldTypeRelevant(queryVarType, arg)) {
          handleInvokeBase(invokeExpr, queryVarType);
          keep = true;
        }
        if (mayAlias(arg.getType(), queryVarType)) {
          handleInvokeBase(invokeExpr, queryVarType);
          keep = true;
        }
      }
      // v as base v.m()
      if (isInvokeBase(queryVarType, invokeExpr)) {
        keep = true;
      }
    }

    if (stmt.isAssignStmt()) {
      Val leftOp = stmt.getLeftOp();
      Val rightOp = stmt.getRightOp();
      if (rightOp.isCast()) {
        rightOp = rightOp.getCastOp();
      }

      if (isOuterClassReference(rightOp)) {
        keep = true;
      }

      // the field of left or right is of the same type
      if (isFieldTypeRelevant(queryVarType, leftOp) || isFieldTypeRelevant(queryVarType, rightOp)) {
        keep = true;
      }

      // the array element of left or right is of the same type
      if (isArrayItemTypeRelevant(queryVarType, leftOp)
          || isArrayItemTypeRelevant(queryVarType, rightOp)) {
        keep = true;
      }

      // left or right is of the same type
      if (mayAlias(leftOp.getType(), queryVarType) || mayAlias(rightOp.getType(), queryVarType)) {
        // always check the invoke base if it is assigned to the type we track, because we need to
        // know how it was allocated
        if (stmt.containsInvokeExpr()) {
          handleInvokeBase(stmt.getInvokeExpr(), queryVarType);
        }
        keep = true;
      }
    }
    return keep;
  }

  /** References to the outer class of inner classes (this$0) */
  private static boolean isOuterClassReference(Val val) {
    if (val.isLocal()) {
      return val.getVariableName().startsWith("this$");
    }
    if (val instanceof InstanceFieldVal) {
      return ((InstanceFieldVal) val).getField().getName().startsWith("this$");
    }
    return false;
  }

  /** Keep the base of an invoke in the graph */
  private void handleInvokeBase(InvokeExpr invokeExpr, Type queryVarType) {
    if (invokeExpr.isInstanceInvokeExpr()) {
      handleContainerType(invokeExpr.getBase(), queryVarType);
    }
  }

  private void handleContainerType(Val base, Type queryVarType) {
    Type baseType = base.getType();
    if (!baseType.equals(queryVarType) && containerTypes.add(baseType)) {
      // we need to find how the base is allocated
      typeWorklist.push(baseType);
    }
  }

  /**
   * Checks whether the value is an instance field whose type may alias with the query type. In that
   * case, the base type of the field is tracked as a container type.
   */
  private boolean isFieldTypeRelevant(Type queryVarType, Val val) {
    if (val instanceof InstanceFieldVal) {
      InstanceFieldVal fieldVal = (InstanceFieldVal) val;
      if (mayAlias(fieldVal.getType(), queryVarType)) {
        handleContainerType(fieldVal.getBase(), queryVarType);
        return true;
      }
    }
    return false;
  }

  /**
   * Checks whether the value is an array element whose type may alias with the query type. In that
   * case, the type of the array is tracked as a container type.
   */
  private boolean isArrayItemTypeRelevant(Type queryVarType, Val val) {
    if (val.isArrayRef() && mayAlias(val.getType(), queryVarType)) {
      handleContainerType(val.getArrayBase().getBase(), queryVarType);
      return true;
    }
    return false;
  }

  private boolean isInvokeBase(Type queryVarType, InvokeExpr invokeExpr) {
    if (invokeExpr.isInstanceInvokeExpr()) {
      return mayAlias(invokeExpr.getBase().getType(), queryVarType);
    }
    return false;
  }
}
