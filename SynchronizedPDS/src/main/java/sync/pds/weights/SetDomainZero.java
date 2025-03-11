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
package sync.pds.weights;

import java.util.Collection;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainZero implements SetDomain {

  @Nonnull private static final SetDomainZero zero = new SetDomainZero();

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return zero();
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (!(other instanceof SetDomainZero)) {
      throw new RuntimeException("SetDomainZero.combineWith() - don't");
    }
    return other;
  }

  @Nonnull
  @Override
  public Collection<Node> getNodes() {
    throw new IllegalStateException("SetDomain.getNodes() - don't");
  }

  public static SetDomainZero zero() {
    return zero;
  }

  @Nonnull
  @Override
  public Collection<Node> elements() {
    throw new IllegalStateException("SetDomainZero.elements() - don't");
  }

  public String toString() {
    return "ZERO";
  }
}
