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
package test.cases.subclassing;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class SubclassingTarget {

  private static class Superclass {
    AllocatedObject o = new AllocatedObject() {};
  }

  private static class Subclass extends Superclass {}

  private static class ClassWithSubclassField {
    Subclass f;

    public ClassWithSubclassField(Subclass t) {
      this.f = t;
    }
  }

  @TestMethod
  public void typingIssue() {
    Subclass subclass = new Subclass();
    ClassWithSubclassField classWithSubclassField = new ClassWithSubclassField(subclass);
    AllocatedObject query = classWithSubclassField.f.o;
    QueryMethods.queryFor(query);
  }
}
