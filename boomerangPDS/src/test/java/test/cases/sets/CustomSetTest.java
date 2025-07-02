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

import java.util.Iterator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestConfig;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
@TestConfig(includedClasses = {"java.util.Iterator"})
public class CustomSetTest {

  @Test
  public void mySetIteratorTest() {
    MySetIterator mySet = new MySetIterator();
    AllocatedObject alias = new AllocatedObject() {};
    mySet.add(alias);
    Object query = null;
    while (mySet.hasNext()) {
      query = mySet.next();
    }
    QueryMethods.queryFor(query);
  }

  public static class MySetIterator implements Iterator<Object> {
    Object[] content = new Object[10];
    int curr = 0;

    void add(Object o) {
      content[curr] = o;
    }

    @Override
    public boolean hasNext() {
      return curr < 10;
    }

    @Override
    public Object next() {
      return content[curr++];
    }

    @Override
    public void remove() {}
  }

  @Test
  public void mySetIterableTest() {
    MySet mySet = new MySet();
    AllocatedObject alias = new SetAlloc();
    mySet.add(alias);
    Object query = null;
    for (Object el : mySet) {
      query = el;
    }
    QueryMethods.queryFor(query);
  }

  public static class MySet implements Iterable<Object> {
    Object[] content = new Object[10];
    int curr = 0;
    Iterator<Object> it;

    void add(Object o) {
      content[curr] = o;
    }

    @Override
    public Iterator<Object> iterator() {
      if (it == null) it = new MySet.SetIterator();
      return it;
    }

    private class SetIterator implements Iterator<Object> {

      @Override
      public boolean hasNext() {
        return curr < 10;
      }

      @Override
      public Object next() {
        return content[curr++];
      }

      @Override
      public void remove() {}
    }
  }
}
