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
package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.RegisterLocal
import boomerang.scope.opal.transformation.StackLocal
import boomerang.scope.opal.transformation.TacLocal
import boomerang.scope.opal.transformation.stack.OperandStackHandler
import org.opalj.collection.immutable.IntIntPair
import org.opalj.tac.ArrayLength
import org.opalj.tac.ArrayLoad
import org.opalj.tac.ArrayStore
import org.opalj.tac.Assignment
import org.opalj.tac.BinaryExpr
import org.opalj.tac.Checkcast
import org.opalj.tac.Compare
import org.opalj.tac.Expr
import org.opalj.tac.ExprStmt
import org.opalj.tac.GetField
import org.opalj.tac.If
import org.opalj.tac.InstanceOf
import org.opalj.tac.InvokedynamicFunctionCall
import org.opalj.tac.InvokedynamicMethodCall
import org.opalj.tac.MonitorEnter
import org.opalj.tac.MonitorExit
import org.opalj.tac.NewArray
import org.opalj.tac.NonVirtualFunctionCall
import org.opalj.tac.NonVirtualMethodCall
import org.opalj.tac.Nop
import org.opalj.tac.PrefixExpr
import org.opalj.tac.PrimitiveTypecastExpr
import org.opalj.tac.PutField
import org.opalj.tac.PutStatic
import org.opalj.tac.ReturnValue
import org.opalj.tac.StaticFunctionCall
import org.opalj.tac.StaticMethodCall
import org.opalj.tac.Stmt
import org.opalj.tac.Switch
import org.opalj.tac.Throw
import org.opalj.tac.VirtualFunctionCall
import org.opalj.tac.VirtualMethodCall

object LocalPropagationTransformer {

