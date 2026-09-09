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
import java.util.Objects;
import sootup.core.signatures.FieldSignature;

public class JimpleUpField implements Field {

  private final FieldSignature delegate;
  private final int hashCode;

  public JimpleUpField(FieldSignature delegate) {
    this.delegate = delegate;
    // Important: Do not include the declaring class because subclasses may access the field, too
    this.hashCode = Objects.hash(delegate.getType(), delegate.getName());
  }

  public FieldSignature getDelegate() {
    return delegate;
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean isInnerClassField() {
    return delegate.getDeclClassType().getFullyQualifiedName().contains("$");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleUpField that = (JimpleUpField) o;
    if (hashCode != that.hashCode) return false;
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.equals(delegate.getType(), that.delegate.getType())
        && Objects.equals(delegate.getName(), that.delegate.getName());
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
