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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class MeetPOITest {

  @Test
  public void wrappedAlloc() {
    A e = new A();
    A g = e;
    wrapper(g);
    C h = e.b.c;
    QueryMethods.queryFor(h);
  }

  private void wrapper(A g) {
    alloc(g);
  }

  private void alloc(A g) {
    g.b.c = new C();
  }

  public class A {
    B b = new B();
  }

  public class B {
    C c;
  }

  public class C implements AllocatedObject {
    String g;
  }
}
