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
package test.cases.callgraph;

import boomerang.options.BoomerangOptions;
import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class ContextSpecificListTypeTest extends AbstractBoomerangTest {

  private final String target = ContextSpecificListTypeTarget.class.getName();

  @Ignore
  @Test
  public void testListType() {
    analyze(target, testName.getMethodName());
  }

  @Override
  protected BoomerangOptions createBoomerangOptions() {
    return BoomerangOptions.builder()
        .enableOnTheFlyCallGraph(true)
        .enableAllowMultipleQueries(true)
        .build();
  }
}
