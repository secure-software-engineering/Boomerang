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

import com.google.common.base.Stopwatch;
import java.time.Duration;

/** Statistics about building one sparse control flow graph. */
public class SparseCFGQueryLog {

  public enum QueryDirection {
    FWD,
    BWD
  }

  private final QueryDirection direction;
  private final Stopwatch watch = Stopwatch.createUnstarted();

  private int initialStmtCount = 0;
  private int finalStmtCount = 0;

  public SparseCFGQueryLog(QueryDirection direction) {
    this.direction = direction;
  }

  public void logStart() {
    watch.start();
  }

  public void logEnd() {
    watch.stop();
  }

  /**
   * @return the time it took to build the sparse graph
   */
  public Duration getDuration() {
    return watch.elapsed();
  }

  public QueryDirection getDirection() {
    return direction;
  }

  /**
   * @return the number of statements in the original graph
   */
  public int getInitialStmtCount() {
    return initialStmtCount;
  }

  public void setInitialStmtCount(int initialStmtCount) {
    this.initialStmtCount = initialStmtCount;
  }

  /**
   * @return the number of statements in the sparse graph
   */
  public int getFinalStmtCount() {
    return finalStmtCount;
  }

  public void setFinalStmtCount(int finalStmtCount) {
    this.finalStmtCount = finalStmtCount;
  }
}
