package test.cases.realworld;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class ScalabilityOfBackwardAnalysisTarget {

  @TestMethod
  public void simpleButDifficult() throws IOException {
    // This test case scales in Whole Program PTS Analysis when we do NOT track subtypes of
    // Exceptions.
    // The backward analysis runs into scalability problem, when we enable unbalanced flows.
    InputStream inputStream = new FileInputStream("");
    inputStream.close();
    inputStream.read();
    QueryMethods.queryFor(inputStream);
  }
}
