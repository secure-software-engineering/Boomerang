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
package boomerang.scope.opal.tac

import boomerang.scope.DeclaredMethod
import boomerang.scope.InvokeExpr
import boomerang.scope.Val
import boomerang.scope.opal.transformation.TacLocal
import java.util
import java.util.Objects
import org.opalj.tac._

class OpalMethodInvokeExpr(
    val delegate: MethodCall[TacLocal],
    method: OpalMethod
) extends InvokeExpr {

  override def getArg(index: Int): Val = getArgs.get(index)

  override def getArgs: util.List[Val] = {
    val result = new util.ArrayList[Val]

    delegate.params.foreach(param => {
      result.add(new OpalVal(param, method))
    })

    result
  }

  override def isInstanceInvokeExpr: Boolean =
    delegate.isInstanceOf[InstanceMethodCall[_]]

  override def getBase: Val = {
    if (isInstanceInvokeExpr) {
      return new OpalVal(delegate.asInstanceMethodCall.receiver, method)
    }

    throw new RuntimeException(
      "Method call is not an instance invoke expression"
    )
  }

  override def getDeclaredMethod: DeclaredMethod =
    OpalDeclaredMethod(this, delegate)

  override def isSpecialInvokeExpr: Boolean =
    delegate.astID == NonVirtualMethodCall.ASTID

  override def isStaticInvokeExpr: Boolean = delegate.isStaticMethodCall

  override def hashCode: Int = Objects.hash(delegate)

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalMethodInvokeExpr => this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = delegate.toString
}

class OpalFunctionInvokeExpr(
    val delegate: FunctionCall[TacLocal],
    method: OpalMethod
) extends InvokeExpr {

  override def getArg(index: Int): Val = getArgs.get(index)

  override def getArgs: util.List[Val] = {
    val result = new util.ArrayList[Val]

    delegate.params.foreach(param => {
      result.add(new OpalVal(param, method))
    })

    result
  }

  override def isInstanceInvokeExpr: Boolean =
    delegate.isInstanceOf[InstanceFunctionCall[_]]

  override def getBase: Val = {
    if (isInstanceInvokeExpr) {
      return new OpalVal(
        delegate.asInstanceFunctionCall.receiver,
        method
      )
    }

    throw new RuntimeException(
      "Function call is not an instance invoke expression"
    )
  }

  override def getDeclaredMethod: DeclaredMethod =
    OpalDeclaredMethod(this, delegate)

  override def isSpecialInvokeExpr: Boolean =
    delegate.astID == NonVirtualFunctionCall.ASTID

  override def isStaticInvokeExpr: Boolean = delegate.isStaticFunctionCall

  override def hashCode: Int = Objects.hash(delegate)

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalFunctionInvokeExpr]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalFunctionInvokeExpr =>
      other.canEqual(this) && this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = delegate.toString
}
