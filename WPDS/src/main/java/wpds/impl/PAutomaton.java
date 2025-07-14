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
import pathexpression.LabeledGraph;
import wpds.interfaces.State;

public abstract class PAutomaton<N extends Location, D extends State>
    extends WeightedPAutomaton<N, D, NoWeight> implements LabeledGraph<D, N> {

  @Override
  public NoWeight getOne() {
    return NoWeight.getInstance();
  }
}
