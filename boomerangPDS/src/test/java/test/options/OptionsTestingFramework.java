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

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Val;
import boomerang.utils.MethodWrapper;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import test.TestingFramework;
import wpds.impl.NoWeight;

public class OptionsTestingFramework extends TestingFramework {

  private static final Logger LOGGER = LoggerFactory.getLogger(OptionsTestingFramework.class);

  public void analyze(
      String targetClassName,
      String targetMethodName,
      BoomerangOptions options,
      String[] expectedAllocSites) {
    analyze(
        targetClassName,
        targetMethodName,
        options,
        DataFlowScope.EXCLUDE_PHANTOM_CLASSES,
        expectedAllocSites);
  }

  public void analyze(
      String targetClassName,
      String targetMethodName,
      BoomerangOptions options,
      DataFlowScope dataFlowScope,
      String[] expectedAllocSites) {
    MethodWrapper methodWrapper = new MethodWrapper(targetClassName, targetMethodName);
    FrameworkScope frameworkScope = super.getFrameworkScope(methodWrapper, dataFlowScope);

    LOGGER.info("Running test \"{}\" in class \"{}\"", targetMethodName, targetClassName);

    /* ----------- Compute queries from the assertions in the test method ----------- */
    AssertionsExtraction extraction = new AssertionsExtraction(frameworkScope);
    Collection<Query> queries = extraction.computeSeeds();

    if (queries.size() != 1) {
      Assertions.fail("Expected to find exactly one assertion in the test");
    }

    Query query = queries.iterator().next();
    if (!(query instanceof BackwardQuery)) {
      Assertions.fail("Expected to find a Backward query in the test");
    }

    /* -----------
     * Transform expected and actual alloc sites to String collections s.t. we can compare them
     * ----------- */
    Collection<AllocVal> allocSites =
        computeAllocSites(frameworkScope, options, (BackwardQuery) query);
    Set<String> actualAllocSiteStrings = new HashSet<>();
    for (AllocVal allocSite : allocSites) {
      Val allocVal = allocSite.getAllocVal();

      if (allocVal.isStringConstant()) {
        actualAllocSiteStrings.add(allocVal.getStringValue());
      } else {
        actualAllocSiteStrings.add(allocVal.toString());
      }
    }

    Set<String> expectedAllocSiteStrings = Set.copyOf(Arrays.asList(expectedAllocSites));

    /* Compare expected and actual allocation sites */
    if (actualAllocSiteStrings.equals(expectedAllocSiteStrings)) {
      LOGGER.info("Test \"{}\" in class \"{}\" passed!", targetMethodName, targetClassName);
    } else {
      Assertions.fail(
          "\nExpected Allocation Sites:\n- "
              + String.join("\n- ", expectedAllocSiteStrings)
              + "\nActual Allocation Sites:\n- "
              + String.join("\n- ", actualAllocSiteStrings));
    }
  }

  private Collection<AllocVal> computeAllocSites(
      FrameworkScope frameworkScope, BoomerangOptions options, BackwardQuery query) {
    Boomerang boomerang = new Boomerang(frameworkScope, options);
    BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);

    Collection<AllocVal> allocSites = new HashSet<>();
    for (ForwardQuery forwardQuery : results.getAllocationSites().keySet()) {
      allocSites.add(forwardQuery.getAllocVal());
    }

    return allocSites;
  }
}
