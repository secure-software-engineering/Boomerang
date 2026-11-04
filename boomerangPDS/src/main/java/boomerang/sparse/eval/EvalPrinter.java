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

import boomerang.sparse.SparseCFGCache;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

/** Writes the statistics of sparse analyses to CSV files. */
public class EvalPrinter {

  private final String evalName;

  public EvalPrinter(String evalName) {
    this.evalName = evalName;
  }

  /**
   * Writes one line per built sparse graph: index, direction, build time in ms, statement count of
   * the original graph and statement count of the sparse graph.
   *
   * @param cache the cache of an analysis instance
   */
  public void printCachePerformance(SparseCFGCache cache) {
    List<SparseCFGQueryLog> queryLogs = cache.getQueryLogs();
    try (FileWriter writer = new FileWriter(evalName + "-sparseCFGCache.csv")) {
      long count = 0;
      for (SparseCFGQueryLog queryLog : queryLogs) {
        count++;
        writer.write(
            count
                + ","
                + queryLog.getDirection()
                + ","
                + queryLog.getDuration().toMillis()
                + ","
                + queryLog.getInitialStmtCount()
                + ","
                + queryLog.getFinalStmtCount()
                + System.lineSeparator());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  /**
   * Writes one line per strategy: name, forward propagations and backward propagations.
   *
   * @param counters the propagation counters by strategy name
   */
  public void printPropagationCount(Map<String, PropagationCounter> counters) {
    try (FileWriter writer = new FileWriter(evalName + "-propCount.csv")) {
      for (Map.Entry<String, PropagationCounter> entry : counters.entrySet()) {
        writer.write(
            entry.getKey()
                + ","
                + entry.getValue().getForwardPropagation()
                + ","
                + entry.getValue().getBackwardPropagation()
                + System.lineSeparator());
      }
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
