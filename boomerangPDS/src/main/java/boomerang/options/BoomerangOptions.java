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
package boomerang.options;

import boomerang.callgraph.BoomerangResolver;
import boomerang.callgraph.ICallerCalleeResolutionStrategy;
import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.flowfunction.IFlowFunctionFactory;
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
  public static BoomerangOptions withAllocationSite(IAllocationSite allocationSite) {
    return new OptionsBuilder().withAllocationSite(allocationSite).build();
  }

  public static OptionsBuilder builder() {
    return new OptionsBuilder();
  }

  public void checkValid() {
    if (builder.trackStaticFieldAtEntryPointToClinit
        && builder.staticFieldStrategy != Strategies.StaticFieldStrategy.FLOW_SENSITIVE) {
      throw new RuntimeException(
          "The 'trackStaticFieldAtEntryPointToClinit' requires the static field strategy 'Flow Sensitive'");
    }
  }

  public IAllocationSite allocationSite() {
    return builder.allocationSite;
  }

  public IFlowFunctionFactory getFlowFunctionFactory() {
    return builder.flowFunctionFactory;
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

  public boolean isFieldSensitive() {
    return builder.fieldSensitivity;
  }

  public boolean isContextSensitive() {
    return builder.contextSensitivity;
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

  public boolean trackStaticFieldAtEntryPointToClinit() {
    return builder.trackStaticFieldAtEntryPointToClinit;
  }

  public boolean handleMaps() {
    return builder.handleMaps;
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
    private IFlowFunctionFactory flowFunctionFactory;
    private Strategies.StaticFieldStrategy staticFieldStrategy;
    private Strategies.ArrayStrategy arrayStrategy;
    private ICallerCalleeResolutionStrategy.Factory resolutionStrategy;
    private SparsificationStrategy<? extends Method, ? extends Statement> sparsificationStrategy;

    private int analysisTimeout;
    private int maxFieldDepth;
    private int maxCallDepth;
    private int maxUnbalancedCallDepth;

    private boolean fieldSensitivity;
    private boolean contextSensitivity;
    private boolean onTheFlyCallGraph;
    private boolean onTheFlyControlFlow;
    private boolean callSummaries;
    private boolean fieldSummaries;
    private boolean trackStaticFieldAtEntryPointToClinit;
    private boolean handleMaps;
    private boolean allowMultipleQueries;
    private boolean handleSpecialInvokeAsNormalPropagation;
    private boolean ignoreSparsificationAfterQuery;

    protected OptionsBuilder() {
      this.allocationSite = new DefaultAllocationSite();
      this.flowFunctionFactory = new DefaultFlowFunctionFactory();
      this.staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON;
      this.arrayStrategy = Strategies.ArrayStrategy.INDEX_SENSITIVE;
      this.resolutionStrategy = BoomerangResolver.FACTORY;
      this.sparsificationStrategy = SparsificationStrategy.NONE;

      this.analysisTimeout = -1;
      this.maxFieldDepth = -1;
      this.maxCallDepth = -1;
      this.maxUnbalancedCallDepth = -1;

      this.fieldSensitivity = true;
      this.contextSensitivity = true;
      this.onTheFlyCallGraph = false;
      this.onTheFlyControlFlow = false;
      this.callSummaries = false;
      this.fieldSummaries = false;
      this.trackStaticFieldAtEntryPointToClinit = false;
      this.handleMaps = true;
      this.allowMultipleQueries = false;
      this.handleSpecialInvokeAsNormalPropagation = false;
      this.ignoreSparsificationAfterQuery = true;
    }

    public BoomerangOptions build() {
      return new BoomerangOptions(this);
    }

    public OptionsBuilder withAllocationSite(IAllocationSite allocationSite) {
      this.allocationSite = allocationSite;
      return this;
    }

    public OptionsBuilder withFlowFunctionFactory(IFlowFunctionFactory flowFunctionFactory) {
      this.flowFunctionFactory = flowFunctionFactory;
      return this;
    }

    /**
     * Sets the strategy {@link Strategies.StaticFieldStrategy} to define how to deal with static
     * fields.
     *
     * @param strategy the array strategy (default: SINGLETON)
     * @return the builder
     */
    public OptionsBuilder withStaticFieldStrategy(Strategies.StaticFieldStrategy strategy) {
      this.staticFieldStrategy = strategy;
      return this;
    }

    /**
     * Sets the strategy {@link Strategies.ArrayStrategy} to define how to deal with arrays.
     *
     * @param strategy the array strategy (default: INDEX_SENSITIVE)
     * @return the builder
     */
    public OptionsBuilder withArrayStrategy(Strategies.ArrayStrategy strategy) {
      this.arrayStrategy = strategy;
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

    public OptionsBuilder enableFieldSensitivity(boolean fieldSensitivity) {
      this.fieldSensitivity = fieldSensitivity;
      return this;
    }

    public OptionsBuilder enableContextSensitivity(boolean contextSensitivity) {
      this.contextSensitivity = contextSensitivity;
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

    public OptionsBuilder enableTrackStaticFieldAtEntryPointToClinit(
        boolean trackStaticFieldAtEntryPointToClinit) {
      this.trackStaticFieldAtEntryPointToClinit = trackStaticFieldAtEntryPointToClinit;
      return this;
    }

    public OptionsBuilder enableHandleMaps(boolean handleMaps) {
      this.handleMaps = handleMaps;
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
