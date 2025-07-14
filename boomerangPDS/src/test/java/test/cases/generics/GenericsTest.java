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
package test.cases.generics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class GenericsTest {

  @Test
  public void genericFieldAccess() {
    GenericClass<GenericType> c = new GenericClass<>();
    GenericType genType = new GenericType();
    c.setField(genType);
    GenericType query = c.getField();
    QueryMethods.queryFor(query);
  }

  @Test
  public void genericFieldAccessWrapped() {
    WrappedGenericClass<GenericType> c = new WrappedGenericClass<>();
    GenericType genType = new GenericType();
    c.setField(genType);
    GenericType query = c.getField();
    QueryMethods.queryFor(query);
  }

  public static class GenericClass<T> {
    T field;

    public void setField(T t) {
      field = t;
    }

    public T getField() {
      return field;
    }
  }

  public static class GenericType implements AllocatedObject {}

  public static class WrappedGenericClass<T> {
    GenericClass<T> gen = new GenericClass<>();

    public void setField(T t) {
      gen.setField(t);
    }

    public T getField() {
      return gen.getField();
    }
  }
}
