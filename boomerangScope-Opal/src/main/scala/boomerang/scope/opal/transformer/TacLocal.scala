package boomerang.scope.opal.transformer

import org.opalj.br.ComputationalType
import org.opalj.tac.{DUVar, Var}
import org.opalj.value.ValueInformation

import java.util.Objects

trait TacLocal extends Var[TacLocal] {

  def id: Int

  def isStackLocal: Boolean

  def isRegisterLocal: Boolean

  def cTpe: ComputationalType

  final def isSideEffectFree: Boolean = true

  override def toCanonicalForm(implicit ev: TacLocal <:< DUVar[ValueInformation]): Nothing = {
    throw new IncompatibleClassChangeError(
      "TacLocal objects are not expected to inherit from DUVar"
    )
  }
}

class StackLocal(identifier: Int, computationalType: ComputationalType) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = true

  override def isRegisterLocal: Boolean = false

  override def name: String = s"$$s$identifier"

  override def cTpe: ComputationalType = computationalType

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: StackLocal => this.id == that.id
    case _ => false
  }

  override def toString: String = name
}

class RegisterLocal(identifier: Int, computationalType: ComputationalType) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = false

  override def isRegisterLocal: Boolean = true

  override def name: String = s"r${-identifier - 1}"

  override def cTpe: ComputationalType = computationalType

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: RegisterLocal => this.id == that.id
    case _ => false
  }

  override def toString: String = name
}


