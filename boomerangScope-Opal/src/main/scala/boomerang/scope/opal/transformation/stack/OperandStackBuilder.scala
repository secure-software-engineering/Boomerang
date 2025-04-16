package boomerang.scope.opal.transformation.stack

import org.opalj.br.{Method, NoPCs}
import org.opalj.br.instructions.{DUP, DUP2, DUP2_X1, DUP2_X2, DUP_X1, DUP_X2, NOP, POP, POP2, WIDE}
import org.opalj.bytecode.PC
import org.opalj.tac.{ArrayLength, ArrayLoad, ArrayStore, Assignment, BinaryExpr, CaughtException, Checkcast, ClassConst, Compare, Const, DoubleConst, DynamicConst, Expr, ExprStmt, FloatConst, GetField, GetStatic, Goto, IdBasedVar, If, InstanceOf, IntConst, InvokedynamicFunctionCall, InvokedynamicMethodCall, JSR, LongConst, MethodHandleConst, MethodTypeConst, MonitorEnter, MonitorExit, NaiveTACode, New, NewArray, NonVirtualFunctionCall, NonVirtualMethodCall, Nop, NullExpr, Param, PrefixExpr, PrimitiveTypecastExpr, PutField, PutStatic, Ret, Return, ReturnValue, StaticFunctionCall, StaticMethodCall, Stmt, StringConst, Switch, Throw, VirtualFunctionCall, VirtualMethodCall}

object OperandStackBuilder {

  def apply(method: Method, tacNaive: NaiveTACode[_]): OperandStackHandler = {
    val stackHandler = new OperandStackHandler

    // Initialize work list; we always start with pc 0
    var workList = List(0)

    val exceptionHandlersPc = tacNaive.exceptionHandlers.map(eh => tacNaive.stmts(eh.handlerPC).pc)
    for (eh <- exceptionHandlersPc) {
      workList ::= eh
    }

    while (workList.nonEmpty) {
      val currPc = workList.head
      val currStmt = tacNaive.stmts(tacNaive.pcToIndex(currPc))
      workList = workList.tail

      processStmt(currStmt)

      def pcOfNextStatement(pc: PC): PC = method.body.get.pcOfNextInstruction(pc)

      def schedule(nextPc: PC, stack: OperandStack): Unit = {
        val merged = stackHandler.mergeStack(nextPc, stack)

        if (merged) {
          workList ::= nextPc
        }
      }

      def processStmt(stmt: Stmt[IdBasedVar]): Unit = {
        val stack = stackHandler.getOrCreate(stmt.pc)

        stmt match {
          case If(pc, left, _, right, target) =>
            val rightOps = processExpr(right)
            rightOps.foreach(op => stack.pop(op))

            val leftOps = processExpr(left)
            leftOps.foreach(op => stack.pop(op))

            schedule(pcOfNextStatement(pc), stack)

            val targetStmt = tacNaive.stmts(target)
            schedule(targetStmt.pc, stack)
          case Goto(_, target) =>
            val targetStmt = tacNaive.stmts(target)
            schedule(targetStmt.pc, stack)
          case Ret(pc, returnAddresses) =>
          // TODO
          case JSR(_, target) =>
            schedule(target, stack)
          case Switch(_, defaultTarget, index: IdBasedVar, nPairs) =>
            stack.pop(index)

            // TODO branch targets
            schedule(defaultTarget, stack)
          case Assignment(pc, targetVar: IdBasedVar, expr: Expr[IdBasedVar]) =>
            // Exception handlers are defined implicitly, so cannot pop them from the stack
            if (!exceptionHandlersPc.contains(pc)) {
              val operands = processExpr(expr)
              operands.foreach(op => stack.pop(op))
            }

            if (targetVar.id >= 0) {
              stack.push(targetVar)
              stackHandler.addDefSite(pc, stack.peek)
            }

            schedule(pcOfNextStatement(pc), stack)
          case ReturnValue(_, expr: IdBasedVar) =>
            stack.pop(expr)
          // No scheduling since there is no next statement
          case Return(_) => // No scheduling since there is no next statement
          case Nop(pc) =>
            val instr = method.body.get.instructions(pc)

            instr.opcode match {
              case NOP.opcode =>
              case POP.opcode | POP2.opcode =>
                stack.pop
              case DUP.opcode =>
                val dupOperand = stack.peek
                stack.push(dupOperand)
              case DUP_X1.opcode =>
                val val1 = stack.pop
                val val2 = stack.pop

                stack.push(val1)
                stack.push(val2)
                stack.push(val1)
              case DUP_X2.opcode =>
                // TODO
              case DUP2.opcode =>
                // TODO
              case DUP2_X1.opcode =>
                // TODO
              case DUP2_X2.opcode =>
                // TODO
              case WIDE.opcode =>
            }

            schedule(pcOfNextStatement(pc), stack)
          case MonitorEnter(pc, objRef: IdBasedVar) =>
            stack.pop(objRef)

            schedule(pcOfNextStatement(pc), stack)
          case MonitorExit(pc, objRef: IdBasedVar) =>
            stack.pop(objRef)

            schedule(pcOfNextStatement(pc), stack)
          case ArrayStore(pc, arrayRef: IdBasedVar, index: IdBasedVar, value: IdBasedVar) =>
            stack.pop(value)
            stack.pop(index)
            stack.pop(arrayRef)

            schedule(pcOfNextStatement(pc), stack)
          case Throw(_, exception: IdBasedVar) =>
            stack.pop(exception)
            // No scheduling since there is no next statement
          case PutStatic(pc, _, _, _, value: IdBasedVar) =>
            stack.pop(value)

            schedule(pcOfNextStatement(pc), stack)
          case PutField(pc, _, _, _, objRef: IdBasedVar, value: IdBasedVar) =>
            stack.pop(value)
            stack.pop(objRef)

            schedule(pcOfNextStatement(pc), stack)
          case NonVirtualMethodCall(pc, _, _, _, _, receiver: IdBasedVar, params: Seq[IdBasedVar]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))
            stack.pop(receiver)

            schedule(pcOfNextStatement(pc), stack)
          case VirtualMethodCall(pc, _, _, _, _, receiver: IdBasedVar, params: Seq[IdBasedVar]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))
            stack.pop(receiver)

            schedule(pcOfNextStatement(pc), stack)
          case StaticMethodCall(pc, _, _, _, _, params: Seq[IdBasedVar]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))

            schedule(pcOfNextStatement(pc), stack)
          case InvokedynamicMethodCall(pc, _, _, _, params: Seq[IdBasedVar]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))

