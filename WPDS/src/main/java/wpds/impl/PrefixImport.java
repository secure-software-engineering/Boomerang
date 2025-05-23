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
import wpds.interfaces.State;
import wpds.interfaces.WPAStateListener;

public class PrefixImport<N extends Location, D extends State, W extends Weight> {

  private final WeightedPAutomaton<N, D, W> baseAutomaton;
  private final WeightedPAutomaton<N, D, W> flowAutomaton;

  private abstract class IntersectionCallback {
    abstract void trigger(Transition<N, D> baseT, Transition<N, D> flowT);
  }

  public PrefixImport(
      WeightedPAutomaton<N, D, W> autA,
      WeightedPAutomaton<N, D, W> autB,
      final Transition<N, D> t) {
    baseAutomaton = autA;
    flowAutomaton = autB;
    baseAutomaton.registerListener(
        new IntersectionListener(
            t.getStart(),
            t.getStart(),
            t.getLabel(),
            new IntersectionCallback() {

              @Override
              public void trigger(Transition<N, D> baseT, Transition<N, D> flowT) {
                // 3.
                baseAutomaton.registerListener(new Import(t.getTarget(), flowT.getTarget()));
                baseAutomaton.registerListener(
                    new IntersectionListenerNoLabel(
                        t.getTarget(),
                        flowT.getTarget(),
                        new IntersectionCallback() {

                          @Override
                          public void trigger(Transition<N, D> baseT, Transition<N, D> flowT) {
                            // 3.
                            baseAutomaton.registerListener(
                                new Import(baseT.getTarget(), flowT.getTarget()));
                          }
                        }));
              }
            }));
  }

  private class Import extends WPAStateListener<N, D, W> {

    private final D flowTarget;

    public Import(D state, D flowTarget) {
      super(state);
      this.flowTarget = flowTarget;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

    @Override
    public void onInTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
      flowAutomaton.addTransition(new Transition<N, D>(t.getStart(), t.getLabel(), flowTarget));
      baseAutomaton.registerListener(new Import(t.getStart(), t.getStart()));
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((flowTarget == null) ? 0 : flowTarget.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      Import other = (Import) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (flowTarget == null) {
        return other.flowTarget == null;
      } else return flowTarget.equals(other.flowTarget);
    }

    private PrefixImport getOuterType() {
      return PrefixImport.this;
    }
  }

  private class IntersectionListener extends WPAStateListener<N, D, W> {

    private final D flowState;
    private final N label;
    private final IntersectionCallback callback;

    public IntersectionListener(D baseState, D flowState, N label, IntersectionCallback callback) {
      super(baseState);
      this.flowState = flowState;
      this.label = label;
      this.callback = callback;
    }

    @Override
    public void onOutTransitionAdded(
        final Transition<N, D> baseT, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
      if (!baseT.getLabel().equals(label)) return;
      flowAutomaton.registerListener(new HasOutTransWithSameLabel(flowState, baseT, callback));
    }

    @Override
    public void onInTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((flowState == null) ? 0 : flowState.hashCode());
      result = prime * result + ((label == null) ? 0 : label.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      IntersectionListener other = (IntersectionListener) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (flowState == null) {
        if (other.flowState != null) return false;
      } else if (!flowState.equals(other.flowState)) return false;
      if (label == null) {
        return other.label == null;
      } else return label.equals(other.label);
    }

    private PrefixImport getOuterType() {
      return PrefixImport.this;
    }
  }

  protected final class HasOutTransWithSameLabel extends WPAStateListener<N, D, W> {
    private final Transition<N, D> baseT;
    private final IntersectionCallback callback;

    private HasOutTransWithSameLabel(
        D state, Transition<N, D> baseT, IntersectionCallback callback) {
      super(state);
      this.baseT = baseT;
      this.callback = callback;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<N, D> flowT, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
      if (flowT.getLabel().equals(baseT.getLabel())) {
        callback.trigger(baseT, flowT);
      }
    }

    @Override
    public void onInTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((baseT == null) ? 0 : baseT.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      HasOutTransWithSameLabel other = (HasOutTransWithSameLabel) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (baseT == null) {
        return other.baseT == null;
      } else return baseT.equals(other.baseT);
    }

    private PrefixImport getOuterType() {
      return PrefixImport.this;
    }
  }

  private class IntersectionListenerNoLabel extends WPAStateListener<N, D, W> {

    private final D flowState;
    private final IntersectionCallback callback;

    public IntersectionListenerNoLabel(D baseState, D flowState, IntersectionCallback callback) {
      super(baseState);
      this.flowState = flowState;
      this.callback = callback;
    }

    @Override
    public void onOutTransitionAdded(
        final Transition<N, D> baseT, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {
      flowAutomaton.registerListener(new HasOutTransWithSameLabel(flowState, baseT, callback));
    }

    @Override
    public void onInTransitionAdded(
        Transition<N, D> t, W w, WeightedPAutomaton<N, D, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((flowState == null) ? 0 : flowState.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      IntersectionListenerNoLabel other = (IntersectionListenerNoLabel) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (flowState == null) {
        return other.flowState == null;
      } else return flowState.equals(other.flowState);
    }

    private PrefixImport getOuterType() {
      return PrefixImport.this;
    }
  }
}
