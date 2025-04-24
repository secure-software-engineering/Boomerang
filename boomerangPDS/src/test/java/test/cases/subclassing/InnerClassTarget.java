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
package test.cases.subclassing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InnerClassTarget {

  public static class Instance {
    public Object o = new SubclassingAlloc();

    public class Inner {
      public Object getOuter() {
        return Instance.this.o;
      }
    }
  }

  @TestMethod
  public void getFromInnerClassTest() {
    Instance instance = new Instance();
    Instance.Inner inner = instance.new Inner();
    Object outer = inner.getOuter();
    QueryMethods.queryFor(outer);
  }

  @TestMethod
  public void getFromInnerClass2Test() {
    Instance2 instance = new Instance2();
    Instance2.Inner inner = instance.new Inner();
    inner.setOuter();
    Object outer = inner.getOuter();
    QueryMethods.queryFor(outer);
  }

  private static class Instance2 {
    private Object o;

    private class Inner {
      private Object getOuter() {
        return Instance2.this.o;
      }

      private void setOuter() {
        Instance2.this.o = new AllocatedObject() {};
      }
    }
  }
}
