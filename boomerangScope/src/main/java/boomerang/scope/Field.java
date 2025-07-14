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
package boomerang.scope;

import de.fraunhofer.iem.Empty;
import de.fraunhofer.iem.Location;
import de.fraunhofer.iem.wildcard.ExclusionWildcard;
import de.fraunhofer.iem.wildcard.Wildcard;
import java.util.Objects;

public class Field implements Location {

  private final String rep;

  private Field(String rep) {
    this.rep = rep;
  }

  protected Field() {
    this.rep = null;
  }

  public static Field wildcard() {
    return new WildcardField();
  }

  public static Field empty() {
    return new EmptyField("{}");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Field field = (Field) o;
    return Objects.equals(rep, field.rep);
  }

  @Override
  public int hashCode() {
    return Objects.hash(rep);
  }

  @Override
  public String toString() {
    return rep;
  }

  private static class EmptyField extends Field implements Empty {
    public EmptyField(String rep) {
      super(rep);
    }
  }

  public static Field string(String key) {
    return new Field(key);
  }

  public static Field epsilon() {
    return new EmptyField("eps_f");
  }

  public static Field array(int index) {
    if (index == -1) return new ArrayField();
    return new ArrayField(index);
  }

  private static class WildcardField extends Field implements Wildcard {
    public WildcardField() {
      super("*");
    }
  }

  private static class ExclusionWildcardField extends Field implements ExclusionWildcard<Field> {
    private final Field excludes;

    public ExclusionWildcardField(Field excl) {
      super();
      this.excludes = excl;
    }

    @Override
    public Field excludes() {
      return excludes;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      ExclusionWildcardField that = (ExclusionWildcardField) o;
      return Objects.equals(excludes, that.excludes);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), excludes);
    }

    @Override
    public String toString() {
      return "not " + excludes;
    }
  }

  public static class ArrayField extends Field {

    private final int index;

    private ArrayField(int index) {
      super("array");
      if (index < 0) {
        throw new RuntimeException("Illegal Array field construction");
      }
      this.index = index;
    }

    public ArrayField() {
      super("array");

      this.index = -1;
    }

    @Override
    public boolean accepts(Location other) {
      if (this.equals(other)) return true;
      return index == -1 && other instanceof ArrayField;
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
    public String toString() {
      return super.toString() + (index == -1 ? " ANY_INDEX" : "Index: " + index);
    }
  }

  public static Field exclusionWildcard(Field exclusion) {
    return new ExclusionWildcardField(exclusion);
  }

  @Override
  public boolean accepts(Location other) {
    return this.equals(other);
  }

  public boolean isPredefinedField() {
    return true;
  }

  public boolean isInnerClassField() {
    return false;
  }

  public Type getType() {
    return null;
  }

  public String getName() {
    return rep;
  }
}
