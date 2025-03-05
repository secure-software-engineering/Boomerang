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
package sync.pds.weights;

import static sync.pds.weights.SetDomainRefactor.one;
import static sync.pds.weights.SetDomainRefactor.zero;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomain<N, Stmt, Fact> implements Weight {

  private final Collection<Node<Stmt, Fact>> nodes;

  private SetDomain(Collection<Node<Stmt, Fact>> nodes) {
    this.nodes = nodes;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(SetDomainRefactor.one())) {
      return other;
    }
    return zero();
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(SetDomainRefactor.zero())) return this;
    if (this.equals(SetDomainRefactor.zero())) return other;
    if (this.equals(SetDomainRefactor.one()) || other.equals(SetDomainRefactor.one())) {
      return one();
    }
    if (other instanceof SetDomain) {
      Set<Node<Stmt, Fact>> merged = Sets.newHashSet(nodes);
      merged.addAll(((SetDomain) other).nodes);
      return new SetDomain<N, Stmt, Fact>(merged);
    }
    return zero();
  }

  @Override
  public String toString() {
    return nodes.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SetDomain other = (SetDomain) obj;
    if (nodes == null) {
      if (other.nodes != null) return false;
    } else if (!nodes.equals(other.nodes)) return false;
    return false;
  }

  public Collection<Node<Stmt, Fact>> elements() {
    return Sets.newHashSet(nodes);
  }
}
