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

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class SingletonTarget {

  private static StaticsAlloc instance;

  @TestMethod
  public void doubleSingleton() {
    StaticsAlloc singleton = i();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void doubleSingletonDirect() {
    StaticsAlloc singleton = objectGetter.getG();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
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
