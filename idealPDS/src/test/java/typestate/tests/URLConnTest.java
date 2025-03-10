/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package typestate.tests;

import java.io.IOException;
import java.util.List;
import org.junit.Test;
import test.IDEALTestingFramework;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;
import typestate.impl.statemachines.URLConnStateMachine;
import typestate.targets.URLConn;

public class URLConnTest extends IDEALTestingFramework {

  private final String target = URLConn.class.getName();

  @Override
  protected TypeStateMachineWeightFunctions getStateMachine() {
    return new URLConnStateMachine();
  }

  @Override
  protected List<String> getIncludedPackages() {
    return List.of("java.net.URLConnection", "java.net.HttpURLConnection");
  }

  @Test
  public void test1() throws IOException {
    analyze(target, testName.getMethodName(), 2, 1);
  }

  @Test
  public void test2() throws IOException {
    analyze(target, testName.getMethodName(), 1, 1);
  }
}
