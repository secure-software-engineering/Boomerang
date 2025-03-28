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
  public Weight extendWith(@NonNull Weight other) {
    Weight one = one();
    if (other == (one)) {
      return this;
    }

    return zero();
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {

    SetDomainZero zero = zero();
    SetDomain one = one();
    if (other == zero) return this;
    if (other == one) return one;
    Set<Node<Stmt, Fact>> merged = Sets.newHashSet(nodes);
      merged.addAll(((SetDomainImpl) other).nodes);
      return new SetDomainImpl<N, Stmt, Fact>(merged);
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
    return false;
  }

  @NonNull
  @Override
  public Collection<Node<Stmt, Fact>> elements() {
    return Sets.newHashSet(nodes);
  }
}
