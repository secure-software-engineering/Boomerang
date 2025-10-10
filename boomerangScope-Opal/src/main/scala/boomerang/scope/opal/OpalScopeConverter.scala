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
import org.opalj.br.ClassType
import org.opalj.br.FieldType
import org.opalj.br.Method
import org.opalj.br.MethodDescriptor
import org.opalj.br.analyses.Project
import org.opalj.tac.Expr
import org.opalj.tac.Stmt

/**
 * Converter to create Boomerang scope objects from Opal objects and extract Opal objects from
 * Boomerang scope objects.
 */
object OpalScopeConverter {

  /**
   * Create an [[OpalField]] from corresponding field information. The returned object represents a
   * field in Boomerang's Opal scope.
   *
   * @param declaringClass the class that defines the field
   * @param fieldType the field's type
   * @param name the field's name
   * @return the field object in Boomerang's Opal scope
   */
  def createOpalField(declaringClass: ClassType, fieldType: FieldType, name: String) =
    new OpalField(declaringClass, fieldType, name)

  /**
   * Extract the [[OpalField]] object from a [[Field]]. This requires the field to be an [[OpalField]].
   *
   * @param field the [[OpalField]]
   * @return the corresponding [[OpalField]]
   */
  def extractOpalField(field: Field): OpalField = field match {
    case opalField: OpalField => opalField
    case _ => throw new RuntimeException("Field is not an OpalField")
  }

  /**
   * Create an [[OpalMethod]] from a [[Method]]. The returned object represents a
   * method in Boomerang's Opal scope. The method is expected to have an existing [[org.opalj.br.Code]].
   *
   * @param method the method with an existing [[org.opalj.br.Code]]
   * @param project  the project containing the method
   * @return the method object in Boomerang's Opal scope
   */
  def createOpalMethod(method: Method, project: Project[_]): OpalMethod = OpalMethod.of(method, project)

  /**
   * Extract the delegated [[Method]] from a [[DefinedMethod]]. This requires the method
   * to be an [[OpalMethod]].
   *
   * @param method the [[OpalMethod]] to extract the [[Method]] from
   * @return the delegated [[Method]]
   */
  def extractOpalMethod(method: DefinedMethod): Method = method match {
    case opalMethod: OpalMethod => opalMethod.delegate
    case _ => throw new RuntimeException("Method is not an OpalMethod")
  }

  /**
   * Create an [[OpalPhantomMethod]] from corresponding method information. The returned object
   * represents a method without an existing [[org.opalj.br.Code]] (phantom method) in Boomerang's Opal scope.
   *
   * @param declaringClassType the class type that declares the method
   * @param name the method's name
   * @param descriptor the method's descriptor
   * @param static whether the method is static
   * @param project the project containing the method
   * @return the method object in Boomerang's Soot scope
   */
  def createOpalPhantomMethod(
      declaringClassType: ClassType,
      name: String,
      descriptor: MethodDescriptor,
      static: Boolean,
      project: Project[_]
  ): OpalPhantomMethod = OpalPhantomMethod.of(declaringClassType, name, descriptor, static, project)

  /**
   * Extract the delegated [[OpalPhantomMethod]]. This requires the
   * method to be a [[OpalPhantomMethod]].
   *
   * @param method the [[PhantomMethod]] to extract the [[OpalPhantomMethod]] from
   * @return the delegated [[OpalPhantomMethod]]
   */
  def extractOpalPhantomMethod(method: PhantomMethod): OpalPhantomMethod = method match {
    case opalMethod: OpalPhantomMethod => opalMethod
    case _ => throw new RuntimeException("Method is not an OpalPhantomMethod")
  }

  /**
   * Create an [[OpalStatement]] from a [[Stmt]]. The returned object represents a
   * statement in Boomerang's Opal scope.
   *
   * @param stmt   the statement to convert
   * @param method the method that contains the statement
   * @return the statement object in Boomerang's Opal scope
   */
  def createOpalStatement(stmt: Stmt[TacLocal], method: OpalMethod) = new OpalStatement(stmt, method)

  /**
   * Creates an [[OpalStatement]] from an arbitrary expression. Note that the generic type is cast to [[TacLocal]].
   * Hence, the expression should align with this Boomerang scope.
   *
   * @param stmt   the statement to wrap
   * @param method the method from the statement
   * @return the [[OpalStatement]] wrapper for the statement
   */
  def createOpalStatementUnsafe(stmt: Stmt[_], method: OpalMethod) =
    new OpalStatement(stmt.asInstanceOf[Stmt[TacLocal]], method)

  /**
   * Extract the delegated [[Stmt]] from a [[Statement]]. This requires the statement to be
   * an [[OpalStatement]].
   *
   * @param statement the [[OpalStatement]] to extract the [[Stmt]] from
   * @return the delegated [[Stmt]]
   */
  def extractOpalStatement(statement: Statement): Stmt[TacLocal] = statement match {
    case stmt: OpalStatement => stmt.delegate
    case _ => throw new RuntimeException("Statement is not an OpalStatement")
  }

  /**
   * Create an [[OpalType]] from a [[org.opalj.br.Type]]. The returned object represents a type in
   * Boomerang's Opal scope.
   *
   * @param t  the type to convert
   * @param project the project to resolve type hierarchy information
   * @return the type object in Boomerang's Soot scope
   */
  def createOpalType(t: org.opalj.br.Type, project: Project[_]) = new OpalType(t, project)

  /**
   * Extract the delegated [[org.opalj.br.Type]] from a [[Type]]. This requires the type
   * to be an [[OpalType]].
   *
   * @param t the [[OpalType]] to extract the [[org.opalj.br.Type]] from
   * @return the delegated [[org.opalj.br.Type]]
   */
  def extractOpalType(t: Type): org.opalj.br.Type = t match {
    case opalType: OpalType => opalType.delegate
    case _ => throw new RuntimeException("Type is not an OpalType")
  }

  /**
   * Create an [[OpalVal]] from an [[Expr]]. The returned object represents an arbitrary
   * expression in Boomerang's Opal scope.
   *
   * @param expr  the expression to convert
   * @param method the method that belongs to the expression
   * @return the val object in Boomerang's SootUp scope
   */
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

  /**
   * Extract the delegated [[Expr]] from a [[Val]]. This requires the type to be an [[OpalVal]].
   *
   * @param v the [[OpalVal]] to extract the [[Expr]] from
   * @return the delegated [[Expr]]
   */
  def extractOpalExpr(v: Val): Expr[TacLocal] = v match {
    case opalVal: OpalVal => opalVal.delegate
    case _ => throw new RuntimeException("Val is not an OpalVal")
  }

  /**
   * Create an [[OpalWrappedClass]] from an [[ClassType]]. The returned object represents a
   * class in Boomerang's Opal scope.
   *
   * @param objectType the object type containing relevant class information
   * @param project    the project containing the class type
   * @return the class object in Boomerang's Opal scope
   */
  def createOpalWrappedClass(objectType: ClassType, project: Project[_]) =
    new OpalWrappedClass(objectType, project)

  /**
   * Extract the delegated [[ClassType]] from a [[WrappedClass]]. This requires the type to
   * be an [[OpalWrappedClass]].
   *
   * @param wrappedClass the [[OpalWrappedClass]] to extract the [[ClassType]] from
   * @return the delegated [[ClassType]]
   */
  def extractOpalClass(wrappedClass: WrappedClass): ClassType = wrappedClass match {
    case opalClass: OpalWrappedClass => opalClass.delegate
    case _ => throw new RuntimeException("WrappedClass is not an OpalWrappedClass")
  }
}
