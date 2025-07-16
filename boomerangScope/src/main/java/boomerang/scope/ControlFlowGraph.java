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
package boomerang.scope;

import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.List;

public interface ControlFlowGraph {

  Collection<Statement> getStartPoints();

  Collection<Statement> getEndPoints();

  Collection<Statement> getSuccsOf(Statement curr);

  Collection<Statement> getPredsOf(Statement curr);

  List<Statement> getStatements();

  class Edge extends Pair<Statement, Statement> implements Location {
    public Edge(Statement start, Statement target) {
      super(start, target);
      if (!start.equals(EpsilonStatement.getInstance())
          && !start.getMethod().equals(target.getMethod())) {
        throw new RuntimeException("Illegal Control Flow Graph Edge constructed");
      }
    }

    public Statement getStart() {
      return getX();
    }

    public Statement getTarget() {
      return getY();
    }

    public Method getMethod() {
      return getStart().getMethod();
    }

    public static Edge epsilon() {
      return new Edge(EpsilonStatement.getInstance(), EpsilonStatement.getInstance());
    }

    @Override
    public String toString() {
      return getStart() + " -> " + getTarget();
    }

    @Override
    public boolean accepts(Location other) {
      return this.equals(other);
    }
  }
}
