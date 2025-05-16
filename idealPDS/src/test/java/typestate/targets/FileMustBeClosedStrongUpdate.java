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
import typestate.targets.helper.ObjectWithField;

@SuppressWarnings("unused")
public class FileMustBeClosedStrongUpdate {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void noStrongUpdatePossible() {
    File b = null;
    File a = new File();
    a.open();
    File e = new File();
    e.open();
    if (staticallyUnknown()) {
      b = a;
    } else {
      b = e;
    }
    b.close();
    Assertions.mayBeInErrorState(a);
    Assertions.mayBeInErrorState(e);
    Assertions.mustBeInAcceptingState(b);
  }

  @TestMethod
  public void aliasSensitive() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    File file = new File();
    file.open();
    a.field = file;
    File loadedFromAlias = b.field;
    loadedFromAlias.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(a.field);
  }
}
