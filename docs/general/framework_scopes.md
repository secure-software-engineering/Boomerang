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
    - SootUp: `View`
    - Opal: `Project`
- A call graph computed from the main instance
- A data-flow scope
- An optional set of entry point methods

Boomerang uses the framework scope to access the main instance, call graph and data-flow scope during the analysis.
Additionally, you may specify a set of entry point methods that define the starting points when using the [AnalysisScope](boomerang_scope.md#AnalysisScope).

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
    
    FrameworkScope scope = new SootFrameworkScope(Scene.v(), callGraph, dataFlowScope entryPoints);
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

    FrameworkScope scope = new SootUpFrameworkScope(view, callGraph, dataFlowScope, Collections.singleton(entryPoint.get()));
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
    val scope = new OpalFrameworkScope(project, callGraph, dataFlowScope, entryPoints)
    ```
