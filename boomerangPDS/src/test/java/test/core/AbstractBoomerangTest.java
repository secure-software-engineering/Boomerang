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
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.WholeProgramBoomerang;
import boomerang.options.BoomerangOptions;
import boomerang.options.IntAndStringAllocationSite;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DataFlowScope;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.util.AccessPath;
import boomerang.util.DefaultValueMap;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.pds.solver.OneWeightFunctions;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import sync.pds.solver.nodes.SingleNode;
import test.TestingFramework;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.NoAllocatedObject;
import boomerang.utils.MethodWrapper;
import wpds.impl.Transition;
import wpds.impl.Weight.NoWeight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.WPAStateListener;

public class AbstractBoomerangTest extends TestingFramework {

  @Rule public TestName testName = new TestName();

  /**
   * Fails the test cases, when any instance of the interface {@link
   * test.core.selfrunning.NoAllocatedObject} is detected.
   */
  private static final boolean FAIL_ON_IMPRECISE = true;

  /**
   * Fails the test cases, when Boomerang's result set contains any object that does not inherit
   * from {@link test.core.selfrunning.AllocatedObject}.
   */
  private static final boolean TRACK_IMPLICIT_IMPRECISE = false;

  private static final Logger LOGGER = LoggerFactory.getLogger(AbstractBoomerangTest.class);

  private QueryForCallSiteDetector queryDetector;
  private Collection<? extends Query> expectedAllocationSites;
  private Collection<? extends Node<Edge, Val>> explicitlyUnexpectedAllocationSites;
  protected Collection<? extends Query> queryForCallSites;
  protected Collection<Error> unsoundErrors = new LinkedHashSet<>();
  protected Collection<Error> imprecisionErrors = new LinkedHashSet<>();
  private static Duration globalQueryTime = Duration.ofMillis(0);

  protected int analysisTimeout = 3000 * 1000;

  public enum AnalysisMode {
    WholeProgram,
    DemandDrivenBackward
  }

  protected AnalysisMode[] getAnalyses() {
    return new AnalysisMode[] {
      // AnalysisMode.WholeProgram,
      AnalysisMode.DemandDrivenBackward
    };
  }

  public int getIterations() {
    return 1;
  }

  protected void analyze(String targetClassName, String targetMethodName) {
    analyze(targetClassName, targetMethodName, DataFlowScope.EXCLUDE_PHANTOM_CLASSES);
  }

  protected void analyze(
      String targetClassName, String targetMethodName, DataFlowScope dataFlowScope) {
    MethodWrapper methodWrapper = new MethodWrapper(targetClassName, targetMethodName);
    FrameworkScope frameworkScope = super.getFrameworkScope(methodWrapper, dataFlowScope);

    analyzeWithCallGraph(frameworkScope);
  }

  private void analyzeWithCallGraph(FrameworkScope frameworkScope) {
    CallGraph callGraph = frameworkScope.getCallGraph();
    queryDetector = new QueryForCallSiteDetector(callGraph);
    queryForCallSites = queryDetector.computeSeeds();

    if (queryDetector.integerQueries) {
      Preanalysis an = new Preanalysis(callGraph, new IntegerAllocationSiteOf());
      expectedAllocationSites = an.computeSeeds();
    } else {
      Preanalysis an =
          new Preanalysis(callGraph, new AllocationSiteOf(AllocatedObject.class.getName()));
      expectedAllocationSites = an.computeSeeds();
      an = new Preanalysis(callGraph, new AllocationSiteOf(NoAllocatedObject.class.getName()));
      explicitlyUnexpectedAllocationSites =
          an.computeSeeds().stream().map(Query::asNode).collect(Collectors.toList());
    }
    for (int i = 0; i < getIterations(); i++) {
      for (AnalysisMode analysis : getAnalyses()) {
        switch (analysis) {
          case WholeProgram:
            if (!queryDetector.integerQueries) runWholeProgram(frameworkScope);
            break;
          case DemandDrivenBackward:
            runDemandDrivenBackward(frameworkScope);
            break;
        }
      }
      if (queryDetector.resultsMustNotBeEmpty) return;
      if (!unsoundErrors.isEmpty()) {
        Assert.fail(Joiner.on("\n").join(unsoundErrors));
      }
      if (!imprecisionErrors.isEmpty() && FAIL_ON_IMPRECISE) {
        Assert.fail(Joiner.on("\n").join(imprecisionErrors));
      }
    }
  }

