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
public class LoopsWithFieldsIntraTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  public void oneFields() {
    Node x = new Node();
    Node p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        x.left = p;

      } else if (staticallyUnknown()) {
        x.right = p;
      }
      p = x;
    }
    Node t;
    if (staticallyUnknown()) {
      t = x.left;

    } else {
      t = x.right;
    }
    Node h = t;
    QueryMethods.queryFor(h);
  }

  @Test
  public void twoFields() {
    Node x = new Node();
    Node p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        x.left.right = p;

      } else if (staticallyUnknown()) {
        x.right.left = p;
      }
      p = x;
    }
    Node t;
    if (staticallyUnknown()) {
      t = x.left.right;
    } else {
      t = x.right.left;
    }
    Node h = t;
    QueryMethods.queryFor(h);
  }

  @Test
  public void twoFieldSimpleLoop() {
    Node x = new Node();
    while (staticallyUnknown()) {
      x.left.right = x;
    }
    Node h = x.left.right;
    QueryMethods.queryFor(h);
  }

  @Test
  public void twoFieldSimpleLoopWithBranched() {
    Node x = new Node();
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        x.left.right = x;
      } else {
        x.right.left = null;
      }
    }
    Node h = x.left.right;
    QueryMethods.queryFor(h);
  }

  @Test
  public void threeFields() {
    TreeNode x = new TreeNode();
    TreeNode p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        x.left.right = p;

      } else if (staticallyUnknown()) {
        x.right.left = p;
      } else {
        TreeNode u = x.parent;
        x = u;
      }
      p = x;
    }
    TreeNode t;
    if (staticallyUnknown()) {
      t = x.left.right;

    } else {
      t = x.right.left;
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

  private static class SingleNode implements AllocatedObject {
    SingleNode left = null;
  }

  @Test
  public void oneFieldSimpleLoopSingle() {
    SingleNode x = new SingleNode();
    while (staticallyUnknown()) {
      x.left = x;
    }
    SingleNode h = x.left;
    QueryMethods.queryFor(h);
  }
}
