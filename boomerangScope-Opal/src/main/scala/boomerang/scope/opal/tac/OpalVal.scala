package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope._
import boomerang.scope.opal.transformer.TacLocal
import org.opalj.br.ReferenceType
import org.opalj.tac._

import java.util.Objects

class OpalVal(val delegate: Expr[TacLocal], method: OpalMethod, unbalanced: ControlFlowGraph.Edge = null) extends Val(method, unbalanced) {
  
  if (delegate.isVar) {
    throw new RuntimeException("OpalVal cannot hold a variable (use OpalLocal)")
  }

  override def getType: Type = delegate match {
    case nullExpr: NullExpr => OpalType(nullExpr.tpe)
    case const: Const => OpalType(const.tpe)
    case newExpr: New => OpalType(newExpr.tpe)
    case newArrayExpr: NewArray[_] => OpalType(newArrayExpr.tpe)
    case functionCall: FunctionCall[_] => OpalType(functionCall.descriptor.returnType)
    case _ => throw new RuntimeException("Type not implemented yet")
  }

  // TODO
  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = delegate.isNew

  override def getNewExprType: Type = {
    if (isNewExpr) {
      return OpalType(delegate.asNew.tpe)
    }

    throw new RuntimeException("Value is not a new expression")
  }

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalVal(delegate, method, stmt)

  override def isLocal: Boolean = false

  override def isArrayAllocationVal: Boolean = delegate.isNewArray

  // TODO Deal with multiple arrays
  override def getArrayAllocationSize: Val = {
    if (isArrayAllocationVal) {
      return new OpalLocal(delegate.asNewArray.counts.head.asVar, method)
    }

    throw new RuntimeException("Value is not an array allocation expression")
  }

  override def isNull: Boolean = delegate.isNullExpr

  override def isStringConstant: Boolean = delegate.isStringConst

  override def getStringValue: String = {
    if (isStringConstant) {
      return delegate.asStringConst.value
    }

    throw new RuntimeException("Value is not a String constant")
  }

  override def isStringBufferOrBuilder: Boolean = {
    val thisType = getType

    thisType.toString.equals("java/lang/String") || thisType.toString.equals("java/lang/StringBuilder") || thisType.toString.equals("java/lang/StringBuffer")
  }

  override def isThrowableAllocationType: Boolean = {
    val thisType = getType

    if (!thisType.isRefType) {
      return false
    }

    val opalType = thisType.asInstanceOf[OpalType].delegate
    OpalClient.getClassHierarchy.isSubtypeOf(opalType.asReferenceType, ReferenceType("java/lang/Throwable"))
  }

  override def isCast: Boolean = delegate.astID == PrimitiveTypecastExpr.ASTID

  override def getCastOp: Val = {
    if (isCast) {
      return new OpalVal(delegate.asPrimitiveTypeCastExpr.operand, method)
    }

    throw new RuntimeException("Expression is not a cast expression")
  }

  override def isArrayRef: Boolean = false

  override def isInstanceOfExpr: Boolean = delegate.astID == InstanceOf.ASTID

  override def getInstanceOfOp: Val = {
    if (isInstanceOfExpr) {
      return new OpalVal(delegate.asInstanceOf.value, method)
    }

    throw new RuntimeException("Expression is not an instanceOf expression")
  }

  override def isLengthExpr: Boolean = delegate.astID == ArrayLength.ASTID

  override def getLengthOp: Val = {
    if (isLengthExpr) {
      return new OpalVal(delegate.asArrayLength, method)
    }

    throw new RuntimeException("Value is not a length expression")
  }

  override def isIntConstant: Boolean = delegate.isIntConst

  override def isClassConstant: Boolean = delegate.isClassConst

  override def getClassConstantType: Type = {
    if (isClassConstant) {
      return OpalType(delegate.asClassConst.value)
    }

    throw new RuntimeException("Value is not a class constant")
  }

  override def withNewMethod(callee: Method): Val = new OpalVal(delegate, callee.asInstanceOf[OpalMethod])

  override def withSecondVal(secondVal: Val) = new OpalDoubleVal(delegate, method, secondVal)

  override def isLongConstant: Boolean = delegate.isLongConst

  override def getIntValue: Int = {
    if (isIntConstant) {
      return delegate.asIntConst.value
    }

    throw new RuntimeException("Value is not an integer constant")
  }

  override def getLongValue: Long = {
    if (isLongConstant) {
      return delegate.asLongConst.value
    }

    throw new RuntimeException("Value is not a long constant")
  }

  override def getArrayBase: Pair[Val, Integer] = throw new RuntimeException("Value is not an array ref")

  override def getVariableName: String = {
    delegate match {
      case stringConst: StringConst => "\"" + stringConst.value + "\""
      case intConst: IntConst => intConst.value.toString
      case longConst: LongConst => longConst.value.toString
      case newExpr: New => s"new ${newExpr.tpe.toJava}"
      case newArrayExpr: NewArray[_] => s"new ${newArrayExpr.tpe.toJava}"
      case _: NullExpr => "null"
      case _ => delegate.toString
    }
  }

  override def hashCode: Int = Objects.hash(super.hashCode(), delegate.hashCode())

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalVal]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalVal => other.canEqual(this) && super.equals(other) && this.delegate == other.delegate
    case _ => false
  }

  override def toString: String = getVariableName
}
