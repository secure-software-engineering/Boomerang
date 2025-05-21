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

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import boomerang.scope.ValCollection;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

public class JimpleInvokeExpr implements InvokeExpr {

  private final soot.jimple.InvokeExpr delegate;
  private final JimpleMethod method;
  private ArrayList<Val> argCache;

  public JimpleInvokeExpr(soot.jimple.InvokeExpr ive, JimpleMethod method) {
    this.delegate = ive;
    this.method = method;
  }

  @Override
  public Val getArg(int index) {
    if (delegate.getArg(index) == null) {
      return ValCollection.zero();
    }
    return new JimpleVal(delegate.getArg(index), method);
  }

  @Override
  public List<Val> getArgs() {
    if (argCache == null) {
      argCache = Lists.newArrayList();
      for (int i = 0; i < delegate.getArgCount(); i++) {
        argCache.add(getArg(i));
      }
    }
    return argCache;
  }

  @Override
  public boolean isInstanceInvokeExpr() {
    return delegate instanceof InstanceInvokeExpr;
  }

  @Override
  public Val getBase() {
    InstanceInvokeExpr iie = (InstanceInvokeExpr) delegate;
    return new JimpleVal(iie.getBase(), method);
  }

  @Override
  public DeclaredMethod getDeclaredMethod() {
    return new JimpleDeclaredMethod(this, delegate.getMethodRef(), method);
  }

  @Override
  public boolean isSpecialInvokeExpr() {
    return delegate instanceof SpecialInvokeExpr;
  }

  @Override
  public boolean isStaticInvokeExpr() {
    return delegate instanceof StaticInvokeExpr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleInvokeExpr that = (JimpleInvokeExpr) o;
    return Objects.equals(delegate, that.delegate) && Objects.equals(method, that.method);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate, method);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
