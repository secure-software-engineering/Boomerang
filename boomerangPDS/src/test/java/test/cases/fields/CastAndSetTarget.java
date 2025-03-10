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
package test.cases.fields;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class CastAndSetTarget {

  @TestMethod
  public void setAndGet() {
    Container container = new Container();
    Object o1 = new Object();
    container.set(o1);
    AllocatedObject o2 = new Alloc();
    container.set(o2);
    AllocatedObject alias = container.get();
    QueryMethods.queryFor(alias);
  }

  private static class Container {
    AllocatedObject o;

    public void set(Object o1) {
      AllocatedObject var = (AllocatedObject) o1;
      this.o = var;
    }

    public AllocatedObject get() {
      return o;
    }
  }
}
