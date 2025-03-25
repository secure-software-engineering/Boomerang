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
      tac.statements.foreach(stmt => {
        if (stmt.pc == -1) {
          val targetVar = stmt.asAssignment.targetVar

          if (targetVar.id == -1) {
            return new OpalLocal(targetVar, this)
          }
        }
      })

      throw new RuntimeException("Could not determine 'this' local in method " + delegate.name)
    }

    throw new RuntimeException("Static method does not have a 'this' local")
  }

  override def getLocals: util.Set[Val] = {
    if (localCache.isEmpty) {
      localCache = Some(new util.HashSet[Val]())

      tac.getLocals.foreach(l => localCache.get.add(new OpalLocal(l, this)))
      /*// 'this' local
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
      })*/
    }

    localCache.get
  }

  override def getParameterLocals: util.List[Val] = {
    if (parameterLocalCache.isEmpty) {
      parameterLocalCache = Some(new util.ArrayList[Val]())

      tac.getParameterLocals.foreach(l => {
        // Exclude the 'this' local from the parameters if this is an instance method
        if (isStatic) {
          parameterLocalCache.get.add(new OpalLocal(l, this))
        } else if (l.id != -1) {
          parameterLocalCache.get.add(new OpalLocal(l, this))
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
