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
package test.cases.fields.complexity;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class Fields4LongTarget {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @TestMethod
  public void test() {
    TreeNode x = new TreeNode();
    TreeNode p = null;
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        x.a = p;
      }
      if (staticallyUnknown()) {
        x.b = p;
      }
      if (staticallyUnknown()) {
        x.c = p;
      }
      if (staticallyUnknown()) {
        x.d = p;
      }
      p = x;
    }
    TreeNode t = null;
    if (staticallyUnknown()) {
      t = x.a;
    }
    if (staticallyUnknown()) {
      t = x.b;
    }
    if (staticallyUnknown()) {
      t = x.c;
    }
    if (staticallyUnknown()) {
      t = x.d;
    }
    TreeNode h = t;
    QueryMethods.queryFor(h);
  }

  private static class TreeNode implements AllocatedObject {
    TreeNode a = new TreeNode();
    TreeNode b = new TreeNode();
    TreeNode c = new TreeNode();
    TreeNode d = new TreeNode();
  }
}
