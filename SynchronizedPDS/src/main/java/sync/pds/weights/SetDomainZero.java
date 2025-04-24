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
package sync.pds.weights;

import static sync.pds.weights.SetDomainOne.one;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.HashSet;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainZero implements SetDomain {

  @NonNull private static final SetDomainZero zero = new SetDomainZero();

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(one())) {
      return other;
    }
    return zero();
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {

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

  @NonNull
  @Override
  public Collection<Node> getNodes() {
    throw new IllegalStateException("SetDomain.getNodes() - don't");
  }

  public static SetDomainZero zero() {
    return zero;
  }

  @NonNull
  @Override
  public Collection<Node> elements() {
    throw new IllegalStateException("SetDomainZero.elements() - don't");
  }

  public String toString() {
    return "ZERO";
  }
}
