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
package boomerang.weights;

import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.Map;
import wpds.impl.Weight;

public interface PathConditionWeight extends Weight {
  Map<Statement, ConditionDomain> getConditions();

  Map<Val, ConditionDomain> getEvaluationMap();

  public enum ConditionDomain {
    TRUE,
    FALSE,
    TOP
  }
}
