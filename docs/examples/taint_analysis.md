# Taint Analysis

## What is a Taint Analysis

A **Taint Analysis** is an analysis technique that is used to track data flows through a program. 
The idea is to mark certain inputs as *tainted* (e.g. a password) and analyze how these inputs are used inside an application.
A basic taint analysis consists of the following 3 components:

- **Sources**: A source is an entry point that returns sensitive data. The returned values are *tainted*
- **Sinks**: A sink is a location in a program that may cause the leak of sensitive data (e.g. a print statement that must not print a password)
- **Sanitizer**: A sanitizer is function or operation that filters and validate tainted data, converting them into untainted data.

The goal of a taint analysis is to detect whether tainted data reaches a sink without being sanitized.
Common examples include SQL injections where user inputs should not be used in SQL queries directly.

## Taint Analysis with Boomerang

Many taint analysis implementations start the analysis from a source and propagate facts from top-to-bottom.
For example, in the following program, the variable `x` is marked as tainted in line 1 and then propagated it through the program until the analysis finds that the alias `y` (line 2) is used in a sink.

```java
A x = source();
A y = x;
sink(y);
```

Boomerang allows us to implement a taint analysis that follows a slightly different approach.
With the concept of backward queries, we can implement a bottom-to-top approach.
The idea is to start the analysis at a sink and propagate the facts backwards to find the allocation sites, that is, we raise the demand whether a variable at a sink points to an object from a source.
In the example above, we start the analysis for the variable `y` at the call to `sink` and propagate it through the program until we find the allocation site `source`.

## Example Implementation with Boomerang

In the following sections, we show how a taint analysis may be implemented with Boomerang.
You can find the complete implementation as Maven project [here]().
To simplify things, we show the setup only for SootUp.
However, a setup with Soot and Opal may be added easily.

### Dependencies

Since we plan to implement the taint analysis only for SootUp, we need the following 2 dependencies (`x.y.z` is the latest version):

```xml
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>boomerangPDS</artifactId>
  <version>x.y.z</version>
</dependency> 
<dependency> 
  <groupId>de.fraunhofer.iem</groupId> 
  <artifactId>boomerangScope-SootUp</artifactId>
  <version>x.y.z</version>
</dependency>
```

### Sinks and Sources

To keep things simple, we assume the target applications have a class `SourceClass` that defines a method `source` and a class `SinkClass` that defines a method `sink`:

??? note "SourceClass.java"
    ```java
    public class SourceClass {

      public static String source() {
        return "secret";
      }
    }
    ```

??? note "SinkClass.java"
    ```java
    public class SinkClass {
    
      public static void sink(String s) {
        System.out.println(s);
      }
    }
    ```

Hence, our analysis targets only one source and one sink.

### AnalysisScope

