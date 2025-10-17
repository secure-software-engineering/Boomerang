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
      stmt match {
        case If(pc, left, condition, right, target) =>
          val updatedLeft =
            updateExpressionWithLocal(left, stackLocal, registerLocal)
          val updatedRight =
            updateExpressionWithLocal(right, stackLocal, registerLocal)

          return If(pc, updatedLeft, condition, updatedRight, target)
        case Switch(pc, defaultTarget, index, npairs) =>
          val updatedIndex = updateExpressionWithLocal(
            index,
            stackLocal,
            registerLocal
          )

          return Switch(
            pc,
            defaultTarget,
            updatedIndex,
            npairs
          )
        case Assignment(pc, targetVar, expr) =>
          val updatedTargetVar = updateExpressionWithLocal(
            targetVar,
            stackLocal,
            registerLocal
          )
          val updatedExpr = updateExpressionWithLocal(
            expr,
            stackLocal,
            registerLocal
          )

          return Assignment(pc, updatedTargetVar.asVar, updatedExpr)
        case ReturnValue(pc, expr) =>
          val updatedExpr = updateExpressionWithLocal(
            expr,
            stackLocal,
            registerLocal
          )

          return ReturnValue(pc, updatedExpr)
        case MonitorEnter(pc, objRef) =>
          val updatedObjRef = updateExpressionWithLocal(
            objRef,
            stackLocal,
            registerLocal
          )

          return MonitorEnter(pc, updatedObjRef)
        case MonitorExit(pc, objRef) =>
          val updatedObjRef = updateExpressionWithLocal(
            objRef,
            stackLocal,
            registerLocal
          )

          return MonitorExit(pc, updatedObjRef)
        case ArrayStore(pc, arrayRef, index, value) =>
          val updatedArrayRef = updateExpressionWithLocal(
            arrayRef,
            stackLocal,
            registerLocal
          )
          val updatedIndex = updateExpressionWithLocal(
            index,
            stackLocal,
            registerLocal
          )
          val updatedValue = updateExpressionWithLocal(
            value,
            stackLocal,
            registerLocal
          )

          return ArrayStore(pc, updatedArrayRef, updatedIndex, updatedValue)
        case Throw(pc, exception) =>
          val updatedException = updateExpressionWithLocal(
            exception,
            stackLocal,
            registerLocal
          )

          return Throw(pc, updatedException)
        case PutStatic(pc, declaringClass, name, declaredFieldType, value) =>
          val updatedValue = updateExpressionWithLocal(
            value,
            stackLocal,
            registerLocal
          )

          return PutStatic(
            pc,
            declaringClass,
            name,
            declaredFieldType,
            updatedValue
          )
        case PutField(pc, declaringClass, name, declaredFieldType, objRef, value) =>
          val updatedObjRef = updateExpressionWithLocal(
            objRef,
            stackLocal,
            registerLocal
          )
          val updatedValue =
            updateExpressionWithLocal(value, stackLocal, registerLocal)

          return PutField(
            pc,
            declaringClass,
            name,
            declaredFieldType,
            updatedObjRef,
            updatedValue
          )
        case NonVirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val baseLocal = updateExpressionWithLocal(
            receiver,
            stackLocal,
            registerLocal
          )
          val paramLocals =
            params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return NonVirtualMethodCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            baseLocal,
            paramLocals
          )
        case VirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val baseLocal = updateExpressionWithLocal(
            receiver,
            stackLocal,
            registerLocal
          )
          val paramLocals = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return VirtualMethodCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            baseLocal,
            paramLocals
          )
        case StaticMethodCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val updatedParams = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return StaticMethodCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            updatedParams
          )
        case InvokedynamicMethodCall(pc, bootstrapMethod, name, descriptor, params) =>
          val updatedParams = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return InvokedynamicMethodCall(
            pc,
            bootstrapMethod,
            name,
            descriptor,
            updatedParams
          )
        case ExprStmt(pc, expr) =>
          val updatedExpr = updateExpressionWithLocal(
            expr,
            stackLocal,
            registerLocal
          )

          return ExprStmt(pc, updatedExpr)
        case Checkcast(pc, value, cmpTpe) =>
          val updatedValue =
            updateExpressionWithLocal(value, stackLocal, registerLocal)

          return Checkcast(pc, updatedValue, cmpTpe)
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

      expr match {
        case InstanceOf(pc, value, cmpTpe) =>
          val updatedValue = updateExpressionWithLocal(
            value,
            stackLocal,
            registerLocal
          )

          return InstanceOf(pc, updatedValue, cmpTpe)
        case Compare(pc, left, condition, right) =>
          val leftLocal = updateExpressionWithLocal(
            left,
            stackLocal,
            registerLocal
          )
          val rightLocal = updateExpressionWithLocal(
            right,
            stackLocal,
            registerLocal
          )

          return Compare(
            pc,
            leftLocal,
            condition,
            rightLocal
          )
        case BinaryExpr(pc, cTpe, op, left, right) =>
          val updatedLeft = updateExpressionWithLocal(
            left,
            stackLocal,
            registerLocal
          )
          val updatedRight = updateExpressionWithLocal(
            right,
            stackLocal,
            registerLocal
          )

          return BinaryExpr(
            pc,
            cTpe,
            op,
            updatedLeft,
            updatedRight
          )
        case PrefixExpr(pc, cTpe, op, operand) =>
          val updatedOperand = updateExpressionWithLocal(
            operand,
            stackLocal,
            registerLocal
          )

          return PrefixExpr(
            pc,
            cTpe,
            op,
            updatedOperand
          )
        case PrimitiveTypecastExpr(pc, targetTpe, operand) =>
          val updatedOperand = updateExpressionWithLocal(
            operand,
            stackLocal,
            registerLocal
          )

          return PrimitiveTypecastExpr(
            pc,
            targetTpe,
            updatedOperand
          )
        case NewArray(pc, counts, tpe) =>
          val updatedCounts = counts.map(c => updateExpressionWithLocal(c, stackLocal, registerLocal))

          return NewArray(pc, updatedCounts, tpe)
        case ArrayLoad(pc, index, arrayRef) =>
          val updatedIndex = updateExpressionWithLocal(
            index,
            stackLocal,
            registerLocal
          )
          val updatedArrayRef = updateExpressionWithLocal(
            arrayRef,
            stackLocal,
            registerLocal
          )

          return ArrayLoad(pc, updatedIndex, updatedArrayRef)
        case ArrayLength(pc, arrayRef) =>
          val updatedArrayRef = updateExpressionWithLocal(
            arrayRef,
            stackLocal,
            registerLocal
          )

          return ArrayLength(pc, updatedArrayRef)
        case GetField(pc, declaringClass, name, declaredFieldType, objRef) =>
          val updatedObjRef = updateExpressionWithLocal(
            objRef,
            stackLocal,
            registerLocal
          )

          return GetField(
            pc,
            declaringClass,
            name,
            declaredFieldType,
            updatedObjRef
          )
        case InvokedynamicFunctionCall(pc, bootstrapMethod, name, descriptor, params) =>
          val updatedParams = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return InvokedynamicFunctionCall(
            pc,
            bootstrapMethod,
            name,
            descriptor,
            updatedParams
          )
        case NonVirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val base = updateExpressionWithLocal(
            receiver,
            stackLocal,
            registerLocal
          )
          val updatedParams = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return NonVirtualFunctionCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            base,
            updatedParams
          )
        case VirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val base = updateExpressionWithLocal(
            receiver,
            stackLocal,
            registerLocal
          )
          val updatedParams = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return VirtualFunctionCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            base,
            updatedParams
          )
        case StaticFunctionCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val paramLocals = params.map(p => updateExpressionWithLocal(p, stackLocal, registerLocal))

          return StaticFunctionCall(
            pc,
            declaringClass,
            isInterface,
            name,
            descriptor,
            paramLocals
          )
        case _ => return expr
      }

      throw new RuntimeException("Could not update expression: " + expr)
    }

    statements
  }
}
