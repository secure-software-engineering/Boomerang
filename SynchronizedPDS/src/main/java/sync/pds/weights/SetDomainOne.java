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

import static sync.pds.weights.SetDomainZero.zero;

import java.util.Collection;
import org.jspecify.annotations.NonNull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class SetDomainOne implements SetDomain {
  @NonNull private static final SetDomainOne one = new SetDomainOne();

  private SetDomainOne() {}

  public static Weight one() {
    return one;
  }

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
      throw new IllegalStateException("SetDomainOne.CombineWith-Dont");
      //      Set<Node<Stmt, Fact>> merged = Sets.newHashSet(getNodes());
      //      merged.addAll(((SetDomainImpl) other).getNodes());
      //      return new SetDomainImpl<N, Stmt, Fact>(merged);
    }
    return zero();
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
