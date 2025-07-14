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
package test.cases.synchronizd;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class BlockTest {

  private Object field;

  @Test
  public void block() {
    synchronized (field) {
      AllocatedObject o = new SynchronizedAlloc();
      QueryMethods.queryFor(o);
    }
  }

  @Test
  public void block2() {
    set();
    synchronized (field) {
      Object o = field;
      QueryMethods.queryFor(o);
    }
  }

  private void set() {
    synchronized (field) {
      field = new SynchronizedAlloc();
    }
  }
}
