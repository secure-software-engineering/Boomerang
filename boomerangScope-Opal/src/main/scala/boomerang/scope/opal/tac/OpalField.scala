package boomerang.scope.opal.tac

import boomerang.scope.{Field, Type}
import org.opalj.br.{FieldType, ObjectType}

import java.util.Objects

case class OpalField(declaringClass: ObjectType, fieldType: FieldType, name: String) extends Field {

  override def isPredefinedField: Boolean = false

  override def isInnerClassField: Boolean = declaringClass.fqn.contains("$")

  override def getType: Type = OpalType(fieldType)

  override def hashCode: Int = Objects.hash(super.hashCode(), declaringClass, fieldType, name)

  override def equals(other: Any): Boolean = other match {
    case that: OpalField => this.declaringClass == that.declaringClass && this.fieldType == that.fieldType && this.name == that.name
    case _ => false
  }

  override def toString: String = s"$name"
}
