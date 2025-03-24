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
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainOne implements SetDomain {
  @Nonnull private static final SetDomainOne one = new SetDomainOne();

  public static SetDomainOne one() {
    return one;
  }

 @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other==(one())) {
      return this;
    }
    if (this==(one())) {
      return other;
    }
    return zero();
  }

  @Nonnull
 @Override
  public Weight combineWith(@Nonnull Weight other) {

    SetDomain zero = zero();
    if (other== zero) return this;
    if (this==(one()) || other==(one())) return one();
    if (other instanceof SetDomainOne) {
      throw new IllegalStateException("SetDomainOne.CombineWith-Dont");
      //      Set<Node<Stmt, Fact>> merged = Sets.newHashSet(getNodes());
      //      merged.addAll(((SetDomainImpl) other).getNodes());
      //      return new SetDomainImpl<N, Stmt, Fact>(merged);
    }
    return zero;
  }

  @Nonnull
  @Override
  public Collection<Node> getNodes() {
    throw new IllegalStateException("SetDomianOne.nodes() - don't");
  }

  @Nonnull
  @Override
  public Collection<Node> elements() {
    throw new IllegalStateException("SetDomianOne.elements() - don't");
  }

  public String toString() {
    return "ONE";
  }
}
