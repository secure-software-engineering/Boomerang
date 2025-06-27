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
public class CastAndSetTest {

  @Test
  public void setAndGet() {
    Container container = new Container();
    Object o1 = new Object();
    container.set(o1);
    AllocatedObject o2 = new FieldAlloc();
    container.set(o2);
    AllocatedObject alias = container.get();
    QueryMethods.queryFor(alias);
  }

  private static class Container {
    AllocatedObject o;

    public void set(Object o1) {
      AllocatedObject var = (AllocatedObject) o1;
      this.o = var;
    }

    public AllocatedObject get() {
      return o;
    }
  }
}
