# Framework Scopes

We provide an implementation of the BoomerangScope for the static analysis frameworks [Soot](https://github.com/soot-oss/soot), [SootUp](https://github.com/soot-oss/sootup) and [Opal](https://github.com/opalj/opal).
Depending on the framework that you plan to use, include the following dependencies in your project (replace `x.y.z` with the most recent version):

=== "Soot"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-Soot</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

=== "SootUp"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-SootUp</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

=== "Opal"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-Opal</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

## Setting up a Framework Scope

Each framework scope consists of the following objects:

- The static analysis framework's main instance:
    - Soot: `Scene`
    - SootUp: `JavaView`
    - Opal: `Project`
- A call graph computed from the main instance
- A data-flow scope
- A set of entry point methods

Boomerang uses the framework scope to access the main instance, call graph and data-flow scope during the analysis.
Additionally, you may specify a set of entry point methods that define the starting points in the call graph when using the [AnalysisScope](boomerang_scope.md#analysisscope).

## Transformations

To perform the analysis correctly, Boomerang requires an initial transformation step before the actual analysis.
This step consists of initializing fields that are not initialized in the original program.
If a field is not initialized, Java assigns a default value (e.g. `null` for objects) that is not part of the actual program.
Hence, Boomerang is not able to find corresponding allocation sites s.t. an explicit assignment with corresponding default values is required.

Additionally, Boomerang provides a transformation that extracts constant parameters and creates new assignments. For example, the following statement

```java
queryFor(10);
```

may be transformed to

```java
varReplacer = 10;
queryFor(varReplacer);
```

The intention of this step is to transform the intermediate code representation into a Boomerang compatible form.
By definition, Boomerang can only solve for query variables (not constants).
This transformation step is optional (enabled by default) and corresponding local variables can be identified by the *varReplacer* name.

The following snippets show the transformations for Soot and SootUp. Boomerang applies the transformation in Opal automatically, that is, there is no transformer/interceptor.

=== "Soot"
    ```java
    // Set to false if constants should not be extracted
    BoomerangPreTransformer.TRANSFORM_CONSTANTS = true;

    // Call this after the call graph construction
    BoomerangPreTransformer.v().reset();
    BoomerangPreTransformer.v().apply();
    ```

=== "SootUp"
    ```java
    // Set to false if constants should not be extracted
    BoomerangPreInterceptor interceptor = new BoomerangPreInterceptor(true);

    // Add the interceptor to the other interceptors when creating a View
    List<BodyInterceptor> interceptors = List.of(<other interceptors>, interceptor);
    ```

## Example Setup

The following snippets show an example of the instantiation of the framework scope for each static analysis framework.
Thereby, we construct the call graphs using the CHA algorithm, and we use a data-flow scope that excludes all methods from classes that are not loaded (*phantom* classes).

=== "Soot"
    ```java
    // Soot setup
    G.reset();
    Options.v().set_whole_program(true);
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().set_keep_line_number(true);
    Options.v().set_soot_classpath("VIRTUAL_FS_FOR_JDK" + File.pathSeparator + "path/to/app");
    Options.v().setPhaseOption("jb.sils", "enabled:false");
    Options.v().setPhaseOption("jb", "use-original-names:true");

    Scene.v().loadNecassaryClasses();

    // Compute call graph
    Options.v().setPhaseOption("cg.cha", "on");
    PackManager.v().getPack("cg").apply();

    // Do not forget the PreTransformer
    BoomerangPretransformer.v().reset();
    BoomerangPretransformer.v().apply();
    
    // Framework scope setup
    DataFlowScope dataFlowScope = DataFlowScope.EXCLUDE_PHANTOM_CLASSES;
    CallGraph callGraph = Scene.v().getCallGraph();
    Collection<SootMethod> entryPoints = EntryPoints.v().mainsOfApplicationClasses();
    
    FrameworkScope scope = new SootFrameworkScope(Scene.v(), callGraph, entryPoints, dataFlowScope);

    // Read the Boomerang scope call graph
    boomerang.scope.CallGraph cg = scope.getCallGraph();
    ```

=== "SootUp"
    ```java
    // SootUp setup (Do not forget the PreInterceptor)
    AnalysisInputLocation inputLocation = new JavaClassPathAnalysisInputLocation("path/to/project", SourceType.Application, List.of(new BoomerangPreInterceptor()));
    JavaView view = new JavaView(inputLocation);

    // Construct call graph
    ClassHierarchyAnalysisAlgorithm cha = new ClassHierarchyAnalysisAlgorithm(view);
    MethodSignature mainMethod = cha.findMainMethod();
    CallGraph callGraph = cha.initialize(List.of(mainMethod));

    // Framework scope setup
    DataFlowScope dataFlowScope = DataFlowScope.EXCLUDE_PHANTOM_CLASSES;
    Optional<JavaSootMethod> entryPoint = view.getMethod(mainMethod);
    if (entryPoint.isEmpty()) {
        throw new RuntimeException("No main method present");
    }

    FrameworkScope scope = new SootUpFrameworkScope(view, callGraph, Collections.singleton(entryPoint.get()), dataFlowScope);
    
    // Read the Boomerang scope call graph
    boomerang.scope.CallGraph cg = scope.getCallGraph();
    ```

=== "Opal"
    ```scala
    // Opal setup
    val project = Project(new File("path/to/project"))

    // Compute call graph
    val callGraph = project.get(CHACallGraphKey)

    // Framework scope setup
    val dataFlowScope = DataFlowScope.EXCLUDE_PHANTOM_CLASSES
    val entryPoints = project.allMethodsWithBody.toSet
    val scope = new OpalFrameworkScope(project, callGraph, entryPoints, dataFlowScope)

    // Read the Boomerang scope call graph
    val opalCallGraph = scope.getCallGraph
    ```
