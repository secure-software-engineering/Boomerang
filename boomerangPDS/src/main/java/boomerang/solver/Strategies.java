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
package boomerang.solver;

import boomerang.arrays.ArrayHandlingStrategy;
import boomerang.arrays.ArrayIndexInsensitiveStrategy;
import boomerang.arrays.ArrayIndexSensitiveStrategy;
import boomerang.arrays.IgnoreArrayStrategy;
import boomerang.scope.Field;
import boomerang.scope.Statement;
import boomerang.staticfields.FlowSensitiveStaticFieldStrategy;
import boomerang.staticfields.IgnoreStaticFieldStrategy;
import boomerang.staticfields.SingletonStaticFieldStrategy;
import boomerang.staticfields.StaticFieldHandlingStrategy;
import com.google.common.collect.Multimap;

public class Strategies {

  private final StaticFieldHandlingStrategy staticFieldHandlingStrategy;
  private final ArrayHandlingStrategy arrayHandlingStrategy;

  public enum StaticFieldStrategy {
    IGNORE,
    SINGLETON,
    FLOW_SENSITIVE
  }

  public enum ArrayStrategy {
    DISABLED,
    INDEX_INSENSITIVE,
    INDEX_SENSITIVE
  }

  public Strategies(
      StaticFieldStrategy staticFieldStrategy,
      ArrayStrategy arrayStrategy,
      AbstractBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements) {
    switch (staticFieldStrategy) {
      case IGNORE:
        staticFieldHandlingStrategy = new IgnoreStaticFieldStrategy();
        break;
      case SINGLETON:
        staticFieldHandlingStrategy =
            new SingletonStaticFieldStrategy(solver, fieldLoadStatements, fieldStoreStatements);
        break;
      case FLOW_SENSITIVE:
      default:
        staticFieldHandlingStrategy = new FlowSensitiveStaticFieldStrategy();
        break;
    }
    switch (arrayStrategy) {
      case DISABLED:
        arrayHandlingStrategy = new IgnoreArrayStrategy();
        break;
      case INDEX_INSENSITIVE:
        arrayHandlingStrategy = new ArrayIndexInsensitiveStrategy();
        break;
      case INDEX_SENSITIVE:
      default:
        arrayHandlingStrategy = new ArrayIndexSensitiveStrategy();
        break;
    }
  }

  public StaticFieldHandlingStrategy getStaticFieldStrategy() {
    return staticFieldHandlingStrategy;
  }

  public ArrayHandlingStrategy getArrayHandlingStrategy() {
    return arrayHandlingStrategy;
  }
}
