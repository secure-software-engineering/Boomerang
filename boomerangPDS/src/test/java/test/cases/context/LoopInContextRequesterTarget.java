package test.cases.context;

import test.TestMethod;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class LoopInContextRequesterTarget {

  @TestMethod
  public void loop() {
    ILoop c;
    c = new Loop1();
    c.loop();
  }

  public interface ILoop {
    void loop();
  }

  public class Loop1 implements ILoop {
    A a = new A();

    @Override
    public void loop() {
      if (Math.random() > 0.5) loop();
      AllocatedObject x = a.d;
      QueryMethods.queryFor(x);
    }
  }

  public class A {
    AllocatedObject d = new AllocatedObject() {};
    A f = new A();
  }
}
