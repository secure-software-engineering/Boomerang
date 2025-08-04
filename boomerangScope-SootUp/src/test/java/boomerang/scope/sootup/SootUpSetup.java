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
package boomerang.scope.sootup;

import boomerang.scope.test.MethodSignature;
import boomerang.scope.test.TargetClassPath;
import java.util.Optional;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

public class SootUpSetup {

  private JavaView view;

  public void setupSootUp(String targetClassName) {
    AnalysisInputLocation inputLocation =
        new JavaClassPathAnalysisInputLocation(TargetClassPath.TARGET_CLASS_PATH);
    view = new JavaView(inputLocation);

    JavaClassType classType = view.getIdentifierFactory().getClassType(targetClassName);
    Optional<JavaSootClass> classOpt = view.getClass(classType);
    if (classOpt.isEmpty()) {
      throw new RuntimeException("Could not find class " + targetClassName);
    }
  }

  public JavaSootMethod resolveMethod(MethodSignature signature) {
    sootup.core.signatures.MethodSignature methodSignature =
        view.getIdentifierFactory()
            .getMethodSignature(
                signature.getDeclaringClass(),
                signature.getMethodName(),
                signature.getReturnType(),
                signature.getParameters());

    Optional<JavaSootMethod> methodOpt = view.getMethod(methodSignature);
    if (methodOpt.isEmpty()) {
      throw new RuntimeException(
          "Could not find method "
              + signature.getMethodName()
              + " in class "
              + signature.getDeclaringClass());
    }

    return methodOpt.get();
  }

  public JavaView getJavaView() {
    return view;
  }
}
