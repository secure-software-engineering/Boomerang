package boomerang.scope.opal.transformation

import boomerang.scope.opal.transformation.stack.OperandStackBuilder
import boomerang.scope.opal.transformation.transformer.{InlineLocalTransformer, LocalTransformer, NopTransformer, NullifyFieldsTransformer}
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.br.cfg.CFGFactory
import org.opalj.tac.{Stmt, TACNaive, TACStmts}

object TacBodyBuilder {

  def apply(project: Project[_], method: Method): BoomerangTACode = {
    val tacNaive = TACNaive(method, project.classHierarchy)
    val stackHandler = OperandStackBuilder(method, tacNaive)

    val domain = new PrimitiveTACAIDomain(project.classHierarchy, method)
    val localTransformedTac = LocalTransformer(method, tacNaive, stackHandler, domain)
    val inlinedTac = InlineLocalTransformer(localTransformedTac, stackHandler)

    // Update the CFG
    val cfg = CFGFactory(method, project.classHierarchy)
    if (cfg.isEmpty) {
      throw new RuntimeException("Could not compute CFG for method " + method.name)
    }

    val tacCfg = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](TACStmts(inlinedTac), tacNaive.pcToIndex, i => i, inlinedTac.length)

    val exceptionHandlers = tacNaive.exceptionHandlers.map(eh => eh.handlerPC).toArray
    val stmtGraph = StmtGraph(inlinedTac, tacCfg, tacNaive.pcToIndex, exceptionHandlers)

    val nopStmtGraph = NopTransformer(stmtGraph)
    val nullifiedStmtGraph = transformer.NullifyFieldsTransformer(method, nopStmtGraph)

    new BoomerangTACode(nullifiedStmtGraph)
  }
}
