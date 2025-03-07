package typestate.targets;

import assertions.Assertions;
import test.TestMethod;
import typestate.targets.helper.File;

@SuppressWarnings("unused")
public class AssertionsTesting {

  @TestMethod
  public void positiveMustBeInAcceptingStateTest() {
    File file = new File();
    file.open();
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @TestMethod
  public void negativeMustBeInAcceptingStateTest() {
    File file = new File();
    file.open();
    Assertions.mustBeInAcceptingState(file);
    file.close();
  }

  @TestMethod
  public void positiveMustBeInErrorStateTest() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    file.close();
  }

  @TestMethod
  public void negativeMustBeInErrorStateTest() {
    File file = new File();
    file.open();
    file.close();
    Assertions.mustBeInErrorState(file);
  }

  @TestMethod
  public void positiveMayBeInAcceptingState() {
    File file = new File();
    file.open();
    if (Math.random() > 0.5) {
      file.close();
    }
    Assertions.mayBeInAcceptingState(file);
    Assertions.mayBeInErrorState(file);
  }

  @TestMethod
  public void negativeMayBeInAcceptingState() {
    File file = new File();
    file.open();
    Assertions.mayBeInAcceptingState(file);
    file.close();
  }

  @TestMethod
  public void positiveMayBeInErrorState() {
    File file = new File();
    file.open();
    if (Math.random() > 0.5) {
      file.close();
    }
    Assertions.mayBeInErrorState(file);
    Assertions.mayBeInAcceptingState(file);
  }

  @TestMethod
  public void negativeMayBeInErrorState() {
    File file = new File();
    file.open();
    file.close();
    Assertions.mayBeInErrorState(file);
  }

  @TestMethod
  public void positiveShouldNotBeAnalyzedTest() {
    File file = new File();
    // wrappedOpen(file);
    file.close();
  }

  @TestMethod
  public void negativeShouldNotBeAnalyzedTest() {
    File file = new File();
    // Method should not be analyzed
    wrappedOpen(file);
    file.close();
  }

  public void wrappedOpen(File file) {
    file.open();
    Assertions.shouldNotBeAnalyzed();
  }
}
