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
import org.opalj.br.ObjectType

class OpalArrayRef(
    val arrayRef: TacLocal,
    val index: Int,
    method: OpalMethod,
    unbalanced: ControlFlowGraph.Edge = null
) extends Val(method, unbalanced) {

  // TODO Which type is expected? int[] or int?
  override def getType: Type = OpalType(ObjectType.Array)

  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = false

  override def getNewExprType: Type = throw new RuntimeException(
    "Array Value is not a new expression"
  )

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val =
    new OpalArrayRef(arrayRef, index, method, stmt)

  override def isLocal: Boolean = true

  override def isArrayAllocationVal: Boolean = false

  override def getArrayAllocationSize: Val = throw new RuntimeException(
    "Array Value has no allocation size"
  )

  override def isNull: Boolean = false

  override def isStringConstant: Boolean = false

  override def getStringValue: String = throw new RuntimeException(
    "Array Value is not a String constant"
  )

  override def isStringBufferOrBuilder: Boolean = false

  override def isThrowableAllocationType: Boolean = false

  override def isCast: Boolean = false

  override def getCastOp: Val = throw new RuntimeException(
    "Array Value is not a cast operation"
  )

  override def isArrayRef: Boolean = true

  override def isInstanceOfExpr: Boolean = false

  override def getInstanceOfOp: Val = throw new RuntimeException(
    "Array Value is not an instance of operation"
  )

  override def isLengthExpr: Boolean = false

  override def getLengthOp: Val = throw new RuntimeException(
    "Array Value is not a length operation"
  )

  override def isIntConstant: Boolean = false

  override def isClassConstant: Boolean = false

  override def getClassConstantType: Type = throw new RuntimeException(
    "Array Value is not a class constant"
  )

  override def withNewMethod(callee: Method): Val =
    new OpalArrayRef(arrayRef, index, callee.asInstanceOf[OpalMethod])

  override def isLongConstant: Boolean = false

  override def getIntValue: Int = throw new RuntimeException(
    "Array Value is not an integer constant"
  )

  override def getLongValue: Long = throw new RuntimeException(
    "Array Value is not a long constant"
  )

  override def getArrayBase: Pair[Val, Integer] = {
    val base = new OpalLocal(arrayRef, method)

    new Pair[Val, Integer](base, index)
  }

  override def getVariableName: String = s"$arrayRef[$index]"

  override def hashCode: Int = Objects.hash(super.hashCode(), arrayRef, index)

  override def equals(other: Any): Boolean = other match {
    case that: OpalArrayRef =>
      super.equals(
        that
      ) && this.arrayRef == that.arrayRef && this.index == that.index
    case _ => false
  }

  override def toString: String = getVariableName
}
