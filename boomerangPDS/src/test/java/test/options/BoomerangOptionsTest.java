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
package test.options;

import boomerang.callgraph.BoomerangResolver;
import boomerang.options.BoomerangOptions;
import boomerang.solver.Strategies;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import sparse.SparsificationStrategy;

public class BoomerangOptionsTest {

  @Test
  public void settingOptionsTest() {
    BoomerangOptions staticFieldStrategy =
        BoomerangOptions.builder()
            .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
            .build();
    Assertions.assertEquals(
        staticFieldStrategy.getStaticFieldStrategy(),
        Strategies.StaticFieldStrategy.FLOW_SENSITIVE);

    BoomerangOptions arrayStrategy =
        BoomerangOptions.builder().withArrayStrategy(Strategies.ArrayStrategy.DISABLED).build();
    Assertions.assertEquals(arrayStrategy.getArrayStrategy(), Strategies.ArrayStrategy.DISABLED);

    BoomerangOptions resolutionStrategy =
        BoomerangOptions.builder().withResolutionStrategy(BoomerangResolver.FACTORY).build();
    Assertions.assertEquals(resolutionStrategy.getResolutionStrategy(), BoomerangResolver.FACTORY);

    BoomerangOptions sparsificationStrategy =
        BoomerangOptions.builder().withSparsificationStrategy(SparsificationStrategy.NONE).build();
    Assertions.assertEquals(
        sparsificationStrategy.getSparsificationStrategy(), SparsificationStrategy.NONE);

    BoomerangOptions analysisTimeout =
        BoomerangOptions.builder().withAnalysisTimeout(10000).build();
    Assertions.assertEquals(analysisTimeout.analysisTimeout(), 10000);

    BoomerangOptions maxFieldDepth = BoomerangOptions.builder().withMaxFieldDepth(5).build();
    Assertions.assertEquals(maxFieldDepth.maxFieldDepth(), 5);

    BoomerangOptions maxCallDepth = BoomerangOptions.builder().withMaxCallDepth(3).build();
    Assertions.assertEquals(maxCallDepth.maxCallDepth(), 3);

    BoomerangOptions maxUnbalancedCallDepth =
        BoomerangOptions.builder().withMaxUnbalancedCallDepth(1).build();
    Assertions.assertEquals(maxUnbalancedCallDepth.maxUnbalancedCallDepth(), 1);

    BoomerangOptions typeCheck = BoomerangOptions.builder().enableTypeCheck(false).build();
    Assertions.assertFalse(typeCheck.typeCheck());

    BoomerangOptions onTheFlyCallGraph =
        BoomerangOptions.builder().enableOnTheFlyCallGraph(true).build();
    Assertions.assertTrue(onTheFlyCallGraph.onTheFlyCallGraph());

    BoomerangOptions onTheFlyControlFlow =
        BoomerangOptions.builder().enableOnTheFlyControlFlow(true).build();
    Assertions.assertTrue(onTheFlyControlFlow.onTheFlyControlFlow());

    BoomerangOptions callSummaries = BoomerangOptions.builder().enableCallSummaries(true).build();
    Assertions.assertTrue(callSummaries.callSummaries());

    BoomerangOptions fieldSummaries = BoomerangOptions.builder().enableFieldSummaries(true).build();
    Assertions.assertTrue(fieldSummaries.fieldSummaries());

    BoomerangOptions trackImplicitFlows =
        BoomerangOptions.builder().enableTrackImplicitFlows(true).build();
    Assertions.assertTrue(trackImplicitFlows.trackImplicitFlows());

    BoomerangOptions killNullAtCast = BoomerangOptions.builder().enableKillNullAtCast(true).build();
    Assertions.assertTrue(killNullAtCast.killNullAtCast());

    BoomerangOptions trackStaticFieldAtEntryPointToClinit =
        BoomerangOptions.builder().enableTrackStaticFieldAtEntryPointToClinit(true).build();
    Assertions.assertTrue(
        trackStaticFieldAtEntryPointToClinit.trackStaticFieldAtEntryPointToClinit());

    BoomerangOptions handleMaps = BoomerangOptions.builder().enableHandleMaps(false).build();
    Assertions.assertFalse(handleMaps.handleMaps());

    BoomerangOptions trackPathConditions =
        BoomerangOptions.builder().enableTrackPathConditions(true).build();
    Assertions.assertTrue(trackPathConditions.trackPathConditions());

    BoomerangOptions prunePathConditions =
        BoomerangOptions.builder().enablePrunePathConditions(true).build();
    Assertions.assertTrue(prunePathConditions.prunePathConditions());

    BoomerangOptions trackDataFlowPath =
        BoomerangOptions.builder().enableTrackDataFlowPath(false).build();
    Assertions.assertFalse(trackDataFlowPath.trackDataFlowPath());

    BoomerangOptions allowMultipleQueries =
        BoomerangOptions.builder().enableAllowMultipleQueries(true).build();
    Assertions.assertTrue(allowMultipleQueries.allowMultipleQueries());

    BoomerangOptions handleSpecialInvokeAsNormalPropagation =
        BoomerangOptions.builder().enableHandleSpecialInvokeAsNormalPropagation(true).build();
    Assertions.assertTrue(
        handleSpecialInvokeAsNormalPropagation.handleSpecialInvokeAsNormalPropagation());

    BoomerangOptions ignoreSparsificationAfterQuery =
        BoomerangOptions.builder().enableIgnoreSparsificationAfterQuery(false).build();
    Assertions.assertFalse(ignoreSparsificationAfterQuery.ignoreSparsificationAfterQuery());
  }

  @Test
  public void checkValidTest() {
    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          BoomerangOptions options =
              BoomerangOptions.builder()
                  .enablePrunePathConditions(true)
                  .enableTrackDataFlowPath(false)
                  .build();

          options.checkValid();
        });
  }
}
