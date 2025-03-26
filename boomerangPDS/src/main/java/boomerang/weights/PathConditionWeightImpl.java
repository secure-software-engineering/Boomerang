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
package boomerang.weights;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Map;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class PathConditionWeightImpl implements PathConditionWeight {

  private Map<Statement, ConditionDomain> ifStatements = Maps.newHashMap();
  private Map<Val, ConditionDomain> variableToValue = Maps.newHashMap();
  private Set<Val> returnVals = Sets.newHashSet();
  private Map<Method, Statement> calleeToCallSite = Maps.newHashMap();

  public PathConditionWeightImpl(Statement callSite, Method callee) {
    this.calleeToCallSite.put(callee, callSite);
  }

  public PathConditionWeightImpl(Val returnVal) {
    this.returnVals.add(returnVal);
  }

  public PathConditionWeightImpl(
      Map<Statement, ConditionDomain> ifStatements,
      Map<Val, ConditionDomain> variableToValue,
      Set<Val> returnVals,
      Map<Method, Statement> calleeToCallSiteMapping) {
    this.ifStatements = ifStatements;
    this.variableToValue = variableToValue;
    this.returnVals = returnVals;
    this.calleeToCallSite = calleeToCallSiteMapping;
  }

  public PathConditionWeightImpl(Statement ifStatement, Boolean condition) {
    ifStatements.put(ifStatement, condition ? ConditionDomain.TRUE : ConditionDomain.FALSE);
  }

  public PathConditionWeightImpl(Val val, ConditionDomain c) {
    variableToValue.put(val, c);
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight o) {
    if (!(o instanceof PathConditionWeightImpl)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathConditionWeightImpl other = (PathConditionWeightImpl) o;
    Map<Statement, ConditionDomain> newIfs = Maps.newHashMap();

    newIfs.putAll(ifStatements);
    for (Map.Entry<Statement, ConditionDomain> e : other.ifStatements.entrySet()) {
      if (newIfs.containsKey(e.getKey()) && e.getValue().equals(ConditionDomain.TOP)) {
        newIfs.put(e.getKey(), ConditionDomain.TOP);
      } else {
        newIfs.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> newVals = Maps.newHashMap();

    newVals.putAll(variableToValue);
    for (Map.Entry<Val, ConditionDomain> e : other.variableToValue.entrySet()) {
      if (newVals.containsKey(e.getKey()) && e.getValue().equals(ConditionDomain.TOP)) {
        newVals.put(e.getKey(), ConditionDomain.TOP);
      } else {
        newVals.put(e.getKey(), e.getValue());
      }
    }

    // May become a performance bottleneck
    Map<Val, ConditionDomain> returnToAssignedVariableMap = Maps.newHashMap();
    if (!returnVals.isEmpty()) {
      for (Map.Entry<Val, ConditionDomain> v : newVals.entrySet()) {
        if (returnVals.contains(v.getKey())) {
          Statement s = calleeToCallSite.get(v.getKey().m());
          if (s != null && s.isAssignStmt()) {
            Val leftOp = s.getLeftOp();
            returnToAssignedVariableMap.put(leftOp, v.getValue());
          }
        }
      }
    }
    newVals.putAll(returnToAssignedVariableMap);
    Set<Val> newReturnVals = Sets.newHashSet(returnVals);
    newReturnVals.addAll(other.returnVals);
    Map<Method, Statement> calleeToCallSiteMapping = Maps.newHashMap(calleeToCallSite);
    calleeToCallSiteMapping.putAll(other.calleeToCallSite);
    return new PathConditionWeightImpl(newIfs, newVals, newReturnVals, calleeToCallSiteMapping);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight o) {
    if (!(o instanceof PathConditionWeightImpl)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathConditionWeightImpl other = (PathConditionWeightImpl) o;
    Map<Statement, ConditionDomain> newIfs = Maps.newHashMap();
    for (Map.Entry<Statement, ConditionDomain> e : ifStatements.entrySet()) {
      if (other.ifStatements.containsKey(e.getKey())) {
        ConditionDomain otherVal = other.ifStatements.get(e.getKey());
        if (e.getValue().equals(otherVal)) {
          newIfs.put(e.getKey(), otherVal);
        } else {
          newIfs.put(e.getKey(), ConditionDomain.TOP);
        }
      } else {
        newIfs.put(e.getKey(), e.getValue());
      }
    }
    for (Map.Entry<Statement, ConditionDomain> e : other.ifStatements.entrySet()) {
      if (!ifStatements.containsKey(e.getKey())) {
        newIfs.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> newVals = Maps.newHashMap();
    for (Map.Entry<Val, ConditionDomain> e : variableToValue.entrySet()) {
      if (other.variableToValue.containsKey(e.getKey())) {
        ConditionDomain otherVal = other.variableToValue.get(e.getKey());
        if (e.getValue().equals(otherVal)) {
          newVals.put(e.getKey(), otherVal);
        } else {
          newVals.put(e.getKey(), ConditionDomain.TOP);
        }
      } else {
        newVals.put(e.getKey(), e.getValue());
      }
    }
    for (Map.Entry<Val, ConditionDomain> e : other.variableToValue.entrySet()) {
      if (!variableToValue.containsKey(e.getKey())) {
        newVals.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> returnToAssignedVariableMap = Maps.newHashMap();
    if (!returnVals.isEmpty()) {
      for (Map.Entry<Val, ConditionDomain> v : newVals.entrySet()) {
        if (returnVals.contains(v.getKey())) {
          Statement s = calleeToCallSite.get(v.getKey().m());
          if (s != null && s.isAssignStmt()) {
            Val leftOp = s.getLeftOp();
            returnToAssignedVariableMap.put(leftOp, v.getValue());
          }
        }
      }
    }
    newVals.putAll(returnToAssignedVariableMap);
    Set<Val> newReturnVals = Sets.newHashSet(returnVals);
    newReturnVals.addAll(other.returnVals);
    Map<Method, Statement> calleeToCallSiteMapping = Maps.newHashMap(calleeToCallSite);
    calleeToCallSiteMapping.putAll(other.calleeToCallSite);
    return new PathConditionWeightImpl(newIfs, newVals, newReturnVals, calleeToCallSiteMapping);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((ifStatements == null) ? 0 : ifStatements.hashCode());
    result = prime * result + ((variableToValue == null) ? 0 : variableToValue.hashCode());

    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    PathConditionWeightImpl other = (PathConditionWeightImpl) obj;
    if (ifStatements == null) {
      if (other.ifStatements != null) {
        return false;
      }
    } else if (!ifStatements.equals(other.ifStatements)) {
      return false;
    }

    if (variableToValue == null) {
      if (other.variableToValue != null) {
        return false;
      }
    } else if (!variableToValue.equals(other.variableToValue)) {
      return false;
    }
    return false;
  }

  @Override
  public Map<Statement, ConditionDomain> getConditions() {
    return ifStatements;
  }

  @Override
  public Map<Val, ConditionDomain> getEvaluationMap() {
    return variableToValue;
  }

  @Override
  public String toString() {
    return "\nIf statements: " + ifStatements + " Vals: " + variableToValue;
  }
}