            schedule(pcOfNextStatement(pc), stack)
          case ExprStmt(pc, expr) =>
            processExpr(expr)

            schedule(pcOfNextStatement(pc), stack)
          case CaughtException(pc, _, _) =>
            // Only used in TACAI, so no stack manipulation is required
            schedule(pcOfNextStatement(pc), stack)
          case Checkcast(pc, value, _) =>
            // TODO Push new stack value
            schedule(pcOfNextStatement(pc), stack)
          case _ => throw new RuntimeException("Unknown statement: " + stmt)
        }
      }

      def processExpr(expr: Expr[IdBasedVar]): List[IdBasedVar] = {
        expr match {
          case v: IdBasedVar => if (v.id >= 0) List(v) else List()
          case InstanceOf(_, value: IdBasedVar, _) => List(value)
          case Compare(_, left: IdBasedVar, _, right: IdBasedVar) => List(right, left)
          case Param(_, _) => List()
          case MethodTypeConst(_, _) => List()
          case MethodHandleConst(_, _) => List()
          case IntConst(_, _) => List()
          case LongConst(_, _) => List()
          case FloatConst(_, _) => List()
          case DoubleConst(_, _) => List()
          case StringConst(_, _) => List()
          case ClassConst(_, _) => List()
          case DynamicConst(_, _, _, _) => List()
          case NullExpr(_) => List()
          case BinaryExpr(_, _, _, left, right) => processExpr(right) ++ processExpr(left)
          case PrefixExpr(_, _, _, operand: IdBasedVar) => List(operand)
          case PrimitiveTypecastExpr(_, _, operand: IdBasedVar) => List(operand)
          case New(_, _) => List()
          case NewArray(_, counts: Seq[IdBasedVar], _) => counts.map(c => c.asVar).toList
          case ArrayLoad(_, index: IdBasedVar, arrayRef: IdBasedVar) => List(index, arrayRef)
          case ArrayLength(_, arrayRef: IdBasedVar) => List(arrayRef)
          case GetField(_, _, _, _, objRef: IdBasedVar) => List(objRef)
          case GetStatic(_, _, _, _) => List()
          case InvokedynamicFunctionCall(_, _, _, _, params: Seq[IdBasedVar]) => params.map(p => p.asVar).toList.reverse
          case NonVirtualFunctionCall(_, _, _, _, _, receiver: IdBasedVar, params: Seq[IdBasedVar]) => params.map(p => p.asVar).toList.reverse :+ receiver
          case VirtualFunctionCall(_, _, _, _, _, receiver: IdBasedVar, params: Seq[IdBasedVar]) => params.map(p => p.asVar).toList.reverse :+ receiver
          case StaticFunctionCall(_, _, _, _, _, params: Seq[IdBasedVar]) => params.map(p => p.asVar).toList.reverse
          case _ => throw new RuntimeException("Unknown expression: " + expr)
        }
      }

    }
    stackHandler
  }

}
