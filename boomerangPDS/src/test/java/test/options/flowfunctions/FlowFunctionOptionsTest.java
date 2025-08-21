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
package test.options.flowfunctions;

import boomerang.flowfunction.FlowFunctionOptions;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FlowFunctionOptionsTest {

  @Test
  public void optionSettingsTest() {
    FlowFunctionOptions defaultOptions = FlowFunctionOptions.DEFAULT();
    Assertions.assertTrue(defaultOptions.trackFields());
    Assertions.assertTrue(defaultOptions.includeInnerClassFields());
    Assertions.assertFalse(defaultOptions.throwFlows());
    Assertions.assertFalse(defaultOptions.trackReturnOfInstanceOf());

    FlowFunctionOptions trackFields =
        FlowFunctionOptions.builder().enableTrackFields(false).build();
    Assertions.assertFalse(trackFields.trackFields());

    FlowFunctionOptions includeInnerClassFields =
        FlowFunctionOptions.builder().enableIncludeInnerClassFields(false).build();
    Assertions.assertFalse(includeInnerClassFields.includeInnerClassFields());

    FlowFunctionOptions throwFlows = FlowFunctionOptions.builder().enableThrowFlows(true).build();
    Assertions.assertTrue(throwFlows.throwFlows());

    FlowFunctionOptions trackReturnOfInstanceOf =
        FlowFunctionOptions.builder().enableTrackReturnOfInstanceOf(true).build();
    Assertions.assertTrue(trackReturnOfInstanceOf.trackReturnOfInstanceOf());
  }
}
