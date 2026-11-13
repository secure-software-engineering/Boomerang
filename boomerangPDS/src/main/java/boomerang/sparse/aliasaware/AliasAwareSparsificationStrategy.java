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
package boomerang.sparse.aliasaware;

import boomerang.options.BoomerangOptions;
import boomerang.sparse.SparseCFGCache;
import boomerang.sparse.SparsificationStrategy;

/**
 * See {@link SparsificationStrategy#ALIAS_AWARE}. Respects {@link
 * BoomerangOptions#ignoreSparsificationAfterQuery()}.
 */
public class AliasAwareSparsificationStrategy implements SparsificationStrategy {

  @Override
  public SparseCFGCache createCache(BoomerangOptions options) {
    return new AliasAwareSparseCFGCache(options.ignoreSparsificationAfterQuery());
  }

  @Override
  public String getName() {
    return "aliasaware";
  }

  @Override
  public String toString() {
    return getName();
  }
}
