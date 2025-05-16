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
package boomerang.results;

import boomerang.Query;
import boomerang.scope.Method;
import java.util.Collection;
import java.util.Set;

public class QueryResults {
  private final Query query;
  private final Collection<Method> visitedMethods;
  private final Collection<AffectedLocation> affectedLocations;
  private final boolean timedout;

  public QueryResults(
      Query query, Set<AffectedLocation> npes, Set<Method> visMethod, boolean timedout) {
    this.query = query;
    this.visitedMethods = visMethod;
    this.affectedLocations = npes;
    this.timedout = timedout;
  }

  public Query getQuery() {
    return query;
  }

  public Collection<Method> getVisitedMethods() {
    return visitedMethods;
  }

  public Collection<AffectedLocation> getAffectedLocations() {
    return affectedLocations;
  }

  public boolean isTimedout() {
    return timedout;
  }
}
