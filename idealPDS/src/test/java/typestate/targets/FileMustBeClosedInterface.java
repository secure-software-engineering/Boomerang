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
package typestate.targets;

import assertions.Assertions;
import test.TestMethod;
import typestate.targets.helper.File;

@SuppressWarnings("unused")
public class FileMustBeClosedInterface {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void mainTest() {
    File file = new File();
    FileMustBeClosedInterface.Flow flow =
        (staticallyUnknown()
            ? new FileMustBeClosedInterface.ImplFlow1()
            : new FileMustBeClosedInterface.ImplFlow2());
    flow.flow(file);
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInAcceptingState(file);
  }

  @TestMethod
  public void otherTest() {
    File file = new File();
    if (staticallyUnknown()) {
      new ImplFlow1().flow(file);
      Assertions.mustBeInErrorState(file);
    } else {
      new ImplFlow2().flow(file);
      Assertions.mustBeInAcceptingState(file);
    }
    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  public static class ImplFlow1 implements FileMustBeClosedInterface.Flow {
    @Override
    public void flow(File file) {
      file.open();
    }
  }

  public static class ImplFlow2 implements FileMustBeClosedInterface.Flow {

    @Override
    public void flow(File file) {
      file.close();
    }
  }

  private interface Flow {
    void flow(File file);
  }
}
