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

public class FlowFunctionOptions {

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

    private boolean trackFields;
    private boolean includeInnerClassFields;
    private boolean throwFlows;
    private boolean trackReturnOfInstanceOf;

    protected FlowFunctionOptionsBuilder() {
      this.trackFields = true;
      this.includeInnerClassFields = true;
      this.throwFlows = false;
      this.trackReturnOfInstanceOf = false;
    }

    public FlowFunctionOptions build() {
      return new FlowFunctionOptions(this);
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
