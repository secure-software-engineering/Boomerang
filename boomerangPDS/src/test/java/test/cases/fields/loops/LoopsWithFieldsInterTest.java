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
package test.cases.fields.loops;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class LoopsWithFieldsInterTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  public void twoFields() {
    Node x = new Node();
    Node p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        leftOf(x).right = p;

      } else {
        rightOf(x).left = p;
      }
      p = x;
    }
    Node t;
    if (staticallyUnknown()) {
      t = rightOf(leftOf(x));

    } else {
      t = leftOf(rightOf(x));
    }
    Node h = t;
    QueryMethods.queryFor(h);
  }

  private Node leftOf(Node x) {
    return x == null ? x.left : null;
  }

  private Node rightOf(Node x) {
    return x == null ? x.right : null;
  }

  private TreeNode leftOf(TreeNode x) {
    return x == null ? x.left : null;
  }

  private TreeNode rightOf(TreeNode x) {
    return x == null ? x.left : null;
  }

  private TreeNode parentOf(TreeNode x) {
    return x == null ? x.parent : null;
  }

  @Test
  public void threeFields() {
    TreeNode x = new TreeNode();
    TreeNode p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        leftOf(x).right = p;

      } else if (staticallyUnknown()) {
        rightOf(x).left = p;
      } else {
        TreeNode u = parentOf(x);
        x = u;
      }
      p = x;
    }
    TreeNode t;
    if (staticallyUnknown()) {
      t = rightOf(leftOf(x));

    } else {
      t = leftOf(rightOf(x));
    }
    TreeNode h = t;
    QueryMethods.queryFor(h);
  }

  private static class Node implements AllocatedObject {
    Node left = new Node();
    Node right = new Node();
  }

  private static class TreeNode implements AllocatedObject {
    TreeNode left = new TreeNode();
    TreeNode right = new TreeNode();
    TreeNode parent = new TreeNode();
  }
}
