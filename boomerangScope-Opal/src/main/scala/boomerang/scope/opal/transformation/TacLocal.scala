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
package boomerang.scope.opal.transformation

import java.util.Objects
import org.opalj.br.ComputationalType
import org.opalj.tac.DUVar
import org.opalj.tac.Var
import org.opalj.value.ValueInformation

trait TacLocal extends Var[TacLocal] {

  def id: Int

  def isStackLocal: Boolean = false

  def isRegisterLocal: Boolean = false

  def isParameterLocal: Boolean = false

  def isThisLocal: Boolean = false

  def isExceptionLocal: Boolean = false

  def cTpe: ComputationalType

  final def isSideEffectFree: Boolean = true

  override def toCanonicalForm(implicit
      ev: TacLocal <:< DUVar[ValueInformation]
  ): Nothing = {
    throw new IncompatibleClassChangeError(
      "TacLocal objects are not expected to inherit from DUVar"
    )
  }

  override def toString: String = name
}

class StackLocal(
    identifier: Int,
    computationalType: ComputationalType,
    isThis: Boolean = false
) extends TacLocal {

  override def id: Int = identifier

  override def isStackLocal: Boolean = true

  override def isThisLocal: Boolean = isThis

  override def name: String = if (isThis) "$this" else s"$$s$identifier"

  override def cTpe: ComputationalType = computationalType

  override def hashCode: Int = Objects.hash(this.getClass.hashCode(), id)

  override def equals(other: Any): Boolean = other match {
    case that: StackLocal => this.id == that.id
    case that: RegisterLocal => this.isThisLocal && that.isThisLocal
    case _ => false
  }
}

class RegisterLocal(
    identifier: Int,
    computationalType: ComputationalType,
    isThis: Boolean = false,
    localName: Option[String] = Option.empty
) extends TacLocal {

  override def id: Int = identifier

  override def isRegisterLocal: Boolean = true

  override def isThisLocal: Boolean = isThis

  override def name: String = {
    if (isThis) return "this"
    if (localName.isDefined) return localName.get

    s"r${-identifier - 1}"
  }

  override def cTpe: ComputationalType = computationalType

  override def hashCode: Int = Objects.hash(this.getClass.hashCode(), id)

  override def equals(other: Any): Boolean = other match {
    case that: RegisterLocal => this.id == that.id
    case that: StackLocal => this.isThisLocal && that.isThisLocal
    case _ => false
  }
}

class ParameterLocal(
    identifier: Int,
    computationalType: ComputationalType,
    paramName: String
) extends TacLocal {

  override def id: Int = identifier

  override def isParameterLocal: Boolean = true

  override def cTpe: ComputationalType = computationalType

  override def name: String = paramName

  override def hashCode: Int = Objects.hash(this.getClass.hashCode(), id)

  override def equals(other: Any): Boolean = other match {
    case that: ParameterLocal => this.id == that.id
    case _ => false
  }
}

class NullifiedLocal(identifier: Int, computationalType: ComputationalType) extends TacLocal {

  override def id: Int = identifier

  override def cTpe: ComputationalType = computationalType

  override def name: String = s"n$identifier"

  override def hashCode: Int = Objects.hash(this.getClass.hashCode(), id)

  override def equals(other: Any): Boolean = other match {
    case that: NullifiedLocal => this.id == that.id
    case _ => false
  }
}

class ExceptionLocal(
    identifier: Int,
    computationalType: ComputationalType
) extends TacLocal {

  override def id: Int = identifier

  override def isExceptionLocal: Boolean = true

  override def cTpe: ComputationalType = computationalType

  override def name: String = s"e$identifier"

  override def hashCode: Int = Objects.hash(this.getClass.hashCode(), id)

  override def equals(other: Any): Boolean = other match {
    case that: ExceptionLocal => this.id == that.id
    case _ => false
  }
}
