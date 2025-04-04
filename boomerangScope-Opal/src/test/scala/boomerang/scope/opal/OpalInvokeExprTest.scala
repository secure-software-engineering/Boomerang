package boomerang.scope.opal

import boomerang.scope.DataFlowScope
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.{ConstructorTarget, InvokeExprTarget, SingleTarget}
import com.typesafe.config.{Config, ConfigValueFactory}
import org.junit.Test
import org.opalj.br.analyses.Project
import org.opalj.br.analyses.cg.InitialEntryPointsKey
import org.opalj.tac.cg.{CHACallGraphKey, CallGraph}

import java.util
import java.util.{HashMap, List, Map, Set}
import scala.jdk.javaapi.CollectionConverters

class OpalInvokeExprTest {

  @Test
  def instanceInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[SingleTarget].getName)

    val signature = new MethodSignature(classOf[SingleTarget].getName, "tryCatch", "V")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)
    opalMethod.getControlFlowGraph

    // Update the project's config to set the test method as the (single) entry point. See
    // https://github.com/opalj/opal/blob/ff01c1c9e696946a88b090a52881a41445cf07f1/DEVELOPING_OPAL/tools/src/main/scala/org/opalj/support/info/CallGraph.scala#L406
    var config = OpalClient.project.get.config

    val key = InitialEntryPointsKey.ConfigKeyPrefix + "entryPoints"
    val currentValues = config.getList(key).unwrapped

    val configValue = new util.HashMap[String, String]
    configValue.put("declaringClass", method.classFile.thisType.toJava.replace(".", "/"))
    configValue.put("name", method.name)

    currentValues.add(ConfigValueFactory.fromMap(configValue))
    config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues))
    config = config.withValue(InitialEntryPointsKey.ConfigKeyPrefix + "analysis", ConfigValueFactory.fromAnyRef("org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder"))
    val project = Project.recreate(OpalClient.project.get, config, useOldConfigAsFallback = true)

    val callGraph: CallGraph = project.get(CHACallGraphKey)
    val scope = new OpalFrameworkScope(project, callGraph, CollectionConverters.asScala(util.Set.of(method)).toSet, DataFlowScope.EXCLUDE_PHANTOM_CLASSES)

    println(opalMethod.tac.statements.mkString("Array(\n\t", "\n\t", "\n)"))
  }

}
