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
import boomerang.scope.Method;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import wpds.impl.Weight;

public class InferenceWeightZero implements Weight {

  @NonNull private static final InferenceWeightZero zero = new InferenceWeightZero();

  private InferenceWeightZero() {}

  @NonNull
  public Set<Method> getInvokedMethods() {
    throw new IllegalStateException("InferenceWeightZero.getInvoke -dont");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return zero();
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return extendWith(other);
  }

  @NonNull
  public static InferenceWeightZero zero() {
    return zero;
  }

  public String toString() {
    return "ZERO";
  }
}
