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
package test.setup;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.opal.OpalFrameworkScope;
import boomerang.scope.opal.tac.OpalMethod;
import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Set;

import org.opalj.ai.AIResult;
import org.opalj.ai.AIResultBuilder;
import org.opalj.ai.BaseAI;
import org.opalj.ai.BaseAI$;
import org.opalj.ai.domain.l0.PrimitiveTACAIDomain;
import org.opalj.ai.fpcf.properties.AIDomainFactoryKey;
import org.opalj.ai.fpcf.properties.AIDomainFactoryKey$;
import org.opalj.br.ClassFile;
import org.opalj.br.MethodDescriptor$;
import org.opalj.br.ObjectType;
import org.opalj.br.analyses.Project;
import org.opalj.log.DevNullLogger$;
import org.opalj.log.GlobalLogContext$;
import org.opalj.log.OPALLogger;
import org.opalj.tac.ComputeTACAIKey;
import org.opalj.tac.LazyDetachedTACAIKey;
import org.opalj.tac.TACAI;
import org.opalj.tac.cg.CHACallGraphKey$;
import org.opalj.tac.cg.CallGraph;
import scala.Option;
import scala.jdk.javaapi.CollectionConverters;

public class OpalTestSetup implements TestSetup {

  private Project<URL> project;
  private org.opalj.br.Method testMethod;

  @Override
  public void initialize(
      String classPath,
      MethodWrapper methodWrapper,
      List<String> includedPackages,
      List<String> excludedPackages) {
    OPALLogger.updateLogger(GlobalLogContext$.MODULE$, DevNullLogger$.MODULE$);

    project = Project.apply(new File(classPath));

    Option<ClassFile> testClass =
        project.classFile(ObjectType.apply(methodWrapper.getDeclaringClass().replace(".", "/")));
    if (testClass.isEmpty()) {
      throw new RuntimeException("Could not find class " + methodWrapper.getDeclaringClass());
    }

    // Opal resolves 'void' with 'Void' TODO Add parameters
    String signature =
        "()"
            + ((methodWrapper.getReturnType().equals(MethodWrapper.VOID))
                ? "Void"
                : methodWrapper.getReturnType());
    Option<org.opalj.br.Method> method =
        testClass
            .get()
            .findMethod(methodWrapper.getMethodName(), MethodDescriptor$.MODULE$.apply(signature));
    if (method.isEmpty()) {
      throw new RuntimeException(
          "Could not find method "
              + methodWrapper.getMethodName()
              + " in class "
              + methodWrapper.getDeclaringClass());
    }

    testMethod = method.get();
  }

  @Override
  public Method getTestMethod() {
    return OpalMethod.apply(testMethod);
  }

  @Override
  public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
    // TODO Shrink to application and included classes only
    CallGraph callGraph = project.get(CHACallGraphKey$.MODULE$);

    return new OpalFrameworkScope(
        project,
        callGraph,
        CollectionConverters.asScala(Set.of(testMethod)).toSet(),
        dataFlowScope);
  }
}
