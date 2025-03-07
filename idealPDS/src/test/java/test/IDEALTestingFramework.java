package test;

import assertions.Assertions;
import assertions.MayBeInAcceptingState;
import assertions.MayBeInErrorState;
import assertions.MustBeInAcceptingState;
import assertions.MustBeInErrorState;
import assertions.ShouldNotBeAnalyzed;
import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.options.BoomerangOptions;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.FrameworkScope;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.Strategies;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALResultHandler;
import ideal.IDEALSeedSolver;
import ideal.StoreIDEALResultHandler;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.pds.solver.WeightFunctions;
import test.setup.MethodWrapper;
import typestate.TransitionFunction;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public abstract class IDEALTestingFramework extends TestingFramework {

  private static final Logger LOGGER = LoggerFactory.getLogger(IDEALTestingFramework.class);

  @Rule public TestName testName = new TestName();

  private final StoreIDEALResultHandler<TransitionFunction> resultHandler;

  protected IDEALTestingFramework() {
    this.resultHandler = new StoreIDEALResultHandler<>();
  }

  public void analyze(
      String targetClassName, String targetMethodName, int expectedAssertions, int expectedSeeds) {
    LOGGER.info(
        "Running '{}' in class '{}' with {} assertions",
        targetMethodName,
        targetClassName,
        expectedAssertions);

    // Set up the framework scope
    MethodWrapper methodWrapper = new MethodWrapper(targetClassName, targetMethodName);
    FrameworkScope frameworkScope = super.getFrameworkScope(methodWrapper);
    Method testMethod = super.getTestMethod();

    // Collect the expected assertions
    Collection<Assertion> assertions =
        parseExpectedQueryResults(frameworkScope.getCallGraph(), testMethod);
    if (assertions.size() != expectedAssertions) {
      Assert.fail(
          "Unexpected number of assertions in target program. Expected "
              + expectedAssertions
              + ", got "
              + assertions.size());
    }
    TestingResultReporter resultReporter = new TestingResultReporter(assertions);

    // Run IDEal
    IDEALAnalysis<TransitionFunction> idealAnalysis = createAnalysis(frameworkScope);
    idealAnalysis.run();

    // Update results
    Collection<WeightedForwardQuery<TransitionFunction>> seeds =
        resultHandler.getResults().keySet();
    if (seeds.size() != expectedSeeds) {
      Assert.fail(
          "Unexpected number of seeds. Expected " + expectedSeeds + ", got " + seeds.size());
    }

    for (WeightedForwardQuery<TransitionFunction> seed : seeds) {
      resultReporter.onSeedFinished(seed.asNode(), resultHandler.getResults().get(seed));
    }

    // Assert the assertions
    assertResults(assertions);
  }

  protected IDEALAnalysis<TransitionFunction> createAnalysis(FrameworkScope frameworkScope) {
    return new IDEALAnalysis<>(
        new IDEALAnalysisDefinition<>() {

          @Override
          public Collection<WeightedForwardQuery<TransitionFunction>> generate(
              ControlFlowGraph.Edge stmt) {
            return getStateMachine().generateSeed(stmt);
          }

          @Override
          public WeightFunctions<
                  ControlFlowGraph.Edge, Val, ControlFlowGraph.Edge, TransitionFunction>
              weightFunctions() {
            return getStateMachine();
          }

          @Override
          public Debugger<TransitionFunction> debugger(IDEALSeedSolver<TransitionFunction> solver) {
            return new Debugger<>();
          }

          @Override
          public IDEALResultHandler<TransitionFunction> getResultHandler() {
            return resultHandler;
          }

          @Override
          public BoomerangOptions boomerangOptions() {
            return BoomerangOptions.builder()
                .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
                .withAnalysisTimeout(-1)
                .enableAllowMultipleQueries(true)
                .build();
          }

          @Override
          public FrameworkScope getFrameworkFactory() {
            return frameworkScope;
          }
        });
  }

  protected abstract TypeStateMachineWeightFunctions getStateMachine();

  private Collection<Assertion> parseExpectedQueryResults(CallGraph callGraph, Method testMethod) {
    Collection<Assertion> results = new HashSet<>();
    parseExpectedQueryResults(callGraph, testMethod, results, new HashSet<>());

    return results;
  }

  private void parseExpectedQueryResults(
      CallGraph callGraph, Method testMethod, Collection<Assertion> queries, Set<Method> visited) {
    if (visited.contains(testMethod)) {
      return;
    }
    visited.add(testMethod);

    for (Statement stmt : testMethod.getStatements()) {
      if (!stmt.containsInvokeExpr()) {
        continue;
      }

      for (CallGraph.Edge callSite : callGraph.edgesOutOf(stmt)) {
        if (callSite.tgt().isDefined()) {
          parseExpectedQueryResults(callGraph, callSite.tgt(), queries, visited);
        }
      }

      InvokeExpr invokeExpr = stmt.getInvokeExpr();
      DeclaredMethod declaredMethod = invokeExpr.getMethod();

      String assertionsName = Assertions.class.getName();
      if (!declaredMethod.getDeclaringClass().getFullyQualifiedName().equals(assertionsName)) {
        continue;
      }

      String invocationName = invokeExpr.getMethod().getName();

      if (invocationName.equals("shouldNotBeAnalyzed")) {
        queries.add(new ShouldNotBeAnalyzed(stmt));
      }

      if (invocationName.equals("mustBeInAcceptingState")) {
        Val seed = invokeExpr.getArg(0);
        queries.add(new MustBeInAcceptingState(stmt, seed));
      }

      if (invocationName.equals("mustBeInErrorState")) {
        Val seed = invokeExpr.getArg(0);
        queries.add(new MustBeInErrorState(stmt, seed));
      }

      if (invocationName.equals("mayBeInAcceptingState")) {
        Val seed = invokeExpr.getArg(0);
        queries.add(new MayBeInAcceptingState(stmt, seed));
      }

      if (invocationName.equals("mayBeInErrorState")) {
        Val seed = invokeExpr.getArg(0);
        queries.add(new MayBeInErrorState(stmt, seed));
      }
    }
  }
}
