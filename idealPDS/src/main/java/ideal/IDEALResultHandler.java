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
package ideal;

import boomerang.WeightedForwardQuery;
import boomerang.results.ForwardBoomerangResults;
import wpds.impl.Weight;

public class IDEALResultHandler<W extends Weight> {

  public void report(WeightedForwardQuery<W> seed, ForwardBoomerangResults<W> res) {}
}
