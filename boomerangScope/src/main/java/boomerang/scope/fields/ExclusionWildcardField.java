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

import boomerang.scope.Field;
import de.fraunhofer.iem.wildcard.ExclusionWildcard;
import java.util.Objects;

public class ExclusionWildcardField extends PredefinedField implements ExclusionWildcard<Field> {

  private final Field excludedField;

  private ExclusionWildcardField(Field excludedField) {
    super("not " + excludedField.getName());

    this.excludedField = excludedField;
  }

  public static ExclusionWildcardField getInstance(Field excludedField) {
    return new ExclusionWildcardField(excludedField);
  }

  @Override
  public Field excludes() {
    return excludedField;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    ExclusionWildcardField that = (ExclusionWildcardField) o;
    return Objects.equals(excludedField, that.excludedField);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), excludedField);
  }
}