  def apply(
      code: Array[Stmt[TacLocal]],
      stackHandler: OperandStackHandler
  ): Array[Stmt[TacLocal]] = {
    val statements = code.map(identity)

    val max = code.length - 1
    Range(0, max).foreach(i => {
      statements(i) match {
        // If we have an assignment $s = r, we replace $s with r in all following statements
        case Assignment(
              pc,
              stackLocal: StackLocal,
              registerLocal: RegisterLocal
            ) =>
          if (!stackHandler.isBranchedOperand(pc, stackLocal.id)) {
            Range
              .inclusive(i + 1, max)
              .foreach(j => {
                val currStmt = statements(j)
                statements(j) = updateStatementWithLocal(currStmt, stackLocal, registerLocal)
              })

            statements(i) = Nop(pc)
          }
        case _ =>
      }
    })

    def updateStatementWithLocal(
        stmt: Stmt[TacLocal],
        stackLocal: StackLocal,
        registerLocal: RegisterLocal
    ): Stmt[TacLocal] = {
      stmt.astID match {
        case If.ASTID =>
          val ifStmt = stmt.asIf

          val left =
            updateExpressionWithLocal(ifStmt.left, stackLocal, registerLocal)
          val right =
            updateExpressionWithLocal(ifStmt.left, stackLocal, registerLocal)

          return If(ifStmt.pc, left, ifStmt.condition, right, ifStmt.targetStmt)
        case Switch.ASTID =>
          val switchStmt = stmt.asSwitch
          val index = updateExpressionWithLocal(
            switchStmt.index,
            stackLocal,
            registerLocal
          )

          return Switch(
            switchStmt.pc,
            switchStmt.defaultStmt,
            index,
            switchStmt.caseStmts.map(p => IntIntPair(-1, p))
          )
        case Assignment.ASTID =>
          val assignStmt = stmt.asAssignment
          val targetVar = updateExpressionWithLocal(
            assignStmt.targetVar,
            stackLocal,
            registerLocal
          )
          val expr = updateExpressionWithLocal(
            assignStmt.expr,
            stackLocal,
            registerLocal
          )

          return Assignment(assignStmt.pc, targetVar.asVar, expr)
        case ReturnValue.ASTID =>
          val expr = updateExpressionWithLocal(
            stmt.asReturnValue.expr,
            stackLocal,
            registerLocal
          )

          return ReturnValue(stmt.pc, expr)
        case MonitorEnter.ASTID =>
          val objRef = updateExpressionWithLocal(
            stmt.asMonitorEnter.objRef,
            stackLocal,
            registerLocal
          )

          return MonitorEnter(stmt.pc, objRef)
        case MonitorExit.ASTID =>
          val objRef = updateExpressionWithLocal(
            stmt.asMonitorExit.objRef,
            stackLocal,
            registerLocal
          )

          return MonitorExit(stmt.pc, objRef)
        case ArrayStore.ASTID =>
          val arrayStore = stmt.asArrayStore

          val arrayRef = updateExpressionWithLocal(
            arrayStore.arrayRef,
            stackLocal,
            registerLocal
          )
          val index = updateExpressionWithLocal(
            arrayStore.index,
            stackLocal,
            registerLocal
          )
          val value = updateExpressionWithLocal(
            arrayStore.value,
            stackLocal,
            registerLocal
          )

          return ArrayStore(arrayStore.pc, arrayRef, index, value)
        case Throw.ASTID =>
          val throwStmt = stmt.asThrow
          val exception = updateExpressionWithLocal(
            throwStmt.exception,
            stackLocal,
            registerLocal
          )

          return Throw(throwStmt.pc, exception)
        case PutStatic.ASTID =>
          val putStatic = stmt.asPutStatic
          val value = updateExpressionWithLocal(
            putStatic.value,
            stackLocal,
            registerLocal
          )

          return PutStatic(
            putStatic.pc,
            putStatic.declaringClass,
            putStatic.name,
            putStatic.declaredFieldType,
            value
          )
        case PutField.ASTID =>
          val putField = stmt.asPutField

          val objRef = updateExpressionWithLocal(
            putField.objRef,
            stackLocal,
            registerLocal
          )
          val value =
            updateExpressionWithLocal(putField.value, stackLocal, registerLocal)

          return PutField(
            putField.pc,
            putField.declaringClass,
            putField.name,
            putField.declaredFieldType,
            objRef,
            value
          )
        case NonVirtualMethodCall.ASTID =>
          val methodCall = stmt.asNonVirtualMethodCall

          val baseLocal = updateExpressionWithLocal(
            methodCall.receiver,
            stackLocal,
            registerLocal
          )
          val paramLocals =
            methodCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return NonVirtualMethodCall(
            methodCall.pc,
            methodCall.declaringClass,
            methodCall.isInterface,
            methodCall.name,
            methodCall.descriptor,
            baseLocal,
            paramLocals
          )
        case VirtualMethodCall.ASTID =>
          val methodCall = stmt.asVirtualMethodCall

          val baseLocal = updateExpressionWithLocal(
            methodCall.receiver,
            stackLocal,
            registerLocal
          )
          val paramLocals =
            methodCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return VirtualMethodCall(
            methodCall.pc,
            methodCall.declaringClass,
            methodCall.isInterface,
            methodCall.name,
            methodCall.descriptor,
            baseLocal,
            paramLocals
          )
        case StaticMethodCall.ASTID =>
          val methodCall = stmt.asStaticMethodCall
          val params = methodCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return StaticMethodCall(
            methodCall.pc,
            methodCall.declaringClass,
            methodCall.isInterface,
            methodCall.name,
            methodCall.descriptor,
            params
          )
        case InvokedynamicMethodCall.ASTID =>
          val methodCall = stmt.asInvokedynamicMethodCall
          val params = methodCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return InvokedynamicMethodCall(
            methodCall.pc,
            methodCall.bootstrapMethod,
            methodCall.name,
            methodCall.descriptor,
            params
          )
        case ExprStmt.ASTID =>
          val expr = updateExpressionWithLocal(
            stmt.asExprStmt.expr,
            stackLocal,
            registerLocal
          )

          return ExprStmt(stmt.pc, expr)
        case Checkcast.ASTID =>
          val castExpr = stmt.asCheckcast
          val value =
            updateExpressionWithLocal(castExpr.value, stackLocal, registerLocal)

          return Checkcast(castExpr.pc, value, castExpr.cmpTpe)
        case _ => return stmt
      }

      throw new RuntimeException("Could not update statement: " + stmt)
    }

    def updateExpressionWithLocal(
        expr: Expr[TacLocal],
        stackLocal: StackLocal,
        registerLocal: RegisterLocal
    ): Expr[TacLocal] = {
      if (expr.isVar) {
        if (expr.asVar.isRegisterLocal) return expr

        // Replace stack local with register local
        if (expr.asVar.isStackLocal && expr.asVar == stackLocal) {
          return registerLocal
        } else {
          return expr
        }
      }

      expr.astID match {
        case InstanceOf.ASTID =>
          val instanceOf = expr.asInstanceOf
          val value = updateExpressionWithLocal(
            instanceOf.value,
            stackLocal,
            registerLocal
          )

          return InstanceOf(instanceOf.pc, value, instanceOf.cmpTpe)
        case Compare.ASTID =>
          val compareExpr = expr.asCompare

          val leftLocal = updateExpressionWithLocal(
            compareExpr.left,
            stackLocal,
            registerLocal
          )
          val rightLocal = updateExpressionWithLocal(
            compareExpr.right,
            stackLocal,
            registerLocal
          )

          return Compare(
            compareExpr.pc,
            leftLocal,
            compareExpr.condition,
            rightLocal
          )
        case BinaryExpr.ASTID =>
          val binaryExpr = expr.asBinaryExpr

          val left = updateExpressionWithLocal(
            binaryExpr.left,
            stackLocal,
            registerLocal
          )
          val right = updateExpressionWithLocal(
            binaryExpr.right,
            stackLocal,
            registerLocal
          )

          return BinaryExpr(
            binaryExpr.pc,
            binaryExpr.cTpe,
            binaryExpr.op,
            left,
            right
          )
        case PrefixExpr.ASTID =>
          val prefixExpr = expr.asPrefixExpr
          val operand = updateExpressionWithLocal(
            prefixExpr.operand,
            stackLocal,
            registerLocal
          )

          return PrefixExpr(
            prefixExpr.pc,
            prefixExpr.cTpe,
            prefixExpr.op,
            operand
          )
        case PrimitiveTypecastExpr.ASTID =>
          val primitiveTypecastExpr = expr.asPrimitiveTypeCastExpr
          val operand = updateExpressionWithLocal(
            primitiveTypecastExpr.operand,
            stackLocal,
            registerLocal
          )

          return PrimitiveTypecastExpr(
            primitiveTypecastExpr.pc,
            primitiveTypecastExpr.targetTpe,
            operand
          )
        case NewArray.ASTID =>
          val newArray = expr.asNewArray
          val counts = newArray.counts.map(c => updateExpressionWithLocal(c, stackLocal, registerLocal))

          return NewArray(newArray.pc, counts, newArray.tpe)
        case ArrayLoad.ASTID =>
          val arrayLoad = expr.asArrayLoad

          val index = updateExpressionWithLocal(
            arrayLoad.index,
            stackLocal,
            registerLocal
          )
          val arrayRef = updateExpressionWithLocal(
            arrayLoad.arrayRef,
            stackLocal,
            registerLocal
          )

          return ArrayLoad(arrayLoad.pc, index, arrayRef)
        case ArrayLength.ASTID =>
          val arrayLength = expr.asArrayLength
          val arrayRef = updateExpressionWithLocal(
            arrayLength.arrayRef,
            stackLocal,
            registerLocal
          )

          return ArrayLength(arrayLength.pc, arrayRef)
        case GetField.ASTID =>
          val getField = expr.asGetField
          val objRef = updateExpressionWithLocal(
            getField.objRef,
            stackLocal,
            registerLocal
          )

          return GetField(
            getField.pc,
            getField.declaringClass,
            getField.name,
            getField.declaredFieldType,
            objRef
          )
        case InvokedynamicFunctionCall.ASTID =>
          val functionCall = expr.asInvokedynamicFunctionCall
          val params = functionCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return InvokedynamicFunctionCall(
            functionCall.pc,
            functionCall.bootstrapMethod,
            functionCall.name,
            functionCall.descriptor,
            params
          )
        case NonVirtualFunctionCall.ASTID =>
          val functionCall = expr.asNonVirtualFunctionCall

          val base = updateExpressionWithLocal(
            functionCall.receiver,
            stackLocal,
            registerLocal
          )
          val params = functionCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return NonVirtualFunctionCall(
            functionCall.pc,
            functionCall.declaringClass,
            functionCall.isInterface,
            functionCall.name,
            functionCall.descriptor,
            base,
            params
          )
        case VirtualFunctionCall.ASTID =>
          val functionCall = expr.asVirtualFunctionCall

          val base = updateExpressionWithLocal(
            functionCall.receiver,
            stackLocal,
            registerLocal
          )
          val params = functionCall.params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return VirtualFunctionCall(
            functionCall.pc,
            functionCall.declaringClass,
            functionCall.isInterface,
            functionCall.name,
            functionCall.descriptor,
            base,
            params
          )
        case StaticFunctionCall.ASTID =>
          val functionCall = expr.asStaticFunctionCall

          val params = functionCall.params
          val paramLocals =
            params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return StaticFunctionCall(
            functionCall.pc,
            functionCall.declaringClass,
            functionCall.isInterface,
            functionCall.name,
            functionCall.descriptor,
            paramLocals
          )
        case _ => return expr
      }

      throw new RuntimeException("Could not update expression: " + expr)
    }

    statements
  }
}
