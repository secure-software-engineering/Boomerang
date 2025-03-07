package boomerang.options;

import boomerang.callgraph.BoomerangResolver;
import boomerang.callgraph.ICallerCalleeResolutionStrategy;
import boomerang.flowfunction.DefaultBackwardFlowFunction;
import boomerang.flowfunction.DefaultBackwardFlowFunctionOptions;
import boomerang.flowfunction.DefaultForwardFlowFunction;
import boomerang.flowfunction.DefaultForwardFlowFunctionOptions;
import boomerang.flowfunction.IBackwardFlowFunction;
import boomerang.flowfunction.IForwardFlowFunction;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.solver.Strategies;
import sparse.SparsificationStrategy;

/**
 * Class that defines all options for executing Boomerang queries. The options include flow
 * functions, strategies to deal with different fields, and flags to impact the precision and
 * runtime. An object can be instantiated with the builder pattern, e.g.
 *
 * <pre>{@code
 * BoomerangOptions options =
 *                     BoomerangOptions.builder()
 *                            .withAllocationSite(new DefaultAllocationSite())
 *                            .withAnalysisTimeout(10000)
 *                            .build();
 *
 * }</pre>
 */
public class BoomerangOptions {

  private final OptionsBuilder builder;

  protected BoomerangOptions(OptionsBuilder builder) {
    this.builder = builder;
  }

  /**
   * Builds options for Boomerang queries that contain all default values.
   *
   * @return the options with all default values
   */
  public static BoomerangOptions DEFAULT() {
    return new OptionsBuilder().build();
  }

  /**
   * Builds options for Boomerang queries with the specified {@link IAllocationSite} and all other
   * default options.
   *
   * @param allocationSite the {@link IAllocationSite} to be used
   * @return the options with the specified {@link IAllocationSite} and all other default options
   */
  public static BoomerangOptions WITH_ALLOCATION_SITE(IAllocationSite allocationSite) {
    return new OptionsBuilder().withAllocationSite(allocationSite).build();
  }

  public static OptionsBuilder builder() {
    return new OptionsBuilder();
  }

  public void checkValid() {
    if (!trackPathConditions() && prunePathConditions()) {
      throw new RuntimeException(
          "InvalidCombinations of options, path conditions must be enabled when pruning path conditions");
    }
  }

  public IAllocationSite allocationSite() {
    return builder.allocationSite;
  }

  public IForwardFlowFunction getForwardFlowFunction() {
    return builder.forwardFlowFunctions;
  }

  public IBackwardFlowFunction getBackwardFlowFunction() {
    return builder.backwardFlowFunction;
  }

  public Strategies.StaticFieldStrategy getStaticFieldStrategy() {
    return builder.staticFieldStrategy;
  }

  public Strategies.ArrayStrategy getArrayStrategy() {
    return builder.arrayStrategy;
  }

  public ICallerCalleeResolutionStrategy.Factory getResolutionStrategy() {
    return builder.resolutionStrategy;
  }

  public SparsificationStrategy<? extends Method, ? extends Statement> getSparsificationStrategy() {
    return builder.sparsificationStrategy;
  }

  public int analysisTimeout() {
    return builder.analysisTimeout;
  }

  public int maxFieldDepth() {
    return builder.maxFieldDepth;
  }

  public int maxCallDepth() {
    return builder.maxCallDepth;
  }

  public int maxUnbalancedCallDepth() {
    return builder.maxUnbalancedCallDepth;
  }

  public boolean typeCheck() {
    return builder.typeCheck;
  }

  public boolean onTheFlyCallGraph() {
    return builder.onTheFlyCallGraph;
  }

  public boolean onTheFlyControlFlow() {
    return builder.onTheFlyControlFlow;
  }

  public boolean callSummaries() {
    return builder.callSummaries;
  }

  public boolean fieldSummaries() {
    return builder.fieldSummaries;
  }

  public boolean trackImplicitFlows() {
    return builder.trackImplicitFlows;
  }

  public boolean killNullAtCast() {
    return builder.killNullAtCast;
  }

  public boolean trackStaticFieldAtEntryPointToClinit() {
    return builder.trackStaticFieldAtEntryPointToClinit;
  }

  public boolean handleMaps() {
    return builder.handleMaps;
  }

  public boolean trackPathConditions() {
    return builder.trackPathConditions;
  }

  public boolean prunePathConditions() {
    return builder.prunePathConditions;
  }

  public boolean trackDataFlowPath() {
    return builder.trackDataFlowPath;
  }

  public boolean allowMultipleQueries() {
    return builder.allowMultipleQueries;
  }

  public boolean handleSpecialInvokeAsNormalPropagation() {
    return builder.handleSpecialInvokeAsNormalPropagation;
  }

  public boolean ignoreSparsificationAfterQuery() {
    return builder.ignoreSparsificationAfterQuery;
  }

