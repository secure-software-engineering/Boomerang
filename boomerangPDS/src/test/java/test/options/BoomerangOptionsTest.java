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
package test.options;

import boomerang.callgraph.BoomerangResolver;
import boomerang.options.BoomerangOptions;
import boomerang.solver.Strategies;
import org.junit.Assert;
import org.junit.Test;
import sparse.SparsificationStrategy;

public class BoomerangOptionsTest {

  @Test
  public void settingOptionsTest() {
    BoomerangOptions staticFieldStrategy =
        BoomerangOptions.builder()
            .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
            .build();
    Assert.assertEquals(
        staticFieldStrategy.getStaticFieldStrategy(),
        Strategies.StaticFieldStrategy.FLOW_SENSITIVE);

    BoomerangOptions arrayStrategy =
        BoomerangOptions.builder().withArrayStrategy(Strategies.ArrayStrategy.DISABLED).build();
    Assert.assertEquals(arrayStrategy.getArrayStrategy(), Strategies.ArrayStrategy.DISABLED);

    BoomerangOptions resolutionStrategy =
        BoomerangOptions.builder().withResolutionStrategy(BoomerangResolver.FACTORY).build();
    Assert.assertEquals(resolutionStrategy.getResolutionStrategy(), BoomerangResolver.FACTORY);

    BoomerangOptions sparsificationStrategy =
        BoomerangOptions.builder().withSparsificationStrategy(SparsificationStrategy.NONE).build();
    Assert.assertEquals(
        sparsificationStrategy.getSparsificationStrategy(), SparsificationStrategy.NONE);

    BoomerangOptions analysisTimeout =
        BoomerangOptions.builder().withAnalysisTimeout(10000).build();
    Assert.assertEquals(analysisTimeout.analysisTimeout(), 10000);

    BoomerangOptions maxFieldDepth = BoomerangOptions.builder().withMaxFieldDepth(5).build();
    Assert.assertEquals(maxFieldDepth.maxFieldDepth(), 5);

    BoomerangOptions maxCallDepth = BoomerangOptions.builder().withMaxCallDepth(3).build();
    Assert.assertEquals(maxCallDepth.maxCallDepth(), 3);

    BoomerangOptions maxUnbalancedCallDepth =
        BoomerangOptions.builder().withMaxUnbalancedCallDepth(1).build();
    Assert.assertEquals(maxUnbalancedCallDepth.maxUnbalancedCallDepth(), 1);

    BoomerangOptions typeCheck = BoomerangOptions.builder().enableTypeCheck(false).build();
    Assert.assertFalse(typeCheck.typeCheck());

    BoomerangOptions onTheFlyCallGraph =
        BoomerangOptions.builder().enableOnTheFlyCallGraph(true).build();
    Assert.assertTrue(onTheFlyCallGraph.onTheFlyCallGraph());

    BoomerangOptions onTheFlyControlFlow =
        BoomerangOptions.builder().enableOnTheFlyControlFlow(true).build();
    Assert.assertTrue(onTheFlyControlFlow.onTheFlyControlFlow());

    BoomerangOptions callSummaries = BoomerangOptions.builder().enableCallSummaries(true).build();
    Assert.assertTrue(callSummaries.callSummaries());

    BoomerangOptions fieldSummaries = BoomerangOptions.builder().enableFieldSummaries(true).build();
    Assert.assertTrue(fieldSummaries.fieldSummaries());

    BoomerangOptions trackImplicitFlows =
        BoomerangOptions.builder().enableTrackImplicitFlows(true).build();
    Assert.assertTrue(trackImplicitFlows.trackImplicitFlows());

    BoomerangOptions killNullAtCast = BoomerangOptions.builder().enableKillNullAtCast(true).build();
    Assert.assertTrue(killNullAtCast.killNullAtCast());

    BoomerangOptions trackStaticFieldAtEntryPointToClinit =
        BoomerangOptions.builder().enableTrackStaticFieldAtEntryPointToClinit(true).build();
    Assert.assertTrue(trackStaticFieldAtEntryPointToClinit.trackStaticFieldAtEntryPointToClinit());

    BoomerangOptions handleMaps = BoomerangOptions.builder().enableHandleMaps(false).build();
    Assert.assertFalse(handleMaps.handleMaps());

    BoomerangOptions trackPathConditions =
        BoomerangOptions.builder().enableTrackPathConditions(true).build();
    Assert.assertTrue(trackPathConditions.trackPathConditions());

    BoomerangOptions prunePathConditions =
        BoomerangOptions.builder().enablePrunePathConditions(true).build();
    Assert.assertTrue(prunePathConditions.prunePathConditions());

    BoomerangOptions trackDataFlowPath =
        BoomerangOptions.builder().enableTrackDataFlowPath(false).build();
    Assert.assertFalse(trackDataFlowPath.trackDataFlowPath());

    BoomerangOptions allowMultipleQueries =
        BoomerangOptions.builder().enableAllowMultipleQueries(true).build();
    Assert.assertTrue(allowMultipleQueries.allowMultipleQueries());

    BoomerangOptions handleSpecialInvokeAsNormalPropagation =
        BoomerangOptions.builder().enableHandleSpecialInvokeAsNormalPropagation(true).build();
    Assert.assertTrue(
        handleSpecialInvokeAsNormalPropagation.handleSpecialInvokeAsNormalPropagation());

    BoomerangOptions ignoreSparsificationAfterQuery =
        BoomerangOptions.builder().enableIgnoreSparsificationAfterQuery(false).build();
    Assert.assertFalse(ignoreSparsificationAfterQuery.ignoreSparsificationAfterQuery());
  }

  @Test(expected = RuntimeException.class)
  public void checkValidTest() {
    BoomerangOptions options =
        BoomerangOptions.builder()
            .enablePrunePathConditions(true)
            .enableTrackDataFlowPath(false)
            .build();

    options.checkValid();
  }
}
