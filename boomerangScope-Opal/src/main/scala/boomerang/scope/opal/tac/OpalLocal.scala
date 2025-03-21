package boomerang.scope.opal.tac

import boomerang.scope._
import org.opalj.br.ObjectType
import org.opalj.tac.{DUVar, DVar, Expr, IdBasedVar, SimpleVar, UVar, Var}
import org.opalj.value.ValueInformation

import java.util.Objects

class OpalLocal(val delegate: Var[IdBasedVar], method: OpalMethod, unbalanced: ControlFlowGraph.Edge = null) extends Val(method, unbalanced) {

  override def getType: Type = delegate match {
    /*case local: SimpleVar =>
      if (local.value.isReferenceValue) {
        OpalType(local.value.asReferenceValue.asReferenceType, local.value.asReferenceValue.isNull.isYes)
      } else if (local.value.isPrimitiveValue) {
        OpalType(local.value.asPrimitiveValue.primitiveType)
      } else if (local.value.isVoid) {
        OpalType(ObjectType.Void)
      } else if (local.value.isArrayValue.isYes) {
        OpalType(ObjectType.Array)
      } else {
        throw new RuntimeException("Could not determine type " + local.value)
      }*/
    case _ => throw new RuntimeException("Cannot compute type of expression that is not a variable")

  }

  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = false

  override def getNewExprType: Type = throw new RuntimeException("Opal local is not a new expression")

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalLocal(delegate, method, stmt)

  override def isLocal: Boolean = true

  override def isArrayAllocationVal: Boolean = false

  override def getArrayAllocationSize: Val = throw new RuntimeException("Opal local is not an array allocation expression")

  override def isNull: Boolean = {
    ???
  }

  override def isStringConstant: Boolean = false

  override def getStringValue: String = throw new RuntimeException("Opal local is not a String constant")

  override def isStringBufferOrBuilder: Boolean = false

  override def isThrowableAllocationType: Boolean = false

  override def isCast: Boolean = false

  override def getCastOp: Val = throw new RuntimeException("Opal local is not a cast operation")

  override def isArrayRef: Boolean = false

  override def isInstanceOfExpr: Boolean = false

  override def getInstanceOfOp: Val = throw new RuntimeException("Opal local is not an instance of operation")

  override def isLengthExpr: Boolean = false

  override def getLengthOp: Val = throw new RuntimeException("Opal local is not a length operation")

  override def isIntConstant: Boolean = false

  override def isClassConstant: Boolean = false

  override def getClassConstantType: Type = throw new RuntimeException("Opal local is not a class constant")

  override def withNewMethod(callee: Method): Val = new OpalLocal(delegate, callee.asInstanceOf[OpalMethod])

  override def isLongConstant: Boolean = false

  override def getIntValue: Int = throw new RuntimeException("Opal local is not an int constant")

  override def getLongValue: Long = throw new RuntimeException("Opal local is not a long constant")

  override def getArrayBase: Pair[Val, Integer] = throw new RuntimeException("Opal local is not array reference")

  override def getVariableName: String = delegate.asVar.name

  override def hashCode: Int = Objects.hash(delegate.asVar.id)

  override def equals(other: Any): Boolean = other match {
    case that: OpalLocal => super.equals(that) && this.delegate.asVar.id == that.delegate.asVar.id
    case _ => false
  }

  override def toString: String = getVariableName
}
