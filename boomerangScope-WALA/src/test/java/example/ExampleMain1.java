/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package example;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.Query;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AnalysisScope;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.wala.WALACallGraph;
import boomerang.scope.wala.WalaFrameworkScope;
import com.google.common.collect.Lists;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.core.util.config.AnalysisScopeReader;
import com.ibm.wala.core.util.io.FileProvider;
import com.ibm.wala.ipa.callgraph.AnalysisCacheImpl;
import com.ibm.wala.ipa.callgraph.AnalysisOptions;
import com.ibm.wala.ipa.callgraph.CallGraphBuilder;
import com.ibm.wala.ipa.callgraph.CallGraphBuilderCancelException;
import com.ibm.wala.ipa.callgraph.Entrypoint;
import com.ibm.wala.ipa.callgraph.IAnalysisCacheView;
import com.ibm.wala.ipa.callgraph.impl.DefaultEntrypoint;
import com.ibm.wala.ipa.callgraph.impl.Util;
import com.ibm.wala.ipa.callgraph.propagation.InstanceKey;
import com.ibm.wala.ipa.cha.ClassHierarchyException;
import com.ibm.wala.ipa.cha.ClassHierarchyFactory;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.types.ClassLoaderReference;
import com.ibm.wala.types.MethodReference;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import wpds.impl.Weight;

public class ExampleMain1 {
  public static void main(String... args)
      throws CallGraphBuilderCancelException, IOException, ClassHierarchyException {
    String mainClass = "example.BoomerangExampleTarget1";
    WALACallGraph walaCallGraph = setupWALA(mainClass);
    performAnalysis(walaCallGraph);
  }

  private static WALACallGraph setupWALA(String mainClass)
      throws CallGraphBuilderCancelException, IOException, ClassHierarchyException {
    com.ibm.wala.ipa.callgraph.AnalysisScope walaScope =
        AnalysisScopeReader.instance.readJavaScope(
            "testScope.txt",
            (new FileProvider()).getFile("exclusion.txt"),
            ExampleMain1.class.getClassLoader());
    IClassHierarchy cha = ClassHierarchyFactory.make(walaScope);
    String testCaseClassName = mainClass.replace(".", "/").replace("class ", "");

    final MethodReference ref =
        MethodReference.findOrCreate(
            ClassLoaderReference.Application,
            "L" + testCaseClassName,
            "main",
            "([Ljava/lang/String;)V");

    IMethod method = cha.resolveMethod(ref);
    Iterable<Entrypoint> singleton =
        new Iterable<Entrypoint>() {

          @Override
          public Iterator<Entrypoint> iterator() {
            ArrayList<Entrypoint> list = Lists.newArrayList();
            list.add(new DefaultEntrypoint(method, cha));
            Iterator<Entrypoint> ret = list.iterator();
            return ret;
          }
        };
    AnalysisOptions options = new AnalysisOptions(walaScope, singleton);
    IAnalysisCacheView cache = new AnalysisCacheImpl();
    CallGraphBuilder<InstanceKey> rtaBuilder = Util.makeRTABuilder(options, cache, cha, walaScope);
    com.ibm.wala.ipa.callgraph.CallGraph makeCallGraph = rtaBuilder.makeCallGraph(options, null);
    return new WALACallGraph(makeCallGraph, cha);
  }

  protected static void performAnalysis(CallGraph cg) {
    AnalysisScope scope =
        new AnalysisScope(cg) {
          @Override
          protected Collection<? extends Query> generate(Edge edge) {
            Statement statement = edge.getStart();
            if (statement.toString().contains("queryFor") && statement.containsInvokeExpr()) {
              Val arg = statement.getInvokeExpr().getArg(0);
              return Collections.singleton(BackwardQuery.make(edge, arg));
            }
            return Collections.emptySet();
          }
        };
    // 1. Create a Boomerang solver.
    Boomerang solver = new Boomerang(new WalaFrameworkScope(), BoomerangOptions.DEFAULT());

    // 2. Submit a query to the solver.
    Collection<Query> seeds = scope.computeSeeds();
    for (Query query : seeds) {
      System.out.println("Solving query: " + query);
      BackwardBoomerangResults<Weight.NoWeight> backwardQueryResults =
          solver.solve((BackwardQuery) query);
      System.out.println("All allocation sites of the query variable are:");
      System.out.println(backwardQueryResults.getAllocationSites());
    }
  }
}
