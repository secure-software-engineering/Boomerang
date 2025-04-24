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
public class SootSceneSetup {

  @TestMethod
  public void simple() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @TestMethod
  public void aliasSimple() {
    File file = new File();
    File alias = file;
    alias.open();
    Assertions.mustBeInErrorState(file);
    Assertions.mustBeInErrorState(alias);
  }
}
