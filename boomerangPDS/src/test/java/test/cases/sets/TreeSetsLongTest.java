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

import java.util.List;
import org.junit.Ignore;
import org.junit.Test;
import test.core.AbstractBoomerangTest;

@Ignore("Figure out which classes from the JDK have to be included")
public class TreeSetsLongTest extends AbstractBoomerangTest {

  private final String target = TreeSetsLongTarget.class.getName();

  @Override
  protected List<String> getIncludedPackages() {
    return List.of(
        "java.util.Set",
        "java.util.AbstractSet",
        "java.util.NavigableSet",
        "java.util.SortedSet",
        "java.util.HashSet",
        "java.util.TreeSet",
        "java.util.Iterator",
        "java.util.Map",
        "java.util.AbstractMap",
        "java.util.TreeMap",
        "java.util.TreeMap$Entry",
        "java.util.NavigableMap",
        "java.util.SortedMap");
  }

  @Test
  public void addAndRetrieve() {
    analyze(target, testName.getMethodName());
  }
}
