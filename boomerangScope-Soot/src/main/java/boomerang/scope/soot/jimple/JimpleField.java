/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.soot.jimple;

import boomerang.scope.Field;
import java.util.Objects;
import soot.SootField;

public class JimpleField extends Field {

  private final SootField delegate;

  public JimpleField(SootField delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean isInnerClassField() {
    return this.delegate.getName().contains("$");
  }

  public SootField getDelegate() {
    return this.delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleField that = (JimpleField) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate);
  }

  @Override
  public String toString() {
    return delegate.getName();
  }
}
