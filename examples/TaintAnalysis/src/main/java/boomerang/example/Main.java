package boomerang.example;

import boomerang.example.analysis.TaintAnalysis;
import boomerang.example.analysis.TaintDataFlowScope;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.sootup.BoomerangPreInterceptor;
import boomerang.scope.sootup.SootUpFrameworkScope;
import boomerang.utils.MethodWrapper;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.RapidTypeAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SourceType;
import sootup.core.signatures.MethodSignature;
import sootup.core.transform.BodyInterceptor;
import sootup.java.bytecode.frontend.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class Main {

  private static final String appToAnalyze = "./targets/BranchingSingleExample.jar";

  private static final Collection<MethodWrapper> sources =
      Set.of(new MethodWrapper("taints.SourceClass", "source", "java.lang.String"));

  private static final Collection<MethodWrapper> sinks =
      Set.of(new MethodWrapper("taints.SinkClass", "sink", "void", List.of("java.lang.String")));

  public static void main(String[] args) {
    List<BodyInterceptor> interceptors = List.of(new BoomerangPreInterceptor());
    AnalysisInputLocation inputLocation =
        new JavaClassPathAnalysisInputLocation(appToAnalyze, SourceType.Application, interceptors);
    JavaView view = new JavaView(inputLocation);

    CallGraphAlgorithm cgAlgorithm = new RapidTypeAnalysisAlgorithm(view);
    CallGraph callGraph = cgAlgorithm.initialize();

    Collection<JavaSootMethod> entryPoints = new HashSet<>();
    for (MethodSignature signature : callGraph.getEntryMethods()) {
      Optional<JavaSootMethod> method = view.getMethod(signature);

      method.ifPresent(entryPoints::add);
    }

    DataFlowScope dataFlowScope = new TaintDataFlowScope(sources, sinks);
    FrameworkScope frameworkScope =
        new SootUpFrameworkScope(view, callGraph, entryPoints, dataFlowScope);

    TaintAnalysis taintAnalysis = new TaintAnalysis(frameworkScope, sources, sinks);
    taintAnalysis.run();
  }
}
