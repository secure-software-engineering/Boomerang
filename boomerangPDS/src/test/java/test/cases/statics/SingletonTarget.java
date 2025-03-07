package test.cases.statics;

import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class SingletonTarget {

  private static Alloc instance;

  @TestMethod
  public void doubleSingleton() {
    Alloc singleton = i();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void doubleSingletonDirect() {
    Alloc singleton = objectGetter.getG();
    Object alias = singleton;
    QueryMethods.queryFor(alias);
  }

  @TestMethod
  public void singletonDirect() {
    Alloc singleton = alloc;
    QueryMethods.queryFor(singleton);
  }

  public static Alloc i() {
    GlobalObjectGetter getter = objectGetter;
    Alloc allocation = getter.getG();
    return allocation;
  }

  public interface GlobalObjectGetter {
    Alloc getG();

    void reset();
  }

  private static Alloc alloc;
  private static final GlobalObjectGetter objectGetter =
      new GlobalObjectGetter() {

        Alloc instance = new Alloc();

        public Alloc getG() {
          return instance;
        }

        public void reset() {
          instance = new Alloc();
        }
      };
}
