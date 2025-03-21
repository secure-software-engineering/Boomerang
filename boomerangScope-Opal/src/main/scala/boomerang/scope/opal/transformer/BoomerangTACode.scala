package boomerang.scope.opal.transformer

import org.opalj.br.ExceptionHandlers
import org.opalj.br.cfg.CFG
import org.opalj.tac.{Param, Parameters, Stmt, TACStmts}

class BoomerangTACode(
                       val params: Parameters[Param],
                       val statements: Array[Stmt[TacLocal]],
                       val pcToIndex: Array[Int],
                       val cfg: CFG[Stmt[TacLocal], TACStmts[TacLocal]],
                       val exceptionHandlers: ExceptionHandlers
                     ) {}
