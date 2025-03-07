package boomerang.scope.opal.tac

import boomerang.scope.opal.OpalClient
import boomerang.scope.{Method, Type, WrappedClass}
import org.opalj.br.{ClassFile, ReferenceType}

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

  override def getFullyQualifiedName: String = delegate.fqn

  override def getName: String = delegate.thisType.toJava

  override def toString: String = delegate.toString()

  override def getDelegate: AnyRef = delegate
}

case class OpalPhantomWrappedClass(delegate: ReferenceType) extends WrappedClass {

  override def getMethods: util.Set[Method] = throw new RuntimeException("Methods of class " + delegate.toString + " are not available")

  override def hasSuperclass: Boolean = false

  override def getSuperclass: WrappedClass = throw new RuntimeException("Super class of " + delegate.toString + " is not available")

  override def getType: Type = OpalType(delegate)

  override def isApplicationClass: Boolean = false

  override def getFullyQualifiedName: String = delegate.toJava

  override def getName: String = delegate.toJava

  override def getDelegate: AnyRef = delegate

  override def toString: String = delegate.toString

}
