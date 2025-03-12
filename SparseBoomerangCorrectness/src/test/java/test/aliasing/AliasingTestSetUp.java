package test.aliasing;

import static org.junit.Assert.assertTrue;

import aliasing.SparseAliasManager;
import boomerang.scene.jimple.BoomerangPretransformer;
import boomerang.util.AccessPath;
import com.google.common.base.Predicate;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.*;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.options.Options;

public class AliasingTestSetUp {

  private static Logger log = LoggerFactory.getLogger(AliasingTestSetUp.class);

  Set<AccessPath> aliases = null;

  protected boolean FalsePositiveInDefaultBoomerang;

  public Set<AccessPath> executeStaticAnalysis(
      String targetClassName,
      String targetMethod,
      String queryLHS,
      SparsificationStrategy sparsificationStrategy,
      boolean ignoreAfterQuery) {
    setupSoot(targetClassName);
    registerSootTransformers(queryLHS, sparsificationStrategy, targetMethod, ignoreAfterQuery);
    executeSootTransformers();
    return aliases;
  }

  protected void setupSoot(String targetTestClassName) {
    G.v().reset();
    String userdir = System.getProperty("user.dir");
    String sootCp =
        userdir
            + File.separator
            + "target"
            + File.separator
            + "test-classes"
            + File.pathSeparator
            + "lib"
            + File.separator
            + "rt.jar";
    Options.v().set_soot_classpath(sootCp);

    // We want to perform a whole program, i.e. an interprocedural analysis.
    // We construct a basic CHA call graph for the program
    Options.v().set_whole_program(true);
    Options.v().setPhaseOption("cg.spark", "on");
    Options.v().setPhaseOption("cg", "all-reachable:true");

    Options.v().set_no_bodies_for_excluded(true);
    Options.v().set_allow_phantom_refs(true);
    Options.v().setPhaseOption("jb.sils", "enabled:false");
    Options.v().setPhaseOption("jb", "use-original-names:true");
    Options.v().set_prepend_classpath(false);

    Scene.v().addBasicClass("java.lang.StringBuilder");
    SootClass c = Scene.v().forceResolve(targetTestClassName, SootClass.BODIES);
    if (c != null) {
      c.setApplicationClass();
    }
    Scene.v().loadNecessaryClasses();
  }

  public Set<AccessPath> getAliases(
      SootMethod method,
      String queryLHS,
      SparsificationStrategy sparsificationStrategy,
      boolean ignoreAfterQuery) {
    String[] split = queryLHS.split("\\.");
    Optional<Unit> unitOp;
    if (split.length > 1) {
      unitOp =
          method.getActiveBody().getUnits().stream()
              .filter(e -> e.toString().startsWith(split[0]) && e.toString().contains(split[1]))
              .findFirst();
    } else {
      unitOp =
          method.getActiveBody().getUnits().stream()
              .filter(e -> e.toString().startsWith(split[0]))
              .findFirst();
    }

    if (unitOp.isPresent()) {
      Unit unit = unitOp.get();
      if (unit instanceof JAssignStmt) {
        JAssignStmt stmt = (JAssignStmt) unit;
        Value leftOp = stmt.getLeftOp();
        if (leftOp instanceof JInstanceFieldRef) {
          // get base
          leftOp = ((JInstanceFieldRef) leftOp).getBase();
        }
        SparseAliasManager sparseAliasManager =
            SparseAliasManager.getInstance(sparsificationStrategy, ignoreAfterQuery);
        return sparseAliasManager.getAliases(stmt, method, leftOp);
      }
    }
    throw new RuntimeException(
        "Query Variable not found. Does variable:"
            + queryLHS
            + " exist in the method:"
            + method.getName());
  }

  protected Transformer createAnalysisTransformer(
      String queryLHS,
      SparsificationStrategy sparsificationStrategy,
      String targetMethod,
      boolean ignoreAfterQuery) {
    return new SceneTransformer() {
      @Override
      protected void internalTransform(String phaseName, Map<String, String> options) {
        aliases =
            getAliases(
                getEntryPointMethod(targetMethod),
                queryLHS,
                sparsificationStrategy,
                ignoreAfterQuery);
      }
    };
  }

  protected SootMethod getEntryPointMethod(String targetMethod) {
    for (SootClass c : Scene.v().getApplicationClasses()) {
      for (SootMethod m : c.getMethods()) {
        if (!m.hasActiveBody()) {
          continue;
        }
        if (targetMethod != null && m.getName().equals(targetMethod)) {
          return m;
        }
        if (m.getName().equals("entryPoint")
            || m.toString().contains("void main(java.lang.String[])")) {
          return m;
        }
      }
    }
    throw new IllegalArgumentException("Method does not exist in scene!");
  }

