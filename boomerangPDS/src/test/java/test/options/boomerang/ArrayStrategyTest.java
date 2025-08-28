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

import boomerang.solver.Strategies;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.options.OptionAssertions;
import test.options.OptionsTestInterceptor;
import test.options.TestOptions;

@ExtendWith(OptionsTestInterceptor.class)
public class ArrayStrategyTest {

  @Test
  @TestOptions(
      expectedAllocSites = {"there"},
      arrayStrategy = Strategies.ArrayStrategy.INDEX_SENSITIVE)
  public void indexSensitiveTest() {
    String[] s = new String[] {"Hello", "there"};
    OptionAssertions.queryFor(s[1]);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"Hello", "there"},
      arrayStrategy = Strategies.ArrayStrategy.INDEX_INSENSITIVE)
  public void indexInsensitiveTest() {
    String[] s = new String[] {"Hello", "there"};
    OptionAssertions.queryFor(s[0]);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      arrayStrategy = Strategies.ArrayStrategy.DISABLED)
  public void disabledTest() {
    String[] s = new String[] {"Hello", "there"};
    OptionAssertions.queryFor(s[1]);
  }
}