  public static class OptionsBuilder {

    private IAllocationSite allocationSite;
    private IForwardFlowFunction forwardFlowFunctions;
    private IBackwardFlowFunction backwardFlowFunction;
    private Strategies.StaticFieldStrategy staticFieldStrategy;
    private Strategies.ArrayStrategy arrayStrategy;
    private ICallerCalleeResolutionStrategy.Factory resolutionStrategy;
    private SparsificationStrategy<? extends Method, ? extends Statement> sparsificationStrategy;

    private int analysisTimeout;
    private int maxFieldDepth;
    private int maxCallDepth;
    private int maxUnbalancedCallDepth;

    private boolean typeCheck; // TODO This may be removed
    private boolean onTheFlyCallGraph;
    private boolean onTheFlyControlFlow;
    private boolean callSummaries;
    private boolean fieldSummaries;
    private boolean trackImplicitFlows;
    private boolean killNullAtCast;
    private boolean trackStaticFieldAtEntryPointToClinit;
    private boolean handleMaps;
    private boolean trackPathConditions;
    private boolean prunePathConditions;
    private boolean trackDataFlowPath;
    private boolean allowMultipleQueries;
    private boolean handleSpecialInvokeAsNormalPropagation;
    private boolean ignoreSparsificationAfterQuery;

    protected OptionsBuilder() {
      this.allocationSite = new DefaultAllocationSite();
      this.forwardFlowFunctions = null;
      this.backwardFlowFunction = null;
      this.staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON;
      this.arrayStrategy = Strategies.ArrayStrategy.INDEX_SENSITIVE;
      this.resolutionStrategy = BoomerangResolver.FACTORY;
      this.sparsificationStrategy = SparsificationStrategy.NONE;

      this.analysisTimeout = -1;
      this.maxFieldDepth = -1;
      this.maxCallDepth = -1;
      this.maxUnbalancedCallDepth = -1;

      this.typeCheck = true;
      this.onTheFlyCallGraph = false;
      this.onTheFlyControlFlow = false;
      this.callSummaries = false;
      this.fieldSummaries = false;
      this.trackImplicitFlows = false;
      this.killNullAtCast = false;
      this.trackStaticFieldAtEntryPointToClinit = false;
      this.handleMaps = true;
      this.trackPathConditions = false;
      this.prunePathConditions = false;
      this.trackDataFlowPath = true;
      this.allowMultipleQueries = false;
      this.handleSpecialInvokeAsNormalPropagation = false;
      this.ignoreSparsificationAfterQuery = true;
    }

    public BoomerangOptions build() {
      if (this.forwardFlowFunctions == null) {
        DefaultForwardFlowFunctionOptions options =
            DefaultForwardFlowFunctionOptions.builder()
                .withStaticFieldStrategy(staticFieldStrategy)
                .withArrayStrategy(arrayStrategy)
                .build();
        this.forwardFlowFunctions = new DefaultForwardFlowFunction(options);
      }

      if (this.backwardFlowFunction == null) {
        DefaultBackwardFlowFunctionOptions options =
            DefaultBackwardFlowFunctionOptions.builder()
                .withAllocationSite(allocationSite)
                .withStaticFieldStrategy(staticFieldStrategy)
                .withArrayStrategy(arrayStrategy)
                .build();
        this.backwardFlowFunction = new DefaultBackwardFlowFunction(options);
      }

      return new BoomerangOptions(this);
    }

    public OptionsBuilder withAllocationSite(IAllocationSite allocationSite) {
      this.allocationSite = allocationSite;
      return this;
    }

    public OptionsBuilder withForwardFlowFunction(IForwardFlowFunction forwardFlowFunction) {
      this.forwardFlowFunctions = forwardFlowFunction;
      return this;
    }

    public OptionsBuilder withBackwardFlowFunction(IBackwardFlowFunction backwardFlowFunction) {
      this.backwardFlowFunction = backwardFlowFunction;
      return this;
    }

    /**
     * Sets the strategy {@link Strategies.StaticFieldStrategy} to define how to deal with static
     * fields.
     *
     * @param staticFieldStrategy the array strategy (default: SINGLETON)
     * @return the builder
     */
    public OptionsBuilder withStaticFieldStrategy(
        Strategies.StaticFieldStrategy staticFieldStrategy) {
      this.staticFieldStrategy = staticFieldStrategy;
      return this;
    }

    /**
     * Sets the strategy {@link Strategies.ArrayStrategy} to define how to deal with arrays.
     *
     * @param arrayStrategy the array strategy (default: INDEX_SENSITIVE)
     * @return the builder
     */
    public OptionsBuilder withArrayStrategy(Strategies.ArrayStrategy arrayStrategy) {
      this.arrayStrategy = arrayStrategy;
      return this;
    }

