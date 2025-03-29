package boomerang.scope.opal.transformer

import org.opalj.br.{Field, Method}
import org.opalj.tac.{Assignment, Expr, NullExpr, PutField, Stmt}

object NullifyFieldsTransformer {

  final val NULLIFIED_FIELD = -2

  def apply(method: Method, tac: Array[Stmt[TacLocal]], pcToIndex: Array[Int]): (Array[Stmt[TacLocal]], Array[Int]) = {
    if (!method.isConstructor || method.isStatic) {
      return (tac.map(identity), pcToIndex)
    }

    var stackCounter = -1

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

    val definedFields = method.classFile.fields.filter(f => isFieldDefined(f))
    val undefinedFields = method.classFile.fields.filter(f => !definedFields.contains(f) && f.isNotStatic && f.isNotFinal)

    // For each undefined field, we add a definition and a field store statement
    val cutIndex = 2 * undefinedFields.size
    val nullifiedTac = new Array[Stmt[TacLocal]](tac.length + cutIndex)

    // Add the parameter definitions
    val paramDefinitions = tac.filter(stmt => stmt.pc == -1)
    Range(0, paramDefinitions.length).foreach(i => nullifiedTac(i) = tac(i))

    // Create the nullified fields
    Range(0, undefinedFields.size).foreach(i => {
      val currField = undefinedFields(i)
      // TODO Types
      val local = new StackLocal(stackCounter, null, null)
      stackCounter -= 1

      val defSite = Assignment[TacLocal](NULLIFIED_FIELD, local, NullExpr(NULLIFIED_FIELD))
      val putField = PutField(NULLIFIED_FIELD, method.classFile.thisType, currField.name, currField.fieldType, getThisLocal, local)

      nullifiedTac(paramDefinitions.length + 2 * i) = defSite
      nullifiedTac(paramDefinitions.length + 2 * i + 1) = putField
    })

    // Append the original tac statements
    val offset = paramDefinitions.length + cutIndex - 1
    Range(paramDefinitions.length, tac.length).foreach(i => {
      nullifiedTac(i + offset) = tac(i)
    })

    // Nullified fields were added before the original statements. Hence, the original
    // statements are shifted by the amount of new statements
    val nullifiedPcToIndex = pcToIndex.map(i => if (i == Int.MinValue) i else i + cutIndex)
    (nullifiedTac, nullifiedPcToIndex)
  }
}
