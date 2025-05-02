/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.setup;

import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.sootup.BoomerangPreInterceptor;
import boomerang.scope.sootup.SootUpFrameworkScope;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import boomerang.utils.MethodWrapper;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SootClassMember;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

public class SootUpTestSetup implements TestSetup {

  private JavaView view;
  private JavaSootMethod testMethod;

  @Override
  public void initialize(
      String classPath,
      MethodWrapper testMethodWrapper,
      List<String> includedPackages,
      List<String> excludedPackages) {

    // TODO
    //  - Add required interceptors
    //  - Check if interceptors need a reset in between runs

    List<BodyInterceptor> interceptors = List.of(new BoomerangPreInterceptor());

    Path testClassPath =
        TestSetupUtils.loadTestClasses(
            classPath, testMethodWrapper.getDeclaringClass(), excludedPackages);
    Path jdkClassPath = TestSetupUtils.loadJDKFiles(includedPackages);

    // Create two input locations to distinguish between test classes and jdk classes
    AnalysisInputLocation testClassInput =
        new JavaClassPathAnalysisInputLocation(
            testClassPath.toString(), SourceType.Application, interceptors);
    AnalysisInputLocation jdkClassInput =
        new JavaClassPathAnalysisInputLocation(
            jdkClassPath.toString(), SourceType.Library, interceptors);
    view = new JavaView(List.of(testClassInput, jdkClassInput));

    // Load the test class
    ClassType classType =
        view.getIdentifierFactory().getClassType(testMethodWrapper.getDeclaringClass());
    Optional<JavaSootClass> testClass = view.getClass(classType);
    if (testClass.isEmpty()) {
      throw new RuntimeException(
          "Could not load test class " + testMethodWrapper.getDeclaringClass());
    }

    // Load the test method
    MethodSignature testMethodSignature =
        view.getIdentifierFactory()
            .getMethodSignature(
                testMethodWrapper.getDeclaringClass(),
                testMethodWrapper.getMethodName(),
                testMethodWrapper.getReturnType(),
                testMethodWrapper.getParameters());
    Optional<JavaSootMethod> testMethodOpt =
        testClass.get().getMethod(testMethodSignature.getSubSignature());
    if (testMethodOpt.isEmpty()) {
      throw new RuntimeException(
          "Could not find method "
              + testMethodWrapper.getMethodName()
              + " in class "
              + testMethodWrapper.getDeclaringClass());
    }

    testMethod = testMethodOpt.get();
  }

  @Override
  public Method getTestMethod() {
    return JimpleUpMethod.of(testMethod, view);
  }

  @Override
  public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
    Collection<JavaSootMethod> entryPoints = new ArrayList<>();
    entryPoints.add(testMethod);

    // Add all static initializers to the entry points
    view.getClasses()
        .forEach(
            c -> {
              for (JavaSootMethod method : c.getMethods()) {
                if (!method.hasBody()) {
                  continue;
                }

                if (!method.getName().equals(SootUpFrameworkScope.STATIC_INITIALIZER_NAME)) {
                  continue;
                }

                if (method
                    .getDeclaringClassType()
                    .getFullyQualifiedName()
                    .startsWith(testMethod.getDeclaringClassType().getFullyQualifiedName())) {
                  entryPoints.add(method);
                }
              }
            });

    CallGraphAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);
    CallGraph callGraph =
        cha.initialize(
            entryPoints.stream().map(SootClassMember::getSignature).collect(Collectors.toList()));

    return new SootUpFrameworkScope(view, callGraph, entryPoints, dataFlowScope);
  }

  @Override
  public void cleanUp() {
    TestSetupUtils.deleteDirectory(TestSetupUtils.APP_CLASSES);
    TestSetupUtils.deleteDirectory(TestSetupUtils.JDK_CLASSES);
  }
}
