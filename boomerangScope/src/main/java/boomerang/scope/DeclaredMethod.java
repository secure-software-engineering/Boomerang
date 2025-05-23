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

import boomerang.utils.MethodWrapper;
import java.util.List;
import java.util.Objects;

public abstract class DeclaredMethod {

  private final InvokeExpr inv;

  public DeclaredMethod(InvokeExpr inv) {
    this.inv = inv;
  }

  public abstract String getSubSignature();

  public abstract String getName();

  public abstract boolean isConstructor();

  public abstract String getSignature();

  public abstract WrappedClass getDeclaringClass();

  public abstract List<Type> getParameterTypes();

  public abstract Type getParameterType(int index);

  public abstract Type getReturnType();

  public abstract MethodWrapper toMethodWrapper();

  public InvokeExpr getInvokeExpr() {
    return inv;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeclaredMethod that = (DeclaredMethod) o;
    return Objects.equals(inv, that.inv);
  }

  @Override
  public int hashCode() {
    return Objects.hash(inv);
  }

  @Override
  public String toString() {
    return inv.toString();
  }
}
