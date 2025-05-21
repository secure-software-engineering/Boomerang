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
package test.cases.statics;

import boomerang.options.BoomerangOptions;
import boomerang.solver.Strategies;
import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore(
    "After discovering the allocation site, the forward flow does not reach the query statement")
public class StaticFieldAtEntryPointToClinitTest extends AbstractBoomerangTest {

  private final String target = StaticFieldAtEntryPointToClinitTarget.class.getName();

  @Override
  protected BoomerangOptions createBoomerangOptions() {
    return BoomerangOptions.builder()
        .withStaticFieldStrategy(Strategies.StaticFieldStrategy.FLOW_SENSITIVE)
        .enableTrackStaticFieldAtEntryPointToClinit(true)
        .build();
  }

  @Test
  public void staticFieldAtEntryPointTest() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void staticFieldAtEntryPointWithLoadTest() {
    analyze(target, testName.getMethodName());
  }
}
