package boomerang.scope.opal.transformation.stack

import org.opalj.br.ComputationalType

import java.util.Objects

class Operand(val id: Int, val cTpe: ComputationalType, private var counter: Int) {

  private var modified = false

  def localId: Int = counter

  def updateCounter(newCount: Int): Unit = {
    counter = newCount
    modified = true
  }

  def isBranchedOperand: Boolean = modified

  override def hashCode: Int = Objects.hash(id)

  override def equals(obj: Any): Boolean = obj match {
    case that: Operand => this.id == that.id
    case _ => false
  }

  override def toString: String = s"op$id ($counter)"
}
