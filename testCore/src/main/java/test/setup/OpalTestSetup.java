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
import boomerang.scope.opal.OpalFrameworkScope;
import boomerang.scope.opal.tac.OpalMethod;
import boomerang.utils.MethodWrapper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigValueFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.opalj.br.BooleanType$;
import org.opalj.br.ByteType$;
import org.opalj.br.CharType$;
import org.opalj.br.ClassFile;
import org.opalj.br.DoubleType$;
import org.opalj.br.FieldType;
import org.opalj.br.FloatType$;
import org.opalj.br.IntegerType$;
import org.opalj.br.JVMMethod;
import org.opalj.br.LongType$;
import org.opalj.br.MethodDescriptor$;
import org.opalj.br.ObjectType;
import org.opalj.br.ReturnType;
import org.opalj.br.ShortType$;
import org.opalj.br.Type;
import org.opalj.br.VoidType$;
import org.opalj.br.analyses.Project;
import org.opalj.br.analyses.Project$;
import org.opalj.br.analyses.cg.InitialEntryPointsKey;
import org.opalj.log.DevNullLogger$;
import org.opalj.log.GlobalLogContext$;
import org.opalj.log.OPALLogger;
import org.opalj.tac.cg.CHACallGraphKey$;
import org.opalj.tac.cg.CallGraph;
import scala.Option;
import scala.collection.immutable.ArraySeq;
import scala.jdk.javaapi.CollectionConverters;
import scala.reflect.ClassTag$;

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

    File[] classpathFiles =
        loadClassPathFiles(classPath, methodWrapper.getDeclaringClass(), excludedPackages);
    File[] includeFiles = loadJDKFiles(includedPackages);
    File[] classFiles =
        Stream.concat(Arrays.stream(classpathFiles), Arrays.stream(includeFiles))
            .toArray(File[]::new);
    project = Project.apply(classFiles, new File[0]); // , package$.MODULE$.RTJar());

    // Load the class that contains the test method
    Option<ClassFile> testClass =
        project.classFile(ObjectType.apply(methodWrapper.getDeclaringClass().replace(".", "/")));
    if (testClass.isEmpty()) {
      throw new RuntimeException("Could not find class " + methodWrapper.getDeclaringClass());
    }

    // To resolve the test method, we use Opal's MethodDescriptor that is identified by the
    // parameter fields and the return type
    List<FieldType> parameterFields =
        methodWrapper.getParameters().stream()
            .map(p -> FieldType.apply(convertType(p)))
            .collect(Collectors.toList());
    Type returnType = ReturnType.apply(convertType(methodWrapper.getReturnType()));

    ArraySeq<FieldType> convertedParamFields =
        ArraySeq.from(
            CollectionConverters.asScala(parameterFields),
            ClassTag$.MODULE$.apply(FieldType.class));

    // Search the test method in the test class
    Option<org.opalj.br.Method> method =
        testClass
            .get()
            .findMethod(
                methodWrapper.getMethodName(),
                MethodDescriptor$.MODULE$.apply(convertedParamFields, returnType));
    if (method.isEmpty()) {
      throw new RuntimeException(
          "Could not find method "
              + methodWrapper.getMethodName()
              + " in class "
              + methodWrapper.getDeclaringClass());
    }

    // Update the project's config to set the test method as the (single) entry point
    // See
    // https://github.com/opalj/opal/blob/ff01c1c9e696946a88b090a52881a41445cf07f1/DEVELOPING_OPAL/tools/src/main/scala/org/opalj/support/info/CallGraph.scala#L406
    Config config = project.config();

    String key = InitialEntryPointsKey.ConfigKeyPrefix() + "entryPoints";
    List<Object> currentValues = config.getList(key).unwrapped();

    Map<String, String> configValue = new HashMap<>();
    configValue.put(
        "declaringClass", method.get().classFile().thisType().toJava().replace(".", "/"));
    configValue.put("name", method.get().name());

    currentValues.add(ConfigValueFactory.fromMap(configValue));
    config = config.withValue(key, ConfigValueFactory.fromIterable(currentValues));
    config =
        config.withValue(
            InitialEntryPointsKey.ConfigKeyPrefix() + "analysis",
            ConfigValueFactory.fromAnyRef(
                "org.opalj.br.analyses.cg.ConfigurationEntryPointsFinder"));
    project = Project$.MODULE$.recreate(project, config, true);

    testMethod = method.get();
  }

  @Override
  public Method getTestMethod() {
    return OpalMethod.apply(testMethod, project);
  }

  @Override
  public FrameworkScope createFrameworkScope(DataFlowScope dataFlowScope) {
    CallGraph callGraph = project.get(CHACallGraphKey$.MODULE$);

    // Add the static initializers of the test class target and its subclasses to the entry points
    scala.collection.immutable.Set<org.opalj.br.Method> allClinit =
        project.allMethodsWithBody().filter(JVMMethod::isStaticInitializer).toSet();
    scala.collection.immutable.Set<org.opalj.br.Method> clinitInTarget =
        allClinit.filter(m -> m.classFile().fqn().startsWith(testMethod.classFile().fqn())).toSet();
    scala.collection.immutable.Set<org.opalj.br.Method> entryPoints =
        clinitInTarget.$plus(testMethod);

    return new OpalFrameworkScope(project, callGraph, entryPoints, dataFlowScope);
  }

  private File[] loadClassPathFiles(
      String classpath, String testClassName, List<String> excludeList) {
    Path path = Path.of(classpath);

    try (Stream<Path> stream = Files.walk(path)) {
      Stream<File> classPathFiles = stream.filter(Files::isRegularFile).map(Path::toFile);

      // Filter for excluded classes. Additionally, exclude all classes from different packages
      // because CHA in Opal would add edges to methods from other classes
      String packageName = testClassName.substring(0, testClassName.lastIndexOf("."));
      return classPathFiles
          .filter(c -> !isExcludedFile(c, classpath, packageName, excludeList))
          .toArray(File[]::new);
    } catch (IOException e) {
      throw new RuntimeException("Could not read classpath: " + e.getMessage());
    }
  }

  private boolean isExcludedFile(
      File file, String classpath, String packageName, List<String> excludeList) {
    // Remove the classpath and the .class ending
    String path = file.getPath().replace("/", ".").replace("\\", ".");
    String formattedClassPath = classpath.replace("/", ".").replace("\\", ".");
    String formattedPath = path.replace(formattedClassPath, "").replace(".class", "").substring(1);

    return !formattedPath.startsWith(packageName) || excludeList.contains(formattedPath);
  }

  private File[] loadJDKFiles(List<String> includeList) {
    Collection<File> result = new ArrayList<>();

    try (FileSystem fs =
        FileSystems.newFileSystem(URI.create("jrt:/"), java.util.Collections.emptyMap())) {
      for (String className : includeList) {
        String pathInJrt = "/modules/java.base/" + className.replace('.', '/') + ".class";
        Path jrtPath = fs.getPath(pathInJrt);

        // Copy to a temp file
        Path tempFile = Files.createTempFile(className.replace('.', '_'), ".class");
        try (InputStream in = Files.newInputStream(jrtPath);
            OutputStream out = Files.newOutputStream(tempFile)) {
          in.transferTo(out);
        }

        result.add(tempFile.toFile());
      }
    } catch (IOException e) {
      throw new RuntimeException("Could not read classes from JDK: " + e.getMessage());
    }

    return result.toArray(new File[0]);
  }

  private String convertType(String type) {
    if (type.equals("void")) return VoidType$.MODULE$.toJVMTypeName();
    if (type.equals("byte")) return ByteType$.MODULE$.toJVMTypeName();
    if (type.equals("char")) return CharType$.MODULE$.toJVMTypeName();
    if (type.equals("double")) return DoubleType$.MODULE$.toJVMTypeName();
    if (type.equals("float")) return FloatType$.MODULE$.toJVMTypeName();
    if (type.equals("int")) return IntegerType$.MODULE$.toJVMTypeName();
    if (type.equals("long")) return LongType$.MODULE$.toJVMTypeName();
    if (type.equals("short")) return ShortType$.MODULE$.toJVMTypeName();
    if (type.equals("boolean")) return BooleanType$.MODULE$.toJVMTypeName();
    // TODO Consider all array types (not just ref types)
    if (type.endsWith("[]")) {
      return new StringBuilder(type.replace(".", "/").replace("[]", ""))
          .insert(0, "[L")
          .append(";")
          .toString();
    }

    // Convert class types: java.lang.Object => Ljava.lang.Object;
    return new StringBuilder(type.replace(".", "/")).insert(0, "L").append(";").toString();
  }
}
