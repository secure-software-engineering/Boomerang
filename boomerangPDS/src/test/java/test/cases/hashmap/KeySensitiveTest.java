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
package test.cases.hashmap;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.TestingFramework;
import test.core.BoomerangTestRunnerInterceptor;
import test.core.QueryMethods;
import test.core.TestParameters;
import test.core.selfrunning.AllocatedObject;
import test.core.selfrunning.NoAllocatedObject;

@ExtendWith(BoomerangTestRunnerInterceptor.class)
public class KeySensitiveTest {

  public static class Allocation implements AllocatedObject {}

  public static class NoAllocation implements NoAllocatedObject {}

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void directAccess() {
    AllocatedObject someValue = new MapAlloc();
    Map<String, AllocatedObject> x = new HashMap<>();
    x.put("key", someValue);
    AllocatedObject t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void directAccess2Keys() {
    AllocatedObject someValue = new MapAlloc();
    Map<String, Object> x = new HashMap<>();
    x.put("key", someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void overwrite() {
    AllocatedObject someValue = new MapAlloc();
    Map<String, Object> x = new HashMap<>();
    // False Positive: Overapproximation. We do not kill during the forward analysis.
    x.put("key", new Allocation());
    x.put("key", someValue);
    Object t = x.get("key");
    QueryMethods.queryFor(t);
  }

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void accessWithAliasedKey() {
    AllocatedObject someValue = new MapAlloc();
    Map<String, Object> x = new HashMap<>();
    String key = "key";
    x.put(key, someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get(key);
    QueryMethods.queryFor(t);
  }

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void accessWithKeyFromReturn() {
    AllocatedObject someValue = new MapAlloc();
    Map<String, Object> x = new HashMap<>();
    x.put(getKey(), someValue);
    x.put("key2", new NoAllocation());
    Object t = x.get(getKey());
    QueryMethods.queryFor(t);
  }

  @Test
  @TestParameters(skipFramework = {TestingFramework.Framework.SOOT_UP})
  public void interprocedural() {
    AllocatedObject someValue = new MapAlloc();
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
