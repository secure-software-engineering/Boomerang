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
package boomerang.scope.soot;

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import java.util.Collection;
import org.jspecify.annotations.NonNull;
import soot.Scene;
import soot.SootMethod;

public class SootFrameworkScope implements FrameworkScope {

  protected final Scene scene;
  protected final SootCallGraph sootCallGraph;
  protected DataFlowScope dataFlowScope;

  public SootFrameworkScope(
      @NonNull Scene scene,
      soot.jimple.toolkits.callgraph.@NonNull CallGraph callGraph,
      @NonNull Collection<SootMethod> entryPoints,
      @NonNull DataFlowScope dataFlowScope) {
    this.scene = scene;

    this.sootCallGraph = new SootCallGraph(scene, callGraph, entryPoints);
    this.dataFlowScope = dataFlowScope;
  }

  @Override
  public CallGraph getCallGraph() {
    return sootCallGraph;
  }

  @Override
  public DataFlowScope getDataFlowScope() {
    return dataFlowScope;
  }
}
