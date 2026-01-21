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
package boomerang.pathtracking;

import boomerang.options.BoomerangOptions;

public class PathTrackingBoomerangOptions extends BoomerangOptions {

  private final PathTrackingOptionsBuilder builder;

  protected PathTrackingBoomerangOptions(PathTrackingOptionsBuilder builder) {
    super(builder);

    this.builder = builder;
  }

  public static PathTrackingBoomerangOptions DEFAULT() {
    return new PathTrackingOptionsBuilder().build();
  }

  public static PathTrackingOptionsBuilder builder() {
    return new PathTrackingOptionsBuilder();
  }

  public void checkValid() {
    if (!trackPathConditions() && prunePathConditions()) {
      throw new RuntimeException(
          "InvalidCombinations of options, path conditions must be enabled when pruning path conditions");
    }
  }

  public boolean trackDataFlowPath() {
    return builder.trackDataFlowPath;
  }

  public boolean trackImplicitFlows() {
    return builder.trackImplicitFlows;
  }

  public boolean trackPathConditions() {
    return builder.trackPathConditions;
  }

  public boolean prunePathConditions() {
    return builder.prunePathConditions;
  }

  public static class PathTrackingOptionsBuilder extends OptionsBuilder {

    private boolean trackDataFlowPath;
    private boolean trackImplicitFlows;
    private boolean trackPathConditions;
    private boolean prunePathConditions;

    protected PathTrackingOptionsBuilder() {
      this.trackDataFlowPath = true;
      this.trackImplicitFlows = false;
      this.trackPathConditions = false;
      this.prunePathConditions = false;
    }

    public PathTrackingBoomerangOptions build() {
      super.build();

      return new PathTrackingBoomerangOptions(this);
    }

    public PathTrackingOptionsBuilder enableTrackDataFlowPath(boolean trackDataFlowPath) {
      this.trackDataFlowPath = trackDataFlowPath;
      return this;
    }

    public PathTrackingOptionsBuilder enableTrackImplicitFlows(boolean trackImplicitFlows) {
      this.trackImplicitFlows = trackImplicitFlows;
      return this;
    }

    public PathTrackingOptionsBuilder enableTrackPathConditions(boolean trackPathConditions) {
      this.trackPathConditions = trackPathConditions;
      return this;
    }

    public PathTrackingOptionsBuilder enablePrunePathConditions(boolean prunePathConditions) {
      this.prunePathConditions = prunePathConditions;
      return this;
    }
  }
}
