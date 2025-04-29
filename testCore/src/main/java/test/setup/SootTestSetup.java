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
import boomerang.scope.soot.BoomerangPretransformer;
import boomerang.scope.soot.SootFrameworkScope;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.utils.MethodWrapper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

public class SootTestSetup implements TestSetup {

  private SootMethod testMethod;

  @Override
  public void initialize(
      String classPath,
      MethodWrapper methodWrapper,
      List<String> includedPackages,
      List<String> excludedPackages) {
    G.reset();

    Options.v().set_whole_program(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_keep_line_number(true);

    Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + classPath);

    Options.v().setPhaseOption("jb.sils", "enabled:false");
    Options.v().setPhaseOption("jb", "use-original-names:true");

    Options.v().setPhaseOption("cg.cha", "on");
    Options.v().setPhaseOption("cg.cha", "all-reachable:true");

    Options.v().set_exclude(excludedPackages);
    Options.v().set_include(includedPackages);

    SootClass sootTestCaseClass =
        Scene.v().forceResolve(methodWrapper.getDeclaringClass(), SootClass.BODIES);
    sootTestCaseClass.setApplicationClass();

    String signature =
        methodWrapper.getReturnType()
            + " "
            + methodWrapper.getMethodName()
            + "("
            + String.join(",", methodWrapper.getParameters())
            + ")";
    testMethod = sootTestCaseClass.getMethod(signature);

    if (testMethod == null) {
      throw new RuntimeException("Could not load testMethod " + signature);
    }
    Scene.v().loadNecessaryClasses();

    List<SootMethod> entryPoints = new ArrayList<>();

    for (SootMethod m : sootTestCaseClass.getMethods()) {
      if (m.isStaticInitializer()) {
        entryPoints.add(m);
      }
    }

    for (SootClass inner : Scene.v().getClasses()) {
      if (inner.getName().contains(sootTestCaseClass.getName())) {
        inner.setApplicationClass();

        for (SootMethod m : inner.getMethods()) {
          if (m.isStaticInitializer()) {
            entryPoints.add(m);
          }
        }
      }
    }

    entryPoints.add(testMethod);
    Scene.v().setEntryPoints(entryPoints);
  }

  @Override
  public Method getTestMethod() {
    return JimpleMethod.of(Scene.v(), testMethod);
  }

  @Override
  public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
    PackManager.v().getPack("cg").apply();

    BoomerangPretransformer.v().reset();
    BoomerangPretransformer.v().apply();

    return new SootFrameworkScope(
        Scene.v(), Scene.v().getCallGraph(), Scene.v().getEntryPoints(), dataFlowScope);
  }
}
