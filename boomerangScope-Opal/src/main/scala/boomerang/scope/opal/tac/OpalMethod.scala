package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.OpalClient
import org.opalj.tac.{InstanceFunctionCall, InstanceMethodCall, UVar}

import java.util
import scala.jdk.CollectionConverters._

case class OpalMethod(delegate: org.opalj.br.Method) extends Method {

  if (delegate.body.isEmpty) {
    throw new RuntimeException("Cannot build OpalMethod without existing body")
  }

  private var localCache: Option[util.Set[Val]] = None
  private var parameterLocalCache: Option[util.List[Val]] = None

  private val cfg = new OpalControlFlowGraph(this)

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

    val thisLocal = isThisLocalDefined
    if (thisLocal.isDefined) {
      return thisLocal.get.equals(fact)
    }

    // TODO This might not be enough
    false
  }

  override def getThisLocal: Val = {
    if (!isStatic) {
      val thisLocal = isThisLocalDefined
      if (thisLocal.isDefined) {
        return thisLocal.get
      }

      // TODO Replace corresponding places
      throw new RuntimeException("this local is not used in method. Use #isThisLocal for comparisons")
    }

    throw new RuntimeException("Static method does not have a 'this' local")
  }

  private def isThisLocalDefined: Option[Val] = {
    val locals = getLocals
    for (local <- locals.asScala) {
      val opalVal = local.asInstanceOf[OpalVal]
      val valDelegate = opalVal.delegate

      if (valDelegate.isInstanceOf[UVar[_]]) {
        if (valDelegate.asVar.definedBy.head == -1) {
          return Some(local)
        }
      }
    }

    None
  }

  override def getLocals: util.Set[Val] = {
    if (localCache.isEmpty) {
      localCache = Some(new util.HashSet[Val]())

      val tac = OpalClient.getTacForMethod(delegate)

      for (stmt <- tac.stmts) {
        if (stmt.isMethodCall) {
          // Extract the base
          if (stmt.isInstanceOf[InstanceMethodCall[_]]) {
            val base = new OpalLocal(stmt.asInstanceMethodCall.receiver, this)
            localCache.get.add(base)
          }

          // Parameters of method calls
          stmt.asMethodCall.params.foreach(param => {
            if (param.isVar) {
              localCache.get.add(new OpalLocal(param, this))
            }
          })
        }

        if (stmt.isAssignment) {
          // Target variable
          val targetVar = stmt.asAssignment.targetVar
          localCache.get.add(new OpalLocal(targetVar, this))

          if (stmt.asAssignment.expr.isFunctionCall) {
            // Extract the base
            if (stmt.asAssignment.expr.isInstanceOf[InstanceFunctionCall[_]]) {
              val base = new OpalLocal(stmt.asAssignment.expr.asInstanceFunctionCall.receiver, this)
              localCache.get.add(base)
            }

            // Parameters of function call
            stmt.asAssignment.expr.asFunctionCall.params.foreach(param => {
              if (param.isVar) {
                localCache.get.add(new OpalLocal(param, this))
              }
            })
          }
        }
      }
    }

    localCache.get
  }

  override def getParameterLocals: util.List[Val] = {
    if (parameterLocalCache.isEmpty) {
      parameterLocalCache = Some(new util.ArrayList[Val]())

      val locals = getLocals
      for (local <- locals.asScala) {
        if (local.isLocal) {
          val opalVal = local.asInstanceOf[OpalVal]
          val valDelegate = opalVal.delegate

          if (valDelegate.isInstanceOf[UVar[_]]) {
            if (valDelegate.asVar.definedBy.head < 0) {
              parameterLocalCache.get.add(local)
            }
          }
        }
      }
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
