package boomerang.scope.opal.transformer

import org.opalj.ai.domain.l0.PrimitiveTACAIDomain
import org.opalj.br.analyses.Project
import org.opalj.br.cfg.CFGFactory
import org.opalj.br.Method
import org.opalj.tac.{Stmt, TACNaive, TACStmts}

object TacTransformer {

  def apply(project: Project[_], method: Method): BoomerangTACode = {
    val tacNaive = TACNaive(method, project.classHierarchy)

    val domain = new PrimitiveTACAIDomain(project.classHierarchy, method)
    val operandStack = OperandStack(tacNaive.stmts, tacNaive.cfg)
    val transformedTac = LocalTransformer(method, tacNaive, domain, operandStack)
    val simplifiedTac = BasicPropagation(transformedTac, operandStack)

    // Update the CFG
    val cfg = CFGFactory(method, project.classHierarchy)
    if (cfg.isEmpty) {
      throw new RuntimeException("Could not compute CFG for method " + method.name)
    }

    val tacCfg = cfg.get.mapPCsToIndexes[Stmt[TacLocal], TACStmts[TacLocal]](TACStmts(simplifiedTac), tacNaive.pcToIndex, i => i, simplifiedTac.length)

    val stmtGraph = StmtGraph(simplifiedTac, tacCfg, tacNaive.pcToIndex)
    val nopStmtGraph = NopTransformer(stmtGraph)
    val nullifiedStmtGraph = NullifyFieldsTransformer(method, nopStmtGraph)

    new BoomerangTACode(nullifiedStmtGraph)
  }
}
