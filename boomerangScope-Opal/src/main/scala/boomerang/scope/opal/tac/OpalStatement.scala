/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.transformation.TacLocal
import java.util
import java.util.Objects
import org.opalj.tac.PrimitiveTypecastExpr
import org.opalj.tac.Stmt

class OpalStatement(val delegate: Stmt[TacLocal], m: OpalMethod) extends Statement(m) {

    override def containsStaticFieldAccess(): Boolean =
        isStaticFieldLoad || isStaticFieldStore

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
                return new OpalMethodInvokeExpr(delegate.asMethodCall, m)
            }

            if (delegate.isAssignment && delegate.asAssignment.expr.isFunctionCall) {
                return new OpalFunctionInvokeExpr(
                    delegate.asAssignment.expr.asFunctionCall,
                    m
                )
            }

            if (delegate.isExprStmt && delegate.asExprStmt.expr.isFunctionCall) {
                return new OpalFunctionInvokeExpr(
                    delegate.asExprStmt.expr.asFunctionCall,
                    m
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
                fieldStore.name
            )
        }

        if (isStaticFieldStore) {
            val fieldStore = delegate.asPutStatic

            return new OpalField(
                fieldStore.declaringClass,
                fieldStore.declaredFieldType,
                fieldStore.name
            )
        }

        if (isArrayStore) {
            return Field.array(getArrayBase.getY)
        }

        throw new RuntimeException("Statement is not a field store operation")
    }

    override def isFieldWriteWithBase(base: Val): Boolean = {
        if (isAssignStmt && isFieldStore) {
            return getFieldStore.getX.equals(base)
        }

        if (isAssignStmt && isArrayStore) {
            return getArrayBase.getX.equals(base)
        }

        false
    }

    override def getLoadedField: Field = {
        // TODO Also array?
        if (isFieldLoad) {
            val fieldLoad = delegate.asAssignment.expr.asGetField

            return new OpalField(
                fieldLoad.declaringClass,
                fieldLoad.declaredFieldType,
                fieldLoad.name
            )
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

    override def isAssignStmt: Boolean = {
        if (isIdentityStmt) return false
        if (delegate.isAssignment) return true

        // Store statements are no assignments in Opal
        isFieldStore || isArrayStore || isStaticFieldStore
    }

    override def getLeftOp: Val = {
        if (isAssignStmt) {
            if (delegate.isAssignment) {
                return new OpalLocal(delegate.asAssignment.targetVar, m)
            }

            if (isFieldStore) {
                val fieldStore = delegate.asPutField

                return new OpalInstanceFieldRef(
                    fieldStore.objRef.asVar,
                    fieldStore.declaredFieldType,
                    fieldStore.name,
                    m
                )
            }

            if (isArrayStore) {
                val base = delegate.asArrayStore.arrayRef
                val indexValue = delegate.asArrayStore.index

                if (indexValue.isIntConst) {
                    return new OpalArrayRef(base.asVar, indexValue.asIntConst.value, m)
                }

                return new OpalArrayRef(base.asVar, -1, m)
            }

            if (isStaticFieldStore) {
                val staticFieldStore = delegate.asPutStatic

                return new OpalStaticFieldRef(
                    staticFieldStore.declaringClass,
                    staticFieldStore.declaredFieldType,
                    staticFieldStore.name,
                    m
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
                        getField.objRef.asVar,
                        getField.declaredFieldType,
                        getField.name,
                        m
                    )
                }

                if (rightExpr.isArrayLoad) {
                    val base = rightExpr.asArrayLoad.arrayRef
                    val indexValue = rightExpr.asArrayLoad.index

                    if (indexValue.isIntConst) {
                        return new OpalArrayRef(base.asVar, indexValue.asIntConst.value, m)
                    }

                    return new OpalArrayRef(base.asVar, -1, m)
                }

                if (rightExpr.isGetStatic) {
                    val staticFieldLoad = rightExpr.asGetStatic

                    return new OpalStaticFieldRef(
                        staticFieldLoad.declaringClass,
                        staticFieldLoad.declaredFieldType,
                        staticFieldLoad.name,
                        m
                    )
                }

                if (rightExpr.isVar) {
                    return new OpalLocal(rightExpr.asVar, m)
                }

                return new OpalVal(delegate.asAssignment.expr, m)
            }

            if (isFieldStore) {
                val fieldStore = delegate.asPutField

                if (fieldStore.value.isVar) {
                    return new OpalLocal(fieldStore.value.asVar, m)
                } else {
                    return new OpalVal(fieldStore.value, m)
                }
            }

            if (isArrayStore) {
                val arrayStore = delegate.asArrayStore

                if (arrayStore.value.isVar) {
                    return new OpalLocal(arrayStore.value.asVar, m)
                } else {
                    return new OpalVal(arrayStore.value, m)
                }
            }

            if (isStaticFieldStore) {
                val staticFieldStore = delegate.asPutStatic

                if (staticFieldStore.value.isVar) {
                    return new OpalLocal(staticFieldStore.value.asVar, m)
                } else {
                    return new OpalVal(staticFieldStore.value, m)
                }
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
            return new OpalLocal(delegate.asReturnValue.expr.asVar, m)
        }

        throw new RuntimeException("Statement is not a return statement")
    }

    override def isMultiArrayAllocation: Boolean = false

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

    override def getFieldStore: Pair[Val, Field] = {
        if (isFieldStore) {
            val fieldStore = delegate.asPutField

            val local = new OpalLocal(fieldStore.objRef.asVar, m)
            val field = new OpalField(
                fieldStore.declaringClass,
                fieldStore.declaredFieldType,
                fieldStore.name
            )

            return new Pair(local, field)
        }

        throw new RuntimeException("Statement is not a field store operation")
    }

    override def getFieldLoad: Pair[Val, Field] = {
        if (isFieldLoad) {
            val fieldLoad = delegate.asAssignment.expr.asGetField

            val local = new OpalLocal(fieldLoad.objRef.asVar, m)
            val field = new OpalField(
                fieldLoad.declaringClass,
                fieldLoad.declaredFieldType,
                fieldLoad.name
            )

            return new Pair(local, field)
        }

        throw new RuntimeException("Statement is not a field load operation")
    }

    override def isStaticFieldLoad: Boolean =
        delegate.isAssignment && delegate.asAssignment.expr.isGetStatic

    override def isStaticFieldStore: Boolean = delegate.isPutStatic

    override def getStaticField: StaticFieldVal = {
        if (isStaticFieldLoad) {
            val staticFieldLoad = delegate.asAssignment.expr.asGetStatic

            val staticField = new OpalField(
                staticFieldLoad.declaringClass,
                staticFieldLoad.declaredFieldType,
                staticFieldLoad.name
            )
            return new OpalStaticFieldVal(staticField, m)
        }

        if (isStaticFieldStore) {
            val staticFieldStore = delegate.asPutStatic

            val staticField = new OpalField(
                staticFieldStore.declaringClass,
                staticFieldStore.declaredFieldType,
                staticFieldStore.name
            )
            return new OpalStaticFieldVal(staticField, m)
        }

        throw new RuntimeException(
            "Statement is neither a static field load nor store operation"
        )
    }

    override def killAtIfStmt(fact: Val, successor: Statement): Boolean = false

    override def getPhiVals: util.Collection[Val] = throw new RuntimeException(
        "Not supported"
    )

    override def getArrayBase: Pair[Val, Integer] = {
        if (isArrayLoad) {
            val rightOp = getRightOp
            return rightOp.getArrayBase
        }

        if (isArrayStore) {
            val leftOp = getLeftOp
            return leftOp.getArrayBase
        }

        throw new RuntimeException(
            "Statement is not an array load or array store operation"
        )
    }

    override def getStartLineNumber: Int =
        m.delegate.body.get.lineNumber(delegate.pc).getOrElse(-1)

    override def getStartColumnNumber: Int = -1

    override def getEndLineNumber: Int = -1

    override def getEndColumnNumber: Int = -1

    override def isCatchStmt: Boolean = delegate.isCaughtException

    override def hashCode: Int = Objects.hash(delegate, m)

    override def equals(other: Any): Boolean = other match {
        case that: OpalStatement =>
            this.delegate == that.delegate && this.method == that.method
        case _ => false
    }

    override def toString: String = OpalStatementFormatter(this)
}
