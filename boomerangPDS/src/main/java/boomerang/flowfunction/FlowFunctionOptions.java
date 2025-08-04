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
package boomerang.flowfunction;

import boomerang.options.DefaultAllocationSite;
import boomerang.options.IAllocationSite;
import boomerang.solver.Strategies;

public class FlowFunctionOptions implements IFlowFunctionOptions {

  private final FlowFunctionOptionsBuilder builder;

  protected FlowFunctionOptions(FlowFunctionOptionsBuilder builder) {
    this.builder = builder;
  }

  public static FlowFunctionOptionsBuilder builder() {
    return new FlowFunctionOptionsBuilder();
  }

  public static FlowFunctionOptions DEFAULT() {
    return new FlowFunctionOptionsBuilder().build();
  }

  public IAllocationSite allocationSite() {
    return builder.allocationSite;
  }

  public Strategies.StaticFieldStrategy staticFieldStrategy() {
    return builder.staticFieldStrategy;
  }

  public Strategies.ArrayStrategy arrayStrategy() {
    return builder.arrayStrategy;
  }

  public boolean trackFields() {
    return builder.trackFields;
  }

  public boolean includeInnerClassFields() {
    return builder.includeInnerClassFields;
  }

  public boolean throwFlows() {
    return builder.throwFlows;
  }

  public boolean trackReturnOfInstanceOf() {
    return builder.trackReturnOfInstanceOf;
  }

  public static class FlowFunctionOptionsBuilder {

    private IAllocationSite allocationSite;
    private Strategies.StaticFieldStrategy staticFieldStrategy;
    private Strategies.ArrayStrategy arrayStrategy;
    private boolean trackFields;
    private boolean includeInnerClassFields;
    private boolean throwFlows;
    private boolean trackReturnOfInstanceOf;

    protected FlowFunctionOptionsBuilder() {
      this.allocationSite = new DefaultAllocationSite();
      this.staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON;
      this.arrayStrategy = Strategies.ArrayStrategy.INDEX_SENSITIVE;
      this.trackFields = true;
      this.includeInnerClassFields = true;
      this.throwFlows = false;
      this.trackReturnOfInstanceOf = false;
    }

    public FlowFunctionOptions build() {
      return new FlowFunctionOptions(this);
    }

    public FlowFunctionOptionsBuilder withAllocationSite(IAllocationSite allocationSite) {
      this.allocationSite = allocationSite;
      return this;
    }

    public FlowFunctionOptionsBuilder withStaticFieldStrategy(
        Strategies.StaticFieldStrategy staticFieldStrategy) {
      this.staticFieldStrategy = staticFieldStrategy;
      return this;
    }

    public FlowFunctionOptionsBuilder withArrayStrategy(Strategies.ArrayStrategy arrayStrategy) {
      this.arrayStrategy = arrayStrategy;
      return this;
    }

    public FlowFunctionOptionsBuilder enableTrackFields(boolean trackFields) {
      this.trackFields = trackFields;
      return this;
    }

    public FlowFunctionOptionsBuilder enableIncludeInnerClassFields(
        boolean includeInnerClassFields) {
      this.includeInnerClassFields = includeInnerClassFields;
      return this;
    }

    public FlowFunctionOptionsBuilder enableThrowFlows(boolean throwFlows) {
      this.throwFlows = throwFlows;
      return this;
    }

    public FlowFunctionOptionsBuilder enableTrackReturnOfInstanceOf(
        boolean trackReturnOfInstanceOf) {
      this.trackReturnOfInstanceOf = trackReturnOfInstanceOf;
      return this;
    }
  }
}
