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
package boomerang.scope.test.targets;

public class ParameterLocalsTarget {

  public static void main(String[] args) {
    ParameterLocalsTarget parameterLocalsTarget = new ParameterLocalsTarget();

    parameterLocalsTarget.noParameters();
    parameterLocalsTarget.oneParameter(10);

    A a = new A();
    parameterLocalsTarget.twoParameters(20, a);
  }

  public void noParameters() {}

  public void oneParameter(@SuppressWarnings("unused") int i) {}

  public void twoParameters(@SuppressWarnings("unused") int i, @SuppressWarnings("unused") A a) {
    a.methodCall(i);
  }
}
