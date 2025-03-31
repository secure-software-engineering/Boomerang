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

import static sync.pds.weights.SetDomainZero.zero;

import java.util.Collection;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainOne implements SetDomain {
  @NonNull private static final SetDomainOne one = new SetDomainOne();

  public static SetDomainOne one() {
    return one;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other == one()) {
      return this;
    }
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {

    SetDomain zero = zero();
    if (other == zero) return this;
    if (other == (one())) return one();
    return zero;
  }

  @NonNull
  @Override
  public Collection<Node> getNodes() {
    throw new IllegalStateException("SetDomianOne.nodes() - don't");
  }

  @NonNull
  @Override
  public Collection<Node> elements() {
    throw new IllegalStateException("SetDomianOne.elements() - don't");
  }

  public String toString() {
    return "ONE";
  }
}
