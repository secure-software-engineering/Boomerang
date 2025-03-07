/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.cases.sets;

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore("Does not terminate")
public class HashMapsLongTest extends AbstractBoomerangTest {

  private final String target = HashMapsLongTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.HashMap",
        "java.util.HashMap$TreeNode",
        "java.util.HashMap$HashIterator",
        "java.util.HashMap$ValueIterator",
        "java.util.HashMap$Values",
        "java.util.AbstractCollection",
        "java.util.HashMap$Node",
        "java.util.AbstractMap",
        "java.util.Map");
  }

  @Test
  public void addAndRetrieve() {
    analyze(target, testName.getMethodName());
  }
}
