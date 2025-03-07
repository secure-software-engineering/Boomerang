package test.cases.threading;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;

@SuppressWarnings("unused")
public class InnerClassWithThreadTarget {

  private static Alloc param;

  @TestMethod
  public void runWithThreadStatic() {
    param = new Alloc();
    Runnable r =
        new Runnable() {

          @Override
          public void run() {
            String cmd = System.getProperty("");
            // if(cmd!=null){
            // param = new Allocation();
            // }
            for (int i = 1; i < 3; i++) {
              Object t = param;
              Object a = t;
              QueryMethods.queryFor(a);
            }
          }
        };
    Thread t = new Thread(r);
    t.start();
  }

  @TestMethod
  public void runWithThread() {
    final Alloc u = new Alloc();
    Runnable r =
        new Runnable() {

          @Override
          public void run() {
            // String cmd = System.getProperty("");
            // if(cmd!=null){
            // param = new Allocation();
            // }
            for (int i = 1; i < 3; i++) {
              QueryMethods.queryFor(u);
            }
          }
        };
    Thread t = new Thread(r);
    t.start();
  }

  @TestMethod
  public void threadQuery() {
    for (int i = 1; i < 3; i++) {
      Thread t = new MyThread();
      t.start();
      QueryMethods.queryFor(t);
    }
  }

  private static class MyThread extends Thread implements AllocatedObject {}
}
