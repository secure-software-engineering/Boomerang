package boomerang.scope.opal.transformer

import org.opalj.ai.{AIResult, BaseAI, Domain}
import org.opalj.br.Method
import org.opalj.collection.immutable.IntIntPair
import org.opalj.tac.{ArrayLength, ArrayLoad, ArrayStore, Assignment, BinaryExpr, CaughtException, Checkcast, ClassConst, Compare, DoubleConst, DynamicConst, Expr, ExprStmt, FloatConst, GetField, GetStatic, Goto, IdBasedVar, If, InstanceOf, IntConst, InvokedynamicFunctionCall, InvokedynamicMethodCall, JSR, LongConst, MethodHandleConst, MethodTypeConst, MonitorEnter, MonitorExit, NaiveTACode, New, NewArray, NonVirtualFunctionCall, NonVirtualMethodCall, Nop, NullExpr, Param, PrefixExpr, PrimitiveTypecastExpr, PutField, PutStatic, Ret, Return, ReturnValue, StaticFunctionCall, StaticMethodCall, Stmt, StringConst, Switch, Throw, VirtualFunctionCall, VirtualMethodCall}

import scala.collection.mutable

object LocalTransformer {

  def apply(method: Method, tacNaive: NaiveTACode[_], domain: Domain, operandStack: OperandStack): Array[Stmt[TacLocal]] = {
    var paramCounter = -1
    val currentLocals = mutable.Map.empty[Int, TacLocal]
    val exceptionHandlers = tacNaive.exceptionHandlers.map(eh => eh.handlerPC)

    // Domain components
    val aiResult: AIResult = BaseAI(method, domain)
    val operandsArray: aiResult.domain.OperandsArray = aiResult.operandsArray
    val localArray: aiResult.domain.LocalsArray = aiResult.localsArray

    def transformStatement(stmt: Stmt[IdBasedVar]): Stmt[TacLocal] = {
      stmt.astID match {
        case If.ASTID =>
          val ifStmt = stmt.asIf

          val left = transformExpr(stmt.pc, ifStmt.left)
          val right = transformExpr(stmt.pc, ifStmt.right)

          return If(ifStmt.pc, left, ifStmt.condition, right, ifStmt.targetStmt)
        case Goto.ASTID =>
          return stmt.asGoto
        case Ret.ASTID =>
          return stmt.asRet
        case JSR.ASTID =>
          return stmt.asJSR
        case Switch.ASTID =>
          val switchStmt = stmt.asSwitch
          val index = transformExpr(stmt.pc, switchStmt.index)

          // Inserting -1 as it is only used in previous remapping steps
          return Switch(switchStmt.pc, switchStmt.defaultStmt, index, switchStmt.caseStmts.map(p => IntIntPair(-1, p)))
        case Assignment.ASTID =>
          val assignStmt = stmt.asAssignment
          val targetVar = assignStmt.targetVar

          if (assignStmt.pc == -1) {
            val paramLocal = createNewParameterLocal(targetVar)
            val transformedExpr = transformExpr(stmt.pc, assignStmt.expr)

            currentLocals(paramLocal.id) = paramLocal
            return new Assignment[TacLocal](stmt.pc, paramLocal, transformedExpr)
          }

          if (exceptionHandlers.contains(tacNaive.pcToIndex(stmt.pc)) && stmt.asAssignment.expr.isVar) {
            val exceptionLocal = createNewExceptionLocal(stmt.pc, stmt.asAssignment.expr.asVar)
            val targetLocal = if (targetVar.id >= 0) createNewStackLocal(stmt.pc, targetVar, exceptionLocal.isThisLocal) else createNewRegisterLocal(stmt.pc, targetVar, exceptionLocal.isThisLocal)

            currentLocals(exceptionLocal.id) = exceptionLocal
            currentLocals(targetLocal.id) = targetLocal
            return new Assignment[TacLocal](stmt.pc, targetLocal, exceptionLocal)
          }

          if (targetVar.id >= 0) {
            val transformedExpr = transformExpr(stmt.pc, assignStmt.expr)

            val isThisAssignment = transformedExpr.isVar && transformedExpr.asVar.isThisLocal
            val targetLocal = createNewStackLocal(stmt.pc, targetVar, isThisAssignment)

            currentLocals(targetLocal.id) = targetLocal
            return new Assignment[TacLocal](stmt.pc, targetLocal, transformedExpr)
          }

          if (targetVar.id < 0) {
            val transformedExpr = transformExpr(stmt.pc, assignStmt.expr)

            val isThisAssignment = transformedExpr.isVar && transformedExpr.asVar.isThisLocal
            val targetLocal = createNewRegisterLocal(stmt.pc, targetVar, isThisAssignment)

            currentLocals(targetLocal.id) = targetLocal
            return new Assignment[TacLocal](stmt.pc, targetLocal, transformedExpr)
          }

          throw new RuntimeException("Could not transform assignment: " + assignStmt)

          /*val transformedExpr = if (exceptionHandlers.contains(tacNaive.pcToIndex(stmt.pc)) && stmt.asAssignment.expr.isVar) {
            val exceptionVar = createNewLocal(stmt.pc, stmt.asAssignment.expr.asVar)
            currentLocals(exceptionVar.id) = exceptionVar

            exceptionVar
          } else {
            transformExpr(stmt.pc, stmt.asAssignment.expr)
          }
          val isThisAssignment = transformedExpr.isVar && transformedExpr.asVar.isThisLocal

          val target = createNewLocal(stmt.pc, stmt.asAssignment.targetVar, isThisAssignment)

          // Store the current stack and register locals on our own 'stack'
          currentLocals(target.id) = target

          return new Assignment(stmt.pc, target, transformedExpr)*/
        case ReturnValue.ASTID =>
          val returnStmt = stmt.asReturnValue
          val returnValue = transformExpr(stmt.pc, returnStmt.expr)

          return ReturnValue(returnStmt.pc, returnValue)
        case Return.ASTID =>
          return stmt.asReturn
        case Nop.ASTID =>
          return stmt.asNop
        case MonitorEnter.ASTID =>
          val monitorEnter = stmt.asMonitorEnter
          val objRef = transformExpr(stmt.pc, monitorEnter.objRef)

          return MonitorEnter(monitorEnter.pc, objRef)
        case MonitorExit.ASTID =>
          val monitorExit = stmt.asMonitorExit
          val objRef = transformExpr(stmt.pc, monitorExit.objRef)

          return MonitorExit(monitorExit.pc, objRef)
        case ArrayStore.ASTID =>
          val arrayStore = stmt.asArrayStore

          val arrayRef = transformExpr(stmt.pc, arrayStore.arrayRef)
          val index = transformExpr(stmt.pc, arrayStore.index)
          val value = transformExpr(stmt.pc, arrayStore.value)

          return ArrayStore(arrayStore.pc, arrayRef, index, value)
        case Throw.ASTID =>
          val throwStmt = stmt.asThrow
          val exception = transformExpr(stmt.pc, throwStmt.exception)

          return Throw(throwStmt.pc, exception)
        case PutStatic.ASTID =>
          val putStatic = stmt.asPutStatic
          val value = transformExpr(stmt.pc, putStatic.value)

          return PutStatic(putStatic.pc, putStatic.declaringClass, putStatic.name, putStatic.declaredFieldType, value)
        case PutField.ASTID =>
          val putField = stmt.asPutField

          val objRef = transformExpr(stmt.pc, putField.objRef)
          val value = transformExpr(stmt.pc, putField.value)

          return PutField(putField.pc, putField.declaringClass, putField.name, putField.declaredFieldType, objRef, value)
        case NonVirtualMethodCall.ASTID =>
          val methodCall = stmt.asNonVirtualMethodCall

          val baseLocal = transformExpr(stmt.pc, methodCall.receiver)
          val paramLocals = methodCall.params.map(p => transformExpr(stmt.pc, p))

          return NonVirtualMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, baseLocal, paramLocals)
        case VirtualMethodCall.ASTID =>
          val methodCall = stmt.asVirtualMethodCall

          val baseLocal = transformExpr(stmt.pc, methodCall.receiver)
          val paramLocals = methodCall.params.map(p => transformExpr(stmt.pc, p))

          return VirtualMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, baseLocal, paramLocals)
        case StaticMethodCall.ASTID =>
          val methodCall = stmt.asStaticMethodCall
          val params = methodCall.params.map(p => transformExpr(stmt.pc, p))

          return StaticMethodCall(methodCall.pc, methodCall.declaringClass, methodCall.isInterface, methodCall.name, methodCall.descriptor, params)
        case InvokedynamicMethodCall.ASTID =>
          val methodCall = stmt.asInvokedynamicMethodCall
          val params = methodCall.params.map(p => transformExpr(stmt.pc, p))

          return InvokedynamicMethodCall(methodCall.pc, methodCall.bootstrapMethod, methodCall.name, methodCall.descriptor, params)
        case ExprStmt.ASTID =>
          val expr = transformExpr(stmt.pc, stmt.asExprStmt.expr)

          return ExprStmt(stmt.pc, expr)
        case CaughtException.ASTID =>
          val caughtException = stmt.asCaughtException

          return CaughtException(caughtException.pc, caughtException.exceptionType, caughtException.origins)
        case Checkcast.ASTID =>
          val castExpr = stmt.asCheckcast
          val value = transformExpr(stmt.pc, castExpr.value)

          return Checkcast(castExpr.pc, value, castExpr.cmpTpe)
        case _ => throw new RuntimeException("Unknown statement: " + stmt)
      }

