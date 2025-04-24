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
package boomerang.scope.opal.transformation.stack

import org.opalj.br.Method
import org.opalj.br.instructions.DUP
import org.opalj.br.instructions.DUP2
import org.opalj.br.instructions.DUP2_X1
import org.opalj.br.instructions.DUP2_X2
import org.opalj.br.instructions.DUP_X1
import org.opalj.br.instructions.DUP_X2
import org.opalj.br.instructions.NOP
import org.opalj.br.instructions.POP
import org.opalj.br.instructions.POP2
import org.opalj.br.instructions.WIDE
import org.opalj.bytecode.PC
import org.opalj.tac._

object OperandStackBuilder {

  def apply(method: Method, tacNaive: NaiveTACode[_]): OperandStackHandler = {
    val stackHandler = new OperandStackHandler

    // Initialize work list; we always start with pc 0
    var workList = List(0)

    val exceptionHandlersPc =
      tacNaive.exceptionHandlers.map(eh => tacNaive.stmts(eh.handlerPC).pc)
    for (eh <- exceptionHandlersPc) {
      workList ::= eh
    }

    if (method.toJava.contains("indirectAllocationSiteTwoFields3Address2()")) {
      println()
    }

    while (workList.nonEmpty) {
      val currPc = workList.head
      val currStmt = tacNaive.stmts(tacNaive.pcToIndex(currPc))
      workList = workList.tail

      processStmt(currStmt)

      def pcOfNextStatement(pc: PC): PC =
        method.body.get.pcOfNextInstruction(pc)

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
          // TODO Not sure how to implement it (not relevant for Java 6+)
          case JSR(_, target) =>
            schedule(target, stack)
          case Switch(_, defaultTarget, index: IdBasedVar, nPairs) =>
            stack.pop(index)

            nPairs.foreach(target => {
              val targetStmt = tacNaive.stmts(target.value)
              schedule(targetStmt.pc, stack)
            })

            val defaultTargetStmt = tacNaive.stmts(defaultTarget)
            schedule(defaultTargetStmt.pc, stack)
          case Assignment(pc, targetVar: IdBasedVar, expr: Expr[IdBasedVar]) =>
            // Exception handlers are defined implicitly, so we cannot pop them from the stack
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
          // TODO Bug in Opal causes to return the wrong operand (fixed but not released yet)
          // stack.pop(expr)
          // No scheduling since there is no next statement
          case Return(_) => // No scheduling since there is no next statement
          case Nop(pc) =>
            val instr = method.body.get.instructions(pc)

            // TODO
            //  Use pattern matching from stack.stackEntries to avoid having so many sequential
            //  operations (as done in TacNaive)
            instr.opcode match {
              case NOP.opcode =>
                schedule(pcOfNextStatement(pc), stack)
              case POP.opcode =>
                stack.pop

                schedule(pcOfNextStatement(pc), stack)
              case POP2.opcode =>
                val top = stack.pop
                if (top.cTpe.categoryId == 1) {
                  stack.pop
                }

                schedule(pcOfNextStatement(pc), stack)
              case DUP.opcode =>
                val dupOperand = stack.peek
                stack.push(dupOperand)

                schedule(pcOfNextStatement(pc), stack)
              case DUP_X1.opcode =>
                val v1 = stack.pop
                val v2 = stack.pop

                stack.push(v1)
                stack.push(v2)
                stack.push(v1)

                schedule(pcOfNextStatement(pc), stack)
              case DUP_X2.opcode =>
                val v1 = stack.pop
                val v2 = stack.pop

                if (v2.cTpe.categoryId == 1) {
                  val v3 = stack.pop

                  stack.push(v1)
                  stack.push(v3)
                  stack.push(v2)
                  stack.push(v1)
                } else {
                  stack.push(v1)
                  stack.push(v2)
                  stack.push(v1)
                }

                schedule(pcOfNextStatement(pc), stack)
              case DUP2.opcode =>
                val v1 = stack.pop

                if (v1.cTpe.categoryId == 1) {
                  val v2 = stack.pop

                  stack.push(v2)
                  stack.push(v1)
                  stack.push(v2)
                  stack.push(v1)
                } else {
                  stack.push(v1)
                  stack.push(v1)
                }

                schedule(pcOfNextStatement(pc), stack)
              case DUP2_X1.opcode =>
                val v1 = stack.pop
                val v2 = stack.pop

                if (v1.cTpe.categoryId == 1) {
                  val v3 = stack.pop

                  stack.push(v2)
                  stack.push(v1)
                  stack.push(v3)
                  stack.push(v2)
                  stack.push(v1)
                } else {
                  stack.push(v1)
                  stack.push(v2)
                  stack.push(v1)
                }

                schedule(pcOfNextStatement(pc), stack)
              case DUP2_X2.opcode =>
                val v1 = stack.pop
                val v2 = stack.pop
                val v3 = stack.pop

                if (v1.cTpe.categoryId == 1 && v2.cTpe.categoryId == 1 && v3.cTpe.categoryId == 1) {
                  val v4 = stack.pop

                  stack.push(v2)
                  stack.push(v1)
                  stack.push(v4)
                  stack.push(v3)
                  stack.push(v2)
                  stack.push(v1)
                } else if (v1.cTpe.categoryId == 2 && v2.cTpe.categoryId == 1 && v3.cTpe.categoryId == 1) {
                  stack.push(v1)
                  stack.push(v3)
                  stack.push(v2)
                  stack.push(v1)
                } else if (v1.cTpe.categoryId == 1 && v2.cTpe.categoryId == 1 && v3.cTpe.categoryId == 2) {
                  stack.push(v2)
                  stack.push(v1)
                  stack.push(v3)
                  stack.push(v2)
                  stack.push(v1)
                } else {
                  stack.push(v3)
                  stack.push(v1)
                  stack.push(v2)
                  stack.push(v1)
                }

                schedule(pcOfNextStatement(pc), stack)
              case WIDE.opcode =>
                schedule(pcOfNextStatement(pc), stack)
              case _ =>
                throw new RuntimeException(
                  "Unknown instruction for NOP: " + instr
                )
            }
          case MonitorEnter(pc, objRef: IdBasedVar) =>
            stack.pop(objRef)

            schedule(pcOfNextStatement(pc), stack)
          case MonitorExit(pc, objRef: IdBasedVar) =>
            stack.pop(objRef)

            schedule(pcOfNextStatement(pc), stack)
          case ArrayStore(
                pc,
                arrayRef: IdBasedVar,
                index: IdBasedVar,
                value: IdBasedVar
              ) =>
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
          case NonVirtualMethodCall(
                pc,
                _,
                _,
                _,
                _,
                receiver: IdBasedVar,
                params: Seq[_]
              ) =>
            params.reverse.foreach(p => stack.pop(p.asVar))
            stack.pop(receiver)

            schedule(pcOfNextStatement(pc), stack)
          case VirtualMethodCall(
                pc,
                _,
                _,
                _,
                _,
                receiver: IdBasedVar,
                params: Seq[_]
              ) =>
            params.reverse.foreach(p => stack.pop(p.asVar))
            stack.pop(receiver)

            schedule(pcOfNextStatement(pc), stack)
          case StaticMethodCall(pc, _, _, _, _, params: Seq[_]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))

            schedule(pcOfNextStatement(pc), stack)
          case InvokedynamicMethodCall(pc, _, _, _, params: Seq[_]) =>
            params.reverse.foreach(p => stack.pop(p.asVar))

            schedule(pcOfNextStatement(pc), stack)
          case ExprStmt(pc, expr) =>
            processExpr(expr)

            schedule(pcOfNextStatement(pc), stack)
          case CaughtException(pc, _, _) =>
            // Only used in TACAI, so no stack manipulation is required
            schedule(pcOfNextStatement(pc), stack)
          case Checkcast(pc, value, _) =>
            // TODO
            //  Soot transforms casts into assignments (s0 = (Class) s0), so
            //  we may do the same (not relevant for Boomerang
            schedule(pcOfNextStatement(pc), stack)
          case _ => throw new RuntimeException("Unknown statement: " + stmt)
        }
      }

      def processExpr(expr: Expr[IdBasedVar]): List[IdBasedVar] = {
        expr match {
          case v: IdBasedVar => if (v.id >= 0) List(v) else List()
          case InstanceOf(_, value: IdBasedVar, _) => List(value)
          case Compare(_, left: IdBasedVar, _, right: IdBasedVar) =>
            List(right, left)
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
          case BinaryExpr(_, _, _, left, right) =>
            processExpr(right) ++ processExpr(left)
          case PrefixExpr(_, _, _, operand: IdBasedVar) => List(operand)
          case PrimitiveTypecastExpr(_, _, operand: IdBasedVar) => List(operand)
          case New(_, _) => List()
          case NewArray(_, counts: Seq[_], _) => counts.map(c => c.asVar).toList
          case ArrayLoad(_, index: IdBasedVar, arrayRef: IdBasedVar) =>
            List(index, arrayRef)
          case ArrayLength(_, arrayRef: IdBasedVar) => List(arrayRef)
          case GetField(_, _, _, _, objRef: IdBasedVar) => List(objRef)
          case GetStatic(_, _, _, _) => List()
          case InvokedynamicFunctionCall(_, _, _, _, params: Seq[_]) =>
            params.map(p => p.asVar).toList.reverse
          case NonVirtualFunctionCall(
                _,
                _,
                _,
                _,
                _,
                receiver: IdBasedVar,
                params: Seq[_]
              ) =>
            params.map(p => p.asVar).toList.reverse :+ receiver
          case VirtualFunctionCall(
                _,
                _,
                _,
                _,
                _,
                receiver: IdBasedVar,
                params: Seq[_]
              ) =>
            params.map(p => p.asVar).toList.reverse :+ receiver
          case StaticFunctionCall(_, _, _, _, _, params: Seq[_]) =>
            params.map(p => p.asVar).toList.reverse
          case _ => throw new RuntimeException("Unknown expression: " + expr)
        }
      }

    }
    stackHandler
  }

}
