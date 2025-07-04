/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal

import boomerang.scope.DataFlowScope
import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.InvokeExprTarget
import boomerang.scope.test.targets.SingleTarget
import com.typesafe.config.ConfigValueFactory
import java.util
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opalj.br.analyses.Project
import org.opalj.br.analyses.cg.InitialEntryPointsKey
import org.opalj.tac.cg.CallGraph
import org.opalj.tac.cg.CHACallGraphKey
import scala.jdk.javaapi.CollectionConverters

class OpalInvokeExprTest {

  @Test
  def instanceInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[SingleTarget].getName)

    val signature =
      new MethodSignature(classOf[SingleTarget].getName, "getAndSetField", "V")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)
    opalMethod.getControlFlowGraph

    // Update the project's config to set the test method as the (single) entry point. See
    // https://github.com/opalj/opal/blob/ff01c1c9e696946a88b090a52881a41445cf07f1/DEVELOPING_OPAL/tools/src/main/scala/org/opalj/support/info/CallGraph.scala#L406
    var config = opalSetup.project.get.config

    val key = InitialEntryPointsKey.ConfigKeyPrefix + "entryPoints"
    val currentValues = config.getList(key).unwrapped

    val configValue = new util.HashMap[String, String]
    configValue.put(
      "declaringClass",
      method.classFile.thisType.toJava.replace(".", "/")
    )
    configValue.put("name", method.name)

    currentValues.add(ConfigValueFactory.fromMap(configValue))
    config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues))
    config = config.withValue(
      InitialEntryPointsKey.ConfigKeyPrefix + "analysis",
      ConfigValueFactory.fromAnyRef(
        "org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder"
      )
    )
    val project = Project.recreate(
      opalSetup.project.get,
      config,
      useOldConfigAsFallback = true
    )

    val callGraph: CallGraph = project.get(CHACallGraphKey)
    val scope = new OpalFrameworkScope(
      project,
      callGraph,
      CollectionConverters.asScala(util.Set.of(method)).toSet,
      DataFlowScope.EXCLUDE_PHANTOM_CLASSES
    )

    println(opalMethod.tac.statements.mkString("Array(\n\t", "\n\t", "\n)"))
  }

  @Test
  def staticInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[InvokeExprTarget].getName)

    val signature = new MethodSignature(
      classOf[InvokeExprTarget].getName,
      "staticInvokeExpr",
      "V"
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    var checked = false
    opalMethod.getStatements.forEach(stmt => {
      if (stmt.containsInvokeExpr()) {
        val invokeExpr = stmt.getInvokeExpr

        Assertions.assertTrue(invokeExpr.isStaticInvokeExpr)
        Assertions.assertEquals(2, invokeExpr.getArgs.size())

        checked = true
      }
    })

    Assertions.assertTrue(checked)
  }

}
