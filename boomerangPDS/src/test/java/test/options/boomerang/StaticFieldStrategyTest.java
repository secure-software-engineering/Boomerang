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
    OptionAssertions.queryForString(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"indirect"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON)
  public void indirectSingletonTest() {
    String s = getInstance();
    OptionAssertions.queryForString(s);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"direct", "insideDirect"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON)
  public void redefinedSingletonTest() {
    directSingleton = "insideDirect";
    OptionAssertions.queryForString(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {"insideFlowSensitive"},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
  public void positiveFlowSensitiveWithoutEntryPointsTest() {
    flowSensitiveStaticField = "insideFlowSensitive";
    OptionAssertions.queryForString(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
  public void negativeFlowSensitiveWithoutEntryPointsTest() {
    OptionAssertions.queryForString(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.FLOW_SENSITIVE,
      trackStaticFieldAtEntryPointToClinit = true)
  public void flowSensitiveWithEntryPoints() {
    OptionAssertions.queryForString(flowSensitiveStaticField);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.IGNORE)
  public void ignoreStaticFieldsTest1() {
    OptionAssertions.queryForString(directSingleton);
  }

  @Test
  @TestOptions(
      expectedAllocSites = {},
      staticFieldStrategy = Strategies.StaticFieldStrategy.IGNORE)
  public void ignoreStaticFieldsTest2() {
    flowSensitiveStaticField = "indirect";
    OptionAssertions.queryForString(flowSensitiveStaticField);
  }
}
