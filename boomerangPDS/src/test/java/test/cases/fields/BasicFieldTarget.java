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

@SuppressWarnings("unused")
public class BasicFieldTarget {

  @TestMethod
  public void basicFieldReadAndWriteTest() {
    ClassWithField c = new ClassWithField();
    Alloc alloc = new Alloc();

    c.field = alloc;
    Alloc query = c.field;

    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void basicFieldGetAndSetTest() {
    ClassWithField c = new ClassWithField();
    Alloc alloc = new Alloc();

    c.setField(alloc);
    Alloc query = c.getField();

    QueryMethods.queryFor(query);
  }

  @TestMethod
  public void nestedFieldReadAndWriteTest() {
    ClassWithNestedFields c = new ClassWithNestedFields();
    Alloc alloc = new Alloc();

    c.c.field = alloc;
    Alloc query = c.c.field;

    QueryMethods.queryFor(query);
  }

  private static class ClassWithField {
    Alloc field;

    public Alloc getField() {
      return field;
    }

    public void setField(Alloc alloc) {
      this.field = alloc;
    }
  }

  private static class ClassWithNestedFields {
    ClassWithField c;

    public ClassWithField getC() {
      return c;
    }

    public void setC(ClassWithField c) {
      this.c = c;
    }
  }
}
