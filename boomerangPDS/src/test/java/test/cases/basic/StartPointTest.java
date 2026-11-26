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
package test.cases.basic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

/** Where the analysis enters and leaves a method that has no identity statements. */
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class StartPointTest {

  @Test
  public void callIsTheFirstStatementOfAStaticMethod() {
    Object alias = delegate();
    QueryMethods.queryFor(alias);
  }

  // no identity statements: the call is the first statement, and the backward analysis enters
  // allocate() only on the edge from the call's predecessor, which the leading nop provides
  private static Object delegate() {
    return allocate();
  }

  private static Object allocate() {
    return new BasicAlloc();
  }
}
