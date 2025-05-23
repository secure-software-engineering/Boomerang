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

public interface NestedAutomatonListener<N extends Location, D extends State, W extends Weight> {
  void nestedAutomaton(WeightedPAutomaton<N, D, W> parent, WeightedPAutomaton<N, D, W> child);
}
