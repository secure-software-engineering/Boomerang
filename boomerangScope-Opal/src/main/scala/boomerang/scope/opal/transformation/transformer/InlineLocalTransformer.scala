package boomerang.scope.opal.transformation.transformer

import boomerang.scope.opal.transformation.{RegisterLocal, StackLocal, TacLocal}
import org.opalj.br.cfg.CFG
import org.opalj.tac._

import scala.collection.mutable

object InlineLocalTransformer {

  def apply(code: Array[Stmt[TacLocal]], cfg: CFG[Stmt[IdBasedVar], TACStmts[IdBasedVar]]): Array[Stmt[TacLocal]] = {
    val statements = code.map(identity)

    val bbs = cfg.allBBs
    bbs.withFilter(bb => bb.startPC < bb.endPC).foreach(bb => {
      val localCache = mutable.Map.empty[TacLocal, Expr[TacLocal]]
      val localDefSites = mutable.Map.empty[TacLocal, (Int, Int)]

      var index = bb.startPC
      while (index <= bb.endPC) {
        code(index) match {
          case Assignment(pc, targetVar: StackLocal, c @ (_: SimpleValueConst | _: FunctionCall[TacLocal] | _: GetField[TacLocal] | _: GetStatic)) =>
            localCache.put(targetVar, c)
            localDefSites.put(targetVar, (index, pc))
          case Assignment(pc, targetVar: RegisterLocal, rightVar: StackLocal) =>
            /*if (localCache.contains(rightVar)) {
              val localExpr = localCache(rightVar)
              statements(index) = Assignment(pc, targetVar, localExpr)

              val localDefSite = localDefSites.getOrElse(rightVar, throw new RuntimeException("Def sites not consistent"))
              statements(localDefSite._1) = Nop(localDefSite._2)
            }*/
          case Assignment(pc, targetVar: StackLocal, expr) =>
            expr match {
              case NewArray(arrPc, counts, arrayType) =>
                var countDefSites = List.empty[(Int, Int)]

                val newCounts = counts.map(c => {
                  if (c.isVar && localCache.contains(c.asVar)) {
                    val localExpr = localCache(c.asVar)

                    if (localExpr.isIntConst) {
                      val countDefSite = localDefSites.getOrElse(c.asVar, throw new RuntimeException("Def sites not consistent"))
                      countDefSites = countDefSites :+ countDefSite

                      localExpr
                    } else {
                      c
                    }
                  } else {
                    c
                  }
                })

                statements(index) = Assignment(pc, targetVar, NewArray(arrPc, newCounts, arrayType))
                countDefSites.foreach(defSite => statements(defSite._1) = Nop(defSite._2))
              case ArrayLoad(arrPc, arrayIndex: StackLocal, arrayRef) =>
                if (localCache.contains(arrayIndex)) {
                  val localExpr = localCache(arrayIndex)

                  if (localExpr.isIntConst) {
                    statements(index) = Assignment(pc, targetVar, ArrayLoad(arrPc, localExpr, arrayRef))

                    val localDefSite = localDefSites.getOrElse(arrayIndex, throw new RuntimeException("Def sites not consistent"))
                    statements(localDefSite._1) = Nop(localDefSite._2)
                  }
                }
              case _ =>
            }
          case ArrayStore(pc, arrayRef, arrayIndex: StackLocal, value) =>
            if (localCache.contains(arrayIndex)) {
              val localExpr = localCache(arrayIndex)

              if (localExpr.isIntConst) {
                // TODO Also inline value if it is a simple constant
                statements(index) = ArrayStore(pc, arrayRef, localExpr, value)

                val localDefSite = localDefSites.getOrElse(arrayIndex, throw new RuntimeException("Def sites not consistent"))
                statements(localDefSite._1) = Nop(localDefSite._2)
              }
            }
          case _ =>
        }

        index += 1
      }
    })

    val max = code.length - 1
    Range(0, max).foreach(i => {
      statements(i) match {

        /* Inline simple stack to register definitions:
         * $s = <expr>
         * r = $s
         *
         * becomes
         * r = <expr>
         */
        case Assignment(pc, targetVar: StackLocal, c @ (_: SimpleValueConst | _: FunctionCall[TacLocal] | _: NewArray[TacLocal] | _: ArrayLoad[TacLocal] | _: GetField[TacLocal] | _: GetStatic)) =>
          statements(i + 1) match {
            case Assignment(nextPc, nextTargetVar: RegisterLocal, `targetVar`) =>
              statements(i) = Nop(pc)
              statements(i + 1) = Assignment(nextPc, nextTargetVar, c)
            case _ =>
          }
        case _ =>
      }
    })

    /*Range(0, max).foreach(i => {
      statements(i) match {
        case ArrayStore(pc, arrayRef, index, value) =>
          val allocSiteIndex = findArrayIndexAllocSite(i, index)
          val allocSiteStmt = statements(i)

          //statements(allocSiteIndex) = Nop(allocSiteStmt.pc)
          //statements(i) = ArrayStore(pc, arrayRef, allocSiteStmt.asAssignment)
        case _ =>
      }
    })

    def findArrayIndexAllocSite(index: Int, expr: Expr[TacLocal]): Int = {
      Range(index, 0, -1).foreach(i => {
        val currStmt = statements(i)

        if (currStmt.isAssignment) {
          val assignStmt = currStmt.asAssignment

          if (assignStmt.targetVar == expr && assignStmt.expr.isIntConst) {
            return i
          }
        }
      })

      -1
    }*/

    statements
  }

}
