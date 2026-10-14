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
package test.aliasing.pointerbench;

import org.junit.jupiter.api.Test;
import target.aliasing.PointerBench.generalJava.*;
import test.aliasing.AliasingTestSetUp;

public class GeneralJavaTest extends AliasingTestSetUp {

  @Test
  public void exception1() {
    String queryLHS = "b_q1";
    String targetClass = Exception1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void interface1() {
    String queryLHS = "c_q1";
    String targetClass = Interface1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void null1() {
    String queryLHS = "b_q1";
    String targetClass = Null1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void null2() {
    String queryLHS = "x_q1";
    String targetClass = Null2.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void outerClass1() {
    String queryLHS = "h_q1";
    String targetClass = OuterClass1.class.getName();
    String targetMethod = "test";
    runAnalyses(queryLHS, targetClass, targetMethod);
  }

  @Test
  public void staticVariables1() {
    String queryLHS = "b_q1";
    String targetClass = StaticVariables1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void superClasses1() {
    String queryLHS = "h_q1";
    String targetClass = SuperClasses1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }
}
