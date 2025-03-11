package boomerang.scope

import boomerang.scope.opal.{OpalCallGraph, OpalClient}
import org.opalj.br.analyses.Project
import org.opalj.br.analyses.cg.InitialEntryPointsKey
import org.opalj.log.{DevNullLogger, GlobalLogContext, OPALLogger}
import org.opalj.tac.cg.CHACallGraphKey

import java.io.File

object Main {

  def main(args: Array[String]): Unit = {
    OPALLogger.updateLogger(GlobalLogContext, DevNullLogger)

    val project = Project(new File("C:\\Users\\Sven\\Documents\\CogniCrypt\\Opal\\Parameter\\Parameter.jar"))
    OpalClient.init(project)

    val callGraph = project.get(CHACallGraphKey)
    val entryPoints = project.get(InitialEntryPointsKey)
    val opalCallGraph = new OpalCallGraph(callGraph, entryPoints.toSet)

    opalCallGraph.getReachableMethods.forEach(method => {
      if (method.isDefined) {
        print(method.getParameterLocals)
      }
    })

    /*var config = project.config

    val key = s"${InitialEntryPointsKey.ConfigKeyPrefix}entryPoints"
    println("InitialEntryPoints " + key)
    val currentValues = project.config.getList(key).unwrapped()
    val allMethods = project.allMethodsWithBody

    allMethods.foreach(method => {
      val configValue = new java.util.HashMap[String, String]
      configValue.put("declaringClass", method.classFile.thisType.toJava)
      configValue.put("name", method.name)

      currentValues.add(ConfigValueFactory.fromMap(configValue))
      config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues))
    })

    config = config.withValue(
      s"${InitialEntryPointsKey.ConfigKeyPrefix}analysis",
      ConfigValueFactory.fromAnyRef("org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder")
    )

    val newProject = Project.recreate(project, config)
    val callGraph = newProject.get(CHACallGraphKey)
    val entryPoints = newProject.get(InitialEntryPointsKey)
    println("Entry Points: " + entryPoints.size)

    OpalClient.init(newProject)

    val opalCallGraph = new OpalCallGraph(callGraph, entryPoints.toSet)
    opalCallGraph.getReachableMethods.forEach(method => {
      if (method.isInstanceOf[OpalMethod]) {
        val locals = method.getLocals
        println("Locals in " + method + ": " + locals)
      }

      method.getStatements.forEach(statement => {

      })
    })*/
  }
}
