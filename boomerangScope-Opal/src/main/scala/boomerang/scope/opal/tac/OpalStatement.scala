package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.transformer.TacLocal
import com.google.common.base.Joiner
import org.opalj.tac.{DUVar, IdBasedVar, PrimitiveTypecastExpr, Stmt, Var}
import org.opalj.value.ValueInformation

import java.util
import java.util.Objects

class OpalStatement(val delegate: Stmt[TacLocal], m: OpalMethod) extends Statement(m) {

  override def containsStaticFieldAccess(): Boolean = isStaticFieldLoad || isStaticFieldStore

  override def containsInvokeExpr(): Boolean = {
    if (delegate.isMethodCall) return true
    if (delegate.isAssignment && delegate.asAssignment.expr.isFunctionCall) return true
    if (delegate.isExprStmt && delegate.asExprStmt.expr.isFunctionCall) return true

    false
  }

  override def getInvokeExpr: InvokeExpr = {
    if (containsInvokeExpr()) {
      if (delegate.isMethodCall) {
        return new OpalMethodInvokeExpr(delegate.asMethodCall, m)
      }

      if (delegate.isAssignment && delegate.asAssignment.expr.isFunctionCall) {
        return new OpalFunctionInvokeExpr(delegate.asAssignment.expr.asFunctionCall, m)
      }

      if (delegate.isExprStmt && delegate.asExprStmt.expr.isFunctionCall) {
        return new OpalFunctionInvokeExpr(delegate.asExprStmt.expr.asFunctionCall, m)
      }
    }

    throw new RuntimeException("Statement does not contain an invoke expression")
  }

  override def getWrittenField: Field = {
    if (isFieldStore) {
      val fieldStore = delegate.asPutField

      return OpalField(fieldStore.declaringClass, fieldStore.declaredFieldType, fieldStore.name)
    }

    if (isStaticFieldStore) {
      val fieldStore = delegate.asPutStatic

      return OpalField(fieldStore.declaringClass, fieldStore.declaredFieldType, fieldStore.name)
    }

    if (isArrayStore) {
      // TODO
      val arrayStore = delegate.asArrayStore
    }

    throw new RuntimeException("Statement is not a field store operation")
  }

  override def isFieldWriteWithBase(base: Val): Boolean = {
    if (delegate.isAssignment && isFieldStore) {
      return getFieldStore.getX.equals(base)
    }

    if (delegate.isAssignment && isArrayStore) {
      return getArrayBase.getX.equals(base)
    }

    false
  }

  override def getLoadedField: Field = {
    // TODO Also array?
    if (isFieldLoad) {
      val fieldLoad = delegate.asAssignment.expr.asGetField

      return OpalField(fieldLoad.declaringClass, fieldLoad.declaredFieldType, fieldLoad.name)
    }

    throw new RuntimeException("Statement is not a field load operation")
  }

  override def isFieldLoadWithBase(base: Val): Boolean = {
    // TODO Also array?
    if (isFieldLoad) {
      return getFieldLoad.getX.equals(base)
    }

    false
  }

  override def isAssignStmt: Boolean = delegate.isAssignment || isFieldStore || isArrayStore

  override def getLeftOp: Val = {
    if (isAssignStmt) {
      if (delegate.isAssignment) {
        return new OpalLocal(delegate.asAssignment.targetVar, m)
      }

      if (isFieldStore) {
        // TODO Is it correct?
        return new OpalVal(delegate.asPutField.objRef, m)
      }

      if (isArrayStore) {
        val base = delegate.asArrayStore.arrayRef
        val indexValue = delegate.asArrayStore.index

        if (!indexValue.isVar) return new OpalArrayRef(base, -1, m)
        //if (!indexValue.asVar.value.isPrimitiveValue) return new OpalArrayRef(base, -1, m)

        //val index = indexValue.asVar.value.asPrimitiveValue.asConstantInteger.intValue()
        return new OpalArrayRef(base, -1, m)
      }
    }

    throw new RuntimeException("Statement is not an assignment")
  }

  override def getRightOp: Val = {
    if (isAssignStmt) {
      if (delegate.isAssignment) {
        val rightExpr = delegate.asAssignment.expr

        // TODO
        if (rightExpr.isGetField) {}

        if (rightExpr.isArrayLoad) {
          val base = rightExpr.asArrayLoad.arrayRef
          val indexValue = rightExpr.asArrayLoad.index

          if (!indexValue.isVar) return new OpalArrayRef(base, -1, m)
          //if (!indexValue.asVar.value.isPrimitiveValue) return new OpalArrayRef(base, -1, m)

          //val index = indexValue.asVar.value.asPrimitiveValue.asConstantInteger.intValue()
          return new OpalArrayRef(base, -1, m)
        }

        if (rightExpr.isVar) {
          return new OpalLocal(rightExpr.asVar, m)
        }

        return new OpalVal(delegate.asAssignment.expr, m)
      }

      if (isFieldStore) {
        // TODO Distinguish between constant and variable
        return new OpalVal(delegate.asPutField.value, m)
      }

      if (isArrayStore) {
        // TODO Distinguish between constant and variable
        val arrayStore = delegate.asArrayStore
        return new OpalLocal(arrayStore.arrayRef.asVar, m)
      }
    }

    throw new RuntimeException("Statement is not an assignment")
  }

