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
public class FluentInterface {

  @TestMethod
  public void fluentOpen() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
  }

  @TestMethod
  public void fluentOpenAndClose() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
    file = file.close();
    Assertions.mustBeInAcceptingState(file);
  }
}
