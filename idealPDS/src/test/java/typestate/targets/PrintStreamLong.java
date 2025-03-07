package typestate.targets;

import assertions.Assertions;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import test.TestMethod;

@SuppressWarnings("unused")
public class PrintStreamLong {

  @TestMethod
  public void test1() throws FileNotFoundException {
    PrintStream inputStream = new PrintStream("");
    inputStream.close();
    inputStream.flush();
    Assertions.mustBeInErrorState(inputStream);
  }

  @TestMethod
  public void test() {
    try {
      FileOutputStream out = new FileOutputStream("foo.txt");
      PrintStream p = new PrintStream(out);
      p.close();
      p.println("foo!");
      p.write(42);
      Assertions.mustBeInErrorState(p);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
