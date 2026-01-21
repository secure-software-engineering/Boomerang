# Boomerang for Android Applications

Boomerang can be applied to Android applications using [FlowDroid](https://github.com/secure-software-engineering/FlowDroid).
FlowDroid is a static analysis tool that computes call graphs and data flows in Android applications.
Since it is based on Soot, one can use the Soot scope to instantiate a [FrameworkScope](../general/framework_scopes.md).

## Dependencies

To use FlowDroid with Boomerang, include the FlowDroid and the Soot scope dependencies in your project:

```
<!-- BoomerangScope for Soot (Make sure to use the most recent Boomerang version) -->
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>boomerangScope-Soot</artifactId>
  <version>a.b.c</version>
</dependency>

<!-- FlowDroid dependencies (Make sure to choose the most recent FlowDroid version) -->
<dependency>
  <groupId>de.fraunhofer.sit.sse.flowdroid</groupId>
  <artifactId>soot-infoflow</artifactId>
  <version>x.y.z</version>
</dependency>
<dependency>
   <groupId>de.fraunhofer.sit.sse.flowdroid</groupId>
   <artifactId>soot-infoflow-summaries</artifactId>
   <version>x.y.z</version>
</dependency>
<dependency>
   <groupId>de.fraunhofer.sit.sse.flowdroid</groupId>
   <artifactId>soot-infoflow-android</artifactId>
   <version>x.y.z</version>
</dependency>
```

## Setting up FlowDroid

To instantiate the `SootFrameworkScope`, we have to compute a call graph.
However, instead of setting up Soot (as described [here](../general/framework_scopes.md)), we use FlowDroid to construct a call graph that takes Android's activity lifecycle into account.
For example, we can use the following FlowDroid setup:

```java
InfoflowAndroidConfiguration config = new InfoFLowAndroidConfiguration();

// Use CHA as call graph algorithm
config.setCallgraphAlgorithm(InfoflowAndroidConfiguration.CallgraphAlgorithm.CHA);

// Set the target app and the platforms for the SDK(s)
config.getAnalysisFileConfig().setTargetAPKFile(<pathToTheAPKFile>);
config.getAnalysisFileConfig().setAndroidPlatformDir(<pathToThePlatformsDir>);

// Further setup: Do not eliminate unreachable code and keep the original line numbers
config.setCodeEliminationMode(InfoflowConfiguration.CodeEliminationMode.NoCodeElimination);
config.setEnableLineNumbers(true);

// Configure FlowDroid
SetupApplication app = new SetupApplication();
app.setSootConfig(new SootConfigForAndroid() {
    @Override
    public void setSootOptions(Options options, InfoFlowConfiguration config) {
        options.setPhaseOptions("jb.sils", "enabled:false");
        
        // By default, FlowDroid loads the Android packages which makes the call graph very large
        // and the analysis slow. Only include them, if they are really needed
        options.set_exclude(List.of("android.*", "androidx.*"));
    }
})
```

With the configured `SetupApplication`, we can now construct the call graph and instantiate the `SootFrameworkScope` as follows:

```java
// Construct the Android specific call graph
app.constructCallGraph();

// Do not forget to apply the PreTransformer
BoomerangPretransformer.v().reset();
BoomerangPretransformer.v().apply();

// Framework scope objects
DataFlowScope dataFlowScope = DataFlowScope.EXCLUDE_PHANTOM_CLASSES;
CallGraph callGraph = Scene.v().getCallGraph();
Collection<SootMethod> entryPoints = Scene.v().getEntryPoints();

// Setup up the framework scope
FrameworkScope scope = new SootFrameworkScope(Scene.v(), callGraph, entryPoints, dataFlowScope);
```

With the `scope`, we can continue with [Boomerang](../boomerang/boomerang_setup.md) and [IDEal]().
