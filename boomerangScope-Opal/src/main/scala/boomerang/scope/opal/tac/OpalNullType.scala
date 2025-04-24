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

import boomerang.scope.Type
import boomerang.scope.Val
import boomerang.scope.WrappedClass

object OpalNullType extends Type {

  override def isNullType: Boolean = true

  override def isRefType: Boolean = false

  override def isArrayType: Boolean = false

  override def getArrayBaseType: Type = throw new RuntimeException(
    "Null type has no array base type"
  )

  override def getWrappedClass: WrappedClass = throw new RuntimeException(
    "Null type has no declaring class"
  )

  override def doesCastFail(targetVal: Type, target: Val): Boolean = true

  override def isSubtypeOf(superType: String): Boolean = false

  override def isSupertypeOf(subType: String): Boolean = false

  override def isBooleanType: Boolean = false
}
