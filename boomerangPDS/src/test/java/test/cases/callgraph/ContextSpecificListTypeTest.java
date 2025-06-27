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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class ContextSpecificListTypeTest {

  @Test
  public void testListType() {
    wrongContext();
    List<Object> list = new ArrayList<>();
    Object query = method(list);
    QueryMethods.queryFor(query);
  }

  public void wrongContext() {
    List<Object> list = new WrongList();
    method(list);
  }

  public Object method(List<Object> list) {
    CallGraphAlloc alloc = new CallGraphAlloc();
    list.add(alloc);
    return alloc;
  }

  public static class WrongList extends LinkedList<Object> {
    @Override
    public boolean add(Object e) {
      unreachable();
      return false;
    }

    public void unreachable() {}
  }
}
