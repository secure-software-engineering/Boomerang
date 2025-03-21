package boomerang.scope.opal

import boomerang.scope.InvokeExpr
import boomerang.scope.opal.tac.{OpalLocal, OpalMethod, OpalStatement}
import boomerang.scope.test.MethodSignature
import boomerang.scope.test.targets.{InvokeExprTarget, ThisLocalTarget}
import org.junit.Test
import org.opalj.ai.{AIResult, Domain, InterruptableAI}
import org.opalj.ai.domain.l0.{BaseDomainWithDefUse, PrimitiveTACAIDomain}
import org.opalj.br.cfg.CFGFactory
import org.opalj.tac.TACAI

class OpalInvokeExprTest {

  @Test
  def instanceInvokeExprTest(): Unit = {
    val opalSetup = new OpalSetup()
    opalSetup.setupOpal(classOf[InvokeExprTarget].getName)

    val signature = new MethodSignature(classOf[InvokeExprTarget].getName, "alias", "Void")
    val method = opalSetup.resolveMethod(signature)
    val opalMethod = OpalMethod(method)

    val domain = new BaseDomainWithDefUse(OpalClient.project.get, method)
    val ai = new InterruptableAI[Domain]
    val result = ai(method, domain)

    opalMethod.getStatements.forEach(stmt => {
      println("\n" + stmt + ":")
      if (stmt.containsInvokeExpr()) {
        val invokeExpr = stmt.getInvokeExpr

        if (invokeExpr.isInstanceInvokeExpr) {
          val base = invokeExpr.getBase

          val baseId = base.asInstanceOf[OpalLocal].delegate.asVar.id
          val base1 = result.domain.operandOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, baseId)
          println("Base " + base1)

          if (invokeExpr.getArgs.size() > 0) {
            val arg = invokeExpr.getArg(0)
            val argId = arg.asInstanceOf[OpalLocal].delegate.asVar.id
            val arg1 = result.domain.operandOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, argId)
            println("Arg " + arg1)
          }
        }
      }

      if (stmt.isAssignStmt) {
        val leftOp = stmt.getLeftOp
        val rightOp = stmt.getRightOp

        if (leftOp.isLocal && rightOp.isLocal) {
          val rightId = rightOp.asInstanceOf[OpalLocal].delegate.asVar.id
          if (rightId >= 0) {
            val right1 = result.domain.operandOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, rightId)
            println("r1: "+ right1)
          } else {
            val right2 = result.domain.localOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, rightId)
            println("r2: " + right2)
          }
        }

        if (leftOp.isLocal && rightOp.isIntConstant) {
          val leftId = leftOp.asInstanceOf[OpalLocal].delegate.asVar.id
          if (leftId >= 0) {
            //val left1 = result.domain.operandOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, leftId)
            println("l1: ")// + left1)
          } else {
            val left2 = result.domain.localOrigin(stmt.asInstanceOf[OpalStatement].delegate.pc, leftId)
            println("l2: " + left2)
          }
        }
      }
    })
  }

}
