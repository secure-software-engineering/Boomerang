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

import javax.annotation.Nonnull;
import wpds.impl.Weight;

public class SetDomainRefactor implements Weight {

  private static SetDomainRefactor one;
  private static SetDomainRefactor zero;
  private final String rep;

  private SetDomainRefactor(String rep) {
    this.rep = rep;
  }

  @Override
  @Nonnull
  public Weight extendWith(@Nonnull Weight other) {
    if (other.equals(one())) {
      return this;
    }
    if (this.equals(one())) {
      return other;
    }
    return zero();
  }

  @Override
  @Nonnull
  public Weight combineWith(@Nonnull Weight other) {
    if (other.equals(zero())) return this;
    if (this.equals(zero())) return other;
    if (this.equals(one()) || other.equals(one())) return one();

    return zero();
  }

  public static SetDomainRefactor one() {
    if (one == null) one = new SetDomainRefactor("<1>");
    return one;
  }

  public static SetDomainRefactor zero() {
    if (zero == null) zero = new SetDomainRefactor("<0>");
    return zero;
  }

  @Override
  public String toString() {
    return (this == one) ? "ONE" : "ZERO";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (rep.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    SetDomainRefactor other = (SetDomainRefactor) obj;
    return rep.equals(other.rep);
  }
}
