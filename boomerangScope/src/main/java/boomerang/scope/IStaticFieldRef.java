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

/**
 * Interface that provides basic methods to deal with static field references. A static field
 * reference <i>C.f</i> consists of a declaring class <i>C</i> and a field <i>f</i>.
 */
public interface IStaticFieldRef {

  WrappedClass getDeclaringClass();

  Field getField();

  StaticFieldVal asStaticFieldVal();
}