      throw new RuntimeException("Could not transform statement: " + stmt)
    }

    def createNewParameterLocal(idBasedVar: IdBasedVar): TacLocal = {
      val local = localArray(0)
      val isThisDef = method.isNotStatic && idBasedVar.id == -1

      new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1), isThisDef)
    }

    def createNewStackLocal(pc: Int, idBasedVar: IdBasedVar, isThis: Boolean): TacLocal = {
      val nextPc = tacNaive.stmts(tacNaive.pcToIndex(pc) + 1).pc
      val index = tacNaive.pcToIndex(pc)
      val counter = operandStack.operandDefSite(index)

      val value = operandsArray(nextPc).head
      new StackLocal(counter, idBasedVar.cTpe, value, isThis)
    }

    def createNewRegisterLocal(pc: Int, idBasedVar: IdBasedVar, isThis: Boolean): TacLocal = {
      val nextPc = tacNaive.stmts(tacNaive.pcToIndex(pc) + 1).pc

      val local = localArray(nextPc)
      new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1), isThis)
    }

    def createNewExceptionLocal(pc: Int, idBasedVar: IdBasedVar): TacLocal = {
      val index = tacNaive.pcToIndex(pc)
      val counter = operandStack.operandDefSite(index)

      val value = operandsArray(pc).head
      new ExceptionLocal(counter, idBasedVar.cTpe, value)
    }

    def transformExpr(pc: Int, expr: Expr[IdBasedVar]): Expr[TacLocal] = {
      if (expr.isVar) {
        if (expr.asVar.id >= 0) {
          val index = tacNaive.pcToIndex(pc)
          val count = operandStack.operandCounterAtStmt(index, expr.asVar.id)

          return currentLocals(count)
        }

        return currentLocals(expr.asVar.id)
      }

      expr.astID match {
        case InstanceOf.ASTID =>
          val instanceOf = expr.asInstanceOf
          val value = transformExpr(pc, instanceOf.value)

          return InstanceOf(instanceOf.pc, value, instanceOf.cmpTpe)
        case Compare.ASTID =>
          val compareExpr = expr.asCompare

          val leftLocal = transformExpr(pc, compareExpr.left)
          val rightLocal = transformExpr(pc, compareExpr.right)

          return Compare(compareExpr.pc, leftLocal, compareExpr.condition, rightLocal)
        case Param.ASTID =>
          paramCounter += 1

          return new ParameterLocal(paramCounter, expr.asParam.cTpe, expr.asParam.name)
        case MethodTypeConst.ASTID =>
          return expr.asMethodTypeConst
        case MethodHandleConst.ASTID =>
          return expr.asMethodHandleConst
        case IntConst.ASTID =>
          return expr.asIntConst
        case LongConst.ASTID =>
          return expr.asLongConst
        case FloatConst.ASTID =>
          return expr.asFloatConst
        case DoubleConst.ASTID =>
          return expr.asDoubleConst
        case StringConst.ASTID =>
          return expr.asStringConst
        case ClassConst.ASTID =>
          return expr.asClassConst
        case DynamicConst.ASTID =>
          return expr.asDynamicConst
        case NullExpr.ASTID =>
          return expr.asNullExpr
        case BinaryExpr.ASTID =>
          val binaryExpr = expr.asBinaryExpr

          val left = transformExpr(pc, binaryExpr.left)
          val right = transformExpr(pc, binaryExpr.right)

          return BinaryExpr(binaryExpr.pc, binaryExpr.cTpe, binaryExpr.op, left, right)
        case PrefixExpr.ASTID =>
          val prefixExpr = expr.asPrefixExpr
          val operand = transformExpr(pc, prefixExpr.operand)

          return PrefixExpr(prefixExpr.pc, prefixExpr.cTpe, prefixExpr.op, operand)
        case PrimitiveTypecastExpr.ASTID =>
          val primitiveTypecastExpr = expr.asPrimitiveTypeCastExpr
          val operand = transformExpr(pc, primitiveTypecastExpr.operand)

          return PrimitiveTypecastExpr(primitiveTypecastExpr.pc, primitiveTypecastExpr.targetTpe, operand)
        case New.ASTID =>
          return expr.asNew
        case NewArray.ASTID =>
          val newArray = expr.asNewArray
          val counts = newArray.counts.map(c => transformExpr(pc, c))

          return NewArray(newArray.pc, counts, newArray.tpe)
        case ArrayLoad.ASTID =>
          val arrayLoad = expr.asArrayLoad

          val index = transformExpr(pc, arrayLoad.index)
          val arrayRef = transformExpr(pc, arrayLoad.arrayRef)

          return ArrayLoad(arrayLoad.pc, index, arrayRef)
        case ArrayLength.ASTID =>
          val arrayLength = expr.asArrayLength
          val arrayRef = transformExpr(pc, arrayLength.arrayRef)

          return ArrayLength(arrayLength.pc, arrayRef)
        case GetField.ASTID =>
          val getField = expr.asGetField
          val objRef = transformExpr(pc, getField.objRef)

          return GetField(getField.pc, getField.declaringClass, getField.name, getField.declaredFieldType, objRef)
        case GetStatic.ASTID =>
          return expr.asGetStatic
        case InvokedynamicFunctionCall.ASTID =>
          val functionCall = expr.asInvokedynamicFunctionCall
          val params = functionCall.params.map(p => transformExpr(pc, p))

          return InvokedynamicFunctionCall(functionCall.pc, functionCall.bootstrapMethod, functionCall.name, functionCall.descriptor, params)
        case NonVirtualFunctionCall.ASTID =>
          val functionCall = expr.asNonVirtualFunctionCall

          val base = transformExpr(pc, functionCall.receiver)
          val params = functionCall.params.map(p => transformExpr(pc, p))

          return NonVirtualFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, base, params)
        case VirtualFunctionCall.ASTID =>
          val functionCall = expr.asVirtualFunctionCall

          val base = transformExpr(pc, functionCall.receiver)
          val params = functionCall.params.map(p => transformExpr(pc, p))

          return VirtualFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, base, params)
        case StaticFunctionCall.ASTID =>
          val functionCall = expr.asStaticFunctionCall

          val params = functionCall.params
          val paramLocals = params.map(p => transformExpr(pc, p))

          return StaticFunctionCall(functionCall.pc, functionCall.declaringClass, functionCall.isInterface, functionCall.name, functionCall.descriptor, paramLocals)
        case _ => throw new RuntimeException("Unknown expression: " + expr)
      }

      throw new RuntimeException("Could not transform expression: " + expr)
    }

    tacNaive.stmts.map(stmt => transformStatement(stmt))
  }
}
