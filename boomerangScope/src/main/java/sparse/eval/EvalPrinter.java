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

import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import sparse.SparseCFGCache;

public class EvalPrinter {

  private final String evalName;

  public EvalPrinter(String evalName) {
    this.evalName = evalName;
  }

  /**
   * how many SparseCFGs retrieved from cache, how many was built. How long did it take in total.
   *
   * @param cache
   */
  public void printCachePerformance(SparseCFGCache cache) {
    List<SparseCFGQueryLog> queryLogs = cache.getQueryLogs();
    try (FileWriter writer = new FileWriter(evalName + "-" + "sparseCFGCache.csv")) {
      long count = 0;
      for (SparseCFGQueryLog queryLog : queryLogs) {
        count++;
        String str =
            Long.toString(count)
                + ","
                + queryLog.isRetrievedFromCache()
                + ","
                + queryLog.getDirection()
                + ","
                + queryLog.getDuration().toMillis()
                + System.lineSeparator();
        writer.write(str);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /* TODO: [ms] was unused - strategy enum does not exist anymore - sparsification currently only soot specific..
  public void printPropagationCount() {
    List<PropagationCounter> counters = new ArrayList<>();
    counters.add(PropagationCounter.getInstance(SparsificationStrategy.NONE));
    counters.add(PropagationCounter.getInstance(SparsificationStrategy.TYPE_BASED));
    counters.add(PropagationCounter.getInstance(SparsificationStrategy.ALIAS_AWARE));
    try (FileWriter writer = new FileWriter(evalName + "-" + "propCount.csv")) {
      StringBuilder str = new StringBuilder();
      for (PropagationCounter counter : counters) {
        str.append(counter.getStrategy().toString());
        str.append(",");
        str.append(counter.getForwardPropagation());
        str.append(",");
        str.append(counter.getBackwardPropagation());
        str.append(System.lineSeparator());
      }
      writer.write(str.toString());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
   */
}
