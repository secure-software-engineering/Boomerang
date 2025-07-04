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

import boomerang.scope._
import boomerang.scope.opal.transformation.TacLocal
import java.util.Objects
import org.opalj.tac._

class OpalVal(
    val delegate: Expr[TacLocal],
    method: OpalMethod,
    unbalanced: ControlFlowGraph.Edge = null
) extends Val(method, unbalanced) {

  override def getType: Type = OpalType.getTypeForExpr(delegate, method.project)

  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = delegate.isNew

  override def getNewExprType: Type = {
    if (isNewExpr) {
      return new OpalType(delegate.asNew.tpe, method.project)
    }

    throw new RuntimeException("Value is not a new expression")
  }

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val =
    new OpalVal(delegate, method, stmt)

  override def isLocal: Boolean = delegate.isVar

  override def isArrayAllocationVal: Boolean = delegate.isNewArray

  override def getArrayAllocationSize: Val = {
    if (isArrayAllocationVal) {
      val counts = delegate.asNewArray.counts

      /* TODO
       *  For now, the scope just returns the size of the first dimension.
       *  In the future, we may change it to returning a list of values
       */
      val firstIndex = counts.last
      return new OpalVal(firstIndex, method)
    }

    throw new RuntimeException("Value is not an array allocation expression")
  }

  override def isNull: Boolean = delegate.isNullExpr

  override def isStringConstant: Boolean = delegate.isStringConst

  override def getStringValue: String = {
    if (isStringConstant) {
      return delegate.asStringConst.value
    }

    throw new RuntimeException("Value is not a String constant")
  }

  override def isCast: Boolean = delegate.astID == PrimitiveTypecastExpr.ASTID

  override def getCastOp: Val = {
    if (isCast) {
      return new OpalVal(delegate.asPrimitiveTypeCastExpr.operand, method)
    }

    throw new RuntimeException("Expression is not a cast expression")
  }

  override def isArrayRef: Boolean = false

  override def isInstanceOfExpr: Boolean = delegate.astID == InstanceOf.ASTID

  override def getInstanceOfOp: Val = {
    if (isInstanceOfExpr) {
      return new OpalVal(delegate.asInstanceOf.value, method)
    }

    throw new RuntimeException("Expression is not an instanceOf expression")
  }

  override def isLengthExpr: Boolean = delegate.astID == ArrayLength.ASTID

  override def getLengthOp: Val = {
    if (isLengthExpr) {
      return new OpalVal(delegate.asArrayLength.arrayRef, method)
    }

    throw new RuntimeException("Value is not a length expression")
  }

  override def isIntConstant: Boolean = delegate.isIntConst

  override def isClassConstant: Boolean = delegate.isClassConst

  override def getClassConstantType: Type = {
    if (isClassConstant) {
      return new OpalType(delegate.asClassConst.value, method.project)
    }

    throw new RuntimeException("Value is not a class constant")
  }

  override def withNewMethod(callee: Method): Val = throw new RuntimeException("Only allowed for static fields")

  override def withSecondVal(secondVal: Val) =
    new OpalDoubleVal(delegate, method, secondVal)

  override def isLongConstant: Boolean = delegate.isLongConst

  override def getIntValue: Int = {
    if (isIntConstant) {
      return delegate.asIntConst.value
    }

    throw new RuntimeException("Value is not an integer constant")
  }

  override def getLongValue: Long = {
    if (isLongConstant) {
      return delegate.asLongConst.value
    }

    throw new RuntimeException("Value is not a long constant")
  }

  override def getArrayBase: IArrayRef = throw new RuntimeException(
    "Value is not an array ref"
  )

  override def getVariableName: String = {
    delegate match {
      case stringConst: StringConst => "\"" + stringConst.value + "\""
      case intConst: IntConst => intConst.value.toString
      case longConst: LongConst => longConst.value.toString
      case newExpr: New => s"new ${newExpr.tpe.toJava}"
      case newArrayExpr: NewArray[_] => s"new ${newArrayExpr.tpe.toJava}"
      case _: NullExpr => "null"
      case _ => delegate.toString
    }
  }

  override def hashCode: Int =
    Objects.hash(super.hashCode(), delegate.hashCode())

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalVal]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalVal =>
      other.canEqual(this) && super.equals(
        other
      ) && this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = s"$getVariableName${if (isUnbalanced) " unbalanced " + unbalancedStmt else ""}"
}

object OpalVal {

  /**
   * Creates an [[OpalVal]] from an arbitrary expression. Note that the generic type is cast to [[TacLocal]].
   * Hence, the expression should align with this Boomerang scope.
   *
   * @param expr the expression to wrap
   * @param method the method from the expression
   * @return the [[OpalVal]] wrapper for the expression
   */
  def createUnsafe(expr: Expr[_], method: OpalMethod) = new OpalVal(expr.asInstanceOf[Expr[TacLocal]], method)
}
