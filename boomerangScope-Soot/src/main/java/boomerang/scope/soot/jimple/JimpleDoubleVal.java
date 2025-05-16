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
package boomerang.scope.soot.jimple;

import boomerang.scope.Val;
import boomerang.scope.ValWithFalseVariable;
import java.util.Objects;
import soot.Value;

// TODO May be removed
public class JimpleDoubleVal extends JimpleVal implements ValWithFalseVariable {

  private final Val falseVariable;

  public JimpleDoubleVal(Value v, JimpleMethod m, Val instanceofValue) {
    super(v, m);
    this.falseVariable = instanceofValue;
  }

  @Override
  public Val getFalseVariable() {
    return falseVariable;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleDoubleVal that = (JimpleDoubleVal) o;
    return Objects.equals(falseVariable, that.falseVariable);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), falseVariable);
  }

  @Override
  public String toString() {
    return "InstanceOf " + falseVariable + " " + super.toString();
  }
}
