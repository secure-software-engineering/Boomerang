/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.sets;

import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore("Does not terminate")
public class HashMapGetLongTest extends AbstractBoomerangTest {

  private final String target = HashMapGetLongTarget.class.getName();

  @Test
  public void addAndRetrieve() {
    analyze(target, testName.getMethodName());
  }

  @Test
  public void addAndLoadFromOther() {
    analyze(target, testName.getMethodName());
  }
}
