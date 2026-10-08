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
package boomerang.sparse.eval;

/** Counts the normal flow propagations of one analysis instance. */
public class PropagationCounter {

  private long forwardPropagation = 0;
  private long backwardPropagation = 0;

  public synchronized void countForwardPropagation() {
    forwardPropagation++;
  }

  public synchronized void countBackwardPropagation() {
    backwardPropagation++;
  }

  public synchronized long getForwardPropagation() {
    return forwardPropagation;
  }

  public synchronized long getBackwardPropagation() {
    return backwardPropagation;
  }

  @Override
  public String toString() {
    return "PropagationCounter{forward="
        + getForwardPropagation()
        + ", backward="
        + getBackwardPropagation()
        + "}";
  }
}
