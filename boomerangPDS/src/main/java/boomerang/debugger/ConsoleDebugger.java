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
package boomerang.debugger;

import boomerang.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpds.impl.Weight;

public class ConsoleDebugger<W extends Weight> extends Debugger<W> {
  private static final Logger logger = LoggerFactory.getLogger(ConsoleDebugger.class);

  public void done(
      java.util.Map<boomerang.ForwardQuery, boomerang.solver.ForwardBoomerangSolver<W>>
          queryToSolvers) {
    int totalRules = 0;
    for (Query q : queryToSolvers.keySet()) {
      totalRules += queryToSolvers.get(q).getNumberOfRules();
    }
    logger.debug("Total number of rules: " + totalRules);
    for (Query q : queryToSolvers.keySet()) {
      logger.debug("========================");
      logger.debug(q.toString());
      logger.debug("========================");
      queryToSolvers.get(q).debugOutput();
      //            for (Method m : queryToSolvers.get(q).getReachableMethods()) {
      //                logger.debug(m + "\n" +
      // Joiner.on("\n\t").join(queryToSolvers.get(q).getResults(m).cellSet()));
      //            }
      queryToSolvers.get(q).debugOutput();
    }
  }
}
