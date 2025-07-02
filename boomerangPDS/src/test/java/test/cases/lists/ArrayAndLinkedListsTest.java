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
package test.cases.lists;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ArrayAndLinkedListsTest {

  @Test
  public void addAndRetrieve() {
    List<Object> list1 = new LinkedList<>();
    Object o = new ListAlloc();
    add(list1, o);
    Object o2 = new Object();
    List<Object> list2 = new ArrayList<>();
    add(list2, o2);
    QueryMethods.queryFor(o);
  }

  private void add(List<Object> list1, Object o) {
    list1.add(o);
  }
}
