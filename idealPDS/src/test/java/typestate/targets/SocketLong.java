package typestate.targets;

import assertions.Assertions;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import test.TestMethod;

@SuppressWarnings("unused")
public class SocketLong {

  @TestMethod
  public void test1() throws IOException {
    Socket socket = new Socket();
    socket.connect(new SocketAddress() {});
    socket.sendUrgentData(2);
    Assertions.mustBeInAcceptingState(socket);
  }

  @TestMethod
  public void test2() throws IOException {
    Socket socket = new Socket();
    socket.sendUrgentData(2);
    Assertions.mustBeInErrorState(socket);
  }

  @TestMethod
  public void test3() throws IOException {
    Socket socket = new Socket();
    socket.sendUrgentData(2);
    socket.sendUrgentData(2);
    Assertions.mustBeInErrorState(socket);
  }

  @TestMethod
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

  @TestMethod
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
