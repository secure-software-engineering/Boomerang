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
package boomerang.utils;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Wrapper class for arbitrary methods. This class is used because each framework uses different
 * signatures to describe and resolve methods. Having an object that contains all method information
 * allows the frameworks to build their own signatures independently.
 */
public class MethodWrapper {

  private final String declaringClass;
  private final String methodName;
  private final String returnType;
  private final List<String> parameters;

  public static final String VOID = "void";

  public MethodWrapper(String declaringClass, String methodName) {
    this(declaringClass, methodName, VOID);
  }

  public MethodWrapper(String declaringClass, String methodName, String returnType) {
    this(declaringClass, methodName, returnType, Collections.emptyList());
  }

  public MethodWrapper(
      String declaringClass, String methodName, String returnType, List<String> parameters) {
    this.declaringClass = declaringClass;
    this.methodName = methodName;
    this.returnType = returnType;
    this.parameters = parameters;
  }

  public String getDeclaringClass() {
    return declaringClass;
  }

  public String getMethodName() {
    return methodName;
  }

  public String getReturnType() {
    return returnType;
  }

  public List<String> getParameters() {
    return parameters;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    MethodWrapper that = (MethodWrapper) o;
    return Objects.equals(declaringClass, that.declaringClass)
        && Objects.equals(methodName, that.methodName)
        && Objects.equals(returnType, that.returnType)
        && Objects.equals(parameters, that.parameters);
  }

  @Override
  public int hashCode() {
    return Objects.hash(declaringClass, methodName, returnType, parameters);
  }

  @Override
  public String toString() {
    return declaringClass + " " + returnType + " " + methodName + " " + parameters;
  }
}
