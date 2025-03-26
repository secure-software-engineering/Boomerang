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
package typestate.finiteautomata;

import java.util.Objects;
import org.jspecify.annotations.NonNull;

public class TransitionImpl implements Transition {
  @NonNull private final State from;
  @NonNull private final State to;

  public TransitionImpl(@NonNull State from, @NonNull State to) {
    this.from = from;
    this.to = to;
  }

  @Override
  @NonNull
  public State from() {
    return from;
  }

  @Override
  @NonNull
  public State to() {
    return to;
  }

  @Override
  public int hashCode() {
    return Objects.hash(from, to);
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
    TransitionImpl other = (TransitionImpl) obj;
    if (!from.equals(other.from)) {
      return false;
    }
    return to.equals(other.to);
  }

  @Override
  @NonNull
  public String toString() {
    return from + " -> " + to;
  }
}
