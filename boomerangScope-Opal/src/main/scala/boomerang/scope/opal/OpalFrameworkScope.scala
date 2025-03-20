package boomerang.scope.opal

import boomerang.scope.{CallGraph, DataFlowScope, Field, FrameworkScope, Method, StaticFieldVal, Val}
import org.opalj.ai.domain
import org.opalj.br.analyses.Project
import org.opalj.tac.ComputeTACAIKey

import java.util.stream

class OpalFrameworkScope(project: Project[_], callGraph: org.opalj.tac.cg.CallGraph, entryPoints: Set[org.opalj.br.Method], dataFlowScope: DataFlowScope) extends FrameworkScope {

  OpalClient.init(project)
  private val opalCallGraph = new OpalCallGraph(callGraph, entryPoints)

  override def getCallGraph: CallGraph = opalCallGraph

  override def getDataFlowScope: DataFlowScope = dataFlowScope

  override def getTrueValue(m: Method): Val = ???

  override def getFalseValue(m: Method): Val = ???

  override def handleStaticFieldInitializers(fact: Val): stream.Stream[Method] = ???

  override def newStaticFieldVal(field: Field, m: Method): StaticFieldVal = ???
}

object OpalFrameworkScope {
  final val STATIC_INITIALIZER: String = "<clinit>"
  final val CONSTRUCTOR: String = "<init>"
}