  private void runWholeProgram(FrameworkScope frameworkScope) {
    final Set<Node<Edge, Val>> results = new LinkedHashSet<>();
    BoomerangOptions options =
        BoomerangOptions.builder().withAnalysisTimeout(analysisTimeout).build();
    WholeProgramBoomerang<NoWeight> solver =
        new WholeProgramBoomerang<>(frameworkScope, options) {

          @Override
          protected WeightFunctions<Edge, Val, Field, NoWeight> getForwardFieldWeights() {
            return new OneWeightFunctions<>(NoWeight.NO_WEIGHT_ONE);
          }

          @Override
          protected WeightFunctions<Edge, Val, Field, NoWeight> getBackwardFieldWeights() {
            return new OneWeightFunctions<>(NoWeight.NO_WEIGHT_ONE);
          }

          @Override
          protected WeightFunctions<Edge, Val, Edge, NoWeight> getBackwardCallWeights() {
            return new OneWeightFunctions<>(NoWeight.NO_WEIGHT_ONE);
          }

          @Override
          protected WeightFunctions<Edge, Val, Edge, NoWeight> getForwardCallWeights(
              ForwardQuery sourceQuery) {
            return new OneWeightFunctions<>(NoWeight.NO_WEIGHT_ONE);
          }
        };
    solver.wholeProgramAnalysis();
    DefaultValueMap<ForwardQuery, ForwardBoomerangSolver<NoWeight>> solvers = solver.getSolvers();
    for (final Query q : solvers.keySet()) {
      for (final Query queryForCallSite : queryForCallSites) {
        solvers
            .get(q)
            .getFieldAutomaton()
            .registerListener(
                new WPAStateListener<Field, INode<Node<Edge, Val>>, NoWeight>(
                    new SingleNode<>(queryForCallSite.asNode())) {

                  @Override
                  public void onOutTransitionAdded(
                      Transition<Field, INode<Node<Edge, Val>>> t,
                      NoWeight w,
                      WeightedPAutomaton<Field, INode<Node<Edge, Val>>, NoWeight>
                          weightedPAutomaton) {
                    if (t.getLabel().equals(Field.empty())
                        && t.getTarget().fact().equals(q.asNode())) {
                      results.add(q.asNode());
                    }
                  }

                  @Override
                  public void onInTransitionAdded(
                      Transition<Field, INode<Node<Edge, Val>>> t,
                      NoWeight w,
                      WeightedPAutomaton<Field, INode<Node<Edge, Val>>, NoWeight>
                          weightedPAutomaton) {}
                });
      }
      for (Node<Edge, Val> s : solvers.get(q).getReachedStates()) {
        if (s.stmt().getMethod().toString().contains("unreachable")
            && !q.toString().contains("dummyClass.main")) {
          Assert.fail("Propagation within unreachable method found: " + q);
        }
      }
    }

    compareQuery(expectedAllocationSites, results, AnalysisMode.WholeProgram);
  }

  private void runDemandDrivenBackward(FrameworkScope frameworkScope) {
    // Run backward analysis
    Set<Node<Edge, Val>> backwardResults = runQuery(frameworkScope, queryForCallSites);
    if (queryDetector.integerQueries) {
      compareIntegerResults(backwardResults, AnalysisMode.DemandDrivenBackward);
    } else {
      compareQuery(expectedAllocationSites, backwardResults, AnalysisMode.DemandDrivenBackward);
    }
  }

  private void compareIntegerResults(Set<Node<Edge, Val>> backwardResults, AnalysisMode analysis) {
    if (queryForCallSites.size() > 1) throw new RuntimeException("Not implemented");
    for (Query q : queryForCallSites) {
      Edge stmt = q.cfgEdge();
      InvokeExpr ie = stmt.getStart().getInvokeExpr();
      Val arg = ie.getArg(1);
      Collection<String> expectedResults = parse(arg);
      LOGGER.info("Expected results: {}", String.join("\n - ", expectedResults));
      boolean imprecise = false;
      for (Node<Edge, Val> v : backwardResults) {
        if (v.fact() instanceof AllocVal) {
          AllocVal allocVal = (AllocVal) v.fact();
          boolean remove = expectedResults.remove(allocVal.toString());
          if (!remove) imprecise = true;
        } else {
          imprecise = true;
        }
      }
      if (!expectedResults.isEmpty()) {
        unsoundErrors.add(new Error(analysis + " Unsound results!"));
      }
      if (imprecise) {
        imprecisionErrors.add(new Error(analysis + " Imprecise results!"));
      }
    }
  }

