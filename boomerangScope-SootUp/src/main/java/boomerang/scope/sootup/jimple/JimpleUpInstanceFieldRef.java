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
import boomerang.scope.InstanceFieldRef;
import boomerang.scope.Val;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.Arrays;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.java.core.JavaSootField;

public class JimpleUpInstanceFieldRef implements InstanceFieldRef {

  private final JInstanceFieldRef delegate;
  private final JimpleUpMethod method;

  public JimpleUpInstanceFieldRef(JInstanceFieldRef delegate, JimpleUpMethod method) {
    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public Val getBase() {
    return new JimpleUpVal(delegate.getBase(), method);
  }

  @Override
  public Field getField() {
    JavaSootField field =
        SootUpFrameworkScope.getInstance().getSootField(delegate.getFieldSignature());
    return new JimpleUpField(field);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpInstanceFieldRef other = (JimpleUpInstanceFieldRef) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
