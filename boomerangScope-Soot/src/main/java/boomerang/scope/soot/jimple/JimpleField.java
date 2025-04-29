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
import boomerang.scope.Type;
import java.util.Objects;
import soot.Scene;
import soot.SootFieldRef;

public class JimpleField extends Field {

  private final Scene scene;
  private final SootFieldRef delegate;

  public JimpleField(Scene scene, SootFieldRef delegate) {
    this.scene = scene;
    this.delegate = delegate;
  }

  public SootFieldRef getDelegate() {
    return this.delegate;
  }

  @Override
  public boolean isPredefinedField() {
    return false;
  }

  @Override
  public boolean isInnerClassField() {
    return this.delegate.name().contains("$");
  }

  @Override
  public Type getType() {
    return new JimpleType(scene, delegate.type());
  }

  @Override
  public String getName() {
    return delegate.name();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleField that = (JimpleField) o;
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.equals(delegate.type(), that.delegate.type())
        && Objects.equals(delegate.name(), that.delegate.name());
  }

  @Override
  public int hashCode() {
    // Important: Do not include the declaring class because subclasses may access the field, too
    return Objects.hash(super.hashCode(), delegate.type(), delegate.name());
  }

  @Override
  public String toString() {
    return delegate.name();
  }
}
