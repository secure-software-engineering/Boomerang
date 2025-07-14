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
package sparse.eval;

public class PropagationCounter {
  private long forwardPropagation = 0;
  private long backwardPropagation = 0;

  public PropagationCounter() {}

  public void countForwardPropagation() {
    forwardPropagation++;
  }

  public void countBackwardProgragation() {
    backwardPropagation++;
  }

  public long getForwardPropagation() {
    return forwardPropagation;
  }

  public long getBackwardPropagation() {
    return backwardPropagation;
  }
}
