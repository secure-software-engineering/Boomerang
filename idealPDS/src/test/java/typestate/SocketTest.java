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
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.ExpectedTestParameters;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import typestate.impl.statemachines.SocketStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = SocketStateMachine.class)
public class SocketTest {

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test1() throws IOException {
    Socket socket = new Socket();
    socket.connect(new SocketAddress() {});
    socket.sendUrgentData(2);
    Assertions.mustBeInAcceptingState(socket);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test2() throws IOException {
    Socket socket = new Socket();
    socket.sendUrgentData(2);
    Assertions.mustBeInErrorState(socket);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test3() throws IOException {
    Socket socket = new Socket();
    socket.sendUrgentData(2);
    socket.sendUrgentData(2);
    Assertions.mustBeInErrorState(socket);
  }

  @Disabled("Reading sockets from an iterator is too complex")
  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test4() throws IOException {
    Collection<Socket> sockets = createSockets();
    for (Iterator<Socket> it = sockets.iterator(); it.hasNext(); ) {
      Socket s = it.next();
      s.connect(null);
      talk(s);
      Assertions.mustBeInAcceptingState(s);
    }

    Collection<Socket> s1 = createOther();
  }

  private Collection<Socket> createOther() {
    Collection<Socket> result = new ArrayList<>();
    for (int i = 0; i < 5; i++) {
      result.add(new Socket());
    }
    return result;
  }

  @Disabled("Reading sockets from an iterator is too complex")
  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test5() {
    Collection<Socket> sockets = createSockets();
    for (Iterator<Socket> it = sockets.iterator(); it.hasNext(); ) {
      Socket s = it.next();
      talk(s);
      Assertions.mayBeInErrorState(s);
    }
  }

  public static Socket createSocket() {
    return new Socket();
  }

  public static Collection<Socket> createSockets() {
    Collection<Socket> result = new LinkedList<>();
    for (int i = 0; i < 5; i++) {
      result.add(new Socket());
    }
    return result;
  }

  public static void talk(Socket s) {
    s.getChannel();
  }
}
