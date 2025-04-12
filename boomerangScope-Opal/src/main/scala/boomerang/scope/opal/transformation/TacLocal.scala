package boomerang.scope.opal.transformation

import org.opalj.br.ComputationalType
import org.opalj.tac.{DUVar, Var}
import org.opalj.value.{IsNullValue, ValueInformation}

import java.util.Objects

trait TacLocal extends Var[TacLocal] {

  def id: Int

  def isStackLocal: Boolean = false

  def isRegisterLocal: Boolean = false

  def isParameterLocal: Boolean = false

  def isThisLocal: Boolean = false

  def isExceptionLocal: Boolean = false

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

class StackLocal(identifier: Int, computationalType: ComputationalType, valueInfo: ValueInformation, isThis: Boolean = false) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = true

  override def isThisLocal: Boolean = isThis

  override def name: String = if (isThis) "$this" else s"$$s$identifier"

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = valueInfo

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: StackLocal => this.id == that.id
    case that: RegisterLocal => this.isThisLocal && that.isThisLocal
    case _ => false
  }
}

class RegisterLocal(identifier: Int, computationalType: ComputationalType, valueInfo: ValueInformation, isThis: Boolean = false) extends TacLocal {

  override def id: Int = identifier

  override def isRegisterLocal: Boolean = true

  override def isThisLocal: Boolean = isThis

  override def name: String = if (isThis) "this" else s"r${-identifier - 1}"

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = valueInfo

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: RegisterLocal => this.id == that.id
    case that: StackLocal => this.isThisLocal && that.isThisLocal
    case _ => false
  }
}

class ParameterLocal(identifier: Int, computationalType: ComputationalType, paramName: String) extends TacLocal {

  override def id: Int = identifier

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

class NullifiedLocal(identifier: Int, computationalType: ComputationalType) extends TacLocal {

  override def id: Int = identifier

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = IsNullValue

  override def name: String = s"n$identifier"

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: NullifiedLocal => this.id == that.id
    case _ => false
  }
}

class ExceptionLocal(identifier: Int, computationalType: ComputationalType, valueInfo: ValueInformation) extends TacLocal {

  override def id: Int = identifier

  override def isExceptionLocal: Boolean = true

  override def cTpe: ComputationalType = computationalType

  override def value: ValueInformation = valueInfo

  override def name: String = s"e$identifier"

  override def hashCode: Int = Objects.hash(id)

  override def equals(other: Any): Boolean = other match {
    case that: ExceptionLocal => this.id == that.id
    case _ => false
  }
}
