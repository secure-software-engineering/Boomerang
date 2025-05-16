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

import boomerang.scope.Field;
import boomerang.scope.Type;
import java.util.Objects;
import sootup.core.signatures.FieldSignature;
import sootup.java.core.views.JavaView;

public class JimpleUpField extends Field {

  private final FieldSignature delegate;
  private final JavaView view;

  public JimpleUpField(FieldSignature delegate, JavaView view) {
    this.delegate = delegate;
    this.view = view;
  }

  public FieldSignature getDelegate() {
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
    return new JimpleUpType(delegate.getType(), view);
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleUpField that = (JimpleUpField) o;
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.equals(delegate.getType(), that.delegate.getType())
        && Objects.equals(delegate.getName(), that.delegate.getName());
  }

  @Override
  public int hashCode() {
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.hash(super.hashCode(), delegate.getType(), delegate.getName());
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
