package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.{Method, Type, WrappedClass}
import org.opalj.br.ClassFile

import java.util

case class OpalWrappedClass(delegate: ClassFile) extends WrappedClass {

  override def getMethods: util.Set[Method] = {
    val methods = new util.HashSet[Method]

    delegate.methods.foreach(method => {
      methods.add(OpalMethod(method))
    })

    methods
  }

  override def hasSuperclass: Boolean = delegate.superclassType.isDefined

  override def getSuperclass: WrappedClass = {
    if (hasSuperclass) {
      val superClass = OpalClient.getClassFileForType(delegate.superclassType.get)

      if (superClass.isDefined) {
        return OpalWrappedClass(superClass.get)
      } else {
        return OpalPhantomWrappedClass(delegate.superclassType.get)
      }
    }

    throw new RuntimeException("Class " + delegate.thisType.toJava + " has no super class")
  }

  override def getType: Type = OpalType(delegate.thisType)

  override def isApplicationClass: Boolean = OpalClient.isApplicationClass(delegate)

  override def getFullyQualifiedName: String = delegate.fqn.replace("/", ".")

  override def isPhantom: Boolean = false

  override def toString: String = delegate.toString()
}
