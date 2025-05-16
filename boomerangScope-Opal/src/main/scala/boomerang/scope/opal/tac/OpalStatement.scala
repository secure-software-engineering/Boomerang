/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.transformation.TacLocal
import java.util
import java.util.Objects
import org.opalj.tac.PrimitiveTypecastExpr
import org.opalj.tac.Stmt

class OpalStatement(val delegate: Stmt[TacLocal], opalMethod: OpalMethod) extends Statement(opalMethod) {

  override def containsInvokeExpr(): Boolean = {
    if (delegate.isMethodCall) return true
    if (delegate.isAssignment && delegate.asAssignment.expr.isFunctionCall)
      return true
    if (delegate.isExprStmt && delegate.asExprStmt.expr.isFunctionCall)
      return true

    false
  }

  override def getInvokeExpr: InvokeExpr = {
    if (containsInvokeExpr()) {
      if (delegate.isMethodCall) {
        return new OpalMethodInvokeExpr(delegate.asMethodCall, opalMethod)
      }

      if (delegate.isAssignment && delegate.asAssignment.expr.isFunctionCall) {
        return new OpalFunctionInvokeExpr(
          delegate.asAssignment.expr.asFunctionCall,
          opalMethod
        )
      }

      if (delegate.isExprStmt && delegate.asExprStmt.expr.isFunctionCall) {
        return new OpalFunctionInvokeExpr(
          delegate.asExprStmt.expr.asFunctionCall,
          opalMethod
        )
      }
    }

    throw new RuntimeException(
      "Statement does not contain an invoke expression"
    )
  }

  override def getWrittenField: Field = {
    if (isFieldStore) {
      val fieldStore = delegate.asPutField

      return new OpalField(
        fieldStore.declaringClass,
        fieldStore.declaredFieldType,
        fieldStore.name,
        opalMethod.project
      )
    }

    if (isStaticFieldStore) {
      val fieldStore = delegate.asPutStatic

      return new OpalField(
        fieldStore.declaringClass,
        fieldStore.declaredFieldType,
        fieldStore.name,
        opalMethod.project
      )
    }

    if (isArrayStore) {
      return Field.array(getArrayBase.getIndex)
    }

    throw new RuntimeException("Statement is not a field store operation")
  }

  override def isFieldWriteWithBase(base: Val): Boolean = {
    if (isFieldStore) {
      val fieldStore = getFieldStore

      return fieldStore.getBase.equals(base)
    }

    if (isAssignStmt && isArrayStore) {
      val arrayBase = getArrayBase

      return arrayBase.getBase.equals(base)
    }

    false
  }

  override def getLoadedField: Field = {
    // TODO
    //  Also consider arrays? Soot does not consider them, but they
    //  are considered in field store operations
    if (isFieldLoad) {
      val fieldLoad = delegate.asAssignment.expr.asGetField

      return new OpalField(
        fieldLoad.declaringClass,
        fieldLoad.declaredFieldType,
        fieldLoad.name,
        opalMethod.project
      )
    }

    throw new RuntimeException("Statement is not a field load operation")
  }

  override def isFieldLoadWithBase(base: Val): Boolean = {
    // TODO
    //  Also consider arrays? Soot does not consider them, but they
    //  are considered in field store operations
    if (isFieldLoad) {
      val fieldLoad = getFieldLoad

      return fieldLoad.getBase.equals(base)
    }

    false
  }

  override def isAssignStmt: Boolean = {
    if (isIdentityStmt) return false
    if (delegate.isAssignment) return true

    // Store statements are no assignments in Opal
    isFieldStore || isArrayStore || isStaticFieldStore
  }

  override def getLeftOp: Val = {
    if (isAssignStmt) {
      if (delegate.isAssignment) {
        return new OpalVal(delegate.asAssignment.targetVar, opalMethod)
      }

      if (isFieldStore) {
        val fieldStore = delegate.asPutField

        return new OpalInstanceFieldRef(
          fieldStore.objRef,
          fieldStore.declaringClass,
          fieldStore.declaredFieldType,
          fieldStore.name,
          opalMethod
        )
      }

      if (isArrayStore) {
        val base = delegate.asArrayStore.arrayRef
        val indexExpr = delegate.asArrayStore.index

        return new OpalArrayRef(base.asVar, indexExpr, opalMethod)
      }

      if (isStaticFieldStore) {
        val staticFieldStore = delegate.asPutStatic

        return new OpalStaticFieldRef(
          staticFieldStore.declaringClass,
          staticFieldStore.declaredFieldType,
          staticFieldStore.name,
          opalMethod
        )
      }
    }

    throw new RuntimeException("Statement is not an assignment")
  }

