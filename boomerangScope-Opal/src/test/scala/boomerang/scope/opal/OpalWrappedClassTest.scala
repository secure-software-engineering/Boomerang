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

import boomerang.scope.opal.tac.OpalWrappedClass
import boomerang.scope.test.targets.AbstractClass
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.opalj.br.ClassType

class OpalWrappedClassTest {

  @Test
  def testGetMethodsAbstractClass(): Unit = {
    val opalSetup = new OpalSetup
    opalSetup.setupOpal(classOf[AbstractClass].getName)
    val wrappedClass = new OpalWrappedClass(opalSetup.targetClass.get.thisType, opalSetup.project.get)
    Assertions.assertEquals(2, wrappedClass.getMethods.size)
  }

  @Test
  def testGetMethodsForClassTypeThatIsNotPartOfTheProject(): Unit = {
    val opalSetup = new OpalSetup
    opalSetup.setupOpal("Some Dummy Value")
    val wrappedClass = new OpalWrappedClass(ClassType.Object, opalSetup.project.get)
    Assertions.assertEquals(0, wrappedClass.getMethods.size)
  }
}
