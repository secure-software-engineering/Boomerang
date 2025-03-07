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

import boomerang.WeightedForwardQuery;
import boomerang.debugger.Debugger;
import boomerang.results.ForwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DataFlowScope;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.BoomerangPretransformer;
import boomerang.scope.soot.SootFrameworkScope;
import com.google.common.base.Joiner;
import com.google.common.collect.Table;
import ideal.IDEALAnalysis;
import ideal.IDEALAnalysisDefinition;
import ideal.IDEALResultHandler;
import ideal.IDEALSeedSolver;
import ideal.StoreIDEALResultHandler;
import inference.InferenceWeight;
import inference.InferenceWeightFunctions;
import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.G;
import soot.PackManager;
import soot.Scene;
import soot.SceneTransformer;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Transformer;
import soot.options.Options;
import sync.pds.solver.WeightFunctions;

public class Main {

  private static final Logger logger = LoggerFactory.getLogger(Main.class);

  public static void main(String... args) {
    String sootClassPath =
        System.getProperty("user.dir") + File.separator + "target" + File.separator + "classes";
    String mainClass = "inference.example.InferenceExample";
    setupSoot(sootClassPath, mainClass);
    analyze();
  }

  private static void setupSoot(String sootClassPath, String mainClass) {
    G.reset();
    Options.v().set_whole_program(true);
    Options.v().setPhaseOption("cg.spark", "on");
    Options.v().set_output_format(Options.output_format_none);
    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);

    List<String> includeList = new LinkedList<>();
    includeList.add("java.lang.*");
    includeList.add("java.util.*");
    includeList.add("java.io.*");
    includeList.add("sun.misc.*");
    includeList.add("java.net.*");
    includeList.add("javax.servlet.*");
    includeList.add("javax.crypto.*");

    Options.v().set_include(includeList);
    Options.v().setPhaseOption("jb", "use-original-names:true");

    Options.v().set_soot_classpath(sootClassPath);
    Options.v().set_prepend_classpath(true);

    Scene.v().loadNecessaryClasses();
    SootClass c = Scene.v().forceResolve(mainClass, SootClass.BODIES);
    if (c != null) {
      c.setApplicationClass();
      for (SootMethod m : c.getMethods()) {
        logger.debug(m.toString());
      }
    }
  }

  private static void analyze() {
    Transform transform = new Transform("wjtp.ifds", createAnalysisTransformer());
    PackManager.v().getPack("wjtp").add(transform);
    PackManager.v().getPack("cg").apply();
    BoomerangPretransformer.v().apply();
    PackManager.v().getPack("wjtp").apply();
  }

  private static Transformer createAnalysisTransformer() {
    return new SceneTransformer() {
      protected void internalTransform(String phaseName, Map<String, String> options) {
        StoreIDEALResultHandler<InferenceWeight> resultHandler = new StoreIDEALResultHandler<>();
        SootFrameworkScope frameworkScope =
            new SootFrameworkScope(
                Scene.v(),
                Scene.v().getCallGraph(),
                Scene.v().getEntryPoints(),
                DataFlowScope.EXCLUDE_PHANTOM_CLASSES);
        IDEALAnalysis<InferenceWeight> solver =
            new IDEALAnalysis<>(
                new IDEALAnalysisDefinition<>() {

                  @Override
                  public Collection<WeightedForwardQuery<InferenceWeight>> generate(Edge edge) {
                    Statement stmt = edge.getStart();
                    if (stmt.isAssignStmt()) {
                      if (stmt.getRightOp().isNewExpr()
                          && stmt.getRightOp()
                              .getType()
                              .toString()
                              .contains("inference.example.InferenceExample$File")) {
                        AllocVal allocVal = new AllocVal(stmt.getLeftOp(), stmt, stmt.getRightOp());
                        return Collections.singleton(
                            new WeightedForwardQuery<>(edge, allocVal, InferenceWeight.one()));
                      }
                    }
                    return Collections.emptySet();
                  }

                  @Override
                  public WeightFunctions<Edge, Val, Edge, InferenceWeight> weightFunctions() {
                    return new InferenceWeightFunctions();
                  }

                  @Override
                  public Debugger<InferenceWeight> debugger(
                      IDEALSeedSolver<InferenceWeight> solver) {
                    return new Debugger<>();
                  }

                  @Override
                  public IDEALResultHandler<InferenceWeight> getResultHandler() {
                    return resultHandler;
                  }

                  @Override
                  public FrameworkScope getFrameworkFactory() {
                    return frameworkScope;
                  }
                });
        solver.run();
        Map<WeightedForwardQuery<InferenceWeight>, ForwardBoomerangResults<InferenceWeight>> res =
            resultHandler.getResults();
        for (Entry<WeightedForwardQuery<InferenceWeight>, ForwardBoomerangResults<InferenceWeight>>
            e : res.entrySet()) {
          Table<Edge, Val, InferenceWeight> results = e.getValue().asEdgeValWeightTable();
          logger.info(Joiner.on("\n").join(results.cellSet()));
        }
      }
    };
  }
}
