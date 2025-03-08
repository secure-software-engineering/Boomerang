package boomerang.scope.opal.tac

import boomerang.scope.{Method, Type, WrappedClass}
import org.opalj.br.ReferenceType

import java.util

case class OpalPhantomWrappedClass(delegate: ReferenceType) extends WrappedClass {

  override def getMethods: util.Set[Method] = throw new RuntimeException("Methods of class " + delegate.toString + " are not available")

  override def hasSuperclass: Boolean = false

  override def getSuperclass: WrappedClass = throw new RuntimeException("Super class of " + delegate.toString + " is not available")

  override def getType: Type = OpalType(delegate)

  override def isApplicationClass: Boolean = false

  override def getFullyQualifiedName: String = delegate.toJava

  override def isPhantom: Boolean = true

  override def toString: String = delegate.toString
}
