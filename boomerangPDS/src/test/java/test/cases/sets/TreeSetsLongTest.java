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
        // TODO Add all superclasses from TreeSet and TreeMap
        "java.util.TreeMap$EntrySpliterator",
        "java.util.TreeMap$ValueSpliterator",
        "java.util.TreeMap$DescendingKeySpliterator",
        "java.util.TreeMap$KeySpliterator",
        "java.util.TreeMap$TreeMapSpliterator",
        "java.util.TreeMap$Entry",
        "java.util.TreeMap$SubMap",
        "java.util.TreeMap$DescendingSubMap",
        "java.util.TreeMap$AscendingSubMap",
        "java.util.TreeMap$NavigableSubMap",
        "java.util.TreeMap$DescendingKeyIterator",
        "java.util.TreeMap$KeyIterator",
        "java.util.TreeMap$ValueIterator",
        "java.util.TreeMap$EntryIterator",
        "java.util.TreeMap$PrivateEntryIterator",
        "java.util.TreeMap$KeySet",
        "java.util.TreeMap$EntrySet",
        "java.util.TreeMap$Values",
        "java.util.TreeMap$DescendingSubMap$DescendingEntrySetView",
        "java.util.TreeMap$AscendingSubMap$AscendingEntrySetView",
        "java.util.TreeMap$NavigableSubMap$DescendingSubMapKeyIterator",
        "java.util.TreeMap$NavigableSubMap$SubMapKeyIterator",
        "java.util.TreeMap$NavigableSubMap$DescendingSubMapEntryIterator",
        "java.util.TreeMap$NavigableSubMap$SubMapEntryIterator",
        "java.util.TreeMap$NavigableSubMap$SubMapIterator",
        "java.util.TreeMap$NavigableSubMap$EntrySetView");
  }

  @Test
  public void addAndRetrieve() {
    analyze(target, testName.getMethodName());
  }
}
