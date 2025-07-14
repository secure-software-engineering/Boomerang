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

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestConfig;

@Disabled("Figure out which classes from the JDK have to be included")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
@TestConfig(
    includedClasses = {
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
      "java.util.TreeMap$NavigableSubMap$EntrySetView"
    })
public class TreeSetsLongTest {

  @Test
  public void addAndRetrieve() {
    Set<Object> set = new TreeSet<>();
    SetAlloc alias = new SetAlloc();
    set.add(alias);
    alias = new SetAlloc();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
    Object ir = alias2;
    Object query2 = ir;
    Set<Object> set2 = new TreeSet<>();
    Object alias1 = new Object();
    set2.add(alias1);
    alias1 = new Object();
    set2.add(alias1);
    alias1 = new Object();
    QueryMethods.queryFor(query2);
    otherMap();
    otherMap2();
    hashMap();
  }

  private void hashMap() {
    HashSet<Object> map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
    map = new HashSet<>();
    map.add(new Object());
  }

  private void otherMap2() {
    Set<Object> set = new TreeSet<>();
    Object alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
  }

  private void otherMap() {
    Set<Object> set = new TreeSet<>();
    Object alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);
    alias = new Object();
    set.add(alias);

    Object alias2 = null;
    for (Object o : set) alias2 = o;
  }
}
