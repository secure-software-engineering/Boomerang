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
import static sync.pds.weights.SetDomainZero.zero;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainImpl<N, Stmt, Fact> implements SetDomain {

  @NonNull private final Collection<? extends Node<Stmt, Fact>> nodes;

  public SetDomainImpl(Collection<Node<Stmt, Fact>> nodes) {
    this.nodes = nodes;
  }

  @NonNull
  @Override
  public Weight extendWith(Weight other) {
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

    if (other instanceof SetDomainImpl) {
      Set<Node<Stmt, Fact>> merged = Sets.newHashSet(nodes);
      merged.addAll(((SetDomainImpl) other).nodes);
      return new SetDomainImpl<N, Stmt, Fact>(merged);
    }
    return zero();
  }

  @NonNull
  @Override
  public Collection<Node> getNodes() {
    return Lists.newArrayList(nodes);
  }

  @Override
  public String toString() {
    return nodes.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + nodes.hashCode();
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    return false;
  }

  @NonNull
  @Override
  public Collection<Node<Stmt, Fact>> elements() {
    return Sets.newHashSet(nodes);
  }
}
