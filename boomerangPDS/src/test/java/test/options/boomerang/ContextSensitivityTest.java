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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;

@Disabled("TODO Does not seem to work correctly")
@ExtendWith(OptionsTestInterceptor.class)
public class ContextSensitivityTest {

  @Test
  @TestOptions(expectedAllocSites = {"first"})
  public void positiveContextSensitivityTest() {
    ClassWithField c1 = new ClassWithField();
    ClassWithField c2 = new ClassWithField();
    setField(c1, "first");
    setField(c2, "second");
    OptionAssertions.queryFor(c1.field);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"first", "second"},
      contextSensitivity = false)
  public void negativeContextSensitivityTest() {
    ClassWithField c1 = new ClassWithField();
    ClassWithField c2 = new ClassWithField();
    setField(c1, "first");
    setField(c2, "second");
    OptionAssertions.queryFor(c1.field);
  }

  private static void setField(ClassWithField c, String s) {
    c.field = s;
  }

  private static class ClassWithField {
    private String field;
  }
}
