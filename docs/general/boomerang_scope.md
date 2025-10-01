# Boomerang Scope

Boomerang defines its own scope that is not related to any static analysis framework.
The scope consists of a set of interfaces and classes that specify relevant information required by Boomerang to perform its analyses. 
Currently, we provide a scope implementations for the static analysis frameworks [Soot](https://github.com/soot-oss/soot), [SootUp](https://github.com/soot-oss/sootup) and [Opal](https://github.com/opalj/opal) (see the [FrameworkScopes](framework_scopes.md)).
The scopes contain implementations for all relevant interfaces and objects such that Boomerang can be used with those frameworks without the need of additional work.

## Dealing with Framework Objects

The Boomerang scope is designed to be as similar as possible to the analysis frameworks.
That is, for most Boomerang scope object there is a corresponding object in the analysis framework that is used to implement the required interface methods.
For example, the Boomerang scope has an abstract class `Method` that expects implementations to override certain methods:

```java
public abstract class Method {
    
    public abstract boolean isStaticInitializer();
}
```

The concrete scope implementations extend this class by delegating the corresponding objects and using them to override the required methods.
For example, for the class `Method`, we have the following (shortened) implementations:

=== "Soot"
    ```java
    public class JimpleMethod extends Method {
        // SootMethod is the corresponding method object in Soot
        private final SootMethod delegate;

        @Override
        public boolean isStaticInitializer() {
            return delegate.isStaticInitializer();
        }
    }
    ```

=== "SootUp"
    ```java
    public class JimpleUpMethod extends Method {
        // JavaSootMethod is the corresponding method object in SootUp
        private final JavaSootMethod delegate;

        @Override
        public boolean isStaticInitializer() {
            return delegate.isStaticInitializer();
        }
    ```

=== "Opal"
    ```scala
    // The method object from the br package is the corresponding object in Opal
    class OpalMethod(val delegate: org.opalj.br.Method) extends Method {
        
        override def isStaticInitializer: Boolean = delegate.isStaticInitializer
    }
    ```

With this setup, one can easily instantiate scope objects from the analysis framework objects and access delegated objects.
The objects from each framework scope can be identified by their name:

- The scope objects for Soot are denoted with the prefix **Jimple** (e.g. `JimpleMethod`, `JimpleStatement` etc.)
- The scope objects for SootUp are denoted with the prefix **JimpleUp** (e.g. `JimpleUpMethod`, `JimpleUpStatement` etc.)
- The scope objects for Opal are denoted with the prefix **Opal** (e.g. `OpalMethod`, `OpalStatement` etc.)

To simplify the process, we provide a `ScopeConverter` for each framework scope.
These utility classes have basic methods to construct scope objects and extract delegated objects.
This concept may be relevant when working with Boomerang's results because Boomerang returns the general Boomerang scope object.
For example, we can work with a `Method` and a `Statement` as follows:

=== "Soot"
    ```java
    // Assumption: 'stmt' is a statement in 'method'
    public void scopeObjects(Scene scene, SootMethod sootMethod, Stmt stmt) {
        // Create a method and statement from the Boomerang scope for Soot
        Method jimpleMethod = SootScopeConverter.createJimpleMethod(sootMethod, scene);
        Statement jimpleStatement = SootScopeConverter.createJimpleStatement(stmt, jimpleMethod);

        // Extract the delegated objects
        SootMethod extractedSootMethod = SootScopeConverter.extractSootMethod(jimpleMethod);
        Stmt extractedStmt = SootScopeConverter.extractSootStatement(jimpleStatement);
    }
    ```

=== "SootUp"
    ```java
    // Assumption: 'stmt' is a statement in 'method'
    public void scopeObjects(JavaView view, JavaSootMethod sootMethod, Stmt stmt) {
        // Create a method and statement from the Boomerang scope for SootUp
        Method jimpleUpMethod = SootUpScopeConverter.createJimpleUpMethod(sootMethod, scene);
        Statement jimpleUpStatement = SootUpScopeConverter.createJimpleUpStatement(stmt, jimpleUpMethod);

        // Extract the delegated objects
        JavaSootMethod extractedSootUpMethod = SootUpScopeConverter.extractSootUpMethod(jimpleUpMethod);
        Stmt extractedStmt = SootUpScopeConverter.extractSootUpStatement(jimpleUpStatement);
    }
    ```

=== "Opal"
    ```scala
    // Assumption: 'stmt' is a statement in 'method'
    def scopeObjects(project: Project[_], method: Method, stmt: Stmt[TacLocal]): Unit = {
        // Create a method and statement from the Boomerang scope for Opal
        val opalMethod = OpalScopeConverter.createOpalMethod(method, project)
        val opalStatement = OpalScopeConverter.createOpalStatement(stmt, opalMethod)

        // Extract the delegated objects
        val extractedMethod = OpalScopeConverter.extractOpalMethod(opalMethod);
        val extractedStmt = OpalScopeConverter.extractOpalStatement(opalStatement);
    }
    ```

## CallGraph

The Boomerang scope contains its own call graph representation that is used to compute data-flows during the analysis.
Each framework scope provides a parser that transforms a generated call graph into the corresponding Boomerang scope representation that is applied when instantiating a [FrameworkScope](framework_scopes.md).
The corresponding call graphs are accessible from the scope instances as follows:

=== "Soot"
    ```java
    SootFrameworkScope scope = new SootFrameworkScope(...);
    SootCallGraph callGraph = scope.getCallGraph();
    System.out.println("CallGraph has " + callGraph.size() + " edges");
    ```

=== "SootUp"
    ```java
    SootUpFrameworkScope scope = new SootUpFrameworkScope(...);
    SootUpCallGraph callGraph = scope.getCallGraph();
    System.out.println("CallGraph has " + callGraph.size() + " edges");
    ```

=== "Opal"
    ```scala
    val scope = new OpalFrameworkScope(...)
    val callGraph = scope.getCallGraph
    println(s"CallGraph has ${callGraph.size} edges");
    ```

## DataFlowScope

The data-flow scope determines the program's scope that is analyzed.
By default, Boomerang computes data-flows along the complete reachable program.
However, in many scenarios, only a subset of the target program is from interest during the analysis.
For example, we are only interested in data-flow paths that belong to the application.
To this end, the data-flow scope allows the exclusion of methods to reduce the data-flows.

A `DataFlowScope` defines two methods `isExcluded(...)` that evaluate whether a method should be excluded from the analysis.
Boomerang calls these methods at each call site and when entering a new method.
If the methods evaluate to `true`, Boomerang skips the methods and steps over corresponding call site.
For example, we can define a `DataFlowScope` that excludes non-application classes and methods with the name `callSite` as follows:

```java
public class ExtendedDataFlowScope implements DataFlowScope {
    
    @Override
    public boolean isExcluded(Method method) {
        // Exclude methods from non-application classes
        if (!method.getDeclaringClass().isApplicationClass()) return true;
        // Exclude methods with the name 'callSite'
        if (method.getName().equals("callSite")) return true;
        
        // All other methods should be analyzed
        return false;
    }
    
    @Override
    public boolean isExcluded(DeclaredMethod declaredMethod) {
        // Exclude call sites from non-application classes
        if (!method.getDeclaringClass().isApplicationClass()) return true;
        // Exclude call sites with the name 'callSite'
        if (method.getName().equals("callSite")) return true;

        // All other call sites should be analyzed
        return false;
    }
}
```

// TODO Example

!!! Important
    You should always make sure that potential allocation sites are considered when excluding call sites.
    In the example above, we exclude the call to `System.getProperty` because the class `java.lang.System` is not an application class.
    At this point, the data-flow stops because Boomerang cannot compute the returned value.
    To deal with such cases, we provide a solution when defining the [AllocationSite](./../boomerang/allocation_sites.md#allocation-site-with-dataflowscope)

## Queries

// TODO ForwardQueries and BackwardQueries

## AnalysisScope

Boomerang provides an `AnalysisScope` to compute initial queries along the complete reachable program.
It traverses the call graph starting at the entry points that are defined in the [FrameworkScopes](framework_scopes.md) while respecting their `DataFlowScopes`.

The `AnalysisScope` calls a method `generateSeed` on each reachable control-flow graph edge where we can decide whether a query should be generated.
For example, we may be interested in the backward analysis of the first parameter of calls to a method `sink`.
Then, we can implement an `AnalysisScope` as follows:

```java
public class SinkAnalysisScope {

    public SinkAnalysisScope(FrameworkScope scope) {
        super(scope);
    }

    @Override
    protected Collection<? extends Query> generate(ControlFlowGraph.Edge edge) {
        // Backward solve means that the current statement is the target
        Statement stmt = edge.getTarget();
        if (stmt.containsInvokeExpr()) {
            InvokeExpr invokeExpr = stmt.getInvokeExpr();
            DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

            if (declaredMethod.getName().equals("sink") && invokeExpr.getArgs().size > 0) {
                Val arg = invokeExpr.getArg(0);

                // Create the query to analyze the first parameter from the call to 'sink'
                BackwardQuery query = BackwardQuery.make(edge, arg);
                return Collections.singleton(query);
            }
        }

        return Collections.emptySet();
    }
}
```

We can use this implementation to compute relevant queries across the complete program as follows (see [FrameworkScopes](framework_scopes.md) on how to initialize a framework scope):

```java
FrameworkScope scope = ...;
AnalysisScope analysisScope = new SinkAnalysisScope(scope);
Collection<Query> queries = analysisScope.computeSeeds();
```

The collection `queries` contains all queries for a statement `sink(v, ...)` that can be solved with the [Boomerang Solver](./../boomerang/boomerang_setup.md).
