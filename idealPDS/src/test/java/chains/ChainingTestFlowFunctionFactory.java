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
package chains;

import boomerang.utils.MethodWrapper;
import java.util.Set;
import typestate.ChainingFlowFunctionFactory;

public class ChainingTestFlowFunctionFactory extends ChainingFlowFunctionFactory {

  public ChainingTestFlowFunctionFactory() {
    super(
        Set.of(
            new MethodWrapper(Chain.class.getName(), "chain1", Chain.class.getName()),
            new MethodWrapper(Chain.class.getName(), "chain2", Chain.class.getName())));
  }
}
