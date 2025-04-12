package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.transformation.TacLocal
import org.opalj.br.ObjectType
import org.opalj.tac.Var

import java.util.Objects

class OpalLocal(val delegate: Var[TacLocal], method: OpalMethod, unbalanced: ControlFlowGraph.Edge = null) extends Val(method, unbalanced) {

  override def getType: Type = {
    val value = delegate.asVar.value

    if (value.isPrimitiveValue) {
      return OpalType(value.asPrimitiveValue.primitiveType)
    }

    if (value.isReferenceValue) {
      if (value.asReferenceValue.isPrecise) {
        if (value.asReferenceValue.isNull.isYes) {
          return OpalNullType
        } else {
          return OpalType(value.asReferenceValue.asReferenceType)
        }
      } else {
        return OpalType(value.asReferenceValue.upperTypeBound.head)
      }

      // Over approximation: Same behavior as in Soot
      return OpalType(ObjectType("java/lang/Object"))
    }

    if (value.isVoid) {
      return OpalType(ObjectType.Void)
    }

    // TODO Array and illegal types
    throw new RuntimeException("Type not implemented yet")
  }

  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = false

  override def getNewExprType: Type = throw new RuntimeException("Opal local is not a new expression")

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalLocal(delegate, method, stmt)

  override def isLocal: Boolean = true

  override def isArrayAllocationVal: Boolean = false

  override def getArrayAllocationSize: Val = throw new RuntimeException("Opal local is not an array allocation expression")

  override def isNull: Boolean = false

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
    case that: OpalLocal => super.equals(that) && this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = getVariableName
}
