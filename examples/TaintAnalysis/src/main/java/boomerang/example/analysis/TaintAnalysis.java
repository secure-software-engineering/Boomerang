package boomerang.example.analysis;

import boomerang.BackwardQuery;
import boomerang.Boomerang;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.options.BoomerangOptions;
import boomerang.results.BackwardBoomerangResults;
import boomerang.scope.AllocVal;
import boomerang.scope.FrameworkScope;
import boomerang.utils.MethodWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpds.impl.NoWeight;

import java.util.Collection;
import java.util.HashSet;

public class TaintAnalysis {

  private static final Logger logger = LoggerFactory.getLogger(TaintAnalysis.class.getSimpleName());

  private final FrameworkScope frameworkScope;
  private final Collection<MethodWrapper> sources;
  private final Collection<MethodWrapper> sinks;

  public TaintAnalysis(
      FrameworkScope frameworkScope,
      Collection<MethodWrapper> sources,
      Collection<MethodWrapper> sinks) {
    this.frameworkScope = frameworkScope;
    this.sources = sources;
    this.sinks = sinks;
  }

  public void run() {
    TaintAnalysisScope scope = new TaintAnalysisScope(frameworkScope, sinks);
    Collection<Query> queries = scope.computeSeeds();

    logger.info("Found " + queries.size() + " sink(s)");

    for (Query query : queries) {
      if (!(query instanceof BackwardQuery)) {
        continue;
      }

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

  public Collection<AllocVal> solveQuery(BackwardQuery query) {
    TaintAllocationSite allocationSite = new TaintAllocationSite(sources);
    BoomerangOptions options =
        BoomerangOptions.builder().withAllocationSite(allocationSite).build();

    Boomerang boomerang = new Boomerang(frameworkScope, options);
    BackwardBoomerangResults<NoWeight> results = boomerang.solve(query);

    Collection<AllocVal> allocSites = new HashSet<>();
    for (ForwardQuery result : results.getAllocationSites().keySet()) {
      allocSites.add(result.getAllocVal());
    }

    return allocSites;
  }
}
