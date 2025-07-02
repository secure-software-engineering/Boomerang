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
package test.cases.fields.complexity;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class Fields6LongTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
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
      if (staticallyUnknown()) {
        x.e = p;
      }
      if (staticallyUnknown()) {
        x.f = p;
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
    if (staticallyUnknown()) {
      t = x.e;
    }
    if (staticallyUnknown()) {
      t = x.f;
    }
    TreeNode h = t;
    QueryMethods.queryFor(h);
  }

  private static class TreeNode implements AllocatedObject {
    TreeNode a = new TreeNode();
    TreeNode b = new TreeNode();
    TreeNode c = new TreeNode();
    TreeNode d = new TreeNode();
    TreeNode e = new TreeNode();
    TreeNode f = new TreeNode();
  }
}
