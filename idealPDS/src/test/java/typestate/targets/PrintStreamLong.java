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
package typestate.targets;

import assertions.Assertions;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import test.TestMethod;

@SuppressWarnings("unused")
public class PrintStreamLong {

  @TestMethod
  public void test1() throws FileNotFoundException {
    PrintStream inputStream = new PrintStream("");
    inputStream.close();
    inputStream.flush();
    Assertions.mustBeInErrorState(inputStream);
  }

  @TestMethod
  public void test() {
    try {
      FileOutputStream out = new FileOutputStream("foo.txt");
      PrintStream p = new PrintStream(out);
      p.close();
      p.println("foo!");
      p.write(42);
      Assertions.mustBeInErrorState(p);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