    public OptionsBuilder withResolutionStrategy(
        ICallerCalleeResolutionStrategy.Factory resolutionStrategy) {
      this.resolutionStrategy = resolutionStrategy;
      return this;
    }

    public OptionsBuilder withSparsificationStrategy(
        SparsificationStrategy<? extends Method, ? extends Statement> sparsificationStrategy) {
      this.sparsificationStrategy = sparsificationStrategy;
      return this;
    }

    /**
     * Sets an analysis timeout in milliseconds for individual Boomerang queries (e.g. 10000 = 10
     * seconds). Use a value smaller than 0 to not use a timeout.
     *
     * @param analysisTimeout the timeout in milliseconds (default: -1 (no timeout))
     * @return the builder
     */
    public OptionsBuilder withAnalysisTimeout(int analysisTimeout) {
      this.analysisTimeout = analysisTimeout;
      return this;
    }

    public OptionsBuilder withMaxFieldDepth(int maxFieldDepth) {
      this.maxFieldDepth = maxFieldDepth;
      return this;
    }

    public OptionsBuilder withMaxCallDepth(int maxCallDepth) {
      this.maxCallDepth = maxCallDepth;
      return this;
    }

    public OptionsBuilder withMaxUnbalancedCallDepth(int maxUnbalancedCallDepth) {
      this.maxUnbalancedCallDepth = maxUnbalancedCallDepth;
      return this;
    }

    public OptionsBuilder enableTypeCheck(boolean typeCheck) {
      this.typeCheck = typeCheck;
      return this;
    }

    public OptionsBuilder enableOnTheFlyCallGraph(boolean onTheFlyCallGraph) {
      this.onTheFlyCallGraph = onTheFlyCallGraph;
      return this;
    }

    public OptionsBuilder enableOnTheFlyControlFlow(boolean onTheFlyControlFlow) {
      this.onTheFlyControlFlow = onTheFlyControlFlow;
      return this;
    }

    public OptionsBuilder enableCallSummaries(boolean callSummaries) {
      this.callSummaries = callSummaries;
      return this;
    }

    public OptionsBuilder enableFieldSummaries(boolean fieldSummaries) {
      this.fieldSummaries = fieldSummaries;
      return this;
    }

    public OptionsBuilder enableTrackImplicitFlows(boolean trackImplicitFlows) {
      this.trackImplicitFlows = trackImplicitFlows;
      return this;
    }

    /**
     * Assume we have the following code:
     *
     * <pre>{@code
     * Object y = null;
     * Object x = (Object) y;
     *
     * }</pre>
     *
     * If the option 'killNullAtCast' is enabled, Boomerang does not continue the analysis with x,
     * that is, it does not consider x as an alias of y (and vice versa). At runtime, 'null
     * instanceof Object' evaluates to false, however the cast '(Object) null' does NOT throw a
     * ClassCastException. Therefore, enabling this option models the runtime behavior more
     * consistent.
     *
     * @param killNullAtCast set to 'true' if cast expressions with 'null' references should not
     *     create an alias (default: false)
     * @return the builder
     */
    public OptionsBuilder enableKillNullAtCast(boolean killNullAtCast) {
      this.killNullAtCast = killNullAtCast;
      return this;
    }

    public OptionsBuilder enableTrackStaticFieldAtEntryPointToClinit(
        boolean trackStaticFieldAtEntryPointToClinit) {
      this.trackStaticFieldAtEntryPointToClinit = trackStaticFieldAtEntryPointToClinit;
      return this;
    }

    public OptionsBuilder enableHandleMaps(boolean handleMaps) {
      this.handleMaps = handleMaps;
      return this;
    }

    public OptionsBuilder enableTrackPathConditions(boolean trackPathConditions) {
      this.trackPathConditions = trackPathConditions;
      return this;
    }

    public OptionsBuilder enablePrunePathConditions(boolean prunePathConditions) {
      this.prunePathConditions = prunePathConditions;
      return this;
    }

    public OptionsBuilder enableTrackDataFlowPath(boolean trackDataFlowPath) {
      this.trackDataFlowPath = trackDataFlowPath;
      return this;
    }

    public OptionsBuilder enableAllowMultipleQueries(boolean allowMultipleQueries) {
      this.allowMultipleQueries = allowMultipleQueries;
      return this;
    }

    public OptionsBuilder enableHandleSpecialInvokeAsNormalPropagation(
        boolean handleSpecialInvokeAsNormalPropagation) {
      this.handleSpecialInvokeAsNormalPropagation = handleSpecialInvokeAsNormalPropagation;
      return this;
    }

    public OptionsBuilder enableIgnoreSparsificationAfterQuery(
        boolean ignoreSparsificationAfterQuery) {
      this.ignoreSparsificationAfterQuery = ignoreSparsificationAfterQuery;
      return this;
    }
  }
}
