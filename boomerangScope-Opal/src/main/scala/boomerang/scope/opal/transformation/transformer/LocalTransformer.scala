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
package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.ExceptionLocal
import boomerang.scope.opal.transformation.ParameterLocal
import boomerang.scope.opal.transformation.RegisterLocal
import boomerang.scope.opal.transformation.StackLocal
import boomerang.scope.opal.transformation.TacLocal
import boomerang.scope.opal.transformation.stack.OperandStackHandler
import org.opalj.ai.AIResult
import org.opalj.ai.BaseAI
import org.opalj.ai.Domain
import org.opalj.br.ComputationalType
import org.opalj.br.ComputationalTypeDouble
import org.opalj.br.ComputationalTypeFloat
import org.opalj.br.ComputationalTypeInt
import org.opalj.br.ComputationalTypeLong
import org.opalj.br.ComputationalTypeReference
import org.opalj.br.ComputationalTypeReturnAddress
import org.opalj.br.DoubleType
import org.opalj.br.FieldType
import org.opalj.br.FloatType
import org.opalj.br.IntegerType
import org.opalj.br.LongType
import org.opalj.br.Method
import org.opalj.br.ObjectType
import org.opalj.br.PC
import org.opalj.br.analyses.Project
import org.opalj.tac.ArrayLength
import org.opalj.tac.ArrayLoad
import org.opalj.tac.ArrayStore
import org.opalj.tac.Assignment
import org.opalj.tac.BinaryExpr
import org.opalj.tac.CaughtException
import org.opalj.tac.Checkcast
import org.opalj.tac.ClassConst
import org.opalj.tac.Compare
import org.opalj.tac.DoubleConst
import org.opalj.tac.DynamicConst
import org.opalj.tac.Expr
import org.opalj.tac.ExprStmt
import org.opalj.tac.FloatConst
import org.opalj.tac.GetField
import org.opalj.tac.GetStatic
import org.opalj.tac.Goto
import org.opalj.tac.IdBasedVar
import org.opalj.tac.If
import org.opalj.tac.InstanceOf
import org.opalj.tac.IntConst
import org.opalj.tac.InvokedynamicFunctionCall
import org.opalj.tac.InvokedynamicMethodCall
import org.opalj.tac.JSR
import org.opalj.tac.LongConst
import org.opalj.tac.MethodHandleConst
import org.opalj.tac.MethodTypeConst
import org.opalj.tac.MonitorEnter
import org.opalj.tac.MonitorExit
import org.opalj.tac.NaiveTACode
import org.opalj.tac.New
import org.opalj.tac.NewArray
import org.opalj.tac.NonVirtualFunctionCall
import org.opalj.tac.NonVirtualMethodCall
import org.opalj.tac.Nop
import org.opalj.tac.NullExpr
import org.opalj.tac.Param
import org.opalj.tac.PrefixExpr
import org.opalj.tac.PrimitiveTypecastExpr
import org.opalj.tac.PutField
import org.opalj.tac.PutStatic
import org.opalj.tac.Ret
import org.opalj.tac.Return
import org.opalj.tac.ReturnValue
import org.opalj.tac.StaticFunctionCall
import org.opalj.tac.StaticMethodCall
import org.opalj.tac.Stmt
import org.opalj.tac.StringConst
import org.opalj.tac.Switch
import org.opalj.tac.Throw
import org.opalj.tac.VirtualFunctionCall
import org.opalj.tac.VirtualMethodCall
import org.opalj.value.ValueInformation
import scala.collection.mutable

object LocalTransformer {

