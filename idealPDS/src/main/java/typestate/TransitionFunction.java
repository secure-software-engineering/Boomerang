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
package typestate;

import boomerang.scope.ControlFlowGraph;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nonnull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public interface TransitionFunction extends Weight {
  @Nonnull
  Collection<Transition> getValues();

  @Nonnull
  Set<ControlFlowGraph.Edge> getStateChangeStatements();

  @Nonnull
  Weight extendWith(@Nonnull Weight other);

  @Nonnull
  Weight combineWith(@Nonnull Weight other);
}
