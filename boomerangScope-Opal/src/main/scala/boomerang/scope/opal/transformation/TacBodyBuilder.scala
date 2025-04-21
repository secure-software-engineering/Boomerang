package boomerang.scope.opal.transformation

import boomerang.scope.opal.transformation.stack.OperandStackBuilder
import boomerang.scope.opal.transformation.transformer.{InlineLocalTransformer, LocalPropagationTransformer, LocalTransformer, NopEliminator, NopTransformer, NullifyFieldsTransformer}
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.br.cfg.CFGFactory
import org.opalj.tac.{Stmt, TACNaive, TACStmts}

object TacBodyBuilder {

  def apply(project: Project[_], method: Method): BoomerangTACode = {
    if (method.body.isEmpty) {
      throw new IllegalArgumentException("Cannot compute TAC for method without existing body: " + method)
    }

    val tacNaive = TACNaive(method, project.classHierarchy)
    val stackHandler = OperandStackBuilder(method, tacNaive)

    val domain = new PrimitiveTACAIDomain(project.classHierarchy, method)
    val localTransformedTac = LocalTransformer(method, tacNaive, stackHandler, domain)
    assert(tacNaive.stmts.length == localTransformedTac.length, "Wrong transformation")

    val inlinedTac = InlineLocalTransformer(localTransformedTac, stackHandler)
    assert(tacNaive.stmts.length == inlinedTac.length, "Wrong transformation")

    val propagatedTac = LocalPropagationTransformer(inlinedTac, stackHandler)
    assert(tacNaive.stmts.length == propagatedTac.length, "Wrong transformation")

    // Update the CFG
    val cfg = CFGFactory(method, project.classHierarchy)
    if (cfg.isEmpty) {
      throw new RuntimeException("Could not compute CFG for method " + method.name)
    }

    val tacCfg = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](TACStmts(propagatedTac), tacNaive.pcToIndex, i => i, propagatedTac.length)

    val exceptionHandlers = tacNaive.exceptionHandlers.map(eh => eh.handlerPC).toArray
    var stmtGraph = StmtGraph(propagatedTac, tacCfg, tacNaive.pcToIndex, exceptionHandlers)

    stmtGraph = NopTransformer(stmtGraph)
    stmtGraph = NullifyFieldsTransformer(method, stmtGraph)
    stmtGraph = NopEliminator(stmtGraph)

    new BoomerangTACode(stmtGraph)
  }
}
