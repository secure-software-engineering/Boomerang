package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.{ExceptionLocal, ParameterLocal, RegisterLocal, StackLocal, TacLocal}
import boomerang.scope.opal.transformation.stack.OperandStackHandler
import org.opalj.ai.{AIResult, BaseAI, Domain}
import org.opalj.br.{Method, PC}
import org.opalj.tac.{ArrayLength, ArrayLoad, ArrayStore, Assignment, BinaryExpr, CaughtException, Checkcast, ClassConst, Compare, DoubleConst, DynamicConst, Expr, ExprStmt, FloatConst, GetField, GetStatic, Goto, IdBasedVar, If, InstanceOf, IntConst, InvokedynamicFunctionCall, InvokedynamicMethodCall, JSR, LongConst, MethodHandleConst, MethodTypeConst, MonitorEnter, MonitorExit, NaiveTACode, New, NewArray, NonVirtualFunctionCall, NonVirtualMethodCall, Nop, NullExpr, Param, PrefixExpr, PrimitiveTypecastExpr, PutField, PutStatic, Ret, Return, ReturnValue, StaticFunctionCall, StaticMethodCall, Stmt, StringConst, Switch, Throw, VirtualFunctionCall, VirtualMethodCall}

import scala.collection.mutable

object LocalTransformer {

  def apply(method: Method, tac: NaiveTACode[_], stackHandler: OperandStackHandler, domain: Domain): Array[Stmt[TacLocal]] = {
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
            val paramLocal = createParameterLocal(targetVar)
            val transformedExpr = transformExpr(pc, expr)

            currentLocals(paramLocal.id) = paramLocal
            return Assignment(pc, paramLocal, transformedExpr)
          }

          if (targetVar.id >= 0) {
            val transformedExpr = transformExpr(pc, expr)

            // Consider 'this' assignment from register to stack: s = this
            val isThisAssignment = transformedExpr.isVar && transformedExpr.asVar.isThisLocal
            val targetLocal = createStackLocal(pc, targetVar, isThisAssignment)

            currentLocals(targetLocal.id) = targetLocal
            return Assignment(pc, targetLocal, transformedExpr)
          }

          if (targetVar.id < 0) {
            val transformedExpr = transformExpr(pc, expr)

            // Consider 'this' assignment from stack to register: r0 = $this
            val isThisAssignment = transformedExpr.isVar && transformedExpr.asVar.isThisLocal
            val targetLocal = createRegisterLocal(pc, targetVar, isThisAssignment)

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

          return PutStatic(pc, declaringClass, name, declaredFieldType, valueExpr)
        case PutField(pc, declaringClass, name, declaredFieldType, objRef, value) =>
          val objRefExpr = transformExpr(pc, objRef)
          val valueExpr = transformExpr(pc, value)

          return PutField(pc, declaringClass, name, declaredFieldType, objRefExpr, valueExpr)
        case NonVirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(pc, receiver)
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return NonVirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case VirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(pc, receiver)
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return VirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case StaticMethodCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return StaticMethodCall(pc, declaringClass, isInterface, name, descriptor, paramsExpr)
        case InvokedynamicMethodCall(pc, bootstrapMethod, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return InvokedynamicMethodCall(pc, bootstrapMethod, name, descriptor, paramsExpr)
        case ExprStmt(pc, expr) =>
          val transformedExpr = transformExpr(pc, expr)

          return ExprStmt(pc, transformedExpr)
        case CaughtException(pc, exceptionType, throwingStatements) => return CaughtException(pc, exceptionType, throwingStatements)
        case Checkcast(pc, value, cmpTpe) =>
          val valueExpr = transformExpr(pc, value)
          // TODO Transform into assignment
          return Checkcast(pc, valueExpr, cmpTpe)
        case _ => throw new RuntimeException("Unknown statement: " + stmt)
      }

      throw new RuntimeException("Could not transform statement: " + stmt)
    }

    def isThisVar(idBasedVar: IdBasedVar): Boolean = method.isNotStatic && idBasedVar.id == -1

    def createParameterLocal(idBasedVar: IdBasedVar): TacLocal = {
      val local = localArray(0)

      new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1), isThisVar(idBasedVar))
    }

    def createStackLocal(pc: PC, idBasedVar: IdBasedVar, isThis: Boolean): TacLocal = {
      val nextPc = method.body.get.pcOfNextInstruction(pc)
      val value = operandsArray(nextPc).head

      val counter = stackHandler.defSiteAtPc(pc)
      new StackLocal(counter, idBasedVar.cTpe, value, isThis)
    }

    def createRegisterLocal(pc: PC, idBasedVar: IdBasedVar, isThis: Boolean): TacLocal = {
      val nextPc = method.body.get.pcOfNextInstruction(pc)
      val local = localArray(nextPc)

      new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1), isThis)
    }

    def createExceptionLocal(pc: PC, idBasedVar: IdBasedVar, localId: Int): TacLocal = {
      val value = operandsArray(pc).head

      new ExceptionLocal(localId, idBasedVar.cTpe, value)
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
            val counter = stackHandler.counterForOperand(pc, v.id)

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
        case DynamicConst(pc, bootstrapMethod, name, descriptor) => return DynamicConst(pc, bootstrapMethod, name, descriptor)
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

          return GetField(pc, declaringClass, name, declaredFieldType, objRefExpr)
        case GetStatic(pc, declaringClass, name, declaredFieldType) => return GetStatic(pc, declaringClass, name, declaredFieldType)
        case InvokedynamicFunctionCall(pc, bootstrapMethod, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return InvokedynamicFunctionCall(pc, bootstrapMethod, name, descriptor, paramsExpr)
        case NonVirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(pc, receiver)
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return NonVirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case VirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(pc, receiver)
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return VirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case StaticFunctionCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(pc, p))

          return StaticFunctionCall(pc, declaringClass, isInterface, name, descriptor, paramsExpr)
        case _ => throw new RuntimeException("Unknown expression: " + expr)
      }

      throw new RuntimeException("Could not transform expr: " + expr)
    }

    tac.stmts.map(stmt => transformStmt(stmt))
  }

}
