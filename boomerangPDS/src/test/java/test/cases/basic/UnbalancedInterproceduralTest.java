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

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class UnbalancedInterproceduralTest {

  @Test
  public void unbalancedCreation() {
    BasicAlloc alias1 = create();
    BasicAlloc query = alias1;
    QueryMethods.queryFor(query);
  }

  @Test
  public void doubleUnbalancedCreation() {
    BasicAlloc alias1 = wrappedCreate();
    BasicAlloc query = alias1;
    QueryMethods.queryFor(query);
  }

  private BasicAlloc wrappedCreate() {
    return create();
  }

  private BasicAlloc create() {
    return new BasicAlloc();
  }
}
