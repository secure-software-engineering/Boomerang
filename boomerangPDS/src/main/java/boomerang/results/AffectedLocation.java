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
package boomerang.results;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Val;
import java.util.List;

public interface AffectedLocation {

  ControlFlowGraph.Edge getStatement();

  Val getVariable();

  List<PathElement> getDataFlowPath();

  String getMessage();

  int getRuleIndex();
}
