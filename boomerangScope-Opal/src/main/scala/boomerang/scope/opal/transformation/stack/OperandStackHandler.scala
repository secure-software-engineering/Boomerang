package boomerang.scope.opal.transformation.stack

import org.opalj.br.PC

import scala.collection.mutable

class OperandStackHandler {

  private val pcToStack = mutable.Map.empty[PC, OperandStack]
  private val defSites = mutable.Map.empty[PC, Operand]
  private var localCounter = 0

  def getOrCreate(pc: PC): OperandStack = {
    if (pcToStack.contains(pc)) {
      pcToStack(pc).copy
    } else {
      val stack = OperandStack(this)
      pcToStack.put(pc, stack)

      stack.copy
    }
  }

  def addDefSite(pc: PC, operand: Operand): Unit = {
    defSites.put(pc, operand)
  }

  def mergeStack(nextPc: PC, incomingStack: OperandStack): Boolean = {
    val existingStack = pcToStack.getOrElse(nextPc, null)

    if (existingStack == null) {
      pcToStack.put(nextPc, incomingStack)

      true
    } else {
      var modified = false
      existingStack.stack.foreach(existingOp => {
        incomingStack.stack.foreach(incomingOp => {
          if (existingOp.id == incomingOp.id && existingOp.localId != incomingOp.localId) {
            incomingOp.updateCounter(existingOp.localId)

            modified = true
          }
        })
      })
      modified
    }
  }

  def nextLocalCounter: Int = {
    localCounter += 1

    localCounter
  }

}