  override def getRightOp: Val = {
    if (isAssignStmt) {
      if (delegate.isAssignment) {
        val rightExpr = delegate.asAssignment.expr

        if (rightExpr.isGetField) {
          val getField = rightExpr.asGetField

          return new OpalInstanceFieldRef(
            getField.objRef,
            getField.declaringClass,
            getField.declaredFieldType,
            getField.name,
            opalMethod
          )
        }

        if (rightExpr.isArrayLoad) {
          val base = rightExpr.asArrayLoad.arrayRef
          val indexExpr = rightExpr.asArrayLoad.index

          return new OpalArrayRef(base.asVar, indexExpr, opalMethod)
        }

        if (rightExpr.isGetStatic) {
          val staticFieldLoad = rightExpr.asGetStatic

          return new OpalStaticFieldRef(
            staticFieldLoad.declaringClass,
            staticFieldLoad.declaredFieldType,
            staticFieldLoad.name,
            opalMethod
          )
        }

        return new OpalVal(rightExpr, opalMethod)
      }

      if (isFieldStore) {
        val fieldStore = delegate.asPutField

        return new OpalVal(fieldStore.value, opalMethod)
      }

      if (isArrayStore) {
        val arrayStore = delegate.asArrayStore

        return new OpalVal(arrayStore.value, opalMethod)
      }

      if (isStaticFieldStore) {
        val staticFieldStore = delegate.asPutStatic

        return new OpalVal(staticFieldStore.value, opalMethod)
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
      return new OpalIfStatement(delegate.asIf, opalMethod)
    }

    throw new RuntimeException("Statement is not an if-statement")
  }

  override def getReturnOp: Val = {
    if (isReturnStmt) {
      return new OpalVal(delegate.asReturnValue.expr, opalMethod)
    }

    throw new RuntimeException("Statement is not a return statement")
  }

  override def isFieldStore: Boolean = delegate.isPutField

  override def isArrayStore: Boolean = delegate.isArrayStore

  override def isArrayLoad: Boolean =
    delegate.isAssignment && delegate.asAssignment.expr.isArrayLoad

  override def isFieldLoad: Boolean =
    delegate.isAssignment && delegate.asAssignment.expr.isGetField

  override def isIdentityStmt: Boolean = {
    if (delegate.isAssignment) {
      /* Difference between Soot and Opal:
       * - Soot considers parameter definitions and self assignments of the this local as identity statements (no assignments)
       * - Opal considers these statements as basic assignments, so we have to exclude them manually
       */
      val targetVar = delegate.asAssignment.targetVar
      val expr = delegate.asAssignment.expr

      if (expr.isVar) {
        if (expr.asVar.isParameterLocal) return true
        if (expr.asVar.isExceptionLocal) return true
        if (targetVar.isThisLocal && expr.asVar.isThisLocal) return true
      }
    }

    false
  }

  override def getFieldStore: IInstanceFieldRef = {
    if (isFieldStore) {
      val fieldStore = delegate.asPutField

      return new OpalInstanceFieldRef(
        fieldStore.objRef,
        fieldStore.declaringClass,
        fieldStore.declaredFieldType,
        fieldStore.name,
        opalMethod
      )
    }

    throw new RuntimeException("Statement is not a field store operation")
  }

  override def getFieldLoad: IInstanceFieldRef = {
    if (isFieldLoad) {
      val fieldLoad = delegate.asAssignment.expr.asGetField

      return new OpalInstanceFieldRef(
        fieldLoad.objRef,
        fieldLoad.declaringClass,
        fieldLoad.declaredFieldType,
        fieldLoad.name,
        opalMethod
      )
    }

    throw new RuntimeException("Statement is not a field load operation")
  }

  override def isStaticFieldLoad: Boolean =
    delegate.isAssignment && delegate.asAssignment.expr.isGetStatic

  override def isStaticFieldStore: Boolean = delegate.isPutStatic

  override def getStaticField: IStaticFieldRef = {
    if (isStaticFieldLoad) {
      val staticFieldLoad = delegate.asAssignment.expr.asGetStatic

      return new OpalStaticFieldRef(
        staticFieldLoad.declaringClass,
        staticFieldLoad.declaredFieldType,
        staticFieldLoad.name,
        opalMethod
      )
    }

    if (isStaticFieldStore) {
      val staticFieldStore = delegate.asPutStatic

      return new OpalStaticFieldRef(
        staticFieldStore.declaringClass,
        staticFieldStore.declaredFieldType,
        staticFieldStore.name,
        opalMethod
      )
    }

    throw new RuntimeException(
      "Statement is not a static field load or store operation"
    )
  }

  override def killAtIfStmt(fact: Val, successor: Statement): Boolean = false

  override def getPhiVals: util.Collection[Val] = throw new RuntimeException(
    "Not supported"
  )

  override def getArrayBase: IArrayRef = {
    if (isArrayLoad) {
      val arrayLoad = delegate.asAssignment.expr.asArrayLoad

      return new OpalArrayRef(arrayLoad.arrayRef.asVar, arrayLoad.index, opalMethod)
    }

    if (isArrayStore) {
      val arrayStore = delegate.asArrayStore

      return new OpalArrayRef(arrayStore.arrayRef.asVar, arrayStore.index, opalMethod)
    }

    throw new RuntimeException(
      "Statement is not an array load or array store statement"
    )
  }

  override def getLineNumber: Int =
    opalMethod.delegate.body.get.lineNumber(delegate.pc).getOrElse(-1)

  override def isCatchStmt: Boolean = delegate.isCaughtException

  override def hashCode: Int = Objects.hash(delegate, opalMethod)

  override def equals(other: Any): Boolean = other match {
    case that: OpalStatement =>
      this.delegate == that.delegate && this.method == that.method
    case _ => false
  }

  override def toString: String = OpalStatementFormatter(this)
}
