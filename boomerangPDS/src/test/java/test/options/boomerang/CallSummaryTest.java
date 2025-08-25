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
public class CallSummaryTest {

  @Test
  @TestOptions(expectedAllocSites = {"summary"})
  public void disabledCallSummaryTest() {
    String s = "summary";
    String s1 = identity(s);
    String s2 = identity(s1);
    OptionAssertions.queryFor(s2);
  }

  @Disabled("TODO Throws a NullPointerException")
  @Test
  @TestOptions(
      expectedAllocSites = {"summary"},
      callSummaries = true)
  public void enabledCallSummaryTest() {
    String s = "summary";
    String s1 = identity(s);
    String s2 = identity(s1);
    OptionAssertions.queryFor(s2);
  }

  public String identity(String s) {
    return s;
  }
}
