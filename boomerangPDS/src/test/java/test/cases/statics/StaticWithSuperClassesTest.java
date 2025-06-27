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
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@Disabled("Static fields are not handled correctly")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class StaticWithSuperClassesTest {

  @Test
  public void simple() {
    List list = new List();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class List {

    private static final Object elementData = new StaticsAlloc();

    public Object get() {
      return elementData;
    }
  }

  @Test
  public void superClass() {
    MyList list = new MyList();
    Object o = list.get();
    QueryMethods.queryForAndNotEmpty(o);
  }

  private static class MyList extends List {

    private static final Object elementData2 = new StaticsAlloc();

    public Object get() {
      return elementData2;
    }
  }
}
