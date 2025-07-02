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
package test.cases.fields;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class BasicFieldTest {

  @Test
  public void basicFieldReadAndWriteTest() {
    ClassWithField c = new ClassWithField();
    FieldAlloc alloc = new FieldAlloc();

    c.field = alloc;
    FieldAlloc query = c.field;

    QueryMethods.queryFor(query);
  }

  @Test
  public void basicFieldGetAndSetTest() {
    ClassWithField c = new ClassWithField();
    FieldAlloc alloc = new FieldAlloc();

    c.setField(alloc);
    FieldAlloc query = c.getField();

    QueryMethods.queryFor(query);
  }

  @Disabled
  @Test
  public void nestedFieldReadAndWriteTest() {
    ClassWithNestedFields c = new ClassWithNestedFields();
    FieldAlloc alloc = new FieldAlloc();

    c.c.field = alloc;
    FieldAlloc query = c.c.field;

    QueryMethods.queryFor(query);
  }

  private static class ClassWithField {
    FieldAlloc field;

    public FieldAlloc getField() {
      return field;
    }

    public void setField(FieldAlloc alloc) {
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
