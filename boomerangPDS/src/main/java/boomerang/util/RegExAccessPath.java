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
package boomerang.util;

import boomerang.scope.Field;
import boomerang.scope.Val;
import pathexpression.IRegEx;

public class RegExAccessPath {
  private final Val val;
  private final IRegEx<Field> fields;

  public RegExAccessPath(Val val, IRegEx<Field> fields) {
    this.val = val;
    this.fields = fields;
  }

  public Val getVal() {
    return val;
  }

  public IRegEx<Field> getFields() {
    return fields;
  }

  @Override
  public String toString() {
    return val + " " + fields.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fields == null) ? 0 : fields.hashCode());
    result = prime * result + ((val == null) ? 0 : val.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    RegExAccessPath other = (RegExAccessPath) obj;
    if (fields == null) {
      if (other.fields != null) return false;
    } else if (!fields.equals(other.fields)) return false;
    if (val == null) {
      return other.val == null;
    } else return val.equals(other.val);
  }
}
