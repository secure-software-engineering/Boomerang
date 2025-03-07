package typestate.targets;

import assertions.Assertions;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import test.TestMethod;

@SuppressWarnings("unused")
public class InputStreamLong {

  @TestMethod
  public void test1() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.close();
    inputStream.read(); // Go into error state
    Assertions.mustBeInErrorState(inputStream);
  }

  @TestMethod
  public void test2() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.close();
    inputStream.close();
    inputStream.read(); // Go into error state
    Assertions.mustBeInErrorState(inputStream);
  }

  @TestMethod
  public void test3() throws IOException {
    InputStream inputStream = new FileInputStream("");
    inputStream.read();
    inputStream.close();
    Assertions.mustBeInAcceptingState(inputStream);
  }
}
