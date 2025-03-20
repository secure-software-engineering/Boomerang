package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.OpalClient
import org.opalj.tac.{InstanceFunctionCall, InstanceMethodCall, Stmt}

import java.util

case class OpalMethod(delegate: org.opalj.br.Method) extends Method {

  if (delegate.body.isEmpty) {
    throw new RuntimeException("Cannot build OpalMethod without existing body")
  }

  private var localCache: Option[util.Set[Val]] = None
  private var parameterLocalCache: Option[util.List[Val]] = None

  private val cfg = new OpalControlFlowGraph(delegate)

  override def isStaticInitializer: Boolean = delegate.isStaticInitializer

  override def isParameterLocal(value: Val): Boolean = {
    // if (value.isStatic) return false

    val parameterLocals = getParameterLocals
    parameterLocals.contains(value)
  }

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
      return new OpalParameterLocal(delegate.classFile.thisType, -1, this)
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

      val tac = OpalClient.getTacForMethod(delegate)

      tac.stmts.foreach(stmt => {
        // Locals are always defined as DVar
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

      val tac = OpalClient.getTacForMethod(delegate)

      delegate.parameterTypes.indices.foreach(i => {
        val paramType = delegate.parameterTypes(i)
        val index = tac.params.parameters(i + 1).origin

        val parameterLocal = new OpalParameterLocal(paramType, index, this)
        parameterLocalCache.get.add(parameterLocal)
      })
    }

    parameterLocalCache.get
  }

  override def isStatic: Boolean = delegate.isStatic

  override def isDefined: Boolean = true

  override def isPhantom: Boolean = false

  override def getStatements: util.List[Statement] = cfg.getStatements

  override def getDeclaringClass: WrappedClass = OpalWrappedClass(delegate.classFile)

  override def getControlFlowGraph: ControlFlowGraph = cfg

  override def getSubSignature: String = delegate.signature.toJava

  override def getName: String = delegate.name

  override def isConstructor: Boolean = delegate.isConstructor

  override def toString: String = delegate.toJava
}
