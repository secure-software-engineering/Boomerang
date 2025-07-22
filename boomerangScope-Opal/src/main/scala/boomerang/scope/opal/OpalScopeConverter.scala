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
package boomerang.scope.opal

import boomerang.scope.DefinedMethod
import boomerang.scope.Field
import boomerang.scope.PhantomMethod
import boomerang.scope.Statement
import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass
import boomerang.scope.opal.tac.OpalField
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.opal.tac.OpalPhantomMethod
import boomerang.scope.opal.tac.OpalStatement
import boomerang.scope.opal.tac.OpalType
import boomerang.scope.opal.tac.OpalVal
import boomerang.scope.opal.tac.OpalWrappedClass
import boomerang.scope.opal.transformation.TacLocal
import org.opalj.br.Method
import org.opalj.br.ObjectType
import org.opalj.tac.Expr
import org.opalj.tac.Stmt

object OpalScopeConverter {

  def toOpalField(field: Field): OpalField = {
    field match {
      case opalField: OpalField => opalField
      case _ => throw new RuntimeException("Field is not an OpalField")
    }
  }

  def toOpalMethod(method: DefinedMethod): Method = {
    method match {
      case opalMethod: OpalMethod => opalMethod.delegate
      case _ => throw new RuntimeException("Method is not an OpalMethod")
    }
  }

  def toOpalPhantomMethod(method: PhantomMethod): OpalPhantomMethod = {
    method match {
      case opalMethod: OpalPhantomMethod => opalMethod
      case _ => throw new RuntimeException("Method is not an OpalPhantomMethod")
    }
  }

  def toOpalStatement(statement: Statement): Stmt[TacLocal] = {
    statement match {
      case stmt: OpalStatement => stmt.delegate
      case _ => throw new RuntimeException("Statement is not an OpalStatement")
    }
  }

  def toOpalType(t: Type): org.opalj.br.Type = {
    t match {
      case opalType: OpalType => opalType.delegate
      case _ => throw new RuntimeException("Type is not an OpalType")
    }
  }

  def toOpalExpr(v: Val): Expr[TacLocal] = {
    v match {
      case opalVal: OpalVal => opalVal.delegate
      case _ => throw new RuntimeException("Val is not an OpalVal")
    }
  }

  def toOpalClass(wrappedClass: WrappedClass): ObjectType = {
    wrappedClass match {
      case opalClass: OpalWrappedClass => opalClass.delegate
      case _ => throw new RuntimeException("WrappedClass is not an OpalWrappedClass")
    }
  }
}
