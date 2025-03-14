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
import java.util.Set;
import javax.annotation.Nonnull;

import com.google.common.collect.Sets;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

import static sync.pds.weights.SetDomainZero.zero;



public class SetDomainOne implements SetDomain {
  @Nonnull private static final SetDomainOne one = new SetDomainOne();

  public static Weight one() {
    return one;
  }

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
      throw new IllegalStateException("SetDomainOne.CombineWith-Dont");
//      Set<Node<Stmt, Fact>> merged = Sets.newHashSet(getNodes());
//      merged.addAll(((SetDomainImpl) other).getNodes());
//      return new SetDomainImpl<N, Stmt, Fact>(merged);
    }
    return zero();
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
