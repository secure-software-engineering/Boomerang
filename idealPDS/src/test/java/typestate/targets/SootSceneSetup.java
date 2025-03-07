package typestate.targets;

import assertions.Assertions;
import test.TestMethod;
import typestate.targets.helper.File;

@SuppressWarnings("unused")
public class SootSceneSetup {

  @TestMethod
  public void simple() {
    File file = new File();
    file.open();
    Assertions.mustBeInErrorState(file);
    file.close();
    Assertions.mustBeInAcceptingState(file);
  }

  @TestMethod
  public void aliasSimple() {
    File file = new File();
    File alias = file;
    alias.open();
    Assertions.mustBeInErrorState(file);
    Assertions.mustBeInErrorState(alias);
  }
}