    def apply(
        project: Project[_],
        method: Method,
        tac: NaiveTACode[_],
        stackHandler: OperandStackHandler,
        domain: Domain
    ): Array[Stmt[TacLocal]] = {
        var paramCount = -1
        var exceptionCount = -1
        val currentLocals = mutable.Map.empty[Int, TacLocal]
        val exceptionHandlers = tac.exceptionHandlers.map(eh => eh.handlerPC)

        // Domain components
        val aiResult: AIResult = BaseAI(method, domain)
        val operandsArray: aiResult.domain.OperandsArray = aiResult.operandsArray
        val localArray: aiResult.domain.LocalsArray = aiResult.localsArray

        def transformStmt(stmt: Stmt[IdBasedVar]): Stmt[TacLocal] = {
            stmt match {
                case If(pc, left, condition, right, target) =>
                    val leftExpr = transformExpr(pc, left)
                    val rightExpr = transformExpr(pc, right)

                    return If(pc, leftExpr, condition, rightExpr, target)
                case Goto(pc, target) => return Goto(pc, target)
                case Ret(pc, returnAddresses) => return Ret(pc, returnAddresses)
                case JSR(pc, target) => return JSR(pc, target)
                case Switch(pc, defaultTarget, index, nPairs) =>
                    val indexExpr = transformExpr(pc, index)

                    return Switch(pc, defaultTarget, indexExpr, nPairs)
                case Assignment(pc, targetVar, expr) =>
                    // Parameter definition statements
                    if (pc == -1) {
                        val transformedExpr = transformExpr(pc, expr)

                        var paramName: Option[String] = None
                        if (transformedExpr.isVar && transformedExpr.asVar.isParameterLocal) {
                            paramName = Some(transformedExpr.asVar.name)
                        }

                        val paramLocal = createParameterLocal(targetVar, paramName)

                        currentLocals(paramLocal.id) = paramLocal
                        return Assignment(pc, paramLocal, transformedExpr)
                    }

                    if (targetVar.id >= 0) {
                        val transformedExpr = transformExpr(pc, expr)

                        // Consider 'this' assignment from register to stack: s = this
                        val isThisAssignment =
                            transformedExpr.isVar && transformedExpr.asVar.isThisLocal
                        val targetLocal = createStackLocal(pc, targetVar, isThisAssignment)

                        currentLocals(targetLocal.id) = targetLocal
                        return Assignment(pc, targetLocal, transformedExpr)
                    }

                    if (targetVar.id < 0) {
                        val transformedExpr = transformExpr(pc, expr)

                        // Consider 'this' assignment from stack to register: r0 = $this
                        val isThisAssignment =
                            transformedExpr.isVar && transformedExpr.asVar.isThisLocal
                        val targetLocal =
                            createRegisterLocal(pc, targetVar, isThisAssignment)

                        currentLocals(targetLocal.id) = targetLocal
                        return Assignment(pc, targetLocal, transformedExpr)
                    }

                    throw new RuntimeException("Should never be reached")
                case ReturnValue(pc, expr) =>
                    val returnExpr = transformExpr(pc, expr)

                    return ReturnValue(pc, returnExpr)
                case Return(pc) => return Return(pc)
                case Nop(pc) => return Nop(pc)
                case MonitorEnter(pc, objRef) =>
                    val objRefExpr = transformExpr(pc, objRef)

                    return MonitorEnter(pc, objRefExpr)
                case MonitorExit(pc, objRef) =>
                    val objRefExpr = transformExpr(pc, objRef)

                    return MonitorExit(pc, objRefExpr)
                case ArrayStore(pc, arrayRef, index, value) =>
                    val arrayRefExpr = transformExpr(pc, arrayRef)
                    val indexExpr = transformExpr(pc, index)
                    val valueExpr = transformExpr(pc, value)

                    return ArrayStore(pc, arrayRefExpr, indexExpr, valueExpr)
                case Throw(pc, exception) =>
                    val exceptionExpr = transformExpr(pc, exception)

                    return Throw(pc, exceptionExpr)
                case PutStatic(pc, declaringClass, name, declaredFieldType, value) =>
                    val valueExpr = transformExpr(pc, value)

                    return PutStatic(
                        pc,
                        declaringClass,
                        name,
                        declaredFieldType,
                        valueExpr
                    )
                case PutField(
                        pc,
                        declaringClass,
                        name,
                        declaredFieldType,
                        objRef,
                        value
                    ) =>
                    val objRefExpr = transformExpr(pc, objRef)
                    val valueExpr = transformExpr(pc, value)

                    return PutField(
                        pc,
                        declaringClass,
                        name,
                        declaredFieldType,
                        objRefExpr,
                        valueExpr
                    )
                case NonVirtualMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiver,
                        params
                    ) =>
                    val receiverExpr = transformExpr(pc, receiver)
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return NonVirtualMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiverExpr,
                        paramsExpr
                    )
                case VirtualMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiver,
                        params
                    ) =>
                    val receiverExpr = transformExpr(pc, receiver)
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return VirtualMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiverExpr,
                        paramsExpr
                    )
                case StaticMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        params
                    ) =>
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return StaticMethodCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        paramsExpr
                    )
                case InvokedynamicMethodCall(
                        pc,
                        bootstrapMethod,
                        name,
                        descriptor,
                        params
                    ) =>
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return InvokedynamicMethodCall(
                        pc,
                        bootstrapMethod,
                        name,
                        descriptor,
                        paramsExpr
                    )
                case ExprStmt(pc, expr) =>
                    val transformedExpr = transformExpr(pc, expr)

                    return ExprStmt(pc, transformedExpr)
                case CaughtException(pc, exceptionType, throwingStatements) =>
                    return CaughtException(pc, exceptionType, throwingStatements)
                case Checkcast(pc, value, cmpTpe) =>
                    val valueExpr = transformExpr(pc, value)
                    // TODO Transform into assignment
                    return Checkcast(pc, valueExpr, cmpTpe)
                case _ => throw new RuntimeException("Unknown statement: " + stmt)
            }

            throw new RuntimeException("Could not transform statement: " + stmt)
        }

        def isThisVar(idBasedVar: IdBasedVar): Boolean =
            method.isNotStatic && idBasedVar.id == -1

        def createParameterLocal(
            idBasedVar: IdBasedVar,
            paramName: Option[String] = Option.empty
        ): TacLocal = {
            val local = localArray(0)

            new RegisterLocal(
                idBasedVar.id,
                idBasedVar.cTpe,
                local(-idBasedVar.id - 1),
                isThisVar(idBasedVar),
                paramName
            )
        }

        def createStackLocal(
            pc: PC,
            idBasedVar: IdBasedVar,
            isThis: Boolean
        ): TacLocal = {
            val nextPc = method.body.get.pcOfNextInstruction(pc)
            val value = operandsArray(nextPc)
            val counter = stackHandler.defSiteAtPc(pc)

            if (value == null) {
                val fieldType = computationalTypeToFieldType(idBasedVar.cTpe)
                new StackLocal(
                    counter,
                    idBasedVar.cTpe,
                    ValueInformation.forProperValue(fieldType)(project.classHierarchy),
                    isThis
                )
            } else {
                new StackLocal(counter, idBasedVar.cTpe, value.head, isThis)
            }
        }

        def createRegisterLocal(
            pc: PC,
            idBasedVar: IdBasedVar,
            isThis: Boolean
        ): TacLocal = {
            val nextPc = method.body.get.pcOfNextInstruction(pc)
            val locals = localArray(nextPc)

            val index = -idBasedVar.id - 1

            val local = method.body.get.localVariable(nextPc, index)
            if (local.isDefined) {
                if (locals == null) {
                    val fieldType = computationalTypeToFieldType(idBasedVar.cTpe)
                    return new RegisterLocal(
                        idBasedVar.id,
                        idBasedVar.cTpe,
                        ValueInformation.forProperValue(fieldType)(project.classHierarchy),
                        isThis,
                        Option(local.get.name)
                    )
                } else {
                    return new RegisterLocal(
                        idBasedVar.id,
                        idBasedVar.cTpe,
                        locals(index),
                        isThis,
                        Option(local.get.name)
                    )
                }
            }

            if (locals == null) {
                val fieldType = computationalTypeToFieldType(idBasedVar.cTpe)
                new RegisterLocal(
                    idBasedVar.id,
                    idBasedVar.cTpe,
                    ValueInformation.forProperValue(fieldType)(project.classHierarchy),
                    isThis
                )
            } else {
                new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, locals(index), isThis)
            }
        }

        def createExceptionLocal(
            pc: PC,
            idBasedVar: IdBasedVar,
            localId: Int
        ): TacLocal = {
            val value = operandsArray(pc)

            if (value == null) {
                val fieldType = computationalTypeToFieldType(idBasedVar.cTpe)
                new ExceptionLocal(
                    localId,
                    idBasedVar.cTpe,
                    ValueInformation.forProperValue(fieldType)(project.classHierarchy)
                )
            } else {
                new ExceptionLocal(localId, idBasedVar.cTpe, value.head)
            }
        }

        def computationalTypeToFieldType(cTpe: ComputationalType): FieldType = {
            cTpe match {
                case ComputationalTypeInt => IntegerType
                case ComputationalTypeFloat => FloatType
                case ComputationalTypeLong => LongType
                case ComputationalTypeDouble => DoubleType
                case ComputationalTypeReference => ObjectType.Object
                case ComputationalTypeReturnAddress => ObjectType.Object
                case _ =>
                    throw new RuntimeException("Unknown computational type " + cTpe)
            }
        }

        def transformExpr(pc: PC, expr: Expr[IdBasedVar]): Expr[TacLocal] = {
            expr match {
                case v: IdBasedVar =>
                    // Exception locals are implicitly defined and denoted with s0. They are
                    // not pushed on the operand stack, so we have to define it explicitly
                    if (exceptionHandlers.contains(tac.pcToIndex(pc))) {
                        exceptionCount += 1

                        return createExceptionLocal(pc, v, exceptionCount)
                    }

                    if (v.id >= 0) {
                        val stmt = tac.stmts(tac.pcToIndex(pc))
                        val counter =
                            stackHandler.counterForOperand(pc, v.id, stmt.isReturnValue)

                        return currentLocals(counter)
                    }

                    return currentLocals(v.id)
                case InstanceOf(pc, value, cmpTpe) =>
                    val valueExpr = transformExpr(pc, value)

                    return InstanceOf(pc, valueExpr, cmpTpe)
                case Compare(pc, left, condition, right) =>
                    val leftExpr = transformExpr(pc, left)
                    val rightExpr = transformExpr(pc, right)

                    return Compare(pc, leftExpr, condition, rightExpr)
                case Param(cTpe, name) =>
                    paramCount += 1

                    return new ParameterLocal(paramCount, cTpe, name)
                case MethodTypeConst(pc, value) => return MethodTypeConst(pc, value)
                case MethodHandleConst(pc, value) => return MethodHandleConst(pc, value)
                case IntConst(pc, value) => return IntConst(pc, value)
                case LongConst(pc, value) => return LongConst(pc, value)
                case FloatConst(pc, value) => return FloatConst(pc, value)
                case DoubleConst(pc, value) => return DoubleConst(pc, value)
                case StringConst(pc, value) => return StringConst(pc, value)
                case ClassConst(pc, value) => return ClassConst(pc, value)
                case DynamicConst(pc, bootstrapMethod, name, descriptor) =>
                    return DynamicConst(pc, bootstrapMethod, name, descriptor)
                case NullExpr(pc) => return NullExpr(pc)
                case BinaryExpr(pc, cTpe, op, left, right) =>
                    val leftExpr = transformExpr(pc, left)
                    val rightExpr = transformExpr(pc, right)

                    return BinaryExpr(pc, cTpe, op, leftExpr, rightExpr)
                case PrefixExpr(pc, cTpe, op, operand) =>
                    val operandExpr = transformExpr(pc, operand)

                    return PrefixExpr(pc, cTpe, op, operandExpr)
                case PrimitiveTypecastExpr(pc, targetTpe, operand) =>
                    val operandExpr = transformExpr(pc, operand)

                    return PrimitiveTypecastExpr(pc, targetTpe, operandExpr)
                case New(pc, tpe) => return New(pc, tpe)
                case NewArray(pc, counts, tpe) =>
                    val countsExpr = counts.map(c => transformExpr(pc, c))

                    return NewArray(pc, countsExpr, tpe)
                case ArrayLoad(pc, index, arrayRef) =>
                    val indexExpr = transformExpr(pc, index)
                    val arrayRefExpr = transformExpr(pc, arrayRef)

                    return ArrayLoad(pc, indexExpr, arrayRefExpr)
                case ArrayLength(pc, arrayRef) =>
                    val arrayRefExpr = transformExpr(pc, arrayRef)

                    return ArrayLength(pc, arrayRefExpr)
                case GetField(pc, declaringClass, name, declaredFieldType, objRef) =>
                    val objRefExpr = transformExpr(pc, objRef)

                    return GetField(
                        pc,
                        declaringClass,
                        name,
                        declaredFieldType,
                        objRefExpr
                    )
                case GetStatic(pc, declaringClass, name, declaredFieldType) =>
                    return GetStatic(pc, declaringClass, name, declaredFieldType)
                case InvokedynamicFunctionCall(
                        pc,
                        bootstrapMethod,
                        name,
                        descriptor,
                        params
                    ) =>
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return InvokedynamicFunctionCall(
                        pc,
                        bootstrapMethod,
                        name,
                        descriptor,
                        paramsExpr
                    )
                case NonVirtualFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiver,
                        params
                    ) =>
                    val receiverExpr = transformExpr(pc, receiver)
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return NonVirtualFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiverExpr,
                        paramsExpr
                    )
                case VirtualFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiver,
                        params
                    ) =>
                    val receiverExpr = transformExpr(pc, receiver)
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return VirtualFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        receiverExpr,
                        paramsExpr
                    )
                case StaticFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        params
                    ) =>
                    val paramsExpr = params.map(p => transformExpr(pc, p))

                    return StaticFunctionCall(
                        pc,
                        declaringClass,
                        isInterface,
                        name,
                        descriptor,
                        paramsExpr
                    )
                case _ => throw new RuntimeException("Unknown expression: " + expr)
            }

            throw new RuntimeException("Could not transform expr: " + expr)
        }

        tac.stmts.map(stmt => transformStmt(stmt))
    }

}
