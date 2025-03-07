package boomerang.util;

import boomerang.scope.Field;
import boomerang.scope.Val;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Set;
import java.util.StringJoiner;

public class AccessPath {
  private final Val val;
  private final Collection<Field> fieldChain;

  public AccessPath(Val value) {
    this.val = value;
    this.fieldChain = Lists.newArrayList();
  }

  public AccessPath(Val value, Field field) {
    this.val = value;
    this.fieldChain = Lists.newArrayList(field);
  }

  public AccessPath(Val value, Collection<Field> fields) {
    this.val = value;
    this.fieldChain = fields;
  }

  @Override
  public String toString() {
    return val.toString()
        + (fieldChain.isEmpty() ? "" : fieldChain.toString())
        + (isOverApproximated() ? "*" : "");
  }

  public boolean isOverApproximated() {
    return fieldChain instanceof Set;
  }

  public Val getBase() {
    return this.val;
  }

  public Collection<Field> getFields() {
    return fieldChain;
  }

  /**
   * Return the AccessPath as a compact representation. For example, a path with a base 'x' and no
   * fields becomes 'x', a base 'y' and the fields [f,g] becomes 'y.f.g'
   *
   * @return the formatted AccessPath
   */
  public String toCompactString() {
    StringJoiner fieldJoiner = new StringJoiner(".");
    fieldChain.forEach(field -> fieldJoiner.add(field.toString()));

    return val.getVariableName() + (fieldJoiner.toString().isEmpty() ? "" : ".") + fieldJoiner;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((fieldChain == null) ? 0 : fieldChain.hashCode());
    result = prime * result + ((val == null) ? 0 : val.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    AccessPath other = (AccessPath) obj;
    if (fieldChain == null) {
      if (other.fieldChain != null) {
        return false;
      }
    } else if (!fieldChain.equals(other.fieldChain)) {
      return false;
    }
    if (val == null) {
      return other.val == null;
    } else return val.equals(other.val);
  }
}
