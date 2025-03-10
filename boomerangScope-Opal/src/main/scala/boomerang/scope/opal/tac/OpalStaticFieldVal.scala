package boomerang.scope.opal.tac

import boomerang.scope._

class OpalStaticFieldVal(field: OpalField, method: Method, unbalanced: ControlFlowGraph.Edge = null) extends StaticFieldVal(method, unbalanced) {

  override def field(): Field = field

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalStaticFieldVal(field, method, stmt)

  override def getType: Type = OpalType(field.delegate.fieldType)

  override def isStatic: Boolean = true

  override def isNewExpr: Boolean = false

  override def getNewExprType: Type = throw new RuntimeException("Static field is not a new expression")

  override def isLocal: Boolean = false

  override def isArrayAllocationVal: Boolean = false

  override def getArrayAllocationSize: Val = throw new RuntimeException("Static field is not an array allocation expression")

  override def isNull: Boolean = false

  override def isStringConstant: Boolean = false

  override def getStringValue: String = throw new RuntimeException("Static field is not a string constant")

  override def isStringBufferOrBuilder: Boolean = false

  override def isThrowableAllocationType: Boolean = false

  override def isCast: Boolean = false

  override def getCastOp: Val = throw new RuntimeException("Static field is not a cast operation")

  override def isArrayRef: Boolean = false

  override def isInstanceOfExpr: Boolean = false

  override def getInstanceOfOp: Val = throw new RuntimeException("Static field is not an instance of operation")

  override def isLengthExpr: Boolean = false

  override def getLengthOp: Val = throw new RuntimeException("Static field is not a length operation")

  override def isIntConstant: Boolean = false

  override def isClassConstant: Boolean = false

  override def getClassConstantType: Type = throw new RuntimeException("Static field has not a class constant type")

  override def withNewMethod(callee: Method): Val = new OpalStaticFieldVal(field, callee)

  override def isLongConstant: Boolean = false

  override def getIntValue: Int = throw new RuntimeException("Static field is not an int value")

  override def getLongValue: Long = throw new RuntimeException("Static field is not a long value")

  override def getArrayBase: Pair[Val, Integer] = throw new RuntimeException("Static field has no array base")

  override def getVariableName: String = field.toString

  override def hashCode(): Int = ???

  override def equals(obj: Any): Boolean = ???

  override def toString: String = ???
}
