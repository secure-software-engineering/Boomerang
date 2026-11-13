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
package test.options.boomerang;

import boomerang.callgraph.BoomerangResolver;
import boomerang.options.BoomerangOptions;
import boomerang.pathtracking.PathTrackingBoomerangOptions;
import boomerang.solver.Strategies;
import boomerang.sparse.SparsificationStrategy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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

    BoomerangOptions fieldSensitivity =
        BoomerangOptions.builder().enableFieldSensitivity(false).build();
    Assertions.assertFalse(fieldSensitivity.isFieldSensitive());

    BoomerangOptions contextSensitivity =
        BoomerangOptions.builder().enableContextSensitivity(false).build();
    Assertions.assertFalse(contextSensitivity.isContextSensitive());

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

    BoomerangOptions trackStaticFieldAtEntryPointToClinit =
        BoomerangOptions.builder().enableTrackStaticFieldAtEntryPointToClinit(true).build();
    Assertions.assertTrue(
        trackStaticFieldAtEntryPointToClinit.trackStaticFieldAtEntryPointToClinit());

    BoomerangOptions handleMaps = BoomerangOptions.builder().enableHandleMaps(false).build();
    Assertions.assertFalse(handleMaps.handleMaps());

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
  public void sparsificationStrategyTest() {
    Assertions.assertEquals(
        BoomerangOptions.DEFAULT().getSparsificationStrategy(), SparsificationStrategy.NONE);

    BoomerangOptions typeBased =
        BoomerangOptions.builder()
            .withSparsificationStrategy(SparsificationStrategy.TYPE_BASED)
            .build();
    Assertions.assertEquals(
        typeBased.getSparsificationStrategy(), SparsificationStrategy.TYPE_BASED);

    BoomerangOptions aliasAware =
        BoomerangOptions.builder()
            .withSparsificationStrategy(SparsificationStrategy.ALIAS_AWARE)
            .build();
    Assertions.assertEquals(
        aliasAware.getSparsificationStrategy(), SparsificationStrategy.ALIAS_AWARE);

    Assertions.assertEquals(SparsificationStrategy.NONE, SparsificationStrategy.fromName("none"));
    Assertions.assertEquals(
        SparsificationStrategy.TYPE_BASED, SparsificationStrategy.fromName("TypeBased"));
    Assertions.assertEquals(
        SparsificationStrategy.ALIAS_AWARE, SparsificationStrategy.fromName("aliasaware"));
    Assertions.assertThrows(
        IllegalArgumentException.class, () -> SparsificationStrategy.fromName("unknown"));
    Assertions.assertThrows(
        NullPointerException.class,
        () -> BoomerangOptions.builder().withSparsificationStrategy(null));
  }

  @Test
  public void checkValidSparsificationOptionsTest() {
    BoomerangOptions options =
        BoomerangOptions.builder()
            .enableOnTheFlyControlFlow(true)
            .withSparsificationStrategy(SparsificationStrategy.ALIAS_AWARE)
            .build();

    Assertions.assertThrows(RuntimeException.class, options::checkValid);
  }

  @Test
  public void checkValidPathTrackingOptionsTest() {
    Assertions.assertThrows(
        RuntimeException.class,
        () -> {
          PathTrackingBoomerangOptions options =
              PathTrackingBoomerangOptions.builder()
                  .enableTrackPathConditions(false)
                  .enableTrackImplicitFlows(true)
                  .enablePrunePathConditions(true)
                  .enableTrackDataFlowPath(false)
                  .build();

          options.checkValid();
        });
  }
}
