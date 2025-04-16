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
package test.core;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.Query;
import boomerang.WeightedBoomerang;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AnalysisScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.rules.Timeout;
import test.TestingFramework;
import boomerang.utils.MethodWrapper;
import wpds.impl.Weight;

public class MultiQueryBoomerangTest extends TestingFramework {

  private static final boolean FAIL_ON_IMPRECISE = false;
  @Rule public Timeout timeout = new Timeout(10000000, TimeUnit.MILLISECONDS);
  @Rule public TestName testName = new TestName();

  protected Collection<? extends Query> queryForCallSites;
  protected Multimap<Query, Query> expectedAllocsForQuery = HashMultimap.create();
  protected Collection<Error> unsoundErrors = new LinkedHashSet<>();
  protected Collection<Error> imprecisionErrors = new LinkedHashSet<>();

  protected int analysisTimeout = 300 * 1000;

  protected void analyze(String targetClassName, String targetMethodName) {
    MethodWrapper methodWrapper = new MethodWrapper(targetClassName, targetMethodName);
    FrameworkScope frameworkScope = super.getFrameworkScope(methodWrapper);

    assertResults(frameworkScope);
  }

  private void assertResults(FrameworkScope frameworkScope) {
    AnalysisScope analysisScope =
        new Preanalysis(frameworkScope.getCallGraph(), new FirstArgumentOf("queryFor.*"));
    queryForCallSites = analysisScope.computeSeeds();

    for (Query q : queryForCallSites) {
      Val arg2 = q.cfgEdge().getStart().getInvokeExpr().getArg(1);
      if (arg2.isClassConstant()) {
        Preanalysis analysis =
            new Preanalysis(
                frameworkScope.getCallGraph(),
                new AllocationSiteOf(arg2.getClassConstantType().toString()));
        expectedAllocsForQuery.putAll(q, analysis.computeSeeds());
      }
    }

    runDemandDrivenBackward(frameworkScope);

    if (!unsoundErrors.isEmpty()) {
      Assert.fail(Joiner.on("\n - ").join(unsoundErrors));
    }

    if (!imprecisionErrors.isEmpty() && FAIL_ON_IMPRECISE) {
      Assert.fail(Joiner.on("\n - ").join(imprecisionErrors));
    }
  }

  private void compareQuery(Query query, Collection<? extends Query> results) {
    Collection<Query> expectedResults = expectedAllocsForQuery.get(query);
    Collection<Query> falseNegativeAllocationSites = new HashSet<>();

    for (Query res : expectedResults) {
      if (!results.contains(res)) falseNegativeAllocationSites.add(res);
    }

    Collection<Query> falsePositiveAllocationSites = new HashSet<>(results);
    for (Query res : expectedResults) {
      falsePositiveAllocationSites.remove(res);
    }

    String answer =
        (falseNegativeAllocationSites.isEmpty() ? "" : "\nFN:" + falseNegativeAllocationSites)
            + (falsePositiveAllocationSites.isEmpty()
                ? ""
                : "\nFP:" + falsePositiveAllocationSites + "\n");
    if (!falseNegativeAllocationSites.isEmpty()) {
      unsoundErrors.add(new Error(" Unsound results for:" + answer));
    }
    if (!falsePositiveAllocationSites.isEmpty())
      imprecisionErrors.add(new Error(" Imprecise results for:" + answer));
    for (Entry<Query, Query> e : expectedAllocsForQuery.entries()) {
      if (!e.getKey().equals(query)) {
        if (results.contains(e.getValue())) {
          Assert.fail(
              "A query contains the result of a different query.\n"
                  + query
                  + " \n contains \n"
                  + e.getValue());
        }
      }
    }
  }

  private void runDemandDrivenBackward(FrameworkScope frameworkScope) {
    BoomerangOptions options =
        BoomerangOptions.builder()
            .withAnalysisTimeout(analysisTimeout)
            .enableAllowMultipleQueries(true)
            .build();
    WeightedBoomerang<Weight.NoWeight> solver = new Boomerang(frameworkScope, options);
    for (final Query query : queryForCallSites) {
      if (query instanceof BackwardQuery) {
        BackwardBoomerangResults<Weight.NoWeight> res = solver.solve((BackwardQuery) query);
        compareQuery(query, res.getAllocationSites().keySet());
      }
    }
    solver.unregisterAllListeners();
  }
}
