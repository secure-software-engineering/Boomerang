package test.setup;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import java.util.List;

public interface TestSetup {

  void initialize(
      String classPath,
      MethodWrapper testMethod,
      List<String> includedPackages,
      List<String> excludedPackages);

  Method getTestMethod();

  FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope);
}
