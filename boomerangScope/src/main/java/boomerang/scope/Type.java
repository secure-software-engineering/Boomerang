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
package boomerang.scope;

public interface Type {

  boolean isNullType();

  boolean isRefType();

  boolean isArrayType();

  Type getArrayBaseType();

  WrappedClass getWrappedClass();

  boolean isSubtypeOf(String type);

  boolean isSupertypeOf(String subType);

  boolean isBooleanType();

  /**
   * Checks whether a value of this type may be stored in a variable of type {@code target}, i.e.
   * whether this type is a subtype of {@code target} or equal to it.
   *
   * <p>Implementations must answer conservatively: if the relation cannot be determined (e.g.
   * phantom classes or missing hierarchy information), they return {@code true}. The default
   * implementation always returns {@code true}.
   *
   * @param target the type of the variable the value is stored in
   * @return false only if a value of this type can never be stored in a variable of type {@code
   *     target}
   */
  default boolean isAssignableTo(Type target) {
    return true;
  }
}
