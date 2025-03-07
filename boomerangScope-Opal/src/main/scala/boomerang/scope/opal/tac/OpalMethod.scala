package boomerang.scope.opal.tac

import boomerang.scope._
import boomerang.scope.opal.OpalClient
import org.opalj.br.{MethodDescriptor, MethodSignature, ObjectType}
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

  override def isNative: Boolean = delegate.isNative

  override def getStatements: util.List[Statement] = cfg.getStatements

  override def getDeclaringClass: WrappedClass = OpalWrappedClass(delegate.classFile)

  override def getControlFlowGraph: ControlFlowGraph = cfg

  override def getSubSignature: String = delegate.signature.toJava

  override def getName: String = delegate.name

  override def isConstructor: Boolean = delegate.isConstructor

  override def isPublic: Boolean = delegate.isPublic

  override def toString: String = delegate.toJava
}

case class OpalPhantomMethod(declaringClassType: ObjectType, name: String, descriptor: MethodDescriptor, isStatic: Boolean) extends Method {

  override def isStaticInitializer: Boolean = name == "<clinit>"

  override def isParameterLocal(value: Val): Boolean = false

  override def getParameterTypes: util.List[Type] = {
    val result = new util.ArrayList[Type]()

    descriptor.parameterTypes.foreach(paramType => {
      result.add(OpalType(paramType))
    })

    result
  }

  override def getParameterType(index: Int): Type = OpalType(descriptor.parameterType(index))

  override def isThisLocal(value: Val): Boolean = false

  override def getLocals: util.Set[Val] = throw new RuntimeException("Locals of phantom method are not available")

  override def getThisLocal: Val = throw new RuntimeException("this local of phantom method is not available")

  override def getParameterLocals: util.List[Val] = throw new RuntimeException("Parameter locals of phantom method are not available")

  override def isNative: Boolean = false

  override def getStatements: util.List[Statement] = new util.ArrayList[Statement]()

  override def getDeclaringClass: WrappedClass = OpalPhantomWrappedClass(declaringClassType)

  override def getControlFlowGraph: ControlFlowGraph = throw new RuntimeException("CFG of phantom method is not available")

  override def getSubSignature: String = MethodSignature(name, descriptor).toJava

  override def getName: String = name

  override def isConstructor: Boolean = name == "<init>"

  override def isPublic: Boolean = true

  override def toString: String = "PHANTOM: " + getSubSignature
}
