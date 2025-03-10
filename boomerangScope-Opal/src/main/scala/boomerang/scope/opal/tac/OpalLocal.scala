package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope._
import org.opalj.br.ObjectType
import org.opalj.tac.{DUVar, DVar, Expr, UVar}
import org.opalj.value.ValueInformation

class OpalLocal(val delegate: Expr[DUVar[ValueInformation]], method: OpalMethod, unbalanced: ControlFlowGraph.Edge = null) extends Val(method, unbalanced) {

  if (!delegate.isVar) {
    throw new RuntimeException("OpalLocal can hold only variables")
  }

  override def getType: Type = delegate match {
    case local: DUVar[ValueInformation] =>
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
      }
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
    if (!delegate.asVar.value.isReferenceValue) {
      delegate.asVar.value.asReferenceValue.isNull.isYes
    }

    false
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

  override def getVariableName: String = delegate match {
    case dVar: DVar[_] => s"var${}" // TODO Add origin
    case uVar: UVar[_] => s"var${uVar.definedBy.head}"
    case _ => delegate.toString
  }

  override def hashCode(): Int = delegate match {
    case uVar: UVar[_] =>
      if (uVar.definedBy.head < 0) {
        // Parameters have no real definition statement
        return 31 * super.hashCode() + delegate.hashCode()
      }

      // UVars should reference their DVar to keep the comparisons consistent
      val tac = OpalClient.getTacForMethod(method.delegate)
      val defStmt = tac.stmts(uVar.definedBy.head)

      if (!defStmt.isAssignment) {
        return 31 * super.hashCode() + delegate.hashCode()
      }

      val targetVar = defStmt.asAssignment.targetVar
      31 * super.hashCode() + targetVar.hashCode()
    case dVar: DVar[_] => 31 * super.hashCode() + dVar.hashCode()
    case _ => throw new RuntimeException("Cannot compute hashCode for non variables")
  }

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalLocal =>
      // DVar does not implement a proper equals() method, so we have to compare the hash codes
      this.delegate match {
        case _: DVar[_] if other.delegate.isInstanceOf[DVar[_]] =>
          super.equals(other) && this.delegate.hashCode() == other.delegate.hashCode()
        case uVar: UVar[_] if other.delegate.isInstanceOf[DVar[_]] =>
          val tac = OpalClient.getTacForMethod(method.delegate)
          val defStmt = tac.stmts(uVar.definedBy.head)

          if (!defStmt.isAssignment) {
            return false
          }

          val targetVar = defStmt.asAssignment.targetVar
          val otherVar = other.delegate.asInstanceOf[DVar[ValueInformation]]
          super.equals(other) && targetVar.hashCode() == otherVar.hashCode()
        case dVar: DVar[_] if other.delegate.isInstanceOf[UVar[_]] =>
          val otherVar = other.delegate.asInstanceOf[UVar[ValueInformation]]

          // Parameter vars have no corresponding DVar
          if (otherVar.definedBy.head < 0) {
            return false
          }

          val tac = OpalClient.getTacForMethod(method.delegate)
          val defStmt = tac.stmts(otherVar.definedBy.head)

          if (!defStmt.isAssignment) {
            return false
          }

          val targetVar = defStmt.asAssignment.targetVar
          super.equals(other) && dVar.hashCode() == targetVar.hashCode()
        case _: UVar[_] if other.delegate.isInstanceOf[UVar[_]] =>
          super.equals(other) && this.delegate.hashCode() == other.delegate.hashCode()
        case _ => throw new RuntimeException("Cannot compare a variable with a non variable")
      }
    case _ => false
  }

  override def toString: String = getVariableName
}
