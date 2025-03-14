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
import java.util.HashSet;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import static sync.pds.weights.SetDomainOne.one;

public class SetDomainZero implements SetDomain {

  @Nonnull private static final SetDomainZero zero = new SetDomainZero();

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(one())) {
      return other;
    }
    return zero();
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {

    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    if (this.equals(one()) || other.equals(one())) return one();

    if (other instanceof SetDomainOne) {
      HashSet<Node> merged = Sets.newHashSet(getNodes());
      merged.addAll(((SetDomainImpl) other).getNodes());
      throw new IllegalStateException("SetDomainZero.CombineWith-Dont");
//      return new SetDomainImpl<N, Stmt, Fact>(merged);
    }
    return zero();
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
