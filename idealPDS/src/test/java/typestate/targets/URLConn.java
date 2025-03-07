package typestate.targets;

import assertions.Assertions;
import java.io.IOException;
import java.net.HttpURLConnection;
import test.TestMethod;

@SuppressWarnings("unused")
public class URLConn {

  @TestMethod
  public void test1() throws IOException {
    HttpURLConnection httpURLConnection =
        new HttpURLConnection(null) {

          @Override
          public void connect() {}

          @Override
          public boolean usingProxy() {
            return false;
          }

          @Override
          public void disconnect() {}
        };
    httpURLConnection.connect();
    httpURLConnection.setDoOutput(true);
    Assertions.mustBeInErrorState(httpURLConnection);
    httpURLConnection.setAllowUserInteraction(false);
    Assertions.mustBeInErrorState(httpURLConnection);
  }

  @TestMethod
  public void test2() throws IOException {
    HttpURLConnection httpURLConnection =
        new HttpURLConnection(null) {

          @Override
          public void connect() {}

          @Override
          public boolean usingProxy() {
            return false;
          }

          @Override
          public void disconnect() {}
        };
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setAllowUserInteraction(false);

    httpURLConnection.connect();
    Assertions.mustBeInAcceptingState(httpURLConnection);
  }
}
