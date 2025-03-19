package boomerang.scope.opal.tac

import boomerang.scope._

import java.util.Objects

class OpalStaticFieldVal(field: OpalField, method: Method, unbalanced: ControlFlowGraph.Edge = null) extends StaticFieldVal(method, unbalanced) {

  override def field: Field = field

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalStaticFieldVal(field, method, stmt)

  override def getType: Type = OpalType(field.fieldType)

  override def withNewMethod(callee: Method): Val = new OpalStaticFieldVal(field, callee)

  override def getVariableName: String = field.toString

  override def hashCode: Int = Objects.hash(super.hashCode(), field)

  override def equals(other: Any): Boolean = other match {
    case that: OpalStaticFieldVal => super.equals(that) && this.field.equals(that.field)
    case _ => false
  }

  override def toString: String = s"StaticField: $field"
}
