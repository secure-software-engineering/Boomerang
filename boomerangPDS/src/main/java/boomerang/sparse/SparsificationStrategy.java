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
package boomerang.sparse;

import boomerang.options.BoomerangOptions;
import boomerang.sparse.aliasaware.AliasAwareSparsificationStrategy;
import boomerang.sparse.typebased.TypeBasedSparsificationStrategy;
import java.util.Locale;

/**
 * Strategy that decides whether and how Boomerang sparsifies the control flow graphs it propagates
 * along. A sparse control flow graph only contains the statements that are relevant for a specific
 * query, so the solvers can skip the remaining statements during the normal flow.
 *
 * <p>Each analysis instance creates its own {@link SparseCFGCache} via {@link
 * #createCache(BoomerangOptions)}. The predefined strategies are
 *
 * <ul>
 *   <li>{@link #NONE}: no sparsification, the solvers propagate along the original control flow
 *       graph
 *   <li>{@link #TYPE_BASED}: keeps the statements that involve values whose type may alias with the
 *       type of the query variable
 *   <li>{@link #ALIAS_AWARE}: keeps the statements that involve the query variable and the values
 *       it may alias with
 * </ul>
 *
 * Custom strategies can be configured by implementing this interface.
 */
public interface SparsificationStrategy {

  SparsificationStrategy NONE = new NoSparsificationStrategy();
  SparsificationStrategy TYPE_BASED = new TypeBasedSparsificationStrategy();
  SparsificationStrategy ALIAS_AWARE = new AliasAwareSparsificationStrategy();

  /**
   * Creates the cache that builds and stores the sparse control flow graphs for one analysis
   * instance.
   *
   * @param options the options of the analysis
   * @return a new cache
   */
  SparseCFGCache createCache(BoomerangOptions options);

  /**
   * @return a short name that identifies the strategy
   */
  String getName();

  /**
   * Resolves one of the predefined strategies by its name (case-insensitive).
   *
   * @param name one of "none", "typebased" or "aliasaware"
   * @return the corresponding strategy
   */
  static SparsificationStrategy fromName(String name) {
    switch (name.toLowerCase(Locale.ROOT)) {
      case "none":
        return NONE;
      case "typebased":
        return TYPE_BASED;
      case "aliasaware":
        return ALIAS_AWARE;
      default:
        throw new IllegalArgumentException(
            "Unknown sparsification strategy: "
                + name
                + " (valid values: none, typebased, aliasaware)");
    }
  }
}