  private ArrayList<String> parse(Val arg) {
    String[] split = arg.getStringValue().split(",");
    return Lists.newArrayList(split);
  }

  private Set<Node<Edge, Val>> runQuery(
      FrameworkScope frameworkScope, Collection<? extends Query> queries) {
    final Set<Node<Edge, Val>> results = new LinkedHashSet<>();

    for (final Query query : queries) {
      BoomerangOptions options = createBoomerangOptions();
      Boomerang solver = new Boomerang(frameworkScope, options);

      if (query instanceof BackwardQuery) {
        Stopwatch watch = Stopwatch.createStarted();
        BackwardBoomerangResults<NoWeight> res = solver.solve((BackwardQuery) query);
        globalQueryTime = globalQueryTime.plus(watch.elapsed());

        LOGGER.info("Solving query took: {}", watch);
        LOGGER.info("Expected results: {}", globalQueryTime);
        for (ForwardQuery q : res.getAllocationSites().keySet()) {
          results.add(q.asNode());

          for (Node<Edge, Val> s : solver.getSolvers().get(q).getReachedStates()) {
            if (s.stmt().getMethod().toString().contains("unreachable")) {
              Assert.fail("Propagation within unreachable method found.");
            }
          }
        }
        if (queryDetector.accessPathQuery) {
          checkContainsAllExpectedAccessPath(res.getAllAliases());
        }
      }
    }
    return results;
  }

  protected BoomerangOptions createBoomerangOptions() {
    if (queryDetector.integerQueries) {
      return BoomerangOptions.WITH_ALLOCATION_SITE(new IntAndStringAllocationSite());
    }

    return BoomerangOptions.builder().withAnalysisTimeout(analysisTimeout).build();
  }

  private void compareQuery(
      Collection<? extends Query> expectedResults,
      Collection<? extends Node<Edge, Val>> results,
      AnalysisMode analysis) {
    LOGGER.info(
        "Boomerang Results:\n - {}",
        results.stream().map(r -> r.fact().toString()).collect(Collectors.joining("\n - ")));
    LOGGER.info(
        "Expected Results:\n - {}",
        expectedResults.stream().map(r -> r.var().toString()).collect(Collectors.joining("\n - ")));
    Collection<Node<Edge, Val>> falseNegativeAllocationSites = new HashSet<>();
    for (Query res : expectedResults) {
      if (!results.contains(res.asNode())) falseNegativeAllocationSites.add(res.asNode());
    }
    Collection<? extends Node<Edge, Val>> falsePositiveAllocationSites = new HashSet<>(results);
    for (Query res : expectedResults) {
      falsePositiveAllocationSites.remove(res.asNode());
    }

    String answer =
        (falseNegativeAllocationSites.isEmpty() ? "" : "\nFN:" + falseNegativeAllocationSites)
            + (falsePositiveAllocationSites.isEmpty()
                ? ""
                : "\nFP:" + falsePositiveAllocationSites + "\n");
    if (!falseNegativeAllocationSites.isEmpty()) {
      unsoundErrors.add(new Error(analysis + " Unsound results for:" + answer));
    }
    if (TRACK_IMPLICIT_IMPRECISE && !falsePositiveAllocationSites.isEmpty())
      imprecisionErrors.add(new Error(analysis + " Imprecise results for:" + answer));

    if (queryDetector.resultsMustNotBeEmpty && results.isEmpty()) {
      Assert.fail("Expected some results, but Boomerang returned no allocation sites.");
    }

    for (Node<Edge, Val> r : results) {
      if (explicitlyUnexpectedAllocationSites.contains(r)) {
        imprecisionErrors.add(new Error(analysis + " Imprecise results for:" + answer));
      }
    }
  }

  private void checkContainsAllExpectedAccessPath(Set<AccessPath> allAliases) {
    HashSet<AccessPath> expected = new LinkedHashSet(queryDetector.expectedAccessPaths);
    expected.removeAll(allAliases);
    if (!expected.isEmpty()) {
      Assert.fail("Did not find all access path! " + expected);
    }
  }
}
