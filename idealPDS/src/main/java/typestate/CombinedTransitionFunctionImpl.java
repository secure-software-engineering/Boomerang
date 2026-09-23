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
package typestate;

import boomerang.scope.Statement;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multimap;
import java.util.Objects;
import java.util.Set;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;

/**
 * The result of combining transition functions with different state change statements. It keeps all
 * of them so that {@link TransitionFunctionImpl#combineWith} stays commutative, while the common
 * single-statement case in {@link TransitionFunctionImpl} does not pay for a set.
 */
final class CombinedTransitionFunctionImpl extends TransitionFunctionImpl {

  @NonNull private final ImmutableSet<Statement> stateChangeStatements;

  /**
   * @param stateChangeStatements at least two statements; the first one is the one {@link
   *     #getStateChangeStatement()} returns
   */
  CombinedTransitionFunctionImpl(
      @NonNull Multimap<Transition, StatementSequence> transitionStatementSequences,
      @NonNull Set<Statement> stateChangeStatements) {
    super(transitionStatementSequences, stateChangeStatements.iterator().next());
    if (stateChangeStatements.size() < 2) {
      throw new IllegalArgumentException(
          "A combined transition function needs at least two state change statements");
    }
    this.stateChangeStatements = ImmutableSet.copyOf(stateChangeStatements);
  }

  @NonNull
  @Override
  TransitionFunctionImpl withStateChangeSequences(
      @NonNull Multimap<Transition, StatementSequence> transitionStatementSequences) {
    return new CombinedTransitionFunctionImpl(transitionStatementSequences, stateChangeStatements);
  }

  @NonNull
  @Override
  Set<Statement> getStateChangeStatements() {
    return stateChangeStatements;
  }

  @Override
  public boolean equals(Object o) {
    // not super.equals: that compares getStateChangeStatement(), which depends on the order in
    // which the functions were combined
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    CombinedTransitionFunctionImpl that = (CombinedTransitionFunctionImpl) o;
    return getStateChangeSequences().equals(that.getStateChangeSequences())
        && stateChangeStatements.equals(that.stateChangeStatements);
  }

  @Override
  int computeHashCode() {
    return Objects.hash(getStateChangeSequences(), stateChangeStatements);
  }
}
