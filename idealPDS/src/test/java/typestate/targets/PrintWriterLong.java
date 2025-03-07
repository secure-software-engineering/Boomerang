package typestate.targets;

import assertions.Assertions;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import test.TestMethod;

@SuppressWarnings("unused")
public class PrintWriterLong {

  @TestMethod
  public void test1() throws FileNotFoundException {
    PrintWriter inputStream = new PrintWriter("");
    inputStream.close();
    inputStream.flush();
    Assertions.mustBeInErrorState(inputStream);
  }
}
