package boomerang.scope.opal.tac

import boomerang.scope.{Field, Type}
import org.opalj.br.{FieldType, ObjectType}

case class OpalField(declaringClass: ObjectType, fieldType: FieldType, name: String) extends Field {

  override def isPredefinedField: Boolean = false

  override def isInnerClassField: Boolean = declaringClass.fqn.contains("$")

  override def getType: Type = OpalType(fieldType)

  override def toString: String = s"${declaringClass.fqn}.${fieldType.toJava} $name"
}
