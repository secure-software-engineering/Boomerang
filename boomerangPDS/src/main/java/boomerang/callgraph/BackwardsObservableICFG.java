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
package boomerang.callgraph;

import boomerang.scope.CallGraph.Edge;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import java.util.Collection;

public class BackwardsObservableICFG implements ObservableICFG<Statement, Method> {
  protected final ObservableICFG<Statement, Method> delegate;

  public BackwardsObservableICFG(ObservableICFG<Statement, Method> fwOICFG) {
    this.delegate = fwOICFG;
  }

  @Override
  public Collection<Statement> getStartPointsOf(Method m) {
    return this.delegate.getEndPointsOf(m);
  }

  @Override
  public boolean isExitStmt(Statement stmt) {
    return this.delegate.isStartPoint(stmt);
  }

  @Override
  public boolean isStartPoint(Statement stmt) {
    return this.delegate.isExitStmt(stmt);
  }

  @Override
  public Collection<Statement> getEndPointsOf(Method m) {
    return this.delegate.getStartPointsOf(m);
  }

  @Override
  public boolean isCallStmt(Statement stmt) {
    return this.delegate.isCallStmt(stmt);
  }

  @Override
  public void addCalleeListener(CalleeListener listener) {
    delegate.addCalleeListener(listener);
  }

  @Override
  public void addCallerListener(CallerListener listener) {
    delegate.addCallerListener(listener);
  }

  @Override
  public int getNumberOfEdgesTakenFromPrecomputedGraph() {
    return delegate.getNumberOfEdgesTakenFromPrecomputedGraph();
  }

  @Override
  public void resetCallGraph() {
    delegate.resetCallGraph();
  }

  @Override
  public void computeFallback() {
    delegate.computeFallback();
  }

  @Override
  public void addEdges(Edge e) {
    this.delegate.addEdges(e);
  }
}
