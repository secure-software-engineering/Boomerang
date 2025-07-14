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
package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.StmtGraph
import org.opalj.tac.Nop

object NopEliminator {

  def apply(stmtGraph: StmtGraph): StmtGraph = {

    def removeNopStatements(stmtGraph: StmtGraph): StmtGraph = {
      val nopStatements =
        stmtGraph.statements.filter(s => s.astID == Nop.ASTID && s.pc >= 0)
      var result = stmtGraph

      nopStatements.foreach(stmt => result = result.remove(stmt))
      result
    }

    removeNopStatements(stmtGraph)
  }
}
