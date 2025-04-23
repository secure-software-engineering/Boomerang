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

import org.jspecify.annotations.NonNull;

public class TransitionIdentity implements Transition {

  @NonNull private static final TransitionIdentity instance = new TransitionIdentity();

  @NonNull
  public static TransitionIdentity identity() {
    return instance;
  }

  private TransitionIdentity() {
    /* Singleton */
  }

  @Override
  @NonNull
  public State from() {
    throw new IllegalStateException("TransitionIdentity.from() - don't");
  }

  @Override
  @NonNull
  public State to() {
    throw new IllegalStateException("TransitionIdentity.to() - don't");
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  @Override
  @NonNull
  public String toString() {
    return "ID -> ID";
  }
}
