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
package boomerang.scope.opal.transformation

import org.opalj.tac.Assignment
import org.opalj.tac.Stmt

class BoomerangTACode(val cfg: StmtGraph) {

  def statements: Array[Stmt[TacLocal]] = cfg.statements.toArray

  def getLocals: Set[TacLocal] = statements
    .filter(stmt => stmt.astID == Assignment.ASTID)
    .map(stmt => stmt.asAssignment.targetVar)
    .toSet

  def getParameterLocals: List[TacLocal] = statements
    .filter(stmt => stmt.pc == -1)
    .map(stmt => stmt.asAssignment.targetVar)
    .toList
}
