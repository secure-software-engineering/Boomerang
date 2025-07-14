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
package test.cases.context;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class OuterAllocationTest {

  @Test
  public void mainTest() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    ObjectWithField otherContainer = new ObjectWithField();
    File a = container.field;
    otherContainer.field = a;
    flows(container);
  }

  private void flows(ObjectWithField container) {
    File field = container.field;
    field.open();
    QueryMethods.queryFor(field);
  }

  private static class File implements AllocatedObject {
    public void open() {}
  }

  private static class ObjectWithField {
    File field;
  }
}
