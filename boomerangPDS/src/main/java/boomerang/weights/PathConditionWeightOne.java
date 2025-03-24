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
import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class PathConditionWeightOne implements PathConditionWeight {

  @Nonnull private static final PathConditionWeightOne one = new PathConditionWeightOne();

  public PathConditionWeightOne(
      Map<Statement, ConditionDomain> newIfs,
      Map<Val, ConditionDomain> newVals) {
    throw new IllegalStateException("PathConditionWeightOne.ExtendWeight called");
  }

  public Map<? extends Statement, ? extends ConditionDomain> getIfStatements() {
    throw new IllegalStateException("PathConditionWeightOne.getIfStatements() - don't");
  }

  public Map<? extends Val, ? extends ConditionDomain> getVariableToValue() {
    throw new IllegalStateException("PathConditionWeightOne.getVariableToValue() - don't");
  }

  public Set<Val> getReturnVals() {
    throw new IllegalStateException("PathConditionWeightOne.getReturnVals() - don't");
  }

  public Map<Method, Statement> getCalleeToCallSite() {
    throw new IllegalStateException("PathConditionWeightOne.getCalleeToCallSite() - don't");
  }

  public PathConditionWeightOne() {}

  public static PathConditionWeightOne one() {
    return one;
  }





  public enum ConditionDomain {
    TRUE,
    FALSE,
    TOP
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight o) {
    if (!(o instanceof PathConditionWeightOne)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathConditionWeightOne other = (PathConditionWeightOne) o;
    Map<Statement, ConditionDomain> newIfs = Maps.newHashMap();

    newIfs.putAll(getIfStatements());
    for (Map.Entry<? extends Statement, ? extends ConditionDomain> e :
        other.getIfStatements().entrySet()) {
      if (newIfs.containsKey(e.getKey()) && e.getValue().equals(ConditionDomain.TOP)) {
        newIfs.put(e.getKey(), ConditionDomain.TOP);
      } else {
        newIfs.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> newVals = Maps.newHashMap();

    newVals.putAll(getVariableToValue());
    for (Map.Entry<? extends Val, ? extends ConditionDomain> e :
        other.getVariableToValue().entrySet()) {
      if (newVals.containsKey(e.getKey()) && e.getValue().equals(ConditionDomain.TOP)) {
        newVals.put(e.getKey(), ConditionDomain.TOP);
      } else {
        newVals.put(e.getKey(), e.getValue());
      }
    }

    // May become a performance bottleneck
    Map<Val, ConditionDomain> returnToAssignedVariableMap = Maps.newHashMap();
    if (!getReturnVals().isEmpty()) {
      for (Map.Entry<Val, ConditionDomain> v : newVals.entrySet()) {
        if (getReturnVals().contains(v.getKey())) {
          Statement s = getCalleeToCallSite().get(v.getKey().m());
          if (s != null && s.isAssignStmt()) {
            Val leftOp = s.getLeftOp();
            returnToAssignedVariableMap.put(leftOp, v.getValue());
          }
        }
      }
    }
    newVals.putAll(returnToAssignedVariableMap);
    Set<Val> newReturnVals = Sets.newHashSet(getReturnVals());
    newReturnVals.addAll(other.getReturnVals());
    Map<Method, Statement> calleeToCallSiteMapping = Maps.newHashMap(getCalleeToCallSite());
    calleeToCallSiteMapping.putAll(other.getCalleeToCallSite());
    return new PathConditionWeightOne(newIfs, newVals);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight o) {
    if (!(o instanceof PathConditionWeightOne)) {
      throw new RuntimeException("Cannot extend to different types of weight!");
    }
    PathConditionWeightOne other = (PathConditionWeightOne) o;
    Map<Statement, ConditionDomain> newIfs = Maps.newHashMap();
    for (Map.Entry<? extends Statement, ? extends ConditionDomain> e :
        getIfStatements().entrySet()) {
      if (other.getIfStatements().containsKey(e.getKey())) {
        ConditionDomain otherVal = other.getIfStatements().get(e.getKey());
        if (e.getValue().equals(otherVal)) {
          newIfs.put(e.getKey(), otherVal);
        } else {
          newIfs.put(e.getKey(), ConditionDomain.TOP);
        }
      } else {
        newIfs.put(e.getKey(), e.getValue());
      }
    }
    for (Map.Entry<? extends Statement, ? extends ConditionDomain> e :
        other.getIfStatements().entrySet()) {
      if (!getIfStatements().containsKey(e.getKey())) {
        newIfs.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> newVals = Maps.newHashMap();
    for (Map.Entry<? extends Val, ? extends ConditionDomain> e : getVariableToValue().entrySet()) {
      if (other.getVariableToValue().containsKey(e.getKey())) {
        ConditionDomain otherVal = other.getVariableToValue().get(e.getKey());
        if (e.getValue().equals(otherVal)) {
          newVals.put(e.getKey(), otherVal);
        } else {
          newVals.put(e.getKey(), ConditionDomain.TOP);
        }
      } else {
        newVals.put(e.getKey(), e.getValue());
      }
    }
    for (Map.Entry<? extends Val, ? extends ConditionDomain> e :
        other.getVariableToValue().entrySet()) {
      if (!getVariableToValue().containsKey(e.getKey())) {
        newVals.put(e.getKey(), e.getValue());
      }
    }

    Map<Val, ConditionDomain> returnToAssignedVariableMap = Maps.newHashMap();
    if (!getReturnVals().isEmpty()) {
      for (Map.Entry<Val, ConditionDomain> v : newVals.entrySet()) {
        if (getReturnVals().contains(v.getKey())) {
          Statement s = getCalleeToCallSite().get(v.getKey().m());
          if (s != null && s.isAssignStmt()) {
            Val leftOp = s.getLeftOp();
            returnToAssignedVariableMap.put(leftOp, v.getValue());
          }
        }
      }
    }
    newVals.putAll(returnToAssignedVariableMap);
    Set<Val> newReturnVals = Sets.newHashSet(getReturnVals());
    newReturnVals.addAll(other.getReturnVals());
    Map<Method, Statement> calleeToCallSiteMapping = Maps.newHashMap(getCalleeToCallSite());
    calleeToCallSiteMapping.putAll(other.getCalleeToCallSite());
    return new PathConditionWeightOne(newIfs, newVals);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((getIfStatements() == null) ? 0 : getIfStatements().hashCode());
    result =
        prime * result + ((getVariableToValue() == null) ? 0 : getVariableToValue().hashCode());
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
    PathConditionWeightOne other = (PathConditionWeightOne) obj;
    if (getIfStatements() == null) {
      if (other.getIfStatements() != null) {
        return false;
      }
    } else if (!getIfStatements().equals(other.getIfStatements())) {
      return false;
    }

    if (getVariableToValue() == null) {
      if (other.getVariableToValue() != null) {
        return false;
      }
    } else if (!getVariableToValue().equals(other.getVariableToValue())) {
      return false;
    }
    return false;
  }

  @Override
  public Map<Statement, PathConditionWeight.ConditionDomain> getConditions() {
    throw new IllegalStateException("PathConditionWeightOne.getCondition");
  }

  @Override
  public Map<Val, PathConditionWeight.ConditionDomain> getEvaluationMap() {
    throw new IllegalStateException("PathConditionWeightOne.getCondition");
  }

  @Override
  public String toString() {
    return "\nIf statements: " + getIfStatements() + " Vals: " + getVariableToValue();
  }
}