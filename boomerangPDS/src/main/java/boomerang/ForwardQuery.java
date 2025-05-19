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
package boomerang;

import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph;
import java.util.Objects;

public class ForwardQuery extends Query {

  private final AllocVal allocVal;

  public ForwardQuery(ControlFlowGraph.Edge edge, AllocVal allocVal) {
    super(edge, allocVal);

    this.allocVal = allocVal;
  }

  public AllocVal getAllocVal() {
    return allocVal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ForwardQuery that = (ForwardQuery) o;
    return Objects.equals(allocVal, that.allocVal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), allocVal);
  }

  @Override
  public String toString() {
    return "ForwardQuery: " + allocVal;
  }
}
