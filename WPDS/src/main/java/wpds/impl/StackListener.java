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
package wpds.impl;

import de.fraunhofer.iem.Location;
import java.util.LinkedHashSet;
import java.util.Set;
import wpds.interfaces.State;
import wpds.interfaces.WPAStateListener;

public abstract class StackListener<N extends Location, D extends State, W extends Weight>
    extends WPAStateListener<N, D, W> {
  /** */
  private final WeightedPAutomaton<N, D, W> aut;

  private final N source;
  private final Set<N> notifiedStacks = new LinkedHashSet<>();

  public StackListener(WeightedPAutomaton<N, D, W> weightedPAutomaton, D state, N source) {
    super(state);
    this.aut = weightedPAutomaton;
    this.source = source;
  }

  @Override
  public void onOutTransitionAdded(
      Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
    if (t.getLabel().equals(aut.epsilon())) return;
    if (this.aut.getInitialStates().contains(t.getTarget())) {
      if (t.getLabel().equals(source)) {
        anyContext(source);
      }
      return;
    }
    if (this.aut.isGeneratedState(t.getTarget())) {
      aut.registerListener(new SubStackListener(t.getTarget(), this));
    }
  }

  @Override
  public void onInTransitionAdded(
      Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

  public abstract void stackElement(N callSite);

  public abstract void anyContext(N end);

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((source == null) ? 0 : source.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    StackListener<N, D, W> other = (StackListener<N, D, W>) obj;
    if (source == null) {
      return other.source == null;
    } else return source.equals(other.source);
  }

  private class SubStackListener extends WPAStateListener<N, D, W> {
    private final StackListener parent;

    public SubStackListener(D state, StackListener parent) {
      super(state);
      this.parent = parent;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
      if (t.getLabel().equals(aut.epsilon())) return;
      stackElement(t.getLabel());
      if (aut.isGeneratedState(t.getTarget()) && !t.getTarget().equals(t.getStart())) {
        aut.registerListener(new SubStackListener(t.getTarget(), parent));
      }
    }

    @Override
    public void onInTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

    public void stackElement(N parent) {
      if (notifiedStacks.add(parent)) {
        StackListener.this.stackElement(parent);
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      SubStackListener other = (SubStackListener) obj;
      if (parent == null) {
        return other.parent == null;
      } else return parent.equals(other.parent);
    }
  }
}
