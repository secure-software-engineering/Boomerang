package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.opal.transformation.TacLocal
import boomerang.scope.{IfStatement, Statement, Val}
import org.opalj.tac.{DUVar, IdBasedVar, If, Var}
import org.opalj.value.ValueInformation

import java.util.Objects

class OpalIfStatement(val delegate: If[TacLocal], method: OpalMethod) extends IfStatement {

  override def getTarget: Statement = {
    /*val tac = OpalClient.getTacForMethod(method.delegate)
    val target = delegate.targetStmt

    new OpalStatement(tac.stmts(target), method)*/
    ???
  }

  override def evaluate(otherVal: Val): IfStatement.Evaluation = IfStatement.Evaluation.UNKNOWN

  override def uses(otherVal: Val): Boolean = {
    // TODO
    if (otherVal.isInstanceOf[OpalVal]) {}
    if (otherVal.isInstanceOf[OpalLocal]) {}
    if (otherVal.isInstanceOf[OpalArrayRef]) {}
    val left = new OpalVal(delegate.left, method)
    val right = new OpalVal(delegate.right, method)

    otherVal.equals(left) || otherVal.equals(right)
  }

  override def hashCode: Int = Objects.hash(delegate)

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalIfStatement]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalIfStatement => other.canEqual(this) && this.delegate.pc == other.delegate.pc
    case _ => false
  }

  override def toString: String = delegate.toString()
}
