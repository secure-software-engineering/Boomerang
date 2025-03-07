package test.cases.hashmap;

import java.util.HashMap;
import java.util.Map;
import test.TestMethod;
import test.cases.fields.Alloc;
import test.core.QueryMethods;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.NoAllocatedObject;

@SuppressWarnings("unused")
public class KeySensitiveTarget {

  public static class Allocation implements AllocatedObject {}

  public static class NoAllocation implements NoAllocatedObject {}

  @TestMethod
  public void directAccess() {
    AllocatedObject someValue = new Alloc();
    Map<String, AllocatedObject> x = new HashMap<>();
    x.put("key", someValue);
    AllocatedObject t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void directAccess2Keys() {
    AllocatedObject someValue = new Alloc();
    Map<String, Object> x = new HashMap<>();
    x.put("key", someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void overwrite() {
    AllocatedObject someValue = new Alloc();
    Map<String, Object> x = new HashMap<>();
    // False Positive: Overapproximation. We do not kill during the forward analysis.
    x.put("key", new Allocation());
    x.put("key", someValue);
    Object t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void accessWithAliasedKey() {
    AllocatedObject someValue = new Alloc();
    Map<String, Object> x = new HashMap<>();
    String key = "key";
    x.put(key, someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get(key);
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void accessWithKeyFromReturn() {
    AllocatedObject someValue = new Alloc();
    Map<String, Object> x = new HashMap<>();
    x.put(getKey(), someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get(getKey());
    QueryMethods.queryFor(t);
  }

  @TestMethod
  public void interprocedural() {
    AllocatedObject someValue = new Alloc();
    Map<String, Object> x = new HashMap<>();
    x.put(getKey(), someValue);
    x.put("key2", new NoAllocation());
    Object t = wrap(x);

    QueryMethods.queryFor(t);
  }

  private Object wrap(Map<String, Object> mp) {
    Object i = mp.get(getKey());
    return i;
  }

  private String getKey() {
    return "KEY";
  }
}
