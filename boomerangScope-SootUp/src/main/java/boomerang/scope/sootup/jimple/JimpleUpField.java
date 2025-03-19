/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.sootup.jimple;

import boomerang.scope.Field;
import boomerang.scope.Type;
import java.util.Arrays;
import sootup.java.core.JavaSootField;

public class JimpleUpField extends Field {

  private final JavaSootField delegate;

  public JimpleUpField(JavaSootField delegate) {
    this.delegate = delegate;
  }

  public JavaSootField getDelegate() {
    return delegate;
  }

  @Override
  public boolean isPredefinedField() {
    return false;
  }

  @Override
  public boolean isInnerClassField() {
    return delegate.getName().contains("$");
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType());
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpField other = (JimpleUpField) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.getName();
  }
}
