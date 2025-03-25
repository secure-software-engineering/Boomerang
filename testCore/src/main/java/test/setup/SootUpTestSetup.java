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
import boomerang.scope.sootup.BoomerangPreInterceptor;
import boomerang.scope.sootup.SootUpFrameworkScope;
import boomerang.scope.sootup.jimple.JimpleUpMethod;
import com.google.common.collect.Lists;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.jspecify.annotations.NonNull;
import sootup.SourceTypeSplittingAnalysisInputLocation;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.RapidTypeAnalysisAlgorithm;
import sootup.core.frontend.BodySource;
import sootup.core.frontend.ResolveException;
import sootup.core.graph.MutableBlockStmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.inputlocation.EagerInputLocation;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.model.ClassModifier;
import sootup.core.model.MethodModifier;
import sootup.core.model.SootClass;
import sootup.core.model.SootClassMember;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.signatures.PackageName;
import sootup.core.transform.BodyInterceptor;
import sootup.interceptors.CastAndReturnInliner;
import sootup.interceptors.LocalSplitter;
import sootup.interceptors.TypeAssigner;
import sootup.java.bytecode.frontend.inputlocation.DefaultRuntimeAnalysisInputLocation;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.OverridingJavaClassSource;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;
import sootup.jimple.frontend.JimpleStringAnalysisInputLocation;
import sootup.jimple.frontend.JimpleView;

public class SootUpTestSetup implements TestSetup {

  JavaView javaView;
  protected JavaSootMethod javaTestMethod;
  List<MethodSignature> entypointSignatures = Lists.newArrayList();

