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
package boomerang.staticfields;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.solver.AbstractBoomerangSolver;
import com.google.common.collect.Multimap;
import java.util.Set;
import sync.pds.solver.nodes.Node;
import wpds.interfaces.State;

public class SingletonStaticFieldStrategy implements StaticFieldHandlingStrategy {

  private final Multimap<Field, Statement> fieldStoreStatements;
  private final Multimap<Field, Statement> fieldLoadStatements;
  private final AbstractBoomerangSolver<?> solver;

  public SingletonStaticFieldStrategy(
      AbstractBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements) {
    this.solver = solver;
    this.fieldStoreStatements = fieldStoreStatements;
    this.fieldLoadStatements = fieldLoadStatements;
  }

  @Override
  public void handleForward(
      Edge storeStmt, Val storedVal, StaticFieldVal staticVal, Set<State> out) {
    for (Statement matchingStore : fieldLoadStatements.get(staticVal.getField())) {
      if (matchingStore.isAssignStmt()) {
        for (Statement succ :
            matchingStore.getMethod().getControlFlowGraph().getSuccsOf(matchingStore)) {
          solver.processNormal(
              new Node<>(storeStmt, storedVal),
              new Node<>(new Edge(matchingStore, succ), matchingStore.getLeftOp()));
        }
      }
    }
  }

  @Override
  public void handleBackward(
      Edge loadStatement, Val loadedVal, StaticFieldVal staticVal, Set<State> out) {
    for (Statement matchingStore : fieldStoreStatements.get(staticVal.getField())) {
      for (Statement pred :
          matchingStore.getMethod().getControlFlowGraph().getPredsOf(matchingStore)) {
        solver.processNormal(
            new Node<>(loadStatement, loadedVal),
            new Node<>(new Edge(pred, matchingStore), matchingStore.getRightOp()));
      }
    }
  }
}
