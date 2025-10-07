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

@ExtendWith(OptionsTestInterceptor.class)
public class FieldSummaryTest {

  @Test
  @TestOptions(expectedAllocSites = {"summary", "null"})
  public void disabledFieldSummaryTest() {
    String s = "summary";

    Layer1 layer = new Layer1();
    writeToField(layer, s);

    OptionAssertions.queryFor(layer.layer.field);
  }

  @Disabled("TODO Throws NullPointerException")
  @Test
  @TestOptions(
      expectedAllocSites = {"summary", "null"},
      fieldSummaries = true)
  public void enabledFieldSummaryTest() {
    String s = "summary";

    Layer1 layer = new Layer1();
    writeToField(layer, s);

    OptionAssertions.queryFor(layer.layer.field);
  }

  private void writeToField(Layer1 layer, String s) {
    layer.layer.field = s;
  }

  private static class Layer1 {
    private final Layer2 layer = new Layer2();
  }

  private static class Layer2 {
    private String field;
  }
}
