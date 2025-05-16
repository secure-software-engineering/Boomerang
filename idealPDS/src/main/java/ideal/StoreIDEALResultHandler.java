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
package ideal;

import boomerang.WeightedForwardQuery;
import boomerang.results.ForwardBoomerangResults;
import com.google.common.collect.Maps;
import java.util.Map;
import wpds.impl.Weight;

public class StoreIDEALResultHandler<W extends Weight> extends IDEALResultHandler<W> {
  Map<WeightedForwardQuery<W>, ForwardBoomerangResults<W>> seedToSolver = Maps.newHashMap();

  @Override
  public void report(WeightedForwardQuery<W> seed, ForwardBoomerangResults<W> res) {
    seedToSolver.put(seed, res);
  }

  public Map<WeightedForwardQuery<W>, ForwardBoomerangResults<W>> getResults() {
    return seedToSolver;
  }
}
