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

import boomerang.scope.opal.tac.OpalMethod
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.ControlFlowGraphTarget
import org.junit.Assert
import org.junit.Test
import org.opalj.br.IntegerType

class OpalControlFlowGraphTest {

  private val integerType = IntegerType.toJVMTypeName

  @Test
  def controlFlowGraphTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[ControlFlowGraphTarget].getName)

    val signature = new MethodSignature(
      classOf[ControlFlowGraphTarget].getName,
      "compute",
      integerType
    )
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod.of(method, opalSetup.project.get)

    val cfg = opalMethod.getControlFlowGraph
    Assert.assertTrue(cfg.getStatements.size() > 0)
    Assert.assertEquals(1, cfg.getStartPoints.size())
    Assert.assertEquals(2, cfg.getEndPoints.size())

    cfg.getEndPoints.forEach(stmt => {
      Assert.assertTrue(stmt.isReturnStmt)
    })
  }

}
