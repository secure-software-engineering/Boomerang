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
package test.cases.multiqueries;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.MultiQueryTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.AllocatedObject2;

@ExtendWith(MultiQueryTestRunnerInterceptor.class)
public class MultiQueryTest {

  @Test
  public void twoQueriesTest() {
    Object alloc1 = new Alloc1();
    Object alias1 = new Alloc2();
    Object query = alloc1;
    QueryMethods.queryFor1(query, AllocatedObject.class);
    QueryMethods.queryFor2(alias1, AllocatedObject2.class);
  }

  @Test
  public void withFieldsTest() {
    Alloc1 alloc1 = new Alloc1();
    Object alias1 = new Alloc2();
    Alloc1 alias = alloc1;
    alias.field = alias1;
    Object query = alloc1.field;
    QueryMethods.queryFor1(alias, AllocatedObject.class);
    QueryMethods.queryFor2(query, AllocatedObject2.class);
  }

  private static class Alloc1 implements AllocatedObject {
    Object field = new Object();
  }

  private static class Alloc2 implements AllocatedObject2 {}
}
