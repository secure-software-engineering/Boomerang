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

import boomerang.scope.opal.tac.OpalField
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.opal.tac.OpalPhantomMethod
import boomerang.scope.opal.tac.OpalStatement
import boomerang.scope.opal.tac.OpalType
import boomerang.scope.opal.tac.OpalVal
import boomerang.scope.opal.tac.OpalWrappedClass
import boomerang.scope.opal.transformation.TacLocal
import org.opalj.br.FieldType
import org.opalj.br.Method
import org.opalj.br.MethodDescriptor
import org.opalj.br.ObjectType
import org.opalj.br.Type
import org.opalj.br.analyses.Project
import org.opalj.tac.Expr
import org.opalj.tac.Stmt

object OpalScopeFactory {

  def createOpalField(declaringClass: ObjectType, fieldType: FieldType, name: String): OpalField =
    new OpalField(declaringClass, fieldType, name)

  def createOpalMethod(method: Method, project: Project[_]): OpalMethod = OpalMethod.of(method, project)

  def createOpalPhantomMethod(
      declaringClassType: ObjectType,
      name: String,
      descriptor: MethodDescriptor,
      static: Boolean,
      project: Project[_]
  ): OpalPhantomMethod = OpalPhantomMethod.of(declaringClassType, name, descriptor, static, project)

  def createOpalStatement(stmt: Stmt[TacLocal], method: OpalMethod): OpalStatement = new OpalStatement(stmt, method)

  /**
   * Creates an [[OpalStatement]] from an arbitrary expression. Note that the generic type is cast to [[TacLocal]].
   * Hence, the expression should align with this Boomerang scope.
   *
   * @param stmt   the statement to wrap
   * @param method the method from the statement
   * @return the [[OpalStatement]] wrapper for the statement
   */
  def createOpalStatementUnsafe(stmt: Stmt[_], method: OpalMethod): OpalStatement =
    new OpalStatement(stmt.asInstanceOf[Stmt[TacLocal]], method)

  def createOpalType(t: Type, project: Project[_]): OpalType = new OpalType(t, project)

  def createOpalVal(expr: Expr[TacLocal], method: OpalMethod) = new OpalVal(expr, method)

  /**
   * Creates an [[OpalVal]] from an arbitrary expression. Note that the generic type is cast to [[TacLocal]].
   * Hence, the expression should align with this Boomerang scope.
   *
   * @param expr   the expression to wrap
   * @param method the method from the expression
   * @return the [[OpalVal]] wrapper for the expression
   */
  def createOpalValUnsafe(expr: Expr[_], method: OpalMethod) = new OpalVal(expr.asInstanceOf[Expr[TacLocal]], method)

  def createOpalWrappedClass(objectType: ObjectType, project: Project[_]): OpalWrappedClass =
    new OpalWrappedClass(objectType, project)
}