The first step consists of finding the sinks in the target program.
We use the [AnalysisScope](../general/boomerang_scope.md#analysisscope) to traverse the complete target program.
If we find a call to `SinkClass.sink(s)`, we create a backward query for `s` at this statement.

??? note "TaintAnalysisScope.java"
    ```java
    public class TaintAnalysisScope extends AnalysisScope {

      /* We use a MethodWrapper to describe the target method SinkClass.sink(s).
       * This way, the descriptor is framework independent and we do not need to 
       * distinguish and compare framework specific signatures.
       */
      private final MethodWrapper sink =
                              new MethodWrapper(
                                  "taints.SinkClass",
                                  "sink",
                                  "void",
                                  List.of("java.lang.String"));

      public TaintAnalysisScope(FrameworkScope frameworkScope) {
        super(frameworkScope);
      }

      @Override
      public Collection<? extends Query> generate(ControlFlowGraph.Edge edge) {
        // Recall that for backward queries, we need the target
        Statement statement = edge.getTarget();

        if (statement.containsInvokeExpr()) {
          InvokeExpr invokeExpr = statement.getInvokeExpr();
          DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();

          // Check whether the statement is a call to SinkClass.sink(s)
          if (declaredMethod.toMethodWrapper().equals(sink) {
            Val arg = invokeExpr.getArg(0);

            // Create a query that solves for s at a statement SinkClass.sink(s)
            BackwardQuery query = BackwardQuery.make(edge, arg);
            return Collections.singleton(query);
          }
        }

        // Do not create a query for the current statement
        return Collections.emptySet();
      }
    }
    ```

With an initialized [FrameworkScope](../general/framework_scopes.md), we can traverse the complete target application and compute the corresponding backward queries as follows:

```java
FrameworkScope frameworkScope = /* previously initialized */
AnalysisScope scope = new TaintAnalysisScope(frameworkScope);
Collection<Query> queries = scope.computeSeeds();
```

### AllocationSite

The idea of implementing the Taint Analysis with Boomerang is to find the allocation sites of variables and check whether these are sources.
We can use the [Allocation Sites](../boomerang/allocation_sites.md) to define this exact behavior.
When the backward analysis reaches a statement `v = SourceClass.source()`, we want to return it as allocation site to indicate that the query variable points to a value that is returned by a source.
We can implement an allocation site as follows:

??? note "TaintAllocationSite.java"
    ```java
    public class TaintAllocationSite implements IAllocationSite {

      /* We use a MethodWrapper to describe the target method SourceClass.source().
       * This way, the descriptor is framework independent and we do not need to 
       * distinguish and compare framework specific signatures.
       */
      private final MethodWrapper source =
                              new MethodWrapper(
                                  "taints.SourceClass",
                                  "source",
                                  "java.lang.String",
                                  Collections.emptyList());
    
      @Override
      public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
        if (!statement.isAssignStmt()) {
          return Optional.empty();
        }

        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();

        if (!leftOp.equals(fact)) {
          return Optional.empty();
        }

        // Check whether we have a statement fact = SourceClass.source()
        if (statement.containsInvokeExpr()) {
          DeclaredMethod declaredMethod = statement.getInvokeExpr().getDeclaredMethod();

          if (declaredMethod.toMethodWrapper().equals(source)) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);

            return Optional.of(allocVal);
          }
        }

        // Statement is not an allocation site from a source
        return Optional.empty();
      }
    }
    ```

We can configure the Boomerang options with this allocation site implementation as follows:

```java
IAllocationSite allocSite = new TaintAllocationSite();
BoomerangOptions options = BoomerangOptions.builer().withAllocationSite(allocSite).build();
```

### DataFlowScope

The last component is the [DataFlowScope](./../general/boomerang_scope.md#dataflowscope).
Since we want to analyze the complete target application, we exclude only data flows from phantom classes.
Boomerang considers a class *phantom* if it is not loaded by the underlying static analysis framework.
In most cases, these are JDK classes or classes from third-party libraries.
Additionally, we exclude the classes `taints.SourceClass` and `taints.SinkClass` because they contain the start (sinks) and end (sources) methods for the data flows.
Recall that we start the analysis at a sink and end it when we reach a source or cannot extend the data flow path any further.

??? note "TaintDataFlowScope.java"
    ```java
    public class TaintDataFlowScope implements DataFlowScope {

      @Override
      public boolean isExcluded(DeclaredMethod declaredMethod) {
        WrappedClass declaringClass = declaredMethod.getDeclaringClass();

        if (declaringClass.isPhantom()) {
          return true;
        }

        String fqn = declaringClass.getFullyQualifiedName();
        if (fqn.equals("tains.SourceClass") || fqn.equals("taints.SinkClass")) {
          return true;
        }

        return false;
      }

      @Override
      public boolean isExcluded(Method method) {
        WrappedClass declaringClass = method.getDeclaringClass();

        if (declaringClass.isPhantom()) {
          return true;
        }

        String fqn = declaringClass.getFullyQualifiedName();
        if (fqn.equals("tains.SourceClass") || fqn.equals("taints.SinkClass")) {
          return true;
        }

        return false;
      }
    }
    ```

### Taint Analysis Main

With the allocation site implementation and the options, we can configure a Boomerang instance to solve our backward queries in a class `TaintAnalysis`:

```java
public Collection<AllocVal> solveQuery(FrameworkScope scope, BackwardQuery query) {
  TaintAllocationSite allocationSite = new TaintAllocationSite();
  BoomerangOptions options =
      BoomerangOptions.builder().withAllocationSite(allocationSite).build();

  // Run Boomerang
  Boomerang boomerang = new Boomerang(scope, options);
  BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);

  // Extract the allocation sites from the results
  Collection<AllocVal> allocSites = new HashSet<>();
  for (ForwardQuery result : results.getAllocationSites().keySet()) {
    allocSites.add(result.getAllocVal());
  }

  return allocSites;
}
```

We can combine this method with the `TaintAnalysisScope` implementation, leading to the following steps:

1) We use the `TaintAnalysisScope` to discover all calls to sinks in the program and to create a backward query for them
2) We use Boomerang to solve the queries and decide whether the variables used in the sinks point to data that are returned from a source

To keep things simple, we log our findings to the command line:

