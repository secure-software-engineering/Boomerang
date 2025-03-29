package boomerang.scope.opal.transformer

import org.opalj.ai.{AIResult, BaseAI}
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.{ClassHierarchy, Method}
import org.opalj.collection.immutable.IntIntPair
import org.opalj.tac.{ArrayLength, ArrayLoad, ArrayStore, Assignment, BinaryExpr, CaughtException, Checkcast, ClassConst, Compare, DoubleConst, DynamicConst, Expr, ExprStmt, FloatConst, GetField, GetStatic, Goto, IdBasedVar, If, InstanceOf, IntConst, InvokedynamicFunctionCall, InvokedynamicMethodCall, JSR, LongConst, MethodHandleConst, MethodTypeConst, MonitorEnter, MonitorExit, NaiveTACode, New, NewArray, NonVirtualFunctionCall, NonVirtualMethodCall, Nop, NullExpr, Param, PrefixExpr, PrimitiveTypecastExpr, PutField, PutStatic, Ret, Return, ReturnValue, StaticFunctionCall, StaticMethodCall, Stmt, StringConst, Switch, TACNaive, Throw, VirtualFunctionCall, VirtualMethodCall}

import scala.collection.mutable

object LocalTransformer {

  def apply(method: Method, tacNaive: NaiveTACode[_], aiResult: AIResult): Array[Stmt[TacLocal]] = {
    var paramCounter = -1
    var stackCounter = -1
    val currentStack = mutable.Map.empty[Int, TacLocal]

    val operandsArray: aiResult.domain.OperandsArray = aiResult.operandsArray
    val localArray = aiResult.localsArray

    def transformStatement(stmt: Stmt[IdBasedVar]): Stmt[TacLocal] = {
      if (stmt.astID == If.ASTID) {
        val ifStmt = stmt.asIf

        val left = transformExpr(ifStmt.left)
        val right = transformExpr(ifStmt.right)

        return If(ifStmt.pc, left, ifStmt.condition, right, ifStmt.targetStmt)
      }

      if (stmt.astID == Goto.ASTID) {
        return stmt.asGoto
      }

      if (stmt.astID == Ret.ASTID) {
        return stmt.asRet
      }

      if (stmt.astID == JSR.ASTID) {
        return stmt.asJSR
      }

      if (stmt.astID == Switch.ASTID) {
        val switchStmt = stmt.asSwitch
        val index = transformExpr(switchStmt.index)

        // Inserting -1 as it is only used in previous remapping steps
        return Switch(switchStmt.pc, switchStmt.defaultStmt, index, switchStmt.caseStmts.map(p => IntIntPair(-1, p)))
      }

      if (stmt.astID == Assignment.ASTID) {
        val transformedExpr = transformExpr(stmt.asAssignment.expr)
        val isThisAssignment = !method.isStatic && transformedExpr.isVar && transformedExpr.asVar.id == -1

        val target = createNewLocal(stmt.pc, stmt.asAssignment.targetVar, isThisAssignment)

        // Store the current stack and register locals on our own 'stack'
        currentStack(stmt.asAssignment.targetVar.id) = target

        return new Assignment(stmt.pc, target, transformedExpr)
      }

      if (stmt.astID == ReturnValue.ASTID) {
        val returnStmt = stmt.asReturnValue
        val returnValue = transformExpr(returnStmt.expr)

        return ReturnValue(returnStmt.pc, returnValue)
      }

      if (stmt.astID == Return.ASTID) {
        return Return(stmt.asReturn.pc)
      }

      if (stmt.astID == Nop.ASTID) {
        return Nop(stmt.asNop.pc)
      }

      if (stmt.astID == MonitorEnter.ASTID) {
        val monitorEnter = stmt.asMonitorEnter
        val objRef = transformExpr(monitorEnter.objRef)

        return MonitorEnter(monitorEnter.pc, objRef)
      }

      if (stmt.astID == MonitorExit.ASTID) {
        val monitorExit = stmt.asMonitorExit
        val objRef = transformExpr(monitorExit.objRef)

        return MonitorExit(monitorExit.pc, objRef)
      }

      if (stmt.astID == ArrayStore.ASTID) {
        val arrayStore = stmt.asArrayStore

        val arrayRef = transformExpr(arrayStore.arrayRef)
        val index = transformExpr(arrayStore.index)
        val value = transformExpr(arrayStore.value)

        return ArrayStore(arrayStore.pc, arrayRef, index, value)
      }

      if (stmt.astID == Throw.ASTID) {
        val throwStmt = stmt.asThrow
        val exception = transformExpr(throwStmt.exception)

        return Throw(throwStmt.pc, exception)
      }

      if (stmt.astID == PutStatic.ASTID) {
        val putStatic = stmt.asPutStatic
        val value = transformExpr(putStatic.value)

        return PutStatic(putStatic.pc, putStatic.declaringClass, putStatic.name, putStatic.declaredFieldType, value)
      }

      if (stmt.astID == PutField.ASTID) {
        val putField = stmt.asPutField

        val objRef = transformExpr(putField.objRef)
        val value = transformExpr(putField.value)

        return PutField(putField.pc, putField.declaringClass, putField.name, putField.declaredFieldType, objRef, value)
      }

      if (stmt.astID == NonVirtualMethodCall.ASTID) {
        val methodCall = stmt.asNonVirtualMethodCall

        val baseLocal = transformExpr(methodCall.receiver)
        val paramLocals = methodCall.params.map(p => transformExpr(p))

        return NonVirtualMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, baseLocal, paramLocals)
      }

      if (stmt.astID == VirtualMethodCall.ASTID) {
        val methodCall = stmt.asVirtualMethodCall

        val baseLocal = transformExpr(methodCall.receiver)
        val paramLocals = methodCall.params.map(p => transformExpr(p))

        return VirtualMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, baseLocal, paramLocals)
      }

