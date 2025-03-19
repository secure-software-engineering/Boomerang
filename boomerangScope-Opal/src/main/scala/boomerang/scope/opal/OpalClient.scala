package boomerang.scope.opal

import org.opalj.br.{ClassFile, ClassHierarchy, DefinedMethod, Field, Method, MethodDescriptor, ObjectType, ReferenceType}
import org.opalj.br.analyses.{DeclaredMethods, DeclaredMethodsKey, Project}
import org.opalj.tac.{AITACode, ComputeTACAIKey, FieldRead, FieldWriteAccessStmt, TACMethodParameter}
import org.opalj.value.ValueInformation

object OpalClient {

  private var project: Option[Project[_]] = None
  private var declaredMethods: Option[DeclaredMethods] = None
  private var tacCodes: Option[Method => AITACode[TACMethodParameter, ValueInformation]] = None

  def init(p: Project[_]): Unit = {
    project = Some(p)
    declaredMethods = Some(p.get(DeclaredMethodsKey))
    tacCodes = Some(p.get(ComputeTACAIKey))
  }

  def getClassHierarchy: ClassHierarchy = project.get.classHierarchy

  def getClassFileForType(objectType: ObjectType): Option[ClassFile] = project.get.classFile(objectType)

  def isApplicationClass(classFile: ClassFile): Boolean = project.get.allProjectClassFiles.toSet.contains(classFile)

  def getTacForMethod(method: Method): AITACode[TACMethodParameter, ValueInformation] = tacCodes.get(method)

}
