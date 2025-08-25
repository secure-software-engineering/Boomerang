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
public class MaxFieldDepthTest {

  private static class FirstLayer {
    private final SecondLayer secondLayer = new SecondLayer();
  }

  private static class SecondLayer {
    private String field;
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"field", "null"},
      maxFieldDepth = 3)
  public void positiveMaxFieldDepthTest() {
    // TODO null should not be an allocation site
    FirstLayer firstLayer = new FirstLayer();
    firstLayer.secondLayer.field = "field";
    OptionAssertions.queryFor(firstLayer.secondLayer.field);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      maxFieldDepth = 2)
  public void negativeMaxFieldDepth() {
    FirstLayer firstLayer = new FirstLayer();
    firstLayer.secondLayer.field = "field";
    OptionAssertions.queryFor(firstLayer.secondLayer.field);
  }
}