  @Override
  public void initialize(
      String classPath,
      MethodWrapper testMethod,
      List<String> includedPackages,
      List<String> excludedPackages) {

    // FIXME: missing parameters!

    String className = testMethod.getDeclaringClass();
    String customEntrypointMethodName = testMethod.getMethodName();

    /*
    Path testClassesBinRoot = Paths.get(System.getProperty("user.dir") + "/target/test-classes/");
    try {
      Files.walk(testClassesBinRoot)
          .filter(f -> f.toFile().isDirectory())
          .forEach(
              x -> {
                if (x != testClassesBinRoot) {
                  Path relativize = testClassesBinRoot.relativize(x);
                  includedPackages.add(relativize.toString().replace("/", ".") + ".*");
                }
              });
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
*/
    // configure interceptors
    List<BodyInterceptor> bodyInterceptors =
        new ArrayList<>(
            List.of(new CastAndReturnInliner(), new LocalSplitter(), new TypeAssigner()));
    //     new ArrayList<>(BytecodeBodyInterceptors.Default.getBodyInterceptors());

    // TODO: check if the interceptor needs a reset in between runs
    bodyInterceptors.add(new BoomerangPreInterceptor());

    // configure AnalysisInputLocations
    List<AnalysisInputLocation> inputLocations = new ArrayList<>();

    DefaultRuntimeAnalysisInputLocation runtimeInputLocation =
        new DefaultRuntimeAnalysisInputLocation();

//    System.out.println("incl" + includedPackages);
//    System.out.println("ex" + excludedPackages);

    // FIXME: respect included / excluded packages
    if (true) {
      inputLocations.add(runtimeInputLocation);
    } else {
      SourceTypeSplittingAnalysisInputLocation.ApplicationAnalysisInputLocation
          applicationAnalysisInputLocationRuntime =
              new SourceTypeSplittingAnalysisInputLocation.ApplicationAnalysisInputLocation(
                  runtimeInputLocation, includedPackages);

      SourceTypeSplittingAnalysisInputLocation.LibraryAnalysisInputLocation
          sourceTypeLibraryAnalysisInputLocationRuntime =
              new SourceTypeSplittingAnalysisInputLocation.LibraryAnalysisInputLocation(
                  applicationAnalysisInputLocationRuntime, excludedPackages);

      inputLocations.add(applicationAnalysisInputLocationRuntime);
      inputLocations.add(sourceTypeLibraryAnalysisInputLocationRuntime);
    }

    JavaClassPathAnalysisInputLocation classPathInputLocation =
        new JavaClassPathAnalysisInputLocation(classPath, SourceType.Application, bodyInterceptors);

    if ( true /*includedPackages.isEmpty() && excludedPackages.isEmpty() */) {
      inputLocations.add(classPathInputLocation);
    } else {
      SourceTypeSplittingAnalysisInputLocation.ApplicationAnalysisInputLocation
          applicationAnalysisInputLocation =
              new SourceTypeSplittingAnalysisInputLocation.ApplicationAnalysisInputLocation(
                  classPathInputLocation, includedPackages);

      SourceTypeSplittingAnalysisInputLocation.LibraryAnalysisInputLocation
          sourceTypeLibraryAnalysisInputLocation =
              new SourceTypeSplittingAnalysisInputLocation.LibraryAnalysisInputLocation(
                  applicationAnalysisInputLocation, excludedPackages);
      inputLocations.add(applicationAnalysisInputLocation);
      inputLocations.add(sourceTypeLibraryAnalysisInputLocation);
    }

    /*
    // before: figure out if included/excluded was intended as: && or ||
                new ScopedAnalysisInputLocation.AllowlistingScopedAnalysisInputLocation(
                    classPathInputLocation, includedPackages),
                new ScopedAnalysisInputLocation.DenylistingScopedAnalysisInputLocation(
                    classPathInputLocation, excludedPackages))
     */

    Collection<JavaSootClass> classes;

    if (customEntrypointMethodName == null) {
      System.out.println(inputLocations);
      javaView = new JavaView(inputLocations);
      System.out.println(inputLocations.get(0).getSourceType());
      inputLocations
          .get(0)
          .getClassSources(javaView)
          .forEach(cs -> System.out.println(cs.getClassType()));
      System.out.println("-----");
      System.out.println(inputLocations.get(1).getSourceType());
      inputLocations
          .get(1)
          .getClassSources(javaView)
          .forEach(cs -> System.out.println(cs.getClassType()));
      System.out.println("-----");
      System.out.println(inputLocations.get(2).getSourceType());
      inputLocations
          .get(2)
          .getClassSources(javaView)
          .forEach(cs -> System.out.println(cs.getClassType()));
      System.out.println("-----");
      // System.out.println(inputLocations.get(3).getSourceType());
      //     inputLocations.get(3).getClassSources(javaView).forEach( cs ->
      // System.out.println(cs.getClassType()));

      classes = javaView.getClasses().collect(Collectors.toList());
      // collect entrypoints
      for (JavaSootClass sootClass : classes) {
        String scStr = sootClass.toString();
        if (scStr.equals(className) || (scStr.contains(className + "$"))) {
          sootClass.getMethods().stream()
              .map(SootClassMember::getSignature)
              .forEach(entypointSignatures::add);
        }
      }

    } else {

      DummyClassBuilder dummyClassBuilder = new DummyClassBuilder(className, customEntrypointMethodName);
      inputLocations.add(dummyClassBuilder.getInputLocation());

      javaView = new JavaView(inputLocations);
      MethodSignature callTgtSig = dummyClassBuilder.getCallTgtSig();
      javaTestMethod = javaView.getMethod(callTgtSig).get();

      MethodSignature dummyEntrypoint = dummyClassBuilder.getMethod().getSignature();
      assert javaView.getMethod(dummyEntrypoint).isPresent();
      entypointSignatures.add(dummyEntrypoint);
    }

    /*
    System.out.println(
        "classes: "
            + result.classes.size()); // soot has 1911 for boomerang.guided.DemandDrivenGuidedAnalysisTest

    result.classes.stream()
        .sorted(Comparator.comparing(sootup.core.model.SootClass::toString))
        .forEach(System.out::println);
    */


  }

  @Override
  public Method getTestMethod() {
    return JimpleUpMethod.of(javaTestMethod);
  }

