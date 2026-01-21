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
package boomerang.scope.soot.jimple;

import boomerang.scope.Field;
import java.util.Objects;
import soot.SootFieldRef;

public class JimpleField implements Field {

  private final SootFieldRef delegate;

  public JimpleField(SootFieldRef delegate) {
    this.delegate = delegate;
  }

  public SootFieldRef getDelegate() {
    return this.delegate;
  }

  @Override
  public String getName() {
    return delegate.name();
  }

  @Override
  public boolean isInnerClassField() {
    return delegate.declaringClass().getName().contains("$");
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleField that = (JimpleField) o;
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.equals(delegate.type(), that.delegate.type())
        && Objects.equals(delegate.name(), that.delegate.name());
  }

  @Override
  public int hashCode() {
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.hash(delegate.type(), delegate.name());
  }

  @Override
  public String toString() {
    return delegate.name();
  }
}
