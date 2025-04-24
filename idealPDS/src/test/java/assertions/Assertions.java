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
package assertions;

public class Assertions {

  public static void mustBeInAcceptingState(@SuppressWarnings("unused") Object o) {}

  public static void mustBeInErrorState(@SuppressWarnings("unused") Object o) {}

  public static void mayBeInAcceptingState(@SuppressWarnings("unused") Object o) {}

  public static void mayBeInErrorState(@SuppressWarnings("unused") Object o) {}

  public static void shouldNotBeAnalyzed() {}
}
