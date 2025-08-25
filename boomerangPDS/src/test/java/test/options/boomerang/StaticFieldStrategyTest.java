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
public class StaticFieldStrategyTest {

  private static String directSingleton = "direct";
  private static String indirectSingleton;
  private static String flowSensitiveStaticField = "flowSensitive";

  public static String getInstance() {
    if (indirectSingleton == null) {
      indirectSingleton = "indirect";
    }

    return indirectSingleton;
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"direct"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON)
  public void directSingletonTest() {
    OptionAssertions.queryFor(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"indirect"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON)
  public void indirectSingletonTest() {
    String s = getInstance();
    OptionAssertions.queryFor(s);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"direct", "insideDirect"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON)
  public void redefinedSingletonTest() {
    directSingleton = "insideDirect";
    OptionAssertions.queryFor(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"insideFlowSensitive"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
  public void positiveFlowSensitiveWithoutEntryPointsTest() {
    flowSensitiveStaticField = "insideFlowSensitive";
    OptionAssertions.queryFor(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
  public void negativeFlowSensitiveWithoutEntryPointsTest() {
    OptionAssertions.queryFor(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE,
      trackStaticFieldAtEntryPointToClinit = true)
  public void flowSensitiveWithEntryPoints() {
    OptionAssertions.queryFor(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.IGNORE)
  public void ignoreStaticFieldsTest1() {
    OptionAssertions.queryFor(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.IGNORE)
  public void ignoreStaticFieldsTest2() {
    flowSensitiveStaticField = "indirect";
    OptionAssertions.queryFor(flowSensitiveStaticField);
  }
}
