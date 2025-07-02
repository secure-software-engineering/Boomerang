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
package test.cases.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class LoopInContextRequesterTest {

  @Test
  public void loop() {
    ILoop c;
    c = new Loop1();
    c.loop();
  }

  public interface ILoop {
    void loop();
  }

  public class Loop1 implements ILoop {
    A a = new A();

    @Override
    public void loop() {
      if (Math.random() > 0.5) loop();
      AllocatedObject x = a.d;
      QueryMethods.queryFor(x);
    }
  }

  public class A {
    AllocatedObject d = new AllocatedObject() {};
    A f = new A();
  }
}
