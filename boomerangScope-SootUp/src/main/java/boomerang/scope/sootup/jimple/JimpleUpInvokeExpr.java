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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import boomerang.scope.ValCollection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import sootup.core.jimple.common.expr.AbstractInstanceInvokeExpr;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.expr.JSpecialInvokeExpr;
import sootup.core.jimple.common.expr.JStaticInvokeExpr;

public class JimpleUpInvokeExpr implements InvokeExpr {

  private final AbstractInvokeExpr delegate;
  private final JimpleUpMethod method;

  private List<Val> argsCache;

  public JimpleUpInvokeExpr(AbstractInvokeExpr delegate, JimpleUpMethod method) {
    this.delegate = delegate;
    this.method = method;
  }

  @Override
  public Val getArg(int index) {
    if (delegate.getArg(index) == null) {
      return ValCollection.zero();
    }
    return new JimpleUpVal(delegate.getArg(index), method);
  }

  @Override
  public List<Val> getArgs() {
    if (argsCache == null) {
      argsCache = new ArrayList<>();

      for (int i = 0; i < delegate.getArgCount(); i++) {
        argsCache.add(getArg(i));
      }
    }
    return argsCache;
  }

  @Override
  public boolean isInstanceInvokeExpr() {
    return delegate instanceof AbstractInstanceInvokeExpr;
  }

  @Override
  public Val getBase() {
    assert isInstanceInvokeExpr();
    AbstractInstanceInvokeExpr iie = (AbstractInstanceInvokeExpr) delegate;

    return new JimpleUpVal(iie.getBase(), method);
  }

  @Override
  public DeclaredMethod getDeclaredMethod() {
    return new JimpleUpDeclaredMethod(this, delegate.getMethodSignature(), method);
  }

  @Override
  public boolean isSpecialInvokeExpr() {
    return delegate instanceof JSpecialInvokeExpr;
  }

  @Override
  public boolean isStaticInvokeExpr() {
    return delegate instanceof JStaticInvokeExpr;
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

    JimpleUpInvokeExpr other = (JimpleUpInvokeExpr) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
