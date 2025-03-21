package boomerang.scope.opal.tac

import boomerang.scope.{Val, ValWithFalseVariable}
import org.opalj.tac.{DUVar, Expr, IdBasedVar, Var}
import org.opalj.value.ValueInformation

import java.util.Objects

class OpalDoubleVal(delegate: Expr[IdBasedVar], method: OpalMethod, falseVal: Val) extends OpalVal(delegate, method) with ValWithFalseVariable {

  override def getFalseVariable: Val = falseVal

  override def hashCode: Int = Objects.hash(super.hashCode, falseVal)

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalDoubleVal]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalDoubleVal =>
      other.canEqual(this) && super.equals(other) && falseVal.equals(other.getFalseVariable)
    case _ => false
  }

  override def toString: String = "FalseVal: " + falseVal + " from " + super.toString
}
