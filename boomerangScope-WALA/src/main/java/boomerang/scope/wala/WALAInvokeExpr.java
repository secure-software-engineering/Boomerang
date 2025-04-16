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
package boomerang.scope.wala;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Val;
import com.google.common.collect.Lists;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import java.util.List;

public class WALAInvokeExpr implements InvokeExpr {

  private final SSAAbstractInvokeInstruction inv;
  private final WALAMethod m;
  private List<Val> argsCache;

  public WALAInvokeExpr(SSAAbstractInvokeInstruction inv, WALAMethod m) {
    this.inv = inv;
    this.m = m;
  }

  @Override
  public Val getArg(int index) {
    return getArgs().get(index);
  }

  @Override
  public List<Val> getArgs() {
    if (argsCache == null) {
      argsCache = Lists.newArrayList();

      for (int i = (inv.isStatic() ? 0 : 1); i < inv.getNumberOfPositionalParameters(); i++) {
        argsCache.add(new WALAVal(inv.getUse(i), m));
      }
    }
    return argsCache;
  }

  @Override
  public boolean isInstanceInvokeExpr() {
    return inv.isDispatch() || inv.isSpecial();
  }

  @Override
  public Val getBase() {
    return new WALAVal(inv.getReceiver(), m);
  }

  @Override
  public DeclaredMethod getDeclaredMethod() {
    return new WALADeclaredMethod(this, inv.getCallSite().getDeclaredTarget());
  }

  @Override
  public boolean isSpecialInvokeExpr() {
    return inv.isSpecial();
  }

  @Override
  public boolean isStaticInvokeExpr() {
    return inv.isStatic();
  }
}
