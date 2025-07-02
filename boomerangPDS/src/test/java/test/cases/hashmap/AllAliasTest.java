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
package test.cases.hashmap;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestParameters;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class AllAliasTest {

  @Test
  @TestParameters(ignoreAllocSites = true)
  public void test() {
    TreeNode<Object, Object> a = new TreeNode<>(0, new Object(), new Object(), null);
    TreeNode<Object, Object> t = new TreeNode<>(0, null, new Object(), a);
    TreeNode.balanceDeletion(t, a);
    // t.balanceInsertion(t, t);
    t.treeify(new TreeNode[] {a, t});
    // t.moveRootToFront(new TreeNode[]{a,t},a);
    QueryMethods.queryFor(t);
  }
}
