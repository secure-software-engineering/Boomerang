package boomerang.scope.opal.transformer

import org.opalj.br.cfg.CFG
import org.opalj.tac.{Assignment, IdBasedVar, Stmt, TACStmts}

import scala.collection.mutable

class OperandStack(tac: Array[Stmt[IdBasedVar]]) {

  private val stmtStacks = new Array[mutable.Map[Int, (Int, List[Int])]](tac.length)
  stmtStacks(0) = mutable.Map.empty[Int, (Int, List[Int])]

  def push(currIndex: Int, nextIndex: Int, operand: IdBasedVar, stackCounter: Int): Unit = {
    val currStack = stmtStacks(currIndex).map(identity)

    // Update all entries with current statement index
    val nextStack = currStack.map { case (k, (id, useSite)) => k -> (id, useSite :+ nextIndex) }
    nextStack.put(operand.id, (stackCounter, List(nextIndex)))

    stmtStacks(nextIndex) = nextStack
  }

  def update(currIndex: Int, nextIndex: Int): Unit = {
    val currStack = stmtStacks(currIndex).map(identity)
    val existingStack = stmtStacks(nextIndex)

    if (existingStack != null) {
      mergeStacks(existingStack.toMap, currStack.toMap)
    } else {
      // Update with currIndex
      val nextStack = currStack.map {
        case (k, (id, scope)) => k -> (id, scope :+ nextIndex)
      }
      stmtStacks(nextIndex) = nextStack
    }
  }

  private def mergeStacks(existingStack: Map[Int, (Int, List[Int])], incomingStack: Map[Int, (Int, List[Int])]): Unit = {
    existingStack.foreach {
      case (id, (currStackCounter, _)) =>
        incomingStack.foreach {
          case (`id`, (incomingStackCounter, scope)) =>
            // If the stack counter is equal, no merge is needed as the operands describe the same stack local
            if (currStackCounter != incomingStackCounter) {
              scope.foreach(s => {
                val currIndex = stmtStacks(s)

                // Update the stack counter s.t. the operands describe the same stack local
                currIndex.put(id, (currStackCounter, currIndex(id)._2))
              })
            }
          case _ =>
        }
    }
  }

  def operandCountersAtStmt(stmtIndex: Int): Map[Int, Int] = {
    val stmtStack = stmtStacks(stmtIndex)

    stmtStack.map { case (id, (counter, _)) => id -> counter}.toMap
  }

  def operandCounterAtStmt(stmtIndex: Int, operandId: Int): Int = {
    operandCountersAtStmt(stmtIndex).getOrElse(operandId, throw new RuntimeException("Could not find operand on stack with id " + operandId))
  }
}

object OperandStack {

  def apply(tac: Array[Stmt[IdBasedVar]], cfg: CFG[Stmt[IdBasedVar], TACStmts[IdBasedVar]]): OperandStack = {
    val stack = new OperandStack(tac)
    var stackCounter = 0

    var workList: List[Int] = List(0)
    while (workList.nonEmpty) {
      val currIndex = workList.head
      val currStmt = tac(currIndex)
      workList = workList.tail

      if (currStmt.pc == -1) {
        schedule(currIndex + 1)
      } else {
        val nextIndices = cfg.successors(currIndex)
        nextIndices.foreach(nextIndex => {
          if (isOperandPushStmt(currStmt)) {
            val targetVar = currStmt.asAssignment.targetVar

            stack.push(currIndex, nextIndex, targetVar, stackCounter)
            stackCounter += 1
          } else {
            stack.update(currIndex, nextIndex)
          }

          schedule(nextIndex)
        })
      }

      def schedule(nextIndex: Int): Unit = workList ::= nextIndex

      def isOperandPushStmt(stmt: Stmt[IdBasedVar]): Boolean = stmt.astID == Assignment.ASTID && stmt.asAssignment.targetVar.id >= 0
    }

    stack
  }
}
