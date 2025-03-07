/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang;

import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;

public class ForwardQuery extends Query {

  public ForwardQuery(ControlFlowGraph.Edge edge, AllocVal variable) {
    super(edge, variable);
  }

  public AllocVal getAllocVal() {
    return (AllocVal) var();
  }

  @Override
  public String toString() {
    return "ForwardQuery: " + super.toString();
  }
}
