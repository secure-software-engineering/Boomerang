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

@Disabled(
    "After discovering the allocation site, the forward flow does not reach the query statement")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class StaticFieldAtEntryPointToClinitTest {

  /*protected BoomerangOptions createBoomerangOptions() {
    return BoomerangOptions.builder()
        .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
        .enableTrackStaticFieldAtEntryPointToClinit(true)
        .build();
  }*/

  public static class ClassWithStaticField {
    private static final StaticsAlloc alloc = new StaticsAlloc();
  }

  @Test
  public void staticFieldAtEntryPointTest() {
    StaticsAlloc loadedAlloc = ClassWithStaticField.alloc;
    QueryMethods.queryFor(loadedAlloc);
  }

  public static void loadStaticField() {
    StaticsAlloc loadedAlloc = ClassWithStaticField.alloc;
    QueryMethods.queryFor(loadedAlloc);
  }

  @Test
  public void staticFieldAtEntryPointWithLoadTest() {
    loadStaticField();
  }
}
