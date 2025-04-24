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
package boomerang.scope.soot.sparse.aliasaware;

import soot.AbstractUnit;
import soot.UnitPrinter;
import soot.Value;

public class DefinedOutside extends AbstractUnit {

  Value value;

  public DefinedOutside(Value value) {
    this.value = value;
  }

  public Value getValue() {
    return value;
  }

  @Override
  public Object clone() {
    return null;
  }

  @Override
  public boolean fallsThrough() {
    return false;
  }

  @Override
  public boolean branches() {
    return false;
  }

  @Override
  public void toString(UnitPrinter unitPrinter) {}
}
