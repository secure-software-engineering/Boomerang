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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ContextSensitivityMyListTarget {

  public void wrongContext() {
    List<Object> type = new MyList();
    method(type);
  }

  public Object method(List<Object> type) {
    CallGraphAlloc alloc = new CallGraphAlloc();
    type.add(alloc);
    return alloc;
  }

  @TestMethod
  public void testOnlyCorrectContextInCallGraph() {
    wrongContext();
    MyCorrectList type = new MyCorrectList();
    Object alloc = method(type);
    QueryMethods.queryFor(alloc);
  }

  static class MyList implements List<Object> {

    @Override
    public int size() {
      return 0;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean contains(Object o) {
      return false;
    }

    @Override
    public Iterator<Object> iterator() {
      return null;
    }

    @Override
    public Object[] toArray() {
      return null;
    }

    @Override
    public Object[] toArray(Object[] a) {
      return null;
    }

    @Override
    public boolean add(Object e) {
      return false;
    }

    @Override
    public boolean remove(Object o) {
      return false;
    }

    @Override
    public boolean containsAll(Collection c) {
      return false;
    }

    @Override
    public boolean addAll(Collection c) {
      return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
      return false;
    }

    @Override
    public boolean removeAll(Collection c) {
      return false;
    }

    @Override
    public boolean retainAll(Collection c) {
      return false;
    }

    @Override
    public void clear() {}

    @Override
    public Object get(int index) {
      return null;
    }

    @Override
    public Object set(int index, Object element) {
      return null;
    }

    @Override
    public void add(int index, Object element) {
      unreachable();
    }

    public void unreachable() {}

    @Override
    public Object remove(int index) {
      return null;
    }

    @Override
    public int indexOf(Object o) {
      return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
      return 0;
    }

    @Override
    public ListIterator<Object> listIterator() {
      return null;
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
      return null;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
      return null;
    }
  }

  static class MyCorrectList implements List<Object> {

    @Override
    public int size() {
      return 0;
    }

    @Override
    public boolean isEmpty() {
      return false;
    }

    @Override
    public boolean contains(Object o) {
      return false;
    }

    @Override
    public Iterator<Object> iterator() {
      return null;
    }

    @Override
    public Object[] toArray() {
      return null;
    }

    @Override
    public Object[] toArray(Object[] a) {
      return null;
    }

    @Override
    public boolean add(Object e) {
      return false;
    }

    @Override
    public boolean remove(Object o) {
      return false;
    }

    @Override
    public boolean containsAll(Collection c) {
      return false;
    }

    @Override
    public boolean addAll(Collection c) {
      return false;
    }

    @Override
    public boolean addAll(int index, Collection c) {
      return false;
    }

    @Override
    public boolean removeAll(Collection c) {
      return false;
    }

    @Override
    public boolean retainAll(Collection c) {
      return false;
    }

    @Override
    public void clear() {}

    @Override
    public Object get(int index) {
      return null;
    }

    @Override
    public Object set(int index, Object element) {
      return null;
    }

    @Override
    public void add(int index, Object element) {}

    @Override
    public Object remove(int index) {
      return null;
    }

    @Override
    public int indexOf(Object o) {
      return 0;
    }

    @Override
    public int lastIndexOf(Object o) {
      return 0;
    }

    @Override
    public ListIterator<Object> listIterator() {
      return null;
    }

    @Override
    public ListIterator<Object> listIterator(int index) {
      return null;
    }

    @Override
    public List<Object> subList(int fromIndex, int toIndex) {
      return null;
    }
  }
}
