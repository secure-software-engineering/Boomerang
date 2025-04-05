package boomerang.scope.opal.transformer

import org.opalj.br.cfg.CFG
import org.opalj.tac.{Assignment, IdBasedVar, NaiveTACode, Stmt, TACStmts}

import scala.collection.mutable

class OperandStack(tac: Array[Stmt[IdBasedVar]], startIndex: Int, exceptionHandlers: List[Int]) {

  private val stmtStacks = new Array[mutable.Map[Int, (Int, List[Int], List[Int])]](tac.length)
  private val operandDefSites = Array.fill(tac.length)(-1)
  stmtStacks(startIndex) = mutable.Map.empty
  exceptionHandlers.foreach(i => stmtStacks(i) = mutable.Map.empty)

  def push(currIndex: Int, nextIndex: Int, operand: IdBasedVar, stackCounter: Int): Unit = {
    val currStack = stmtStacks(currIndex).map(identity)

    // Update all entries with current statement index
    val nextStack = currStack.map { case (k, (id, defSite, scope)) => k -> (id, defSite, scope :+ nextIndex) }
    nextStack.put(operand.id, (stackCounter, List(currIndex), List(nextIndex)))

    operandDefSites(currIndex) = stackCounter

    val existingStack = stmtStacks(nextIndex)
    if (existingStack != null) {
      mergeStacks(nextIndex, existingStack.toMap, nextStack.toMap)
    } else {
      stmtStacks(nextIndex) = nextStack
    }
  }

  def update(currIndex: Int, nextIndex: Int): Unit = {
    val currStack = stmtStacks(currIndex).map(identity)
    val existingStack = stmtStacks(nextIndex)

    if (existingStack != null) {
      mergeStacks(nextIndex, existingStack.toMap, currStack.toMap)
    } else {
      // Update with next index
      val nextStack = currStack.map {
        case (k, (id, defSite, scope)) => k -> (id, defSite, scope :+ nextIndex)
      }
      stmtStacks(nextIndex) = nextStack
    }
  }

  private def mergeStacks(currIndex: Int, existingStack: Map[Int, (Int, List[Int], List[Int])], incomingStack: Map[Int, (Int, List[Int], List[Int])]): Unit = {
    existingStack.foreach {
      case (id, (currStackCounter, existingDefSites, existingScope)) =>
        incomingStack.foreach {
          case (`id`, (incomingStackCounter, incomingDefSites, incomingScope)) =>
            if (currStackCounter != incomingStackCounter) {
              incomingScope.foreach(s => {
                val currStack = stmtStacks(s)

                // Update the stack counter s.t. the operands describe the same stack local
                currStack.put(id, (currStackCounter, incomingDefSites, currStack(id)._3))
                incomingDefSites.foreach(d => operandDefSites(d) = currStackCounter)
              })
            }

            // Update the def sites and scopes for the merge statement
            val currStack = stmtStacks(currIndex)
            val mergedDefSites = (existingDefSites ++ incomingDefSites).distinct
            val mergedScopes = (existingScope ++ incomingScope).distinct
            currStack.put(id, (currStackCounter, mergedDefSites, mergedScopes))

          case _ =>
        }
    }
  }

  def operandDefSite(stmtIndex: Int): Int = operandDefSites(stmtIndex)

  def operandCounterAtStmt(stmtIndex: Int, operandId: Int): Int = stmtStacks(stmtIndex).getOrElse(operandId, throw new RuntimeException("Could not find operand on stack with id " + operandId))._1

  def operandHasMultipleDefSites(stackLocalId: Int): Boolean = {
    stmtStacks.filter(m => m != null).foreach(m => {
      m.foreach {
        case (_, (`stackLocalId`, defSites, _)) => if (defSites.size > 1) return true
        case _ =>
      }
    })

    false
  }
}

object OperandStack {

  def apply(code: NaiveTACode[_]): OperandStack = {
    val tac = code.stmts
    val cfg = code.cfg
    val exceptionHandlers = code.exceptionHandlers.map(eh => eh.handlerPC).toList

    val firstNonNegativeIndex = tac.indexWhere(stmt => stmt.pc >= 0)
    val stack = new OperandStack(tac, firstNonNegativeIndex, exceptionHandlers)
    var stackCounter = 0

    var workList: List[Int] = firstNonNegativeIndex :: exceptionHandlers
    while (workList.nonEmpty) {
      val currIndex = workList.head
      val currStmt = tac(currIndex)
      workList = workList.tail

      if (currStmt.pc == -1) {
        schedule(currIndex + 1)
      } else {
        // Reversing is not needed; however, this way, the stack locals are enumerated in ascending order
        val nextIndices = cfg.successors(currIndex).toList.reverse
        nextIndices.foreach(nextIndex => {
          if (currIndex < nextIndex && !exceptionHandlers.contains(nextIndex)) {
            if (isOperandPushStmt(currStmt)) {
              val targetVar = currStmt.asAssignment.targetVar

              stack.push(currIndex, nextIndex, targetVar, stackCounter)
              stackCounter += 1
            } else if (isExceptionLoadStmt(currStmt)) {
              val exceptionVar = currStmt.asAssignment.expr.asVar

              stack.push(currIndex, nextIndex, exceptionVar, stackCounter)
              stackCounter += 1
            } else {
              stack.update(currIndex, nextIndex)
            }

            schedule(nextIndex)
          }
        })
      }

      def schedule(nextIndex: Int): Unit = workList ::= nextIndex

      def isOperandPushStmt(stmt: Stmt[IdBasedVar]): Boolean = stmt.astID == Assignment.ASTID && stmt.asAssignment.targetVar.id >= 0

      def isExceptionLoadStmt(stmt: Stmt[IdBasedVar]): Boolean = stmt.astID == Assignment.ASTID && stmt.asAssignment.expr.isVar && exceptionHandlers.contains(code.pcToIndex(stmt.pc))
    }

    stack
  }
}
