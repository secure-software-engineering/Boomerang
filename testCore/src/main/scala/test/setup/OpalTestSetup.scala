package test.setup
import boomerang.scope.{DataFlowScope, FrameworkScope, Method}

import java.util

class OpalTestSetup extends TestSetup {

  override def initialize(classPath: String, testMethod: MethodWrapper, includedPackages: util.List[String], excludedPackages: util.List[String]): Unit = ???

  override def getTestMethod: Method = ???

  override def createFrameworkScope(dataFlowScope: DataFlowScope): FrameworkScope = ???
}
