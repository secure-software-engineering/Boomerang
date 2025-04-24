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
package sparse;

import boomerang.scope.Method;
import boomerang.scope.Statement;
import sparse.eval.PropagationCounter;

public interface SparsificationStrategy<M extends Method, S extends Statement> {

  SparseCFGCache<M, S> getInstance(boolean ignoreAfterQuery);

  PropagationCounter getCounter();

  SparsificationStrategy<Method, Statement> NONE = new NoSparsificationStrategy();

  class NoSparsificationStrategy implements SparsificationStrategy<Method, Statement> {
    @Override
    public SparseCFGCache<Method, Statement> getInstance(boolean ignoreAfterQuery) {
      // TODO [ms]: not used in code (when commented code is re-enabled)
      throw new UnsupportedOperationException("not implemented yet.");
    }

    @Override
    public PropagationCounter getCounter() {
      return new PropagationCounter();
    }
  }
}
