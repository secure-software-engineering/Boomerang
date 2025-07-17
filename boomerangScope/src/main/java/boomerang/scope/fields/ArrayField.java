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
package boomerang.scope.fields;

import de.fraunhofer.iem.Location;
import java.util.Objects;

public class ArrayField extends PredefinedField {

  private final int index;

  private ArrayField(int index) {
    super("array");

    if (index < 0) {
      this.index = -1;
    } else {
      this.index = index;
    }
  }

  public static ArrayField getInstance(int index) {
    return new ArrayField(index);
  }

  public int getIndex() {
    return index;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ArrayField that = (ArrayField) o;
    return index == that.index;
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), index);
  }

  @Override
  public boolean accepts(Location other) {
    if (this.equals(other)) return true;
    return index == -1 && other instanceof ArrayField;
  }

  @Override
  public String toString() {
    return super.toString() + (index == -1 ? " ANY_INDEX" : "Index: " + index);
  }
}
