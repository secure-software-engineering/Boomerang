package boomerang.scope.opal.transformation

import boomerang.scope.opal.transformation.stack.OperandStackBuilder
import boomerang.scope.opal.transformer.BoomerangTACode
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.tac.TACNaive

object TacBodyBuilder {

  def apply(project: Project[_], method: Method): BoomerangTACode = {
    val tacNaive = TACNaive(method, project.classHierarchy)
    val stackHandler = OperandStackBuilder(method, tacNaive)
    ???
  }
}
