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
public class TypeChangeTarget {

  @TestMethod
  public void returnValue() {
    D f = new D();
    Object amIThere = f.getField();
    QueryMethods.queryFor(amIThere);
  }

  @TestMethod
  public void doubleReturnValue() {
    D f = new D();
    Object t = f.getDoubleField();
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void returnValueAndBackCast() {
    D f = new D();
    Object t = f.getField();
    AllocatedObject u = (AllocatedObject) t;
    QueryMethods.queryFor(u);
  }

  public static class D {
    FieldAlloc f = new FieldAlloc();
    D d = new D();

    public Object getField() {
      FieldAlloc varShouldBeThere = this.f;
      return varShouldBeThere;
    }

    public Object getDoubleField() {
      return d.getField();
    }
  }
}
