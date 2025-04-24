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

import boomerang.scope.opal.transformation.stack.OperandStackBuilder
import boomerang.scope.opal.transformation.transformer._
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.br.cfg.CFGFactory
import org.opalj.tac.Stmt
import org.opalj.tac.TACNaive
import org.opalj.tac.TACStmts

object TacBodyBuilder {

    def apply(project: Project[_], method: Method): BoomerangTACode = {
        if (method.body.isEmpty) {
            throw new IllegalArgumentException(
                "Cannot compute TAC for method without existing body: " + method
            )
        }

        val tacNaive = TACNaive(method, project.classHierarchy)
        val stackHandler = OperandStackBuilder(method, tacNaive)

        // TODO Use other domain to compute static type information
        val domain = new PrimitiveTACAIDomain(project.classHierarchy, method)
        val localTransformedTac =
            LocalTransformer(project, method, tacNaive, stackHandler, domain)
        assert(
            tacNaive.stmts.length == localTransformedTac.length,
            "Wrong transformation"
        )

        val inlinedTac = InlineLocalTransformer(localTransformedTac, stackHandler)
        assert(tacNaive.stmts.length == inlinedTac.length, "Wrong transformation")

        val propagatedTac = LocalPropagationTransformer(inlinedTac, stackHandler)
        assert(
            tacNaive.stmts.length == propagatedTac.length,
            "Wrong transformation"
        )

        // Update the CFG
        val cfg = CFGFactory(method, project.classHierarchy)
        if (cfg.isEmpty) {
            throw new RuntimeException(
                "Could not compute CFG for method " + method.name
            )
        }

        val tacCfg = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](
            TACStmts(propagatedTac),
            tacNaive.pcToIndex,
            i => i,
            propagatedTac.length
        )

        val exceptionHandlers =
            tacNaive.exceptionHandlers.map(eh => eh.handlerPC).toArray
        var stmtGraph =
            StmtGraph(propagatedTac, tacCfg, tacNaive.pcToIndex, exceptionHandlers)

        stmtGraph = NopTransformer(stmtGraph)
        stmtGraph = NullifyFieldsTransformer(method, stmtGraph)
        stmtGraph = NopEliminator(stmtGraph)

        new BoomerangTACode(stmtGraph)
    }
}
