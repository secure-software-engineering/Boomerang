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
package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.NullifiedLocal
import boomerang.scope.opal.transformation.StmtGraph
import boomerang.scope.opal.transformation.TacLocal
import org.opalj.br.ComputationalTypeReference
import org.opalj.br.Field
import org.opalj.br.Method
import org.opalj.tac.Assignment
import org.opalj.tac.Expr
import org.opalj.tac.NullExpr
import org.opalj.tac.PutField

object NullifyFieldsTransformer {

  private final val NULLIFIED_FIELD = -2

  def apply(method: Method, stmtGraph: StmtGraph): StmtGraph = {
    if (!method.isConstructor || method.isStatic) {
      return stmtGraph
    }

    val tac = stmtGraph.tac
    var localCounter = 0

    def isFieldDefined(field: Field): Boolean = {
      // TODO Soot considers super classes, too (not sure if required)
      tac.foreach {
        // TODO Maybe also match 'this' local?
        case PutField(_, _, field.name, field.fieldType, _, _) => return true
        case _ =>
      }
      false
    }

    def getThisLocal: Expr[TacLocal] = {
      tac.foreach(stmt => {
        if (stmt.pc == -1 && stmt.asAssignment.targetVar.id == -1)
          return stmt.asAssignment.targetVar
      })

      throw new RuntimeException(
        "'this' local not found in method: " + method.name
      )
    }

    def createNullifiedLocal(localCounter: Int): NullifiedLocal = {
      new NullifiedLocal(
        localCounter,
        ComputationalTypeReference
      )
    }

    val definedFields = method.classFile.fields.filter(f => isFieldDefined(f))
    val undefinedFields =
      method.classFile.fields.filter(f => !definedFields.contains(f) && f.isNotStatic && f.isNotFinal)
    val firstOriginalStmt = tac.find(stmt => stmt.pc >= 0).get
    var result = stmtGraph

    undefinedFields.foreach(field => {
      val local = createNullifiedLocal(localCounter)
      localCounter += 1

      val defSite =
        Assignment[TacLocal](NULLIFIED_FIELD, local, NullExpr(NULLIFIED_FIELD))
      val putField = PutField(
        NULLIFIED_FIELD,
        method.classFile.thisType,
        field.name,
        field.fieldType,
        getThisLocal,
        local
      )

      result = result.insertBefore(defSite, firstOriginalStmt)
      result = result.insertBefore(putField, firstOriginalStmt)
    })

    result
  }

}
