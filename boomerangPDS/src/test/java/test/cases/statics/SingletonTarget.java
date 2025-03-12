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
package test.cases.statics;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class SingletonTarget {

  private static Alloc instance;

  @TestMethod
  public void doubleSingleton() {
    Alloc singleton = i();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void doubleSingletonDirect() {
    Alloc singleton = objectGetter.getG();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void singletonDirect() {
    Alloc singleton = alloc;
    QueryMethods.queryFor(singleton);
  }

  public static Alloc i() {
    GlobalObjectGetter getter = objectGetter;
    Alloc allocation = getter.getG();
    return allocation;
  }

  public interface GlobalObjectGetter {
    Alloc getG();

    void reset();
  }

  private static Alloc alloc;
  private static final GlobalObjectGetter objectGetter =
      new GlobalObjectGetter() {

        Alloc instance = new Alloc();

        public Alloc getG() {
          return instance;
        }

        public void reset() {
          instance = new Alloc();
        }
      };
}
