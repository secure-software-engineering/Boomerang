package boomerang.scope.opal

import org.opalj.br.{ClassFile, ClassHierarchy, DefinedMethod, Field, Method, MethodDescriptor, ObjectType, ReferenceType}
import org.opalj.br.analyses.{DeclaredMethods, DeclaredMethodsKey, Project}

object OpalClient {

  var project: Option[Project[_]] = None

  def init(p: Project[_]): Unit = {
    project = Some(p)
  }

  def getClassHierarchy: ClassHierarchy = project.get.classHierarchy

  def getClassFileForType(objectType: ObjectType): Option[ClassFile] = project.get.classFile(objectType)

  def isApplicationClass(classFile: ClassFile): Boolean = project.get.allProjectClassFiles.toSet.contains(classFile)

}
