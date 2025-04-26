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

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.scope.sootup.jimple.JimpleUpVal;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Optional;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import sootup.core.inputlocation.EagerInputLocation;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.jimple.common.constant.IntConstant;
import sootup.core.model.ClassModifier;
import sootup.core.model.SourceType;
import sootup.core.signatures.FieldSignature;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.ClassType;
import sootup.java.core.*;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

public class SootUpFrameworkScope implements FrameworkScope {

  protected final JavaView view;
  protected final CallGraph sootUpCallGraph;
  protected DataFlowScope dataflowScope;

  public SootUpFrameworkScope(
      @NonNull JavaView view,
      sootup.callgraph.@NonNull CallGraph callGraph,
      @NonNull Collection<JavaSootMethod> entryPoints,
      @NonNull DataFlowScope dataFlowScope) {
    INSTANCE = this; // FIXME! [ms] this hack is disgusting!

    this.view = view;

    this.sootUpCallGraph = new SootUpCallGraph(callGraph, entryPoints);
    this.dataflowScope = dataFlowScope;
  }

  private static SootUpFrameworkScope INSTANCE;

  public static SootUpFrameworkScope getInstance() {
    if (INSTANCE == null) {
      throw new RuntimeException("Client hasn't been initialized. Call setInstance first");
    }
    return INSTANCE;
  }

  @Override
  @NonNull
  public Val getTrueValue(Method m) {
    return new JimpleUpVal(IntConstant.getInstance(1), m);
  }

  @Override
  @NonNull
  public Val getFalseValue(Method m) {
    return new JimpleUpVal(IntConstant.getInstance(0), m);
  }

  @Override
  @NonNull
  public Stream<Method> handleStaticFieldInitializers(Val fact) {
    /*JimpleUpStaticFieldVal val = ((JimpleUpStaticFieldVal) fact);
    ClassType declaringClassType =
        ((JimpleUpField) val.getField()).getDelegate().getDeclaringClassType();

    return view.getClass(declaringClassType).get().getMethods().stream()
        .filter(sootup.core.model.SootMethod::hasBody)
        .map(JimpleUpMethod::of);*/
    throw new UnsupportedOperationException("Reimplement");
  }

  @Override
  @NonNull
  public StaticFieldVal newStaticFieldVal(Field field, Method m) {
    // return new JimpleUpStaticFieldVal((JimpleUpField) field, m);
    throw new UnsupportedOperationException("Reimplement");
  }

  @Override
  public CallGraph getCallGraph() {
    return sootUpCallGraph;
  }

  @Override
  public DataFlowScope getDataFlowScope() {
    return dataflowScope;
  }

  // ---

  public static final String CONSTRUCTOR_NAME = "<init>";
  public static final String STATIC_INITIALIZER_NAME = "<clinit>";

  public JavaView getView() {
    return view;
  }

  public JavaIdentifierFactory getIdentifierFactory() {
    return view.getIdentifierFactory();
  }

  public JavaSootClass getSootClass(JavaClassType classType) {
    Optional<JavaSootClass> sootClass = view.getClass(classType);
    if (sootClass.isPresent()) {
      return sootClass.get();
    }

    OverridingJavaClassSource phantomClassSource =
        new OverridingJavaClassSource(
            new EagerInputLocation(),
            Paths.get("/phantom-class-in-memory"),
            classType,
            null,
            Collections.emptySet(),
            null,
            Collections.emptySet(),
            Collections.emptySet(),
            NoPositionInformation.getInstance(),
            EnumSet.noneOf(ClassModifier.class),
            Collections.emptyList(),
            Collections.emptyList(),
            Collections.emptyList());
    return new PhantomClass(phantomClassSource, SourceType.Application);
  }

  public Optional<JavaSootMethod> getSootMethod(MethodSignature methodSignature) {
    Optional<JavaSootMethod> method = view.getMethod(methodSignature);
    if (method.isPresent()) {
      return method;
    }

    //   System.out.println("get" + methodSignature);

    Optional<ClassType> declaredClassOfMethod =
        view.getTypeHierarchy()
            .superClassesOf(methodSignature.getDeclClassType())
            .filter(
                type -> {
                  //            System.out.println("is it in? " + type);
                  Optional<JavaSootClass> aClass = view.getClass(type);
                  return aClass
                      .map(
                          javaSootClass -> {
                            return javaSootClass
                                .getMethod(methodSignature.getSubSignature())
                                .isPresent();
                          })
                      .orElse(false);
                })
            .findAny();

    if (declaredClassOfMethod.isEmpty()) {
      return Optional.empty();
    }
    ClassType declClassType = declaredClassOfMethod.get();

    Optional<JavaSootClass> aClass = view.getClass(declClassType);
    return aClass.flatMap(
        javaSootClass -> javaSootClass.getMethod(methodSignature.getSubSignature()));
    // throw new RuntimeException("Method not found: " + methodSignature);
  }

  public JavaSootField getSootField(FieldSignature fieldSignature) {
    Optional<JavaSootField> field = view.getField(fieldSignature);
    if (field.isPresent()) {
      return field.get();
    }
    throw new RuntimeException("Field not found: " + fieldSignature);
  }

  public static boolean isConstructor(JavaSootMethod sootMethod) {
    return sootMethod.getSignature().getName().equals(CONSTRUCTOR_NAME);
  }

  public static boolean isStaticInitializer(JavaSootMethod sootMethod) {
    return sootMethod.getSignature().getName().equals(STATIC_INITIALIZER_NAME);
  }

  /** Dummy Phantom Class representation to mimic what Soot would do */
  public static class PhantomClass extends JavaSootClass {
    public PhantomClass(JavaSootClassSource classSource, SourceType sourceType) {
      super(classSource, sourceType);
    }
  }
}
