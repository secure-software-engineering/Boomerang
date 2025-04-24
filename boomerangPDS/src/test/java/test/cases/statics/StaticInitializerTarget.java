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
public class StaticInitializerTarget {

  private static final Object alloc = new StaticsAlloc();

  @TestMethod
  public void doubleSingleton() {
    Object alias = alloc;
    QueryMethods.queryFor(alias);
  }
}
