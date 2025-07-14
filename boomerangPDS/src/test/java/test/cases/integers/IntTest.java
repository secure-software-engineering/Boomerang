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
package test.cases.integers;

import java.math.BigInteger;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;

@Disabled("Extend IntAndStringAllocationSite")
@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class IntTest {

  @Test
  public void simpleAssign() {
    int allocation = 1;
    QueryMethods.intQueryFor(allocation, "1");
  }

  @Test
  public void simpleAssignBranched() {
    int allocation = 2;
    if (Math.random() > 0.5) {
      allocation = 1;
    }
    QueryMethods.intQueryFor(allocation, "1,2");
  }

  @Test
  public void simpleIntraAssign() {
    int allocation = 1;
    int y = allocation;
    QueryMethods.intQueryFor(y, "1");
  }

  @Test
  public void simpleInterAssign() {
    int allocation = 1;
    int y = foo(allocation);
    QueryMethods.intQueryFor(y, "1");
  }

  @Test
  public void returnDirect() {
    int allocation = getVal();
    QueryMethods.intQueryFor(allocation, "1");
  }

  @Test
  public void returnInDirect() {
    int x = getValIndirect();
    QueryMethods.intQueryFor(x, "1");
  }

  private int getValIndirect() {
    int allocation = 1;
    return allocation;
  }

  private int getVal() {
    return 1;
  }

  private int foo(int x) {
    int y = x;
    return y;
  }

  @Test
  public void wrappedType() {
    Integer integer = new Integer(1);
    int allocation = integer;
    QueryMethods.intQueryFor(allocation, "1");
  }

  @Test
  public void wrappedTypeBigInteger() {
    BigInteger integer = BigInteger.valueOf(1);
    QueryMethods.intQueryFor(integer, "1L");
  }
}
