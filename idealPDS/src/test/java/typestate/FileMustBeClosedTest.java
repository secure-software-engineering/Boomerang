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
package typestate;

import assertions.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import test.TestParameters;
import typestate.helper.File;
import typestate.helper.ObjectWithField;
import typestate.impl.statemachines.FileMustBeClosedStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = FileMustBeClosedStateMachine.class)
public class FileMustBeClosedTest {

  private boolean staticallyUnknown() {
    return Math.random() > 0.5;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simple() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void simple2() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simple0() {
    File file = new File();
    file.open();
    escape(file);
    Assertions.mustBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simple0a() {
    File file = new File();
    file.open();
    File alias = file;
    escape(alias);
    Assertions.mustBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simpleStrongUpdate() {
    File file = new File();
    File alias = file;
    file.open();
    // mustBeInErrorState(file);
    Assertions.mustBeInErrorState(alias);
    alias.close();
    // mustBeInAcceptingState(alias);
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void simpleStrongUpdate1() {
    File file = new File();
    File alias = file;
    file.open();
    Assertions.mustBeInErrorState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simpleStrongUpdate1a() {
    File file = new File();
    File alias = file;
    file.open();
    Assertions.mustBeInErrorState(file);
    Assertions.mustBeInErrorState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simpleStrongUpdate2() {
    File x = new File();
    File y = x;
    x.open();
    x.close();
    Assertions.mustBeInAcceptingState(x);
    Assertions.mustBeInAcceptingState(y);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void recursion() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    recursive(file);
    Assertions.mustBeInAcceptingState(file);
  }

  public void recursive(File file) {
    file.close();
    if (!staticallyUnknown()) {
      File alias = file;
      recursive(alias);
    }
  }

  public void escape(File other) {
    Assertions.mustBeInErrorState(other);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void simple1() {
    File file = new File();
    File alias = file;
    alias.open();
    Assertions.mustBeInErrorState(file);
    Assertions.mustBeInErrorState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 2, expectedAssertionCount = 3)
  public void simpleNoStrongUpdate() {
    File file = new File();
    File alias;
    if (staticallyUnknown()) {
      alias = file;
      alias.open();
      Assertions.mustBeInErrorState(file);
    } else {
      alias = new File();
    }
    alias.open();
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInErrorState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void branching() {
    File file = new File();
    if (staticallyUnknown()) file.open();
    Assertions.mayBeInErrorState(file);
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test222() {
    File file = new File();
    if (staticallyUnknown()) {
      file.open();
    }
    file.close();
    Assertions.mayBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void branchingMay() {
    File file = new File();
    if (staticallyUnknown()) file.open();
    else file.close();
    System.out.println(2);
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void continued() {
    File file = new File();
    file.open();
    file.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(file);
    System.out.println(2);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void aliasing() {
    File file = new File();
    File alias = file;
    if (staticallyUnknown()) file.open();
    Assertions.mayBeInErrorState(file);
    alias.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 2, expectedAssertionCount = 3)
  public void summaryTest() {
    File file1 = new File();
    call(file1);
    int y = 1;
    file1.close();
    Assertions.mustBeInAcceptingState(file1);
    File file = new File();
    File alias = file;
    call(alias);
    file.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void simpleAlias() {
    File y = new File();
    File x = y;
    x.open();
    int z = 1;
    Assertions.mustBeInErrorState(x);
    y.close();
    Assertions.mustBeInAcceptingState(x);
    Assertions.mustBeInAcceptingState(y);
  }

  public static void call(File alias) {
    alias.open();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void wrappedOpenCall() {
    File file1 = new File();
    call3(file1, file1);
    Assertions.mustBeInErrorState(file1);
  }

  public static void call3(File other, File alias) {
    alias.open();
    Assertions.mustBeInErrorState(alias);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void interprocedural() {
    File file = new File();
    file.open();
    flows(file, true);
    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  public static void flows(File other, boolean b) {
    if (b) other.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void interprocedural2() {
    File file = new File();
    file.open();
    flows2(file, true);
    Assertions.mustBeInAcceptingState(file);
  }

  public static void flows2(File other, boolean b) {
    other.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void intraprocedural() {
    File file = new File();
    file.open();
    if (staticallyUnknown()) file.close();

    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void flowViaField() {
    ObjectWithField container = new ObjectWithField();
    flows(container);
    if (staticallyUnknown()) container.field.close();

    Assertions.mayBeInErrorState(container.field);
  }

  public static void flows(ObjectWithField container) {
    container.field = new File();
    File field = container.field;
    field.open();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void flowViaFieldDirect() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    File field = container.field;
    field.open();
    File f2 = container.field;
    Assertions.mustBeInErrorState(f2);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void flowViaFieldDirect2() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    File field = container.field;
    field.open();
    Assertions.mustBeInErrorState(container.field);
    File field2 = container.field;
    field2.close();
    Assertions.mustBeInAcceptingState(container.field);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void flowViaFieldNotUnbalanced() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    open(container);
    if (staticallyUnknown()) {
      container.field.close();
      Assertions.mustBeInAcceptingState(container.field);
    }
    Assertions.mayBeInErrorState(container.field);
    Assertions.mayBeInAcceptingState(container.field);
  }

  public void open(ObjectWithField container) {
    File field = container.field;
    field.open();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void indirectFlow() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    flows(a, b);
    Assertions.mayBeInAcceptingState(a.field);
    Assertions.mayBeInAcceptingState(b.field);
  }

  public void flows(ObjectWithField aInner, ObjectWithField bInner) {
    File file = new File();
    file.open();
    aInner.field = file;
    File alias = bInner.field;
    Assertions.mustBeInErrorState(alias);
    alias.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void parameterAlias() {
    File file = new File();
    File alias = file;
    call(alias, file);
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(alias);
  }

  public void call(File file1, File file2) {
    file1.open();
    file2.close();
    Assertions.mustBeInAcceptingState(file1);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void parameterAlias2() {
    File file = new File();
    File alias = file;
    call2(alias, file);
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInErrorState(alias);
  }

  public void call2(File file1, File file2) {
    file1.open();
    if (staticallyUnknown()) file2.close();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void aliasInInnerScope() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    File file = new File();
    file.open();
    bar(a, b, file);
    b.field.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(a.field);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void noStrongUpdate() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = new ObjectWithField();
    File file = new File();
    if (staticallyUnknown()) {
      b.field = file;
    } else {
      a.field = file;
    }
    a.field.open();
    b.field.close();
    // Debatable
    Assertions.mayBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void unbalancedReturn1() {
    File second = createOpenedFile();
    Assertions.mustBeInErrorState(second);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void unbalancedReturn2() {
    File first = createOpenedFile();
    int x = 1;
    close(first);
    Assertions.mustBeInAcceptingState(first);
    File second = createOpenedFile();
    second.hashCode();
    Assertions.mustBeInErrorState(second);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void unbalancedReturnAndBalanced() {
    File first = createOpenedFile();
    int x = 1;
    close(first);
    Assertions.mustBeInAcceptingState(first);
  }

  public static void close(File first) {
    first.close();
  }

  public static File createOpenedFile() {
    File f = new File();
    f.open();
    Assertions.mustBeInErrorState(f);
    return f;
  }

  public void bar(ObjectWithField a, ObjectWithField b, File file) {
    a.field = file;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void lateWriteToField() {
    ObjectWithField a = new ObjectWithField();
    ObjectWithField b = a;
    File file = new File();
    bar(a, file);
    File c = b.field;
    c.close();
    Assertions.mustBeInAcceptingState(file);
  }

  public void bar(ObjectWithField a, File file) {
    file.open();
    a.field = file;
    File whoAmI = a.field;
    Assertions.mustBeInErrorState(whoAmI);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void fieldStoreAndLoad1() {
    ObjectWithField container = new ObjectWithField();
    File file = new File();
    container.field = file;
    File a = container.field;
    a.open();
    Assertions.mustBeInErrorState(a);
    Assertions.mustBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 3)
  public void fieldStoreAndLoad2() {
    ObjectWithField container = new ObjectWithField();
    container.field = new File();
    ObjectWithField otherContainer = new ObjectWithField();
    File a = container.field;
    otherContainer.field = a;
    flowsToField(container);
    // mustBeInErrorState( container.field);
    Assertions.mustBeInErrorState(a);
  }

  public void flowsToField(ObjectWithField parameterContainer) {
    File field = parameterContainer.field;
    field.open();
    Assertions.mustBeInErrorState(field);
    File aliasedVar = parameterContainer.field;
    Assertions.mustBeInErrorState(aliasedVar);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 4)
  public void wrappedClose() {
    File file = new File();
    File alias = file;
    alias.open();
    Assertions.mustBeInErrorState(alias);
    Assertions.mustBeInErrorState(file);
    file.wrappedClose();
    Assertions.mustBeInAcceptingState(alias);
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void wrappedClose2() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    wrappedParamClose(file);
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void wrappedOpen2() {
    File file = new File();
    wrappedParamOpen(file);
    Assertions.mustBeInErrorState(file);
  }

  public void wrappedParamOpen(File a) {
    openCall(a);
  }

  public void openCall(File f) {
    f.open();
    int x = 1;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void wrappedClose1() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    cls(file);
    Assertions.mustBeInAcceptingState(file);
  }

  public void wrappedParamClose(File o1) {
    cls(o1);
  }

  public static void cls(File o2) {
    o2.close();
    int x = 1;
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void wrappedOpen() {
    File file = new File();
    change(file);
    Assertions.mustBeInErrorState(file);
  }

  public void change(File other) {
    other.open();
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 4)
  public void multipleStates() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    Assertions.mustBeInErrorState(file);
    file.close();
    Assertions.mustBeInAcceptingState(file);
    Assertions.mustBeInAcceptingState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void doubleBranching() {
    File file = new File();
    if (staticallyUnknown()) {
      file.open();
      if (staticallyUnknown()) file.close();
    } else if (staticallyUnknown()) file.close();
    else {
      System.out.println(2);
    }
    Assertions.mayBeInErrorState(file);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void whileLoopBranching() {
    File file = new File();
    while (staticallyUnknown()) {
      if (staticallyUnknown()) {
        file.open();
        if (staticallyUnknown()) file.close();
      } else if (staticallyUnknown()) file.close();
      else {
        System.out.println(2);
      }
    }
    Assertions.mayBeInErrorState(file);
  }

  static File v;

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void staticFlow() {
    File a = new File();
    v = a;
    v.open();
    foo();
    Assertions.mustBeInErrorState(v);
    v.close();
    Assertions.mustBeInAcceptingState(v);
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void staticFlowSimple() {
    File a = new File();
    v = a;
    v.open();
    Assertions.mustBeInErrorState(v);
  }

  public static void foo() {}

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void storedInObject() {
    InnerObject o = new InnerObject();
    File file = o.file;
    Assertions.mustBeInErrorState(file);
  }

  public static class InnerObject {
    public File file;

    public InnerObject() {
      this.file = new File();
      this.file.open();
    }

    public InnerObject(String string) {
      this.file = new File();
    }

    public void doClose() {
      Assertions.mustBeInErrorState(file);
      this.file.close();
      Assertions.mustBeInAcceptingState(file);
    }

    public void doOpen() {
      this.file.open();
      Assertions.mustBeInErrorState(file);
    }
  }

  @Test
  @TestParameters(expectedSeedCount = 1, expectedAssertionCount = 4)
  public void storedInObject2() {
    InnerObject o = new InnerObject("");
    o.doOpen();
    o.doClose();
    Assertions.mustBeInAcceptingState(o.file);
  }
}
