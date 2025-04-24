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

import boomerang.scope.Method;
import boomerang.scope.Val;
import boomerang.scope.ValWithFalseVariable;
import java.util.Arrays;
import sootup.core.jimple.basic.Value;

public class JimpleUpDoubleVal extends JimpleUpVal implements ValWithFalseVariable {

  private final Val falseVal;

  public JimpleUpDoubleVal(Value value, Method method, Val falseVal) {
    super(value, method);

    this.falseVal = falseVal;
  }

  @Override
  public Val getFalseVariable() {
    return falseVal;
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {super.hashCode(), falseVal});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpDoubleVal other = (JimpleUpDoubleVal) obj;
    if (falseVal == null) {
      return other.falseVal == null;
    } else return falseVal.equals(other.falseVal);
  }

  @Override
  public String toString() {
    return "InstanceOf " + falseVal + " " + super.toString();
  }
}
