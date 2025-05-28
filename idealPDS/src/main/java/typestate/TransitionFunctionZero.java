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
package typestate;

import boomerang.scope.Statement;
import java.util.Map;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public class TransitionFunctionZero implements TransitionFunction {

  @NonNull private static final TransitionFunctionZero zero = new TransitionFunctionZero();

  private TransitionFunctionZero() {}

  @NonNull
  public static TransitionFunctionZero zero() {
    return zero;
  }

  @NonNull
  @Override
  public Map<Transition, Statement> getStateChangeStatements() {
    throw new IllegalStateException("TransitionZero.getStateChangeStatements() - don't");
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return this;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    if (!(other instanceof TransitionFunction)) {
      throw new IllegalStateException();
    }
    return other;
  }

  public String toString() {
    return "ZERO";
  }
}
