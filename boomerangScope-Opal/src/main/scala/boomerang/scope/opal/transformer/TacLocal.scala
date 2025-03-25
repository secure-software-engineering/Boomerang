package boomerang.scope.opal.transformer

import org.opalj.br.ComputationalType
import org.opalj.tac.{DUVar, Var}
import org.opalj.value.ValueInformation

import java.util.Objects

trait TacLocal extends Var[TacLocal] {

  def id: Int

  def isStackLocal: Boolean

  def isRegisterLocal: Boolean

  def isParameterLocal: Boolean

  def cTpe: ComputationalType

  def value: ValueInformation

  final def isSideEffectFree: Boolean = true

  override def toCanonicalForm(implicit ev: TacLocal <:< DUVar[ValueInformation]): Nothing = {
    throw new IncompatibleClassChangeError(
      "TacLocal objects are not expected to inherit from DUVar"
    )
  }

  override def toString: String = name
}

class StackLocal(identifier: Int, computationalType: ComputationalType, valueInfo: ValueInformation) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = true

  override def isRegisterLocal: Boolean = false

  override def isParameterLocal: Boolean = false

  override def name: String = {
    if (identifier == -1) {
      "this"
    } else {
      s"$$s$identifier"
    }
  }

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = valueInfo

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: StackLocal => this.id == that.id
    case _ => false
  }
}

class RegisterLocal(identifier: Int, computationalType: ComputationalType, valueInfo: ValueInformation) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = false

  override def isRegisterLocal: Boolean = true

  override def isParameterLocal: Boolean = false

  override def name: String = s"r${-identifier - 1}"

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = valueInfo

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: RegisterLocal => this.id == that.id
    case _ => false
  }
}

class ParameterLocal(identifier: Int, computationalType: ComputationalType, paramName: String) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = false

  override def isRegisterLocal: Boolean = false

  override def isParameterLocal: Boolean = true

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = throw new UnsupportedOperationException("No value information available for parameter local")

  override def name: String = paramName

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: ParameterLocal => this.id == that.id
    case _ => false
  }
}

// TODO Consider TempLocal in SWAP
