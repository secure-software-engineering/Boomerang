package boomerang.scope.opal.transformer

import org.opalj.ai.{AIResult, BaseAI}
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.analyses.Project
import org.opalj.br.cfg.CFGFactory
import org.opalj.br.Method
import org.opalj.tac.{Stmt, TACNaive, TACStmts}

object TacTransformer {

  def apply(project: Project[_], method: Method): BoomerangTACode = {

    val aiResult: AIResult = BaseAI(method, new PrimitiveTACAIDomain(project.classHierarchy, method))

    val tacNaive = TACNaive(method, project.classHierarchy)

    val transformedTac: Array[Stmt[TacLocal]] = LocalTransformer(method, tacNaive, aiResult)
    val simplifiedTac: Array[Stmt[TacLocal]] = BasicPropagation(transformedTac)
    val nullifiedTac = NullifyFieldsTransformer(method, simplifiedTac, tacNaive.pcToIndex)

    // Update the CFG
    val cfg = CFGFactory(method, project.classHierarchy)
    if (cfg.isEmpty) {
      throw new RuntimeException("Could not compute CFG for method " + method.name)
    }

    val tacCfg = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](TACStmts(nullifiedTac._1), nullifiedTac._2, i => i, nullifiedTac._1.length)
    val tacCfg2 = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](TACStmts(simplifiedTac), tacNaive.pcToIndex, i => i, simplifiedTac.length)
    val preds = tacCfg.predecessors(5)
    val preds2 = tacCfg2.predecessors(5)

    new BoomerangTACode(tacNaive.params, simplifiedTac, tacNaive.pcToIndex, tacCfg, tacNaive.exceptionHandlers)
  }
}
