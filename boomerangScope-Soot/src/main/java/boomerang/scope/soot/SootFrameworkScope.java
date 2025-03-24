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
package boomerang.scope.soot;

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimpleStaticFieldVal;
import boomerang.scope.soot.jimple.JimpleVal;
import java.util.Collection;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.IntConstant;

public class SootFrameworkScope implements FrameworkScope {

  protected final Scene scene;
  protected final SootCallGraph sootCallGraph;
  protected DataFlowScope dataFlowScope;

  public SootFrameworkScope(
      @NonNull Scene scene,
      //      @NonNull soot.jimple.toolkits.callgraph.CallGraph callGraph,
      soot.jimple.toolkits.callgraph.CallGraph callGraph,
      @NonNull Collection<SootMethod> entryPoints,
      @NonNull DataFlowScope dataFlowScope) {
    this.scene = scene;

    this.sootCallGraph = new SootCallGraph(callGraph, entryPoints);
    this.dataFlowScope = dataFlowScope;
  }

  @Override
  @NonNull
  public Val getTrueValue(Method m) {
    return new JimpleVal(IntConstant.v(1), m);
  }

  @Override
  @NonNull
  public Val getFalseValue(Method m) {
    return new JimpleVal(IntConstant.v(0), m);
  }

  @Override
  @NonNull
  public Stream<Method> handleStaticFieldInitializers(Val fact) {
    JimpleStaticFieldVal val = ((JimpleStaticFieldVal) fact);
    return ((JimpleField) val.field())
        .getDelegate().getDeclaringClass().getMethods().stream()
            .filter(SootMethod::hasActiveBody)
            .map(JimpleMethod::of);
  }

  @Override
  @NonNull
  public StaticFieldVal newStaticFieldVal(Field field, Method m) {
    return new JimpleStaticFieldVal((JimpleField) field, m);
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
