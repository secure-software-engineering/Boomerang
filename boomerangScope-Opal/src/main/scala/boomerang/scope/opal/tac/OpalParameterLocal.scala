package boomerang.scope.opal.tac

import boomerang.scope.{ControlFlowGraph, Method, Pair, Type, Val}
import org.opalj.br.FieldType

class OpalParameterLocal(parameterType: FieldType, method: OpalMethod, unbalanced: ControlFlowGraph.Edge = null) extends Val(method, unbalanced) {

  override def getType: Type = OpalType(parameterType)

  override def isStatic: Boolean = false

  override def isNewExpr: Boolean = false

  override def getNewExprType: Type = throw new RuntimeException("Parameter local is not a new expression")

  override def asUnbalanced(stmt: ControlFlowGraph.Edge): Val = new OpalParameterLocal(parameterType, method, stmt)

  override def isLocal: Boolean = true

  override def isArrayAllocationVal: Boolean = false

  override def getArrayAllocationSize: Val = throw new RuntimeException("Parameter local is not an array allocation val")

  override def isNull: Boolean = false

  override def isStringConstant: Boolean = false

  override def getStringValue: String = throw new RuntimeException("Parameter local is not a string constant")

  override def isStringBufferOrBuilder: Boolean = false

  override def isThrowableAllocationType: Boolean = false

  override def isCast: Boolean = false

  override def getCastOp: Val = throw new RuntimeException("Parameter local is not a cast expression")

  override def isArrayRef: Boolean = ???

  override def isInstanceOfExpr: Boolean = false

  override def getInstanceOfOp: Val = throw new RuntimeException("Parameter local is not an instanceOf expression")

  override def isLengthExpr: Boolean = false

  override def getLengthOp: Val = throw new RuntimeException("Parameter local is not a length expression")

  override def isIntConstant: Boolean = false

  override def isClassConstant: Boolean = false

  override def getClassConstantType: Type = throw new RuntimeException("Parameter local is not a class constant")

  override def withNewMethod(callee: Method): Val = new OpalParameterLocal(parameterType, callee.asInstanceOf[OpalMethod])

  override def isLongConstant: Boolean = false

  override def getIntValue: Int = throw new RuntimeException("Parameter local is not an int constant")

  override def getLongValue: Long = throw new RuntimeException("Parameter local is not a long constant")

  override def getArrayBase: Pair[Val, Integer] = throw new RuntimeException("Parameter local is not an array ref")

  override def getVariableName: String = ???
}
