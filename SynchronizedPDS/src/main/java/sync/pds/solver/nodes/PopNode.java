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
package sync.pds.solver.nodes;

import sync.pds.solver.SyncPDSSolver.PDSSystem;
import wpds.interfaces.State;

public class PopNode<Location> implements State {

  private final PDSSystem system;
  private final Location location;

  public PopNode(Location location, PDSSystem system) {
    this.system = system;
    this.location = location;
  }

  public PDSSystem system() {
    return system;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((location == null) ? 0 : location.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    PopNode other = (PopNode) obj;
    if (location == null) {
      return other.location == null;
    } else return location.equals(other.location);
  }

  public Location location() {
    return location;
  }

  @Override
  public String toString() {
    return "Pop " + location();
  }
}
