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

import boomerang.scope.ControlFlowGraph
import boomerang.scope.Method
import boomerang.scope.Pair
import boomerang.scope.Type
import boomerang.scope.Val
import java.util.Objects
import org.opalj.br.ObjectType

class OpalStaticFieldRef(
    val declaringClass: ObjectType,
    val fieldType: org.opalj.br.Type,
    val fieldName: String,
    method: OpalMethod,
    unbalanced: ControlFlowGraph.Edge = null
) extends Val(method, unbalanced) {

    override def getType: Type = OpalType(fieldType)

    override def isStatic: Boolean = true

    override def isNewExpr: Boolean = false

    override def getNewExprType: Type = throw new RuntimeException(
        "Static field ref is not a new expression"
    )

    override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val =
        new OpalStaticFieldRef(declaringClass, fieldType, fieldName, method, stmt)

    override def isLocal: Boolean = false

    override def isArrayAllocationVal: Boolean = false

    override def getArrayAllocationSize: Val = throw new RuntimeException(
        "Static field ref is not an array allocation site"
    )

    override def isNull: Boolean = false

    override def isStringConstant: Boolean = false

    override def getStringValue: String = throw new RuntimeException(
        "Static field ref is not a String constant"
    )

    override def isStringBufferOrBuilder: Boolean = false

    override def isThrowableAllocationType: Boolean = false

    override def isCast: Boolean = false

    override def getCastOp: Val = throw new RuntimeException(
        "Static field ref is not a cast expression"
    )

    override def isArrayRef: Boolean = false

    override def isInstanceOfExpr: Boolean = false

    override def getInstanceOfOp: Val = throw new RuntimeException(
        "Static field ref is not an instanceOf expression"
    )

    override def isLengthExpr: Boolean = false

    override def getLengthOp: Val = throw new RuntimeException(
        "Static field ref is not a length expression"
    )

    override def isIntConstant: Boolean = false

    override def isClassConstant: Boolean = false

    override def getClassConstantType: Type = throw new RuntimeException(
        "Static field ref is not a class constant"
    )

    override def withNewMethod(callee: Method): Val = new OpalStaticFieldRef(
        declaringClass,
        fieldType,
        fieldName,
        callee.asInstanceOf[OpalMethod]
    )

    override def isLongConstant: Boolean = false

    override def getIntValue: Int = throw new RuntimeException(
        "Static field ref is not an int constant"
    )

    override def getLongValue: Long = throw new RuntimeException(
        "Static field ref is not a long constant"
    )

    override def getArrayBase: Pair[Val, Integer] = throw new RuntimeException(
        "Static field ref has no array base"
    )

    override def getVariableName: String = fieldName

    override def hashCode: Int =
        Objects.hash(super.hashCode(), declaringClass, fieldType, fieldName)

    override def equals(other: Any): Boolean = other match {
        case that: OpalStaticFieldRef =>
            super.equals(
                that
            ) && this.declaringClass == that.declaringClass && this.fieldType == that.fieldType && this.fieldName == that.fieldName
        case _ => false
    }

    override def toString: String = s"${declaringClass.toJava}.$fieldName"
}
