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
package boomerang;

import boomerang.options.BoomerangOptions;
import boomerang.scope.AllocVal;
import boomerang.scope.AnalysisScope;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Statement;
import java.util.Collection;
import java.util.Collections;
import wpds.impl.Weight;

public abstract class WholeProgramBoomerang<W extends Weight> extends WeightedBoomerang<W> {
  private int reachableMethodCount;
  private int allocationSites;
  private final CallGraph callGraph;

  public WholeProgramBoomerang(FrameworkScope frameworkScope, BoomerangOptions options) {
    super(frameworkScope, options);
    this.callGraph = frameworkScope.getCallGraph();
  }

  public WholeProgramBoomerang(FrameworkScope frameworkScope) {
    this(frameworkScope, BoomerangOptions.DEFAULT());
  }

  public void wholeProgramAnalysis() {
    long before = System.currentTimeMillis();
    AnalysisScope scope =
        new AnalysisScope(callGraph) {
          @Override
          protected Collection<? extends Query> generate(Edge cfgEdge) {
            Statement stmt = cfgEdge.getStart();
            if (stmt.isAssignStmt()) {
              if (stmt.getRightOp().isNewExpr()) {
                AllocVal allocVal = new AllocVal(stmt.getLeftOp(), stmt, stmt.getRightOp());
                return Collections.singleton(new ForwardQuery(cfgEdge, allocVal));
              }
            }
            return Collections.emptySet();
          }
        };
    for (Query s : scope.computeSeeds()) {
      solve((ForwardQuery) s);
    }

    long after = System.currentTimeMillis();
    System.out.println("Analysis Time (in ms):\t" + (after - before));
    System.out.println("Analyzed methods:\t" + reachableMethodCount);
    System.out.println("Total solvers:\t" + this.getSolvers().size());
    System.out.println("Allocation Sites:\t" + allocationSites);
    System.out.println(getStats());
  }

  @Override
  protected void backwardSolve(BackwardQuery query) {}
}
