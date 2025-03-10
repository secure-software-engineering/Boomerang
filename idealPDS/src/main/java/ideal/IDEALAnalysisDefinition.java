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
import boomerang.debugger.Debugger;
import boomerang.options.BoomerangOptions;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import boomerang.solver.Strategies;
import java.util.Collection;
import sync.pds.solver.WeightFunctions;
import wpds.impl.Weight;

public abstract class IDEALAnalysisDefinition<W extends Weight> {

  /**
   * This function generates the seed. Each (reachable) statement of the analyzed code is visited.
   * To place a seed, a pair of access graph and an edge function must be specified. From this node
   * the analysis starts its analysis.
   *
   * @param stmt The statement over which is iterated over
   * @return the generated seeds
   */
  public abstract Collection<WeightedForwardQuery<W>> generate(Edge stmt);

  /**
   * This function must generate and return the AnalysisEdgeFunctions that are used for the
   * analysis. As for standard IDE in Heros, the edge functions for normal-, call-, return- and
   * call-to-return flows have to be specified.
   */
  public abstract WeightFunctions<Edge, Val, Edge, W> weightFunctions();

  public CallGraph callGraph() {
    return getFrameworkFactory().getCallGraph();
  }

  public boolean enableStrongUpdates() {
    return true;
  }

  public String toString() {
    String str = "====== IDEal Analysis Options ======";
    // str += "\nEdge Functions:\t\t" + edgeFunctions();
    // str += "\nDebugger Class:\t\t" + debugger();
    // str += "\nAnalysisBudget(sec):\t" + (analysisBudgetInSeconds());
    // str += "\nStrong Updates:\t\t" + (enableStrongUpdates() ? "ENABLED" : "DISABLED");
    // str += "\nAliasing:\t\t" + (enableAliasing() ? "ENABLED" : "DISABLED");
    // str += "\nNull POAs:\t\t" + (enableNullPointOfAlias() ? "ENABLED" : "DISABLED");
    // str += "\n" + boomerangOptions();
    return str;
  }

  public abstract Debugger<W> debugger(IDEALSeedSolver<W> idealSeedSolver);

  public BoomerangOptions boomerangOptions() {
    return BoomerangOptions.builder()
        .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
        .enableAllowMultipleQueries(true)
        .build();
  }

  public IDEALResultHandler getResultHandler() {
    return new IDEALResultHandler();
  }

  protected DataFlowScope getDataFlowScope() {
    return getFrameworkFactory().getDataFlowScope();
  }

  public abstract FrameworkScope getFrameworkFactory();
}