  @Override
  public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {

    // initialize CallGraphAlgorithm
    // TODO: use spark when available
    CallGraphAlgorithm cga = new RapidTypeAnalysisAlgorithm(javaView);
    CallGraph cg = cga.initialize(entypointSignatures);

    Collection<JavaSootMethod> entryPoints = new HashSet<>();
    for (MethodSignature signature : entypointSignatures) {
      Optional<JavaSootMethod> sootMethod = javaView.getMethod(signature);
      sootMethod.ifPresent(entryPoints::add);
    }

    return new SootUpFrameworkScope(javaView, cg, entryPoints, dataFlowScope);
  }

  private static class DummyClassBuilder {
    protected final AnalysisInputLocation inputLocation;
    protected final JavaSootMethod method;

    public MethodSignature getCallTgtSig() {
      return callTgtSig;
    }

    protected final MethodSignature callTgtSig;


    public DummyClassBuilder(String className, String customEntrypointMethodName) {

      // build dummy entrypoint class
      String callTgtSignatureStr = "<"+className
              + ": void "
              + customEntrypointMethodName
              + "(java.lang.String[])>";

      String jimpleClassStr =
              "class dummyClass\n"
                      + "{\n"
                      + "    public static void main(java.lang.String[])\n"
                      + "    {\n"
                      + "        "
                      + className
                      + " dummyObj;\n"
                      + "        java.lang.String[] l0;\n"
                      + "        l0 := @parameter0: java.lang.String[];\n"
                      + "        dummyObj = new "
                      + className
                      + ";\n"
                      + "        virtualinvoke dummyObj."
                      + callTgtSignatureStr + "(l0);\n"
                      + "        return;\n"
                      + "    }\n"
                      + "}";

      JavaClassType dummyClassType = new JavaClassType("dummyClass", new PackageName(""));
      JimpleStringAnalysisInputLocation jimpleStringAnalysisInputLocation =
              new JimpleStringAnalysisInputLocation(
                      jimpleClassStr, SourceType.Application, Collections.emptyList());
      JimpleView jimpleView = new JimpleView(jimpleStringAnalysisInputLocation);
      Optional<SootClass> aClass = jimpleView.getClass(dummyClassType);

      MethodSignature methodSignature =
              jimpleView
                      .getIdentifierFactory()
                      .parseMethodSignature("<dummyClass: void main(java.lang.String[])>");

      callTgtSig =
              jimpleView
                      .getIdentifierFactory()
                      .parseMethodSignature(callTgtSignatureStr);

      assert aClass.isPresent();
      SootClass sootClass = aClass.get();

      BodySource bodySource =
              new BodySource() {
                @Override
                public sootup.core.model.@NonNull Body resolveBody(
                        @NonNull Iterable<MethodModifier> iterable) throws ResolveException {
                  return sootup.core.model.Body.builder(
                                  new MutableBlockStmtGraph(
                                          sootClass
                                                  .getMethod(methodSignature.getSubSignature())
                                                  .get()
                                                  .getBody()
                                                  .getStmtGraph()))
                          .setMethodSignature(methodSignature)
                          .build();
                }

                @Override
                public Object resolveAnnotationsDefaultValue() {
                  return null;
                }

                @NonNull
                @Override
                public MethodSignature getSignature() {
                  return methodSignature;
                }
              };
      method =
              new JavaSootMethod(
                      bodySource,
                      methodSignature,
                      EnumSet.of(MethodModifier.PUBLIC, MethodModifier.STATIC),
                      Collections.emptySet(),
                      Collections.emptyList(),
                      NoPositionInformation.getInstance());

      OverridingJavaClassSource dummyClassSource =
              new OverridingJavaClassSource(
                      new EagerInputLocation(),
                      Paths.get("/in-memory"),
                      dummyClassType,
                      null,
                      Collections.emptySet(),
                      null,
                      Collections.emptySet(),
                      Collections.singleton(method),
                      NoPositionInformation.getInstance(),
                      EnumSet.of(ClassModifier.PUBLIC),
                      Collections.emptyList(),
                      Collections.emptyList(),
                      Collections.emptyList());

      inputLocation = new EagerInputLocation(Collections.singletonMap(dummyClassType, dummyClassSource), SourceType.Application);
    }

    private JavaSootMethod getMethod() {
      return method;
    }

    public AnalysisInputLocation getInputLocation() {
      return inputLocation;
    }

  }
}
