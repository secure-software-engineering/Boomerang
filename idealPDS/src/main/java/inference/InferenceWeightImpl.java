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
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class InferenceWeightImpl implements InferenceWeight {

  @NonNull
  public Set<Method> getInvokedMethods() {
    return invokedMethods;
  }

  @NonNull private final Set<Method> invokedMethods;

  private InferenceWeightImpl(@NonNull Set<Method> res) {
    this.invokedMethods = res;
  }

  public InferenceWeightImpl(@NonNull Method m) {
    this.invokedMethods = Collections.singleton(m);
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    InferenceWeight one = one();
    if (other == (one)) return this;
    InferenceWeight zero = (InferenceWeight) zero();
    if (other == zero) {
      return zero;
    }
    InferenceWeightImpl func = (InferenceWeightImpl) other;
    Set<Method> res = new HashSet<>(invokedMethods);
    res.addAll((func).invokedMethods);
    return new InferenceWeightImpl(res);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
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