  override def isInstanceOfStatement(fact: Val): Boolean = {
    if (delegate.isAssignment) {
      if (getRightOp.isInstanceOfExpr) {
        val insOf = getRightOp.getInstanceOfOp

        return insOf.equals(fact)
      }
    }

    false
  }

  override def isCast: Boolean = {
    // Primitive type casts
    if (delegate.isAssignment) {
      val assignExpr = delegate.asAssignment.expr

      if (assignExpr.astID == PrimitiveTypecastExpr.ASTID) {
        return true
      }
    }

    // Class casts
    delegate.isCheckcast
  }

  override def isPhiStatement: Boolean = false

  override def isReturnStmt: Boolean = delegate.isReturnValue

  override def isThrowStmt: Boolean = delegate.isThrow

  override def isIfStmt: Boolean = delegate.isIf

  override def getIfStmt: IfStatement = {
    if (isIfStmt) {
      return new OpalIfStatement(delegate.asIf, m)
    }

    throw new RuntimeException("Statement is not an if-statement")
  }

  override def getReturnOp: Val = {
    if (isReturnStmt) {
      return new OpalVal(delegate.asReturnValue.expr, m)
    }

    throw new RuntimeException("Statement is not a return statement")
  }

  override def isMultiArrayAllocation: Boolean = false

  override def isFieldStore: Boolean = delegate.isPutField

  override def isArrayStore: Boolean = delegate.isArrayStore

  override def isArrayLoad: Boolean = delegate.isAssignment && delegate.asAssignment.expr.isArrayLoad

  override def isFieldLoad: Boolean = delegate.isAssignment && delegate.asAssignment.expr.isGetField

  override def isIdentityStmt: Boolean = false

  override def getFieldStore: Pair[Val, Field] = {
    if (isFieldStore) {
      val fieldStore = delegate.asPutField

      val local = new OpalLocal(fieldStore.objRef.asVar, m)
      val field = OpalField(fieldStore.declaringClass, fieldStore.declaredFieldType, fieldStore.name)

      return new Pair(local, field)
    }

    throw new RuntimeException("Statement is not a field store operation")
  }

  override def getFieldLoad: Pair[Val, Field] = {
    if (isFieldLoad) {
      val fieldLoad = delegate.asAssignment.expr.asGetField

      val local = new OpalLocal(fieldLoad.objRef.asVar, m)
      val field = OpalField(fieldLoad.declaringClass, fieldLoad.declaredFieldType, fieldLoad.name)

      return new Pair(local, field)
    }

    throw new RuntimeException("Statement is not a field load operation")
  }

  override def isStaticFieldLoad: Boolean = delegate.isAssignment && delegate.asAssignment.expr.isGetStatic

  override def isStaticFieldStore: Boolean = delegate.isPutStatic

  override def getStaticField: StaticFieldVal = {
    if (isStaticFieldLoad) {
      val staticFieldLoad = delegate.asAssignment.expr.asGetStatic

      val staticField = OpalField(staticFieldLoad.declaringClass, staticFieldLoad.declaredFieldType, staticFieldLoad.name)
      return new OpalStaticFieldVal(staticField, m)
    }

    if (isStaticFieldStore) {
      val staticFieldStore = delegate.asPutStatic

      val staticField = OpalField(staticFieldStore.declaringClass, staticFieldStore.declaredFieldType, staticFieldStore.name)
      return new OpalStaticFieldVal(staticField, m)
    }

    throw new RuntimeException("Statement is neither a static field load nor store operation")
  }

  override def killAtIfStmt(fact: Val, successor: Statement): Boolean = false

  override def getPhiVals: util.Collection[Val] = throw new RuntimeException("Not supported")

  override def getArrayBase: Pair[Val, Integer] = {
    if (isArrayLoad) {
      val rightOp = getRightOp
      return rightOp.getArrayBase
    }

    if (isArrayStore) {
      val leftOp = getLeftOp
      return leftOp.getArrayBase
    }

    throw new RuntimeException("Statement is not an array load or array store operation")
  }

  override def getStartLineNumber: Int = m.delegate.body.get.lineNumber(delegate.pc).getOrElse(-1)

  override def getStartColumnNumber: Int = -1

  override def getEndLineNumber: Int = -1

  override def getEndColumnNumber: Int = -1

  override def isCatchStmt: Boolean = delegate.isCaughtException

  override def hashCode: Int = Objects.hash(delegate)

  private def canEqual(a: Any): Boolean = a.isInstanceOf[OpalStatement]

  override def equals(obj: Any): Boolean = obj match {
    case other: OpalStatement => other.canEqual(this) && this.delegate.pc == other.delegate.pc
    case _ => false
  }

  override def toString: String = OpalStatementFormatter(this)
}
