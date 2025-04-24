/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.opal

import boomerang.scope.opal.transformation.TacBodyBuilder
import java.io.File
import java.net.URI
import java.nio.file.Files
import java.nio.file.FileSystems
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import org.junit.Test
import org.opalj.br.Method
import org.opalj.br.analyses.Project
import org.opalj.log.DevNullLogger
import org.opalj.log.GlobalLogContext
import org.opalj.log.OPALLogger
import scala.collection.mutable.ArrayBuffer
import scala.jdk.CollectionConverters._

class TacBodyBuilderTest {

    @Test
    def applyTacBodyBuilderTest(): Unit = {
        OPALLogger.updateLogger(GlobalLogContext, DevNullLogger)
        val jdkFiles = loadJDKFiles()

        val project = Project(jdkFiles, Array.empty[File])
        project.allProjectClassFiles.foreach(cf => {
            cf.methods.foreach(method => {
                if (!isOnIgnoreList(method)) {
                    TacBodyBuilder(project, method)
                }
            })
        })
    }

    def isOnIgnoreList(method: Method): Boolean = {
        // No existing body -> No transformation possible
        if (method.body.isEmpty) return true

        // Consider only java.lang package classes to reduce the number of classes
        // TODO
        //  Maybe create additional tests for other packages (java.io, java.util) that run in parallel.
        //  Running them sequential would take too long
        if (!method.toJava.startsWith("java.lang.")) return true

        // Static initializers may be very complex and take some time to compute (e.g. com.sun.crypto.provider.AESCrypt)
        if (method.isStaticInitializer) return true

        // Bug in Opal causes an exception
        if (method.toJava.equals(
                "java.lang.Thread{ private static long nextThreadID() }"
            )
        ) return true
        if (method.toJava.equals(
                "java.util.concurrent.CompletableFuture$Signaller{ public boolean isReleasable() }"
            )
        ) return true

        false
    }

    def loadJDKFiles(): Array[File] = {
        val javaHome = sys.env("JAVA_HOME")
        val jmodPath = Paths.get(javaHome, "jmods", "java.base.jmod")

        val outputDir = Paths.get("extracted_classes")
        Files.createDirectories(outputDir)

        val classFiles = ArrayBuffer[File]()

        // Open the .jmod file as a zip filesystem
        val uri = URI.create(s"jar:${jmodPath.toUri}")
        val env = Map("create" -> "false").asJava

        val fs = FileSystems.newFileSystem(uri, env)
        val rootPath = fs.getPath("/classes")

        // Walk the file tree inside the jmod's /classes directory
        Files
            .walk(rootPath)
            .iterator()
            .asScala
            .filter(p => Files.isRegularFile(p) && p.toString.endsWith(".class"))
            .foreach { classPath =>
                val relativePath = rootPath.relativize(classPath)
                val targetPath = outputDir.resolve(relativePath.toString)

                Files.createDirectories(targetPath.getParent)
                Files.copy(classPath, targetPath, StandardCopyOption.REPLACE_EXISTING)

                classFiles += targetPath.toFile
            }

        fs.close()

        classFiles.toArray
    }

}
