package typestate.targets;

import assertions.Assertions;
import test.TestMethod;
import typestate.targets.helper.File;

@SuppressWarnings("unused")
public class FileMustBeClosedDemandDriven {

  @TestMethod
  public void notCaughtByCHA() {
    I b = new B();
    callOnInterface(b);
  }

  private void callOnInterface(I i) {
    File file = new File();
    file.open();
    i.flow(file);
    Assertions.mustBeInAcceptingState(file);
  }

  @TestMethod
  public void notCaughtByRTA() {
    I a = new A();
    I b = new B();
    callOnInterface(b);
  }

  private interface I {
    void flow(File f);
  }

  private static class B implements I {
    @Override
    public void flow(File f) {
      f.close();
    }
  }

  private static class A implements I {
    @Override
    public void flow(File f) {
      Assertions.shouldNotBeAnalyzed();
    }
  }
}
