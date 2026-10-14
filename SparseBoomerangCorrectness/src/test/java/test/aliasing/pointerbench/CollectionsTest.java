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
import target.aliasing.PointerBench.collections.*;
import test.aliasing.AliasingTestSetUp;

public class CollectionsTest extends AliasingTestSetUp {

  @Test
  public void array1() {
    String queryLHS = "c_q1";
    String targetClass = Array1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void list1() {
    String queryLHS = "b_q1";
    String targetClass = List1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void list2() {
    String queryLHS = "b_q1";
    String targetClass = List2.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void Map1() {
    String queryLHS = "c_q1";
    String targetClass = Map1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }

  @Test
  public void Set1() {
    String queryLHS = "c_q1";
    String targetClass = Set1.class.getName();
    runAnalyses(queryLHS, targetClass, null);
  }
}
