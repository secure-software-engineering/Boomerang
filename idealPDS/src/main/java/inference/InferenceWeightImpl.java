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
package inference;

import static inference.InferenceWeightOne.one;
import static inference.InferenceWeightZero.zero;

import boomerang.scope.Method;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class InferenceWeightImpl implements InferenceWeight {

  @Nonnull
  public Set<Method> getInvokedMethods() {
    return invokedMethods;
  }

  @Nonnull private final Set<Method> invokedMethods;

  private InferenceWeightImpl(@Nonnull Set<Method> res) {
    this.invokedMethods = res;
  }

  public InferenceWeightImpl(@Nonnull Method m) {
    this.invokedMethods = Collections.singleton(m);
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    if (other.equals(zero()) || this.equals(zero())) {
      return zero();
    }
    InferenceWeight func = (InferenceWeightImpl) other;
    Set<Method> otherInvokedMethods = ((InferenceWeightImpl) func).invokedMethods;
    Set<Method> res = new HashSet<>(invokedMethods);
    res.addAll(otherInvokedMethods);
    return new InferenceWeightImpl(res);
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return extendWith(other);
  }

  public String toString() {
    return "{Func:" + invokedMethods + "}";
  }

  @Override
  public int hashCode() {
    return invokedMethods.hashCode();
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
    InferenceWeightImpl other = (InferenceWeightImpl) obj;
    return invokedMethods.equals(other.invokedMethods);
  }
}