      if (stmt.astID == StaticMethodCall.ASTID) {
        val methodCall = stmt.asStaticMethodCall
        val params = methodCall.params.map(p => transformExpr(p))

        return StaticMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, params)
      }

      if (stmt.astID == InvokedynamicMethodCall.ASTID) {
        val methodCall = stmt.asInvokedynamicMethodCall
        val params = methodCall.params.map(p => transformExpr(p))

        return InvokedynamicMethodCall(methodCall.pc, methodCall.bootstrapMethod, methodCall.name, methodCall.descriptor, params)
      }

      if (stmt.astID == ExprStmt.ASTID) {
        val expr = transformExpr(stmt.asExprStmt.expr)

        return ExprStmt(stmt.pc, expr)
      }

      if (stmt.astID == CaughtException.ASTID) {
        val caughtException = stmt.asCaughtException

        return CaughtException(caughtException.pc, caughtException.exceptionType, caughtException.origins)
      }

      if (stmt.astID == Checkcast.ASTID) {
        val castExpr = stmt.asCheckcast
        val value = transformExpr(castExpr.value)

        return Checkcast(castExpr.pc, value, castExpr.cmpTpe)
      }

      throw new RuntimeException("Could not transform statement: " + stmt)
    }

    def createNewLocal(pc: Int, idBasedVar: IdBasedVar, isThis: Boolean = false): TacLocal = {
      if (pc == -1) {
        val local = localArray(0)

        return new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1))
      }

      val nextPc = tacNaive.stmts(tacNaive.pcToIndex(pc) + 1).pc

      if (idBasedVar.id >= 0) {
        /*if (isThis) {
          val value = operandsArray(nextPc).head
          new StackLocal(-1, idBasedVar.cTpe, value)
        } else {*/
        stackCounter += 1

        val value = operandsArray(nextPc).head
        new StackLocal(stackCounter, idBasedVar.cTpe, value)
        //}
      } else {
        val local = localArray(nextPc)
        new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1))
      }
    }

    def transformExpr(expr: Expr[IdBasedVar]): Expr[TacLocal] = {
      if (expr.isVar) {
        return currentStack.getOrElse(expr.asVar.id, throw new RuntimeException("No local on stack"))
      }

      if (expr.astID == InstanceOf.ASTID) {
        val instanceOf = expr.asInstanceOf
        val value = transformExpr(instanceOf.value)

        return InstanceOf(instanceOf.pc, value, instanceOf.cmpTpe)
      }

      if (expr.astID == Compare.ASTID) {
        val compareExpr = expr.asCompare

        val leftLocal = transformExpr(compareExpr.left)
        val rightLocal = transformExpr(compareExpr.right)

        return Compare(compareExpr.pc, leftLocal, compareExpr.condition, rightLocal)
      }

      if (expr.astID == Param.ASTID) {
        paramCounter += 1
        return new ParameterLocal(paramCounter, expr.asParam.cTpe, expr.asParam.name)
      }

      if (expr.astID == MethodTypeConst.ASTID) {
        return MethodTypeConst(expr.asMethodTypeConst.pc, expr.asMethodTypeConst.value)
      }

      if (expr.astID == MethodHandleConst.ASTID) {
        return MethodHandleConst(expr.asMethodHandleConst.pc, expr.asMethodHandleConst.value)
      }

      if (expr.astID == IntConst.ASTID) {
        return IntConst(expr.asIntConst.pc, expr.asIntConst.value)
      }

      if (expr.astID == LongConst.ASTID) {
        return LongConst(expr.asLongConst.pc, expr.asLongConst.value)
      }

      if (expr.astID == FloatConst.ASTID) {
        return FloatConst(expr.asFloatConst.pc, expr.asFloatConst.value)
      }

      if (expr.astID == DoubleConst.ASTID) {
        return DoubleConst(expr.asDoubleConst.pc, expr.asDoubleConst.value)
      }

      if (expr.astID == StringConst.ASTID) {
        return StringConst(expr.asStringConst.pc, expr.asStringConst.value)
      }

      if (expr.astID == ClassConst.ASTID) {
        return ClassConst(expr.asClassConst.pc, expr.asClassConst.value)
      }

      if (expr.astID == DynamicConst.ASTID) {
        return DynamicConst(expr.asDynamicConst.pc, expr.asDynamicConst.bootstrapMethod, expr.asDynamicConst.name, expr.asDynamicConst.descriptor)
      }

      if (expr.astID == NullExpr.ASTID) {
        return NullExpr(expr.asNullExpr.pc)
      }

      if (expr.astID == BinaryExpr.ASTID) {
        val binaryExpr = expr.asBinaryExpr

        val left = transformExpr(binaryExpr.left)
        val right = transformExpr(binaryExpr.right)

        return BinaryExpr(binaryExpr.pc, binaryExpr.cTpe, binaryExpr.op, left, right)
      }

      if (expr.astID == PrefixExpr.ASTID) {
        val prefixExpr = expr.asPrefixExpr
        val operand = transformExpr(prefixExpr.operand)

        return PrefixExpr(prefixExpr.pc, prefixExpr.cTpe, prefixExpr.op, operand)
      }

      if (expr.astID == PrimitiveTypecastExpr.ASTID) {
        val primitiveTypecastExpr = expr.asPrimitiveTypeCastExpr
        val operand = transformExpr(primitiveTypecastExpr.operand)

        return PrimitiveTypecastExpr(primitiveTypecastExpr.pc, primitiveTypecastExpr.targetTpe, operand)
      }

      if (expr.astID == New.ASTID) {
        return New(expr.asNew.pc, expr.asNew.tpe)
      }

      if (expr.astID == NewArray.ASTID) {
        val newArray = expr.asNewArray
        val counts = newArray.counts.map(c => transformExpr(c))

        return NewArray(newArray.pc, counts, newArray.tpe)
      }

      if (expr.astID == ArrayLoad.ASTID) {
        val arrayLoad = expr.asArrayLoad

        val index = transformExpr(arrayLoad.index)
        val arrayRef = transformExpr(arrayLoad.arrayRef)

        return ArrayLoad(arrayLoad.pc, index, arrayRef)
      }

      if (expr.astID == ArrayLength.ASTID) {
        val arrayLength = expr.asArrayLength
        val arrayRef = transformExpr(arrayLength.arrayRef)

        return ArrayLength(arrayLength.pc, arrayRef)
      }

      if (expr.astID == GetField.ASTID) {
        val getField = expr.asGetField
        val objRef = transformExpr(getField.objRef)

        return GetField(getField.pc, getField.declaringClass, getField.name, getField.declaredFieldType, objRef)
      }

      if (expr.astID == GetStatic.ASTID) {
        return GetStatic(expr.asGetStatic.pc, expr.asGetStatic.declaringClass, expr.asGetStatic.name, expr.asGetStatic.declaredFieldType)
      }

      if (expr.astID == InvokedynamicFunctionCall.ASTID) {
        val functionCall = expr.asInvokedynamicFunctionCall
        val params = functionCall.params.map(p => transformExpr(p))

        return InvokedynamicFunctionCall(functionCall.pc, functionCall.bootstrapMethod, functionCall.name, functionCall.descriptor, params)
      }

      if (expr.astID == NonVirtualFunctionCall.ASTID) {
        val functionCall = expr.asNonVirtualFunctionCall

        val base = transformExpr(functionCall.receiver)
        val params = functionCall.params.map(p => transformExpr(p))

        return NonVirtualFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, base, params)
      }

      if (expr.astID == VirtualFunctionCall.ASTID) {
        val functionCall = expr.asVirtualFunctionCall

        val base = transformExpr(functionCall.receiver)
        val params = functionCall.params.map(p => transformExpr(p))

        return VirtualFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, base, params)
      }

      if (expr.astID == StaticFunctionCall.ASTID) {
        val functionCall = expr.asStaticFunctionCall

        val params = functionCall.params
        val paramLocals = params.map(p => transformExpr(p))

        return StaticFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, paramLocals)
      }

      throw new RuntimeException("Could not transform expression: " + expr)
    }

    tacNaive.stmts.map(stmt => transformStatement(stmt))
  }
}
