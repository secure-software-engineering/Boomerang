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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class InnerClass2Test {

  @Test
  public void run() {
    Object alloc = new Allocation();
    String cmd = System.getProperty("");
    if (cmd != null) {
      alloc = new Allocation();
    }
    InnerClass2Test outer = new InnerClass2Test();
    outer.doThings(alloc);
  }

  public void doThings(final Object name) {
    class MyInner {
      public void seeOuter() {
        QueryMethods.queryFor(name);
      }
    }
    MyInner inner = new MyInner();
    inner.seeOuter();
  }

  private static class Allocation implements AllocatedObject {}
}
