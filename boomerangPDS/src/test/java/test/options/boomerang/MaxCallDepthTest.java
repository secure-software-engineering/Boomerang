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
public class MaxCallDepthTest {

  @Test
  @TestOptions(
      expectedAllocSites = {"alloc"},
      maxCallDepth = 3)
  public void positiveMaxCallDepthTest() {
    String s = call1();
    OptionAssertions.queryFor(s);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      maxCallDepth = 2)
  public void negativeMaxCallDepthTest() {
    String s = call1();
    OptionAssertions.queryFor(s);
  }

  public String call1() {
    return call2();
  }

  public String call2() {
    return "alloc";
  }
}
