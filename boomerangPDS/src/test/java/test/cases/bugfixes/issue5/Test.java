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
package test.cases.bugfixes.issue5;

import java.util.LinkedList;
import java.util.List;

public class Test {

  public static List<Foo> foos() {
    Foo foo = new Foo();
    foo.baz();
    System.out.println(foo);
    List<Foo> x = new LinkedList<>();
    x.add(foo);
    foo.bar();
    return x;
  }

  public static void main(String[] args) {
    System.out.println(foos());
  }
}
