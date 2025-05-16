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
package boomerang.scope.sootup.jimple;

import boomerang.scope.Val;
import boomerang.scope.ValWithFalseVariable;
import java.util.Objects;
import sootup.core.jimple.basic.Value;

public class JimpleUpDoubleVal extends JimpleUpVal implements ValWithFalseVariable {

  private final Val falseVal;

  public JimpleUpDoubleVal(Value value, JimpleUpMethod method, Val falseVal) {
    super(value, method);

    this.falseVal = falseVal;
  }

  @Override
  public Val getFalseVariable() {
    return falseVal;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleUpDoubleVal that = (JimpleUpDoubleVal) o;
    return Objects.equals(falseVal, that.falseVal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), falseVal);
  }

  @Override
  public String toString() {
    return "InstanceOf " + falseVal + " " + super.toString();
  }
}
