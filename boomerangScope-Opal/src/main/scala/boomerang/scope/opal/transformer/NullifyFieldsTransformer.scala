package boomerang.scope.opal.transformer

import org.opalj.br.{ComputationalTypeReference, Field, FieldType, Method}
import org.opalj.tac.{Assignment, Expr, NullExpr, PutField}

object NullifyFieldsTransformer {

  final val NULLIFIED_FIELD = -2

  def apply(method: Method, stmtGraph: StmtGraph): StmtGraph = {
    if (!method.isConstructor || method.isStatic) {
      return stmtGraph
    }

    val tac = stmtGraph.tac
    var localCounter = 0

    def isFieldDefined(field: Field): Boolean = {
      // TODO Also consider super classes
      tac.foreach {
        // TODO Maybe also match 'this' local?
        case PutField(_, _, field.name, field.fieldType, _, _) => return true
        case _ =>
      }
      false
    }

    def getThisLocal: Expr[TacLocal] = {
      tac.foreach(stmt => {
        if (stmt.pc == -1 && stmt.asAssignment.targetVar.id == -1) return stmt.asAssignment.targetVar
      })

      throw new RuntimeException("'this' local not found in method: " + method.name)
    }

    def createNullifiedLocal(localCounter: Int, fieldType: FieldType): NullifiedLocal = {
      // TODO Types
      new NullifiedLocal(localCounter, ComputationalTypeReference)
    }

    val definedFields = method.classFile.fields.filter(f => isFieldDefined(f))
    val undefinedFields = method.classFile.fields.filter(f => !definedFields.contains(f) && f.isNotStatic && f.isNotFinal)
    val firstOriginalStmt = tac.find(stmt => stmt.pc >= 0).get
    var result = stmtGraph

    undefinedFields.foreach(field => {
      val local = createNullifiedLocal(localCounter, field.fieldType)
      localCounter += 1

      val defSite = Assignment[TacLocal](NULLIFIED_FIELD, local, NullExpr(NULLIFIED_FIELD))
      val putField = PutField(NULLIFIED_FIELD, method.classFile.thisType, field.name, field.fieldType, getThisLocal, local)

      result = result.insertBefore(defSite, firstOriginalStmt)
      result = result.insertBefore(putField, firstOriginalStmt)

    })

    result
  }

}
