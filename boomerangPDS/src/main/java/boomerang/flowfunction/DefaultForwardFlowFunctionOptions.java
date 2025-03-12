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
package boomerang.flowfunction;

import boomerang.solver.Strategies;

public class DefaultForwardFlowFunctionOptions {

  private final ForwardFlowFunctionOptionsBuilder builder;

  private DefaultForwardFlowFunctionOptions(ForwardFlowFunctionOptionsBuilder builder) {
    this.builder = builder;
  }

  public Strategies.StaticFieldStrategy getStaticFieldStrategy() {
    return builder.staticFieldStrategy;
  }

  public Strategies.ArrayStrategy getArrayStrategy() {
    return builder.arrayStrategy;
  }

  public boolean throwFlows() {
    return builder.throwFlows;
  }

  public boolean trackReturnOfInstanceOf() {
    return builder.trackReturnOfInstanceOf;
  }

  public boolean trackFields() {
    return builder.trackFields;
  }

  public boolean includeInnerClassFields() {
    return builder.includeInnerClassFields;
  }

  public static ForwardFlowFunctionOptionsBuilder builder() {
    return new ForwardFlowFunctionOptionsBuilder();
  }

  public static DefaultForwardFlowFunctionOptions DEFAULT() {
    return new ForwardFlowFunctionOptionsBuilder().build();
  }

  public static class ForwardFlowFunctionOptionsBuilder {

    private Strategies.StaticFieldStrategy staticFieldStrategy;
    private Strategies.ArrayStrategy arrayStrategy;
    private boolean throwFlows;
    private boolean trackReturnOfInstanceOf;
    private boolean trackFields;
    private boolean includeInnerClassFields;

    private ForwardFlowFunctionOptionsBuilder() {
      this.staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON;
      this.arrayStrategy = Strategies.ArrayStrategy.INDEX_INSENSITIVE;
      this.throwFlows = false;
      this.trackReturnOfInstanceOf = false;
      this.trackFields = true;
      this.includeInnerClassFields = true;
    }

    public ForwardFlowFunctionOptionsBuilder withStaticFieldStrategy(
        Strategies.StaticFieldStrategy staticFieldStrategy) {
      this.staticFieldStrategy = staticFieldStrategy;
      return this;
    }

    public ForwardFlowFunctionOptionsBuilder withArrayStrategy(
        Strategies.ArrayStrategy arrayStrategy) {
      this.arrayStrategy = arrayStrategy;
      return this;
    }

    public ForwardFlowFunctionOptionsBuilder enableThrowFlows(boolean throwFlows) {
      this.throwFlows = throwFlows;
      return this;
    }

    public ForwardFlowFunctionOptionsBuilder enableTrackReturnOfInstanceOf(
        boolean trackReturnOfInstanceOf) {
      this.trackReturnOfInstanceOf = trackReturnOfInstanceOf;
      return this;
    }

    public ForwardFlowFunctionOptionsBuilder enableTrackFields(boolean trackFields) {
      this.trackFields = trackFields;
      return this;
    }

    public ForwardFlowFunctionOptionsBuilder enableIncludeInnerClassFields(
        boolean includeInnerClassFields) {
      this.includeInnerClassFields = includeInnerClassFields;
      return this;
    }

    public DefaultForwardFlowFunctionOptions build() {
      return new DefaultForwardFlowFunctionOptions(this);
    }
  }
}
