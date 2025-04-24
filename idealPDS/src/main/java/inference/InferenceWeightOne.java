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
package inference;

/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
import static inference.InferenceWeightZero.zero;

import boomerang.scope.Method;
import java.util.HashSet;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class InferenceWeightOne implements InferenceWeight {

  @NonNull private static final InferenceWeightOne one = new InferenceWeightOne();

  private InferenceWeightOne() {}

  @NonNull
  public Set<Method> getInvokedMethods() {
    throw new IllegalStateException("InferenceWeightOne.getInvoke -dont");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other.equals(one())) return this;
    if (this.equals(one())) return other;
    if (other.equals(zero()) || this.equals(zero())) {
      return zero();
    }
    InferenceWeight func = (InferenceWeightImpl) other;
    Set<Method> otherInvokedMethods = ((InferenceWeightImpl) func).getInvokedMethods();
    Set<Method> res = new HashSet<>(getInvokedMethods());
    res.addAll(otherInvokedMethods);
    return new InferenceWeightImpl((Method) res);
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return extendWith(other);
  }

  @NonNull
  public static InferenceWeightOne one() {
    return one;
  }

  public String toString() {
    return "ONE";
  }
}
