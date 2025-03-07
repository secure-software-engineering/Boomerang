package typestate.targets;

import assertions.Assertions;
import test.TestMethod;
import typestate.targets.helper.File;

@SuppressWarnings("unused")
public class FluentInterface {

  @TestMethod
  public void fluentOpen() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
  }

  @TestMethod
  public void fluentOpenAndClose() {
    File file = new File();
    file = file.open();
    Assertions.mustBeInErrorState(file);
    file = file.close();
    Assertions.mustBeInAcceptingState(file);
  }
}
