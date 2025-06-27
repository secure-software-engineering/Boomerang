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
package test.cases.statics;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import test.core.QueryMethods;

public class SingletonTest {

  @Disabled("Static fields are not handled correctly")
  @Test
  public void doubleSingleton() {
    StaticsAlloc singleton = i();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @Test
  public void doubleSingletonDirect() {
    StaticsAlloc singleton = objectGetter.getG();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @Disabled("Static fields are not handled correctly")
  @Test
  public void singletonDirect() {
    StaticsAlloc singleton = alloc;
    QueryMethods.queryFor(singleton);
  }

  public static StaticsAlloc i() {
    GlobalObjectGetter getter = objectGetter;
    StaticsAlloc allocation = getter.getG();
    return allocation;
  }

  public interface GlobalObjectGetter {
    StaticsAlloc getG();

    void reset();
  }

  private static StaticsAlloc alloc;
  private static final GlobalObjectGetter objectGetter =
      new GlobalObjectGetter() {

        StaticsAlloc instance = new StaticsAlloc();

        public StaticsAlloc getG() {
          return instance;
        }

        public void reset() {
          instance = new StaticsAlloc();
        }
      };
}
