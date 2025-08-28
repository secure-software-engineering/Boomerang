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
package test.options.boomerang;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;

@ExtendWith(OptionsTestInterceptor.class)
public class FieldSensitivityTest {

  @Test
  @TestOptions(expectedAllocSites = {"safe"})
  public void positiveFieldSensitivityTest() {
    ClassWithFields c = new ClassWithFields();
    c.field1 = "safe";
    c.field2 = "unsafe";
    OptionAssertions.queryFor(c.field1);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"safe", "unsafe", "null"},
      fieldSensitivity = false)
  public void negativeFieldSensitivityTest() {
    ClassWithFields c = new ClassWithFields();
    c.field1 = "safe";
    c.field2 = "unsafe";
    OptionAssertions.queryFor(c.field2);
  }

  private static class ClassWithFields {
    private String field1;
    private String field2;
  }
}
