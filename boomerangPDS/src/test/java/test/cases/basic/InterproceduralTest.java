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
package test.cases.basic;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class InterproceduralTest {

  @Test
  public void identityTest() {
    AllocatedObject alias1 = new AllocatedObject() {};
    AllocatedObject alias2 = identity(alias1);
    QueryMethods.queryFor(alias2);
  }

  @Test
  public void simpleAnonymous() {
    AllocatedObject alias1 = new AllocatedObject() {};
    QueryMethods.queryFor(alias1);
  }

  @Test
  public void simpleNonAnonymous() {
    AllocatedObject alias1 = new BasicAlloc();
    QueryMethods.queryFor(alias1);
  }

  @Test
  public void identityTest1() {
    BasicAlloc alias1 = new BasicAlloc();
    Object alias2 = alias1;
    identity(alias1);
    otherCall(alias2);
    QueryMethods.queryFor(alias1);
  }

  private void otherCall(Object alias2) {}

  @Test
  public void summaryReuseTest1() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias4);
  }

  @Test
  public void failedCast() {
    Object o = new BasicAlloc();
    Object returned = flow(o);
    String t = (String) returned;
    QueryMethods.queryFor(t);
  }

  private Object flow(Object o) {
    return o;
  }

  @Test
  public void summaryReuseTest4() {
    BasicAlloc alias2;
    if (Math.random() > 0.5) {
      BasicAlloc alias1 = new BasicAlloc();
      alias2 = nestedIdentity(alias1);
    } else {
      BasicAlloc alias1 = new BasicAlloc();
      alias2 = nestedIdentity(alias1);
    }
    QueryMethods.queryFor(alias2);
  }

  @Test
  public void branchWithCall() {
    BasicAlloc a1 = new BasicAlloc();
    BasicAlloc a2 = new BasicAlloc();
    Object a = null;
    if (Math.random() > 0.5) {
      a = a1;
    } else {
      a = a2;
    }
    wrappedFoo(a);
    QueryMethods.queryFor(a);
  }

  private void wrappedFoo(Object param) {}

  private BasicAlloc nestedIdentity(BasicAlloc param2) {
    int shouldNotSeeThis = 1;
    BasicAlloc returnVal = param2;
    return returnVal;
  }

  @Test
  public void summaryReuseTest2() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias3);
  }

  @Test
  public void summaryReuseTest3() {
    AllocatedObject alias1 = new AllocatedObject() {}, alias2, alias3, alias4;
    alias2 = identity(alias1);
    alias3 = identity(alias2);
    alias4 = alias1;
    QueryMethods.queryFor(alias2);
  }

  @Test
  public void interLoop() {
    AllocatedObject alias = new BasicAlloc() {};
    AllocatedObject aliased2;
    Object aliased = new AllocatedObject() {}, notAlias = new Object();
    for (int i = 0; i < 20; i++) {
      aliased = identity(alias);
    }
    aliased2 = (AllocatedObject) aliased;
    QueryMethods.queryFor(aliased);
  }

  @Test
  public void wrappedAllocationSite() {
    AllocatedObject alias1 = wrappedCreate();
    QueryMethods.queryFor(alias1);
  }

  @Test
  public void branchedObjectCreation() {
    Object alias1;
    if (Math.random() > 0.5) alias1 = create();
    else {
      AllocatedObject intermediate = create();
      alias1 = intermediate;
    }
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  @Test
  public void unbalancedCreation() {
    Object alias1 = create();
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  @Test
  public void unbalancedCreationStatic() {
    Object alias1 = createStatic();
    Object query = alias1;
    QueryMethods.queryFor(query);
  }

  private Object createStatic() {
    return new BasicAlloc();
  }

  public AllocatedObject wrappedCreate() {
    return create();
  }

  public AllocatedObject create() {
    AllocatedObject alloc1 = new AllocatedObject() {};
    return alloc1;
  }

  private AllocatedObject identity(AllocatedObject param) {
    AllocatedObject mapped = param;
    return mapped;
  }

  @Test
  public void heavySummary() {
    BasicAlloc alias1 = new BasicAlloc();
    Object q;
    if (Math.random() > 0.5) {
      q = doSummarize(alias1);
    } else if (Math.random() > 0.5) {
      BasicAlloc alias2 = new BasicAlloc();
      q = doSummarize(alias2);
    } else {
      BasicAlloc alias3 = new BasicAlloc();
      q = doSummarize(alias3);
    }

    QueryMethods.queryFor(q);
  }

  private BasicAlloc doSummarize(BasicAlloc alias1) {
    BasicAlloc a = alias1;
    BasicAlloc b = a;
    BasicAlloc c = b;
    BasicAlloc d = c;

    BasicAlloc e = d;
    BasicAlloc f = evenFurtherNested(e);
    BasicAlloc g = alias1;
    if (Math.random() > 0.5) {
      g = f;
    }
    BasicAlloc h = g;
    return f;
  }

  private BasicAlloc evenFurtherNested(BasicAlloc e) {
    return e;
  }

  @Test
  public void summaryTest() {
    BasicAlloc alias1 = new BasicAlloc();
    Object q;
    if (Math.random() > 0.5) {
      q = summary(alias1);
    } else {
      BasicAlloc alias2 = new BasicAlloc();
      q = summary(alias2);
    }

    QueryMethods.queryFor(q);
  }

  private Object summary(BasicAlloc inner) {
    BasicAlloc ret = inner;
    return ret;
  }

  @Test
  public void doubleNestedSummary() {
    BasicAlloc alias1 = new BasicAlloc();
    Object q;
    if (Math.random() > 0.5) {
      q = nestedSummary(alias1);
    } else {
      BasicAlloc alias2 = new BasicAlloc();
      q = nestedSummary(alias2);
    }

    QueryMethods.queryFor(q);
  }

  private Object nestedSummary(BasicAlloc inner) {
    Object ret = summary(inner);
    return ret;
  }
}
