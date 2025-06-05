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
package boomerang.scope;

import boomerang.Query;
import com.google.common.base.Stopwatch;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AnalysisScope {

  private static final Logger LOGGER = LoggerFactory.getLogger(AnalysisScope.class);
  protected final FrameworkScope frameworkScope;

  public AnalysisScope(FrameworkScope frameworkScope) {
    this.frameworkScope = frameworkScope;
  }

  public Collection<Query> computeSeeds() {
    CallGraph callGraph = frameworkScope.getCallGraph();
    Collection<Method> entryPoints = callGraph.getEntryPoints();
    LOGGER.info("Computing seeds starting at {} entry method(s)", entryPoints.size());

    Collection<Query> seeds = new LinkedHashSet<>();
    Collection<Method> processed = new HashSet<>();
    int statementCount = 0;

    Stopwatch watch = Stopwatch.createStarted();
    Queue<Method> workList = new LinkedList<>(entryPoints);
    while (!workList.isEmpty()) {
      Method m = workList.poll();
      if (!processed.add(m)) {
        continue;
      }

      if (isExcluded(m)) {
        continue;
      }

      LOGGER.trace("Processing {}", m);
      for (Statement stmt : m.getStatements()) {
        statementCount++;

        if (stmt.containsInvokeExpr()) {
          Collection<CallGraph.Edge> edgesOutOf = callGraph.edgesOutOf(stmt);

          for (CallGraph.Edge e : edgesOutOf) {
            Method tgt = e.tgt();
            if (tgt.isPhantom()) {
              continue;
            }

            if (!processed.contains(tgt)) {
              workList.add(tgt);
            }
          }
        }

        for (Statement succ : stmt.getMethod().getControlFlowGraph().getSuccsOf(stmt)) {
          seeds.addAll(generate(new ControlFlowGraph.Edge(stmt, succ)));
        }
      }
    }
    LOGGER.info("Found {} seed(s) in {} in {} LOC", seeds.size(), watch, statementCount);

    return seeds;
  }

  protected boolean isExcluded(Method method) {
    return frameworkScope.getDataFlowScope().isExcluded(method);
  }

  protected abstract Collection<? extends Query> generate(ControlFlowGraph.Edge seed);
}
