package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.{ExceptionLocal, ParameterLocal, RegisterLocal, TacLocal}
import boomerang.scope.opal.transformation.stack.OperandStackHandler
import org.opalj.ai.{AIResult, BaseAI, Domain}
import org.opalj.br.{Method, PC}
import org.opalj.tac.{ArrayLength, ArrayLoad, ArrayStore, Assignment, BinaryExpr, CaughtException, Checkcast, ClassConst, Compare, DoubleConst, DynamicConst, Expr, ExprStmt, FloatConst, GetField, GetStatic, Goto, IdBasedVar, If, InstanceOf, IntConst, InvokedynamicFunctionCall, InvokedynamicMethodCall, JSR, LongConst, MethodHandleConst, MethodTypeConst, MonitorEnter, MonitorExit, NaiveTACode, New, NewArray, NonVirtualFunctionCall, NonVirtualMethodCall, Nop, NullExpr, Param, PrefixExpr, PrimitiveTypecastExpr, PutField, PutStatic, Ret, Return, ReturnValue, StaticFunctionCall, StaticMethodCall, Stmt, StringConst, Switch, Throw, VirtualFunctionCall, VirtualMethodCall}

object LocalTransformer {

  def apply(method: Method, tac: NaiveTACode[_], stackHandler: OperandStackHandler, domain: Domain): Array[Stmt[TacLocal]] = {
    var paramCount = -1
    var exceptionCount = -1
    val exceptionHandlers = tac.exceptionHandlers.map(eh => eh.handlerPC)

    // Domain components
    val aiResult: AIResult = BaseAI(method, domain)
    val operandsArray: aiResult.domain.OperandsArray = aiResult.operandsArray
    val localArray: aiResult.domain.LocalsArray = aiResult.localsArray

    def transformStmt(stmt: Stmt[IdBasedVar]): Stmt[TacLocal] = {
      stmt match {
        case If(pc, left, condition, right, target) =>
          val leftExpr = transformExpr(left)
          val rightExpr = transformExpr(right)

          return If(pc, leftExpr, condition, rightExpr, target)
        case Goto(pc, target) => return Goto(pc, target)
        case Ret(pc, returnAddresses) => return Ret(pc, returnAddresses)
        case JSR(pc, target) => return JSR(pc, target)
        case Switch(pc, defaultTarget, index, nPairs) =>
          val indexExpr = transformExpr(index)

          return Switch(pc,defaultTarget, indexExpr, nPairs)
        case Assignment(pc, targetVar, expr) =>
          // Parameter definition statements
          if (pc == -1) {
            val paramLocal = createNewParameterLocal(targetVar)
            val transformedExpr = transformExpr(expr)

            return Assignment(pc, paramLocal, transformedExpr)
          }

          // Definition of exception handlers
          if (exceptionHandlers.contains(tac.pcToIndex(pc)) && expr.isVar) {
            val exceptionLocal = createNewExceptionLocal(pc, expr.asVar)
          }
          // TODO
        case ReturnValue(pc, expr) =>
          val returnExpr = transformExpr(expr)

          return ReturnValue(pc, returnExpr)
        case Return(pc) => return Return(pc)
        case Nop(pc) => return Nop(pc)
        case MonitorEnter(pc, objRef) =>
          val objRefExpr = transformExpr(objRef)

          return MonitorEnter(pc, objRefExpr)
        case MonitorExit(pc, objRef) =>
          val objRefExpr = transformExpr(objRef)

          return MonitorExit(pc, objRefExpr)
        case ArrayStore(pc, arrayRef, index, value) =>
          val arrayRefExpr = transformExpr(arrayRef)
          val indexExpr = transformExpr(index)
          val valueExpr = transformExpr(value)

          return ArrayStore(pc, arrayRefExpr, indexExpr, valueExpr)
        case Throw(pc, exception) =>
          val exceptionExpr = transformExpr(exception)

          return Throw(pc, exceptionExpr)
        case PutStatic(pc, declaringClass, name, declaredFieldType, value) =>
          val valueExpr = transformExpr(value)

          return PutStatic(pc, declaringClass, name, declaredFieldType, valueExpr)
        case PutField(pc, declaringClass, name, declaredFieldType, objRef, value) =>
          val objRefExpr = transformExpr(objRef)
          val valueExpr = transformExpr(value)

          return PutField(pc, declaringClass, name, declaredFieldType, objRefExpr, valueExpr)
        case NonVirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(receiver)
          val paramsExpr = params.map(p => transformExpr(p))

          return NonVirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case VirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(receiver)
          val paramsExpr = params.map(p => transformExpr(p))

          return VirtualMethodCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case StaticMethodCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(p))

          return StaticMethodCall(pc, declaringClass, isInterface, name, descriptor, paramsExpr)
        case InvokedynamicMethodCall(pc, bootstrapMethod, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(p))

          return InvokedynamicMethodCall(pc, bootstrapMethod, name, descriptor, paramsExpr)
        case ExprStmt(pc, expr) =>
          val transformedExpr = transformExpr(expr)

          return ExprStmt(pc, transformedExpr)
        case CaughtException(pc, exceptionType, throwingStatements) => return CaughtException(pc, exceptionType, throwingStatements)
        case Checkcast(pc, value, cmpTpe) =>
          val valueExpr = transformExpr(value)
          // TODO Transform into assignment
          return Checkcast(pc, valueExpr, cmpTpe)
        case _ => throw new RuntimeException("Unknown statement: " + stmt)
      }

      throw new RuntimeException("Could not transform statement: " + stmt)
    }

