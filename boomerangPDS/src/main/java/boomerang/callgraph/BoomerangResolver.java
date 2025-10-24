/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.callgraph;

import boomerang.BackwardQuery;
import boomerang.ForwardQuery;
import boomerang.Query;
import boomerang.SolverCreationListener;
import boomerang.WeightedBoomerang;
import boomerang.results.ExtractAllocationSiteStateListener;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import wpds.impl.Weight;

public class BoomerangResolver implements ICallerCalleeResolutionStrategy {
  public static final Factory FACTORY = BoomerangResolver::new;

  private static final Logger logger = LoggerFactory.getLogger(BoomerangResolver.class);

  public enum NoCalleeFoundFallbackOptions {
    PRECOMPUTED,
    BYPASS
  }

  private static final String THREAD_CLASS = "java.lang.Thread";
  private static final String THREAD_START_SIGNATURE = "<java.lang.Thread: void start()>";
  private static final String THREAD_RUN_SUB_SIGNATURE = "void run()";

  private static final NoCalleeFoundFallbackOptions FALLBACK_OPTION =
      NoCalleeFoundFallbackOptions.PRECOMPUTED;
  private static final Multimap<DeclaredMethod, WrappedClass> didNotFindMethodLog =
      HashMultimap.create();

  private final CallGraph precomputedCallGraph;
  private final WeightedBoomerang<? extends Weight> solver;
  private final Set<Statement> queriedInvokeExprAndAllocationSitesFound = new LinkedHashSet<>();
  private Set<Statement> queriedInvokeExpr = new LinkedHashSet<>();

  public BoomerangResolver(WeightedBoomerang<? extends Weight> solver, CallGraph initialCallGraph) {
    this.solver = solver;
    this.precomputedCallGraph = initialCallGraph;
  }

  @Override
  public boolean computeFallback(
      BiConsumer<Statement, Method> onCallerCalleeFoundCallback,
      Consumer<Statement> onNoCalleeFoundCallback) {
    int refined = 0;
    int precomputed = 0;
    Set<Statement> todo = queriedInvokeExpr;
    queriedInvokeExpr = new LinkedHashSet<>();
    boolean changes = false;
    for (Statement s : todo) {
      if (!queriedInvokeExprAndAllocationSitesFound.contains(s)) {
        logger.debug("Call graph ends at {}", s);
        precomputed++;
        changes = true;
        if (FALLBACK_OPTION == NoCalleeFoundFallbackOptions.PRECOMPUTED) {
          // strictly speaking, no alloc site was found - but we shouldn't process this stmt again
          queriedInvokeExprAndAllocationSitesFound.add(s);
          for (CallGraph.Edge e : precomputedCallGraph.edgesOutOf(s)) {
            // TODO Refactor. Should not be required, if the backward analysis is sound (data-flow
            // of static fields)
            if (e.tgt().isDefined()) {
              onCallerCalleeFoundCallback.accept(e.src(), e.tgt());
            }
          }
        }
        if (FALLBACK_OPTION == NoCalleeFoundFallbackOptions.BYPASS) {
          onNoCalleeFoundCallback.accept(s);
        }
      } else {
        refined++;
      }
    }
    logger.debug("Refined edges {}, fallback to precomputed {}", refined, precomputed);
    return changes;
  }

  @Override
  public void resolveCallersForCalleeFallback(
      Method callee, BiConsumer<Statement, Method> onCallerCalleeFoundCallback) {
    Collection<CallGraph.Edge> edges = precomputedCallGraph.edgesInto(callee);
    for (CallGraph.Edge edge : edges) {
      onCallerCalleeFoundCallback.accept(edge.src(), edge.tgt());
    }
  }

  @Override
  public void resolveSpecialInvoke(
      Statement stmt, BiConsumer<Statement, Method> onCallerCalleeFoundCallback) {
    InvokeExpr ie = stmt.getInvokeExpr();
    Collection<Method> methodFromClassOrFromSuperclass =
        getMethodFromClassOrFromSuperclass(
            ie.getDeclaredMethod(), ie.getDeclaredMethod().getDeclaringClass());
    if (methodFromClassOrFromSuperclass.size() > 1) {
      throw new RuntimeException(
          "Illegal state, a special call should exactly resolve to one target");
    } else if (!methodFromClassOrFromSuperclass.isEmpty()) {
      onCallerCalleeFoundCallback.accept(
          stmt, methodFromClassOrFromSuperclass.stream().findFirst().get());
    }
  }

  @Override
  public void resolveStaticInvoke(
      Statement stmt, BiConsumer<Statement, Method> onCallerCalleeFoundCallback) {
    InvokeExpr ie = stmt.getInvokeExpr();
    Collection<Method> methodFromClassOrFromSuperclass =
        getMethodFromClassOrFromSuperclass(
            ie.getDeclaredMethod(), ie.getDeclaredMethod().getDeclaringClass());
    if (methodFromClassOrFromSuperclass.size() > 1) {
      throw new RuntimeException(
          "Illegal state, a static call should exactly resolve to one target");
    } else if (!methodFromClassOrFromSuperclass.isEmpty()) {
      onCallerCalleeFoundCallback.accept(
          stmt, methodFromClassOrFromSuperclass.stream().findFirst().get());
    }
  }

