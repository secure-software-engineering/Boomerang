package boomerang.scope.opal.transformer

import org.opalj.tac.{Assignment, Expr, NewArray, Nop, SimpleValueConst, Stmt}

object BasicPropagation {

  def apply(tacStatements: Array[Stmt[TacLocal]]): Array[Stmt[TacLocal]] = {
    val statements = tacStatements.map(identity)

    Range(0, tacStatements.length - 1).foreach(i => {
      tacStatements(i) match {
        /* Transform constant assignments:
         * $s = <constant>
         * r = $s
         *
         * becomes
         *
         * Nop
         * r = <constant>
         */
        case Assignment(pc, targetVar, c @ (_: SimpleValueConst)) =>
          tacStatements(i + 1) match {
            // Case: r = $s
            case Assignment(nextPc, nextTargetVar, `targetVar`) =>
              statements(i) = Nop(pc)
              statements(i + 1) = Assignment(nextPc, nextTargetVar, c)
            case _ =>
          }

        /* Transform array assignments and try to find their counts:
         * $s0 = 0
         * $s1 = 1
         * $s2 = NewArray(counts($s1, $s0))
         * r = $s2
         *
         * becomes
         *
         * Nop
         * Nop
         * Nop
         * r = NewArray(counts(1, 0))
         */
        case Assignment(pc, targetVar, NewArray(exprPc, counts, tpe)) =>
          val newCounts = counts.map(c => {
            val allocSite = propagateBackward(i, c)

            if (allocSite.isDefined) {
              val allocStmt = statements(allocSite.get)
              statements(allocSite.get) = Nop(allocStmt.pc)

              allocStmt.asAssignment.expr
            } else {
              c
            }
          })

          val newArray = NewArray(exprPc, newCounts, tpe)
          tacStatements(i + 1) match {
            case Assignment(nextPc, nextTargetVar, `targetVar`) =>
              statements(i) = Nop(pc)
              statements(i + 1) = Assignment(nextPc, nextTargetVar, newArray)
            case _ =>
          }
        case _ =>
      }
    })

    def propagateBackward(i: Int, local: Expr[TacLocal]): Option[Int] = {
      Range.inclusive(i - 1, 0, -1).foreach(j => {
        val currStmt = statements(j)

        if (currStmt.astID == Assignment.ASTID) {
          if (currStmt.asAssignment.targetVar == local && currStmt.asAssignment.expr.isConst) {
            return Option(j)
          }
        }
      })

      Option.empty
    }

    statements
  }

}
