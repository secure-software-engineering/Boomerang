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
package typestate.finiteautomata;

import javax.annotation.Nonnull;

public class TransitionIdentity implements Transition {

  @Nonnull private static final TransitionIdentity instance = new TransitionIdentity();

  public static TransitionIdentity getIdentity() {
    return instance;
  }

  private TransitionIdentity() {
    /* Singleton */
  }

  @Override
  @Nonnull
  public State from() {
    throw new IllegalStateException("don't");
  }

  @Override
  @Nonnull
  public State to() {
    throw new IllegalStateException("don't");
  }

  @Override
  public boolean equals(Object obj) {
    return this == obj;
  }

  public String toString() {
    return "ID -> ID";
  }
}
