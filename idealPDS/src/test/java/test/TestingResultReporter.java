/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test;

import assertions.ShouldNotBeAnalyzed;
import assertions.StateResult;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.collect.Table;
import java.util.Collection;
import java.util.HashSet;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;

public class TestingResultReporter {

  private final Collection<StateResult> expectedStateResults;
  private final Collection<ShouldNotBeAnalyzed> expectedShouldNotBeAnalyzed;

  public TestingResultReporter(Collection<Assertion> expectedResults) {
    this.expectedStateResults = new HashSet<>();
    this.expectedShouldNotBeAnalyzed = new HashSet<>();

    for (Assertion a : expectedResults) {
      if (a instanceof StateResult) {
        StateResult stateResult = (StateResult) a;
        expectedStateResults.add(stateResult);
      }

      if (a instanceof ShouldNotBeAnalyzed) {
        ShouldNotBeAnalyzed shouldNotBeAnalyzed = (ShouldNotBeAnalyzed) a;
        expectedShouldNotBeAnalyzed.add(shouldNotBeAnalyzed);
      }
    }
  }

  public void onSeedFinished(
      Node<Edge, Val> seed, ForwardBoomerangResults<TransitionFunction> res) {
    Table<Statement, Val, TransitionFunction> results = res.asStatementValWeightTable();

    for (StateResult stateResult : expectedStateResults) {
      TransitionFunction function = results.get(stateResult.getStmt(), stateResult.getSeed());

      if (function != null) {
        stateResult.computedResults(function);
      }
    }

    // check if any of the methods that should not be analyzed have been analyzed
    for (ShouldNotBeAnalyzed shouldNotBeAnalyzed : expectedShouldNotBeAnalyzed) {
      if (results.containsRow(shouldNotBeAnalyzed.getStatement())) {
        shouldNotBeAnalyzed.hasBeenAnalyzed();
      }
    }
  }
}