  protected void registerSootTransformers(
      String queryLHS,
      SparsificationStrategy sparsificationStrategy,
      String targetMethod,
      boolean ignoreAfterQuery) {
    Transform transform =
        new Transform(
            "wjtp.ifds",
            createAnalysisTransformer(
                queryLHS, sparsificationStrategy, targetMethod, ignoreAfterQuery));
    PackManager.v().getPack("wjtp").add(transform);
  }

  protected void executeSootTransformers() {
    // Apply all necessary packs of soot. This will execute the respective Transformer
    PackManager.v().getPack("cg").apply();
    // Must have for Boomerang
    BoomerangPretransformer.v().reset();
    BoomerangPretransformer.v().apply();
    PackManager.v().getPack("wjtp").apply();
  }

  protected void runAnalyses(String queryLHS, String targetClass, String targetMethod) {
    Set<AccessPath> nonSparseAliases =
        getAliases(targetClass, queryLHS, targetMethod, SparsificationStrategy.NONE, true);
    Set<AccessPath> typeBasedSparseAliases =
        getAliases(targetClass, queryLHS, targetMethod, SparsificationStrategy.TYPE_BASED, true);
    Set<AccessPath> aliasAwareSparseAliases =
        getAliases(targetClass, queryLHS, targetMethod, SparsificationStrategy.ALIAS_AWARE, true);
    checkResults(SparsificationStrategy.TYPE_BASED, typeBasedSparseAliases, nonSparseAliases);
    checkResults(SparsificationStrategy.ALIAS_AWARE, aliasAwareSparseAliases, nonSparseAliases);
  }

  protected void runAnalyses(
      String queryLHS, String targetClass, String targetMethod, boolean ignoreAfterQuery) {
    Set<AccessPath> nonSparseAliases =
        getAliases(
            targetClass, queryLHS, targetMethod, SparsificationStrategy.NONE, ignoreAfterQuery);
    Set<AccessPath> typeBasedSparseAliases =
        getAliases(
            targetClass,
            queryLHS,
            targetMethod,
            SparsificationStrategy.TYPE_BASED,
            ignoreAfterQuery);
    Set<AccessPath> aliasAwareSparseAliases =
        getAliases(
            targetClass,
            queryLHS,
            targetMethod,
            SparsificationStrategy.ALIAS_AWARE,
            ignoreAfterQuery);
    checkResults(SparsificationStrategy.TYPE_BASED, typeBasedSparseAliases, nonSparseAliases);
    checkResults(SparsificationStrategy.ALIAS_AWARE, aliasAwareSparseAliases, nonSparseAliases);
  }

  protected Set<AccessPath> getAliases(
      String targetClass,
      String queryLHS,
      String targetMethod,
      SparsificationStrategy sparsificationStrategy,
      boolean ignoreAfterQuery) {
    Set<AccessPath> aliases =
        executeStaticAnalysis(
            targetClass, targetMethod, queryLHS, sparsificationStrategy, ignoreAfterQuery);
    return aliases;
  }

  protected void checkResults(
      SparsificationStrategy strategy,
      Set<AccessPath> sparseAliases,
      Set<AccessPath> nonSparseAliases) {
    List<String> nonSparse =
        nonSparseAliases.stream().map(e -> e.toString()).collect(Collectors.toList());
    List<String> sparse =
        sparseAliases.stream().map(e -> e.toString()).collect(Collectors.toList());
    removeIntermediateLocals(nonSparse, sparse);
    if (!FalsePositiveInDefaultBoomerang)
      assertTrue(
          strategy + " " + generateDiffMessage(nonSparse, sparse), sparse.containsAll(nonSparse));
    assertTrue(
        "nonSparse " + generateDiffMessage(sparse, nonSparse), nonSparse.containsAll(sparse));
  }

  private void removeIntermediateLocals(List<String> nonSparse, List<String> sparse) {
    Predicate<String> isIntermediate = e -> e.startsWith("$stack");
    nonSparse.removeIf(isIntermediate);
    sparse.removeIf(isIntermediate);
  }

  private String generateDiffMessage(List<String> larger, List<String> smaller) {
    larger.removeAll(smaller); // remove only for message generation
    String collect = larger.stream().collect(Collectors.joining(System.lineSeparator()));
    larger.addAll(smaller);
    return "missing " + collect;
  }
}
