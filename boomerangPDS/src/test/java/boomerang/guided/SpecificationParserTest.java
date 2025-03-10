/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.guided;

import org.junit.Assert;
import org.junit.Test;

public class SpecificationParserTest {

  @Test
  public void specificationParserTest1() {
    Specification specification =
        Specification.create("<GO{F}java.lang.String: void <init>(ON{F}java.lang.String)>");
    Assert.assertEquals(specification.getMethodAndQueries().size(), 1);
  }

  @Test
  public void specificationParserTest2() {
    Specification specification =
        Specification.create("<ON{B}java.lang.String: void <init>(GO{B}java.lang.String)>)");
    Assert.assertEquals(specification.getMethodAndQueries().size(), 1);
  }
}
