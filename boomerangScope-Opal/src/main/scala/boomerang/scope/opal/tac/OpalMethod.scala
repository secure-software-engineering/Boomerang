package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.OpalClient
import boomerang.scope.opal.transformer.{BoomerangTACode, TacTransformer}

import java.util
import java.util.Objects

class OpalMethod private(val delegate: org.opalj.br.Method, val tac: BoomerangTACode) extends Method {

  if (delegate.body.isEmpty) {
    throw new RuntimeException("Cannot build OpalMethod without existing body")
  }

  private val cfg = new OpalControlFlowGraph(this)

  private var localCache: Option[util.Set[Val]] = None
  private var parameterLocalCache: Option[util.List[Val]] = None

  override def isStaticInitializer: Boolean = delegate.isStaticInitializer

  override def isParameterLocal(value: Val): Boolean = getParameterLocals.contains(value)

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    delegate.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = getParameterTypes.get(index)

  override def getReturnType: Type = OpalType(delegate.descriptor.returnType)

  override def isThisLocal(fact: Val): Boolean = {
    if (isStatic) return false

    val thisLocal = getThisLocal
    thisLocal.equals(fact)
  }

  override def getThisLocal: Val = {
    if (!isStatic) {
      /* The 'this' local is implicitly defined as parameter local with id -1. If the 'this' local
       * is not used within the method, it stays at -1. However, if it is used, there is an additional
       * assignment to an actual variable. Therefore, we have to check first for the usage.
       */
      tac.statements.foreach(stmt => {
        if (stmt.isAssignment && stmt.asAssignment.expr.isVar) {
          if (stmt.asAssignment.expr.asVar.id == -1) {
            return new OpalLocal(stmt.asAssignment.targetVar, this)
          }
        }
      })

      // 'this' local is not used; return just the parameter local
      tac.statements.foreach(stmt => {
        if (stmt.isAssignment && stmt.asAssignment.targetVar.id == -1) {
          return new OpalLocal(stmt.asAssignment.targetVar, this)
        }
      })

      throw new RuntimeException("Could not determine 'this' local in method " + delegate.name)
    }

    throw new RuntimeException("Static method does not have a 'this' local")
  }

  override def getLocals: util.Set[Val] = {
    if (localCache.isEmpty) {
      localCache = Some(new util.HashSet[Val]())

      // 'this' local
      if (!isStatic) {
        localCache.get.add(getThisLocal)
      }

      // Parameter locals
      localCache.get.addAll(getParameterLocals)

      tac.statements.foreach(stmt => {
        if (stmt.isAssignment) {
          val targetVar = stmt.asAssignment.targetVar

          localCache.get.add(new OpalLocal(targetVar, this))
        }
      })
    }

    localCache.get
  }

  override def getParameterLocals: util.List[Val] = {
    if (parameterLocalCache.isEmpty) {
      parameterLocalCache = Some(new util.ArrayList[Val]())

      val indices = new util.HashSet[Integer]()

      tac.statements.foreach(stmt => {
        if (stmt.isAssignment && stmt.asAssignment.expr.isVar) {
          val param = stmt.asAssignment.expr.asVar

          // Exclude the 'this' local
          if (param.id < 0 && param.id != -1) {
            val paramLocal = new OpalLocal(stmt.asAssignment.targetVar, this)

            parameterLocalCache.get.add(paramLocal)
            indices.add(param.id)
          }
        }
      })

      // Collect all unused parameter locals
      tac.statements.foreach(stmt => {
        if (stmt.isAssignment) {
          val target = stmt.asAssignment.targetVar

          // Exclude 'this' local
          if (target.id < 0 && target.id != -1) {
            if (!indices.contains(target.id)) {
              val paramLocal = new OpalLocal(target, this)

              parameterLocalCache.get.add(paramLocal)
            }
          }
        }
      })
    }

    parameterLocalCache.get
  }

  override def isStatic: Boolean = delegate.isStatic

  override def isDefined: Boolean = true

  override def isPhantom: Boolean = false

  override def getStatements: util.List[Statement] = cfg.getStatements

  override def getDeclaringClass: WrappedClass = OpalWrappedClass(delegate.classFile)

  override def getControlFlowGraph: ControlFlowGraph = cfg.get()

  override def getSubSignature: String = delegate.signature.toJava

  override def getName: String = delegate.name

  override def isConstructor: Boolean = delegate.isConstructor

  override def hashCode: Int = Objects.hash(delegate)

  override def equals(other: Any): Boolean = other match {
    case that: OpalMethod => this.delegate == that.delegate
    case _ => false
  }

  override def toString: String = delegate.toJava
}

object OpalMethod {

  def apply(delegate: org.opalj.br.Method): OpalMethod = new OpalMethod(delegate, TacTransformer(delegate, OpalClient.getClassHierarchy))

  def apply(delegate: org.opalj.br.Method, tac: BoomerangTACode): OpalMethod = new OpalMethod(delegate, tac)
}