    def isThisVar(idBasedVar: IdBasedVar): Boolean = method.isNotStatic && idBasedVar.id == -1

    def createNewParameterLocal(idBasedVar: IdBasedVar): TacLocal = {
      val local = localArray(0)

      new RegisterLocal(idBasedVar.id, idBasedVar.cTpe, local(-idBasedVar.id - 1), isThisVar(idBasedVar))
    }

    def createNewExceptionLocal(pc: PC, idBasedVar: IdBasedVar): TacLocal = {
      exceptionCount += 1

      val value = operandsArray(pc).head
      new ExceptionLocal(exceptionCount, idBasedVar.cTpe, value)
    }

    def transformExpr(expr: Expr[IdBasedVar]): Expr[TacLocal] = {
      expr match {
        case v: IdBasedVar => ???
        case InstanceOf(pc, value, cmpTpe) =>
          val valueExpr = transformExpr(value)

          return InstanceOf(pc, valueExpr, cmpTpe)
        case Compare(pc, left, condition, right) =>
          val leftExpr = transformExpr(left)
          val rightExpr = transformExpr(right)

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
          val leftExpr = transformExpr(left)
          val rightExpr = transformExpr(right)

          return BinaryExpr(pc, cTpe, op, leftExpr, rightExpr)
        case PrefixExpr(pc, cTpe, op, operand) =>
          val operandExpr = transformExpr(operand)

          return PrefixExpr(pc, cTpe, op, operandExpr)
        case PrimitiveTypecastExpr(pc, targetTpe, operand) =>
          val operandExpr = transformExpr(operand)

          return PrimitiveTypecastExpr(pc, targetTpe, operandExpr)
        case New(pc, tpe) => return New(pc, tpe)
        case NewArray(pc, counts, tpe) =>
          val countsExpr = counts.map(c => transformExpr(c))

          return NewArray(pc, countsExpr, tpe)
        case ArrayLoad(pc, index, arrayRef) =>
          val indexExpr = transformExpr(index)
          val arrayRefExpr = transformExpr(arrayRef)

          return ArrayLoad(pc, indexExpr, arrayRefExpr)
        case ArrayLength(pc, arrayRef) =>
          val arrayRefExpr = transformExpr(arrayRef)

          return ArrayLength(pc, arrayRefExpr)
        case GetField(pc, declaringClass, name, declaredFieldType, objRef) =>
          val objRefExpr = transformExpr(objRef)

          return GetField(pc, declaringClass, name, declaredFieldType, objRefExpr)
        case GetStatic(pc, declaringClass, name, declaredFieldType) => return GetStatic(pc, declaringClass, name, declaredFieldType)
        case InvokedynamicFunctionCall(pc, bootstrapMethod, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(p))

          return InvokedynamicFunctionCall(pc, bootstrapMethod, name, descriptor, paramsExpr)
        case NonVirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(receiver)
          val paramsExpr = params.map(p => transformExpr(p))

          return NonVirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case VirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiver, params) =>
          val receiverExpr = transformExpr(receiver)
          val paramsExpr = params.map(p => transformExpr(p))

          return VirtualFunctionCall(pc, declaringClass, isInterface, name, descriptor, receiverExpr, paramsExpr)
        case StaticFunctionCall(pc, declaringClass, isInterface, name, descriptor, params) =>
          val paramsExpr = params.map(p => transformExpr(p))

          return StaticFunctionCall(pc, declaringClass, isInterface, name, descriptor, paramsExpr)
        case _ => throw new RuntimeException("Unknown expression: " + expr)
      }

      throw new RuntimeException("Could not transform expr: " + expr)
    }

    tac.stmts.map(stmt => transformStmt(stmt))
  }

}