  @Override
  public void resolveInstanceInvoke(
      Statement resolvingStmt, BiConsumer<Statement, Method> onCallerCalleeFoundCallback) {
    logger.debug("Queried for callees of '{}'.", resolvingStmt);
    // Construct BackwardQuery, so we know which types the object might have
    InvokeExpr invokeExpr = resolvingStmt.getInvokeExpr();
    queriedInvokeExpr.add(resolvingStmt);
    Val value = invokeExpr.getBase();

    // Not using cfg here because we are iterating backward
    for (Statement pred :
        resolvingStmt.getMethod().getControlFlowGraph().getPredsOf(resolvingStmt)) {
      BackwardQuery query = BackwardQuery.make(new Edge(pred, resolvingStmt), value);
      solver.solve(query, false, false);
      solver.registerSolverCreationListener(
          new IterateSolvers(query, resolvingStmt, onCallerCalleeFoundCallback));
    }
  }

  // XXX: interface default methods
  private Collection<Method> getMethodFromClassOrFromSuperclass(
      DeclaredMethod method, WrappedClass sootClass) {
    Set<Method> res = new LinkedHashSet<>();
    WrappedClass originalClass = sootClass;
    while (sootClass != null) {
      for (Method candidate : sootClass.getMethods()) {
        if (candidate.getSubSignature().equals(method.getSubSignature())) {
          res.add(candidate);
        }
      }
      if (!res.isEmpty()) {
        handlingForThreading(method, originalClass, res);
        return res;
      }
      if (sootClass.hasSuperclass()) {
        sootClass = sootClass.getSuperclass();
      } else {
        logDidNotFindMethod(method, originalClass);
        return res;
      }
    }
    logDidNotFindMethod(method, originalClass);
    return res;
  }

  private void logDidNotFindMethod(DeclaredMethod method, WrappedClass originalClass) {
    if (didNotFindMethodLog.put(method, originalClass)) {
      logger.debug("Did not find method {} for class {}", method, originalClass);
    }
  }

  private void handlingForThreading(
      DeclaredMethod method, WrappedClass wrappedClass, Set<Method> res) {
    if (!THREAD_START_SIGNATURE.equals(method.getSignature())) {
      return;
    }
    List<WrappedClass> lookupClasses = new ArrayList<>();
    lookupClasses.add(wrappedClass);
    boolean inheritsFromThreadClass = THREAD_CLASS.equals(wrappedClass.getFullyQualifiedName());
    while (wrappedClass.hasSuperclass() && !inheritsFromThreadClass) {
      wrappedClass = wrappedClass.getSuperclass();
      lookupClasses.add(wrappedClass);
      inheritsFromThreadClass = THREAD_CLASS.equals(wrappedClass.getFullyQualifiedName());
    }
    if (!inheritsFromThreadClass) {
      return;
    }
    for (WrappedClass candidateClass : lookupClasses) {
      Optional<Method> runMethod =
          candidateClass.getMethods().stream()
              .filter(m -> THREAD_RUN_SUB_SIGNATURE.equals(m.getSubSignature()))
              .findFirst();
      if (runMethod.isPresent()) {
        res.add(runMethod.get());
        break;
      }
    }
  }

  private final class IterateSolvers<W extends Weight> implements SolverCreationListener<W> {
    private final BackwardQuery query;
    private final Statement invokeExpr;
    private final BiConsumer<Statement, Method> onCallerCalleeFoundCallback;

    private IterateSolvers(
        BackwardQuery query,
        Statement invokeExpr,
        BiConsumer<Statement, Method> onCallerCalleeFoundCallback) {
      this.query = query;
      this.invokeExpr = invokeExpr;
      this.onCallerCalleeFoundCallback = onCallerCalleeFoundCallback;
    }

    @Override
    public void onCreatedSolver(Query q, AbstractBoomerangSolver<W> solver) {
      if (solver instanceof ForwardBoomerangSolver) {
        ForwardQuery forwardQuery = (ForwardQuery) q;
        ForwardBoomerangSolver<W> forwardBoomerangSolver = (ForwardBoomerangSolver<W>) solver;
        forwardBoomerangSolver
            .getFieldAutomaton()
            .registerListener(
                initialState ->
                    forwardBoomerangSolver
                        .getFieldAutomaton()
                        .registerListener(
                            new ExtractAllocationSiteStateListener<W>(
                                initialState, query, (ForwardQuery) q) {

                              @Override
                              protected void allocationSiteFound(
                                  ForwardQuery allocationSite, BackwardQuery query) {
                                logger.debug("Found AllocationSite '{}'.", forwardQuery);
                                queriedInvokeExprAndAllocationSitesFound.add(invokeExpr);
                                Type type = forwardQuery.getType();
                                if (type.isNullType()) {
                                  return;
                                }
                                if (type.isRefType()) {
                                  for (Method calleeMethod :
                                      getMethodFromClassOrFromSuperclass(
                                          invokeExpr.getInvokeExpr().getDeclaredMethod(),
                                          type.getWrappedClass())) {
                                    onCallerCalleeFoundCallback.accept(invokeExpr, calleeMethod);
                                  }
                                } else if (type.isArrayType()) {
                                  Type base = type.getArrayBaseType();
                                  if (base.isRefType()) {
                                    for (Method calleeMethod :
                                        getMethodFromClassOrFromSuperclass(
                                            invokeExpr.getInvokeExpr().getDeclaredMethod(),
                                            base.getWrappedClass())) {
                                      onCallerCalleeFoundCallback.accept(invokeExpr, calleeMethod);
                                    }
                                  }
                                }
                              }
                            }));
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((query == null) ? 0 : query.hashCode());
      result = prime * result + ((invokeExpr == null) ? 0 : invokeExpr.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      IterateSolvers other = (IterateSolvers) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (query == null) {
        if (other.query != null) return false;
      } else if (!query.equals(other.query)) return false;
      if (invokeExpr == null) {
        return other.invokeExpr == null;
      } else return invokeExpr.equals(other.invokeExpr);
    }

    private BoomerangResolver getOuterType() {
      // TODO why is this type of importance?
      return BoomerangResolver.this;
    }
  }
}