```java
public void run(FrameworkScope frameworkScope) {
  // Instantiate the AnalysisScope and compute the backward queries
  TaintAnalysisScope scope = new TaintAnalysisScope(frameworkScope, sinks);
  Collection<Query> queries = scope.computeSeeds();

  logger.info("Found " + queries.size() + " sink(s)");

  for (Query query : queries) {
    if (!(query instanceof BackwardQuery)) {
      continue;
    }

    // Use Boomerang to compute whether the sink variables point to 
    Collection<AllocVal> allocSites = solveQuery((BackwardQuery) query);
    logger.info(
        "Found {} leaks for variable {} @ {}:",
        allocSites.size(),
        query.var().getVariableName(),
        query.cfgEdge().getTarget());

    for (AllocVal allocVal : allocSites) {
      logger.info(
            "\tSource: "
              + allocVal.getAllocVal()
              + " @ "
              + allocVal.getAllocStatement()
              + " @ line "
              + allocVal.getAllocStatement().getLineNumber());
    }
  }

  if (queries.isEmpty()) {
    logger.info("Did not find any leaks!");
  }
}
```

### Running the program

We have all components ready to run the analysis.
Last, we need to set up a [FrameworkScope](./../general/framework_scopes.md).
Recall that, in this example, we focus on SootUp.
Hence, we use its `AnalysisInputLocation` and `JavaView` setup to read a target application and construct the call graph.
To keep thinks simple, we use `RTA` as the call graph algorithm, and we import the call graph's entry points.
We can then implement the main method of the taint analysis as follows:

```java
public static void main(String[] args) {
  // Set up SootUp (Do not forget the PreInterceptor)  
  List<BodyInterceptor> interceptors = List.of(new BoomerangPreInterceptor());
  AnalysisInputLocation inputLocation =
        new JavaClassPathAnalysisInputLocation(appToAnalyze, SourceType.Application, interceptors);
  JavaView view = new JavaView(inputLocation);

  // Compute the call graph with RTA
  CallGraphAlgorithm cgAlgorithm = new RapidTypeAnalysisAlgorithm(view);
  CallGraph callGraph = cgAlgorithm.initialize();

  // Import the entry points from the call graph
  Collection<JavaSootMethod> entryPoints = new HashSet<>();
  for (MethodSignature signature : callGraph.getEntryMethods()) {
    Optional<JavaSootMethod> method = view.getMethod(signature);

    method.ifPresent(entryPoints::add);
  }

  // Set up the dataflow scope and framwework scope
  DataFlowScope dataFlowScope = new TaintDataFlowScope(sources, sinks);
  FrameworkScope frameworkScope =
        new SootUpFrameworkScope(view, callGraph, entryPoints, dataFlowScope);

  // Instantiate and run our taint analysis implemenetation
  TaintAnalysis taintAnalysis = new TaintAnalysis();
  taintAnalysis.run(frameworkScope);
}
```

## Running the Taint Analysis

We can run the analysis in an IDE or compile the project and run it via the command line.
To show its functionality, we apply the analysis to 3 simple examples:

### Example 1

In the first example, we consider a program with a simple alias:

```java
String s = SourceClass.source();
String z = s;
SinkClass.sink(z);
```

Applying our program yields the following output:

```bash
INFO TaintAnalysis - Found 1 sink(s)
INFO TaintAnalysis - Found 1 leaks for variable l2 @ sink(l2):
INFO TaintAnalysis - 	Source: staticinvoke <taints.SourceClass: java.lang.String source()>() @ l1 = source() @ line 1
```

### Example 2

In the second example, we have a program with branches.
The analyzed variable is tainted only in an optional branch:

```java
// Our analysis does not return this allocation site because we do not 
// configure the call to 'noSource' as source in TaintAllocationSite.java
String s = SourceClass.noSource();

if (staticallyUnknown()) {
    s = SourceClass.source();
}

SinkClass.sink(s);
```

Applying our program yields the following output:

```bash
INFO TaintAnalysis - Found 1 sink(s)
INFO TaintAnalysis - Found 1 leaks for variable l1 @ sink(l1):
INFO TaintAnalysis - 	Source: staticinvoke <taints.SourceClass: java.lang.String source()>() @ l1 = source() @ line 6
```

### Example 3

The third example consists of a program with two variables `x` and `y` that point to the same object.
We use `x` to taint a field `f` of this object and `y` to read the tainted value:

```java
// x points to some object o
A x = new A();

// Read a tainted value and store it in field f of o
String s = SourceClass.source();
x.f = s;
        
// y aliases with x s.t. we read again the tainted value from the field f
A y = x;
String z = y.f;
sink(z);
```

Since Boomerang can deal with aliases and field accesses, our analysis prints the correct results:

```bash
INFO Found 1 sink(s)
INFO TaintAnalysis - Found 1 leaks for variable $stack5 @ sink($stack5):
INFO TaintAnalysis - 	Source: staticinvoke <taints.SourceClass: java.lang.String source()>() @ l1 = source() @ line 5
```
