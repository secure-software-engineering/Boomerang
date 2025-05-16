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
package test.cases.sets;

import boomerang.options.BoomerangOptions;
import java.util.List;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

public class TreeMapMultipleInstancesTest extends AbstractBoomerangTest {

  private final String target = TreeMapMultipleInstancesTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.Map", "java.util.AbstractMap", "java.util.TreeMap", "java.util.TreeMap$Entry");
  }

  @Test
  public void addAndRetrieve() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void contextSensitive() {
    analyze(target, testName.getMethodName());
  }

  @Override
  protected BoomerangOptions createBoomerangOptions() {
    return BoomerangOptions.builder()
        .enableHandleMaps(false)
        .withAnalysisTimeout(analysisTimeout)
        .build();
  }
}
