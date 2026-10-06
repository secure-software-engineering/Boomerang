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
package boomerang.scope.wala;

import boomerang.scope.LazyCallGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import com.google.common.collect.Lists;
import com.ibm.wala.classLoader.CallSiteReference;
import com.ibm.wala.classLoader.IMethod;
import com.ibm.wala.ipa.callgraph.CGNode;
import com.ibm.wala.ipa.cha.IClassHierarchy;
import com.ibm.wala.ssa.SSAAbstractInvokeInstruction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/** Forwards to WALA's call graph, which is kept alive by this object. */
public class WALACallGraph extends LazyCallGraph {

  private final com.ibm.wala.ipa.callgraph.CallGraph cg;
  private final IClassHierarchy cha;
  private final Map<IMethod, WALAMethod> iMethodToWALAMethod = new HashMap<>();

  // The node whose call sites represent each method: the first one reached from the entry points
  private Map<IMethod, CGNode> expandedNodes;

  public WALACallGraph(com.ibm.wala.ipa.callgraph.CallGraph cg, IClassHierarchy cha) {
    this.cg = cg;
    this.cha = cha;
  }

  private Map<IMethod, CGNode> expandedNodes() {
    if (expandedNodes != null) {
      return expandedNodes;
    }

    expandedNodes = new LinkedHashMap<>();
    LinkedList<CGNode> worklist = Lists.newLinkedList(cg.getEntrypointNodes());
    while (!worklist.isEmpty()) {
      CGNode curr = worklist.poll();
      if (ignore(curr.getMethod())) continue;
      if (expandedNodes.putIfAbsent(curr.getMethod(), curr) != null) continue;
      cg.getSuccNodes(curr).forEachRemaining(worklist::add);
    }
    return expandedNodes;
  }

  private WALAMethod getOrCreate(CGNode curr) {
    IMethod method = curr.getMethod();
    WALAMethod walaMethod = iMethodToWALAMethod.get(method);
    if (walaMethod != null) return walaMethod;
    WALAMethod m = new WALAMethod(curr.getMethod(), curr.getIR(), cha);
    iMethodToWALAMethod.put(method, m);
    return m;
  }

  private boolean ignore(IMethod method) {
    return method.isBridge()
        || method.isClinit()
        || method.isNative()
        || method.isSynthetic()
        || method.isWalaSynthetic();
  }

  @Override
  protected Collection<Edge> computeEdgesOutOf(Statement callSite) {
    if (!(callSite instanceof WALAStatement) || !(callSite.getMethod() instanceof WALAMethod)) {
      return new ArrayList<>();
    }

    CGNode node = expandedNodes().get(((WALAMethod) callSite.getMethod()).getDelegate());
    if (node == null) {
      return new ArrayList<>();
    }

    CallSiteReference site =
        ((SSAAbstractInvokeInstruction) ((WALAStatement) callSite).getDelegate()).getCallSite();
    Collection<Edge> edges = new ArrayList<>();
    for (CGNode succ : cg.getPossibleTargets(node, site)) {
      if (!ignore(succ.getMethod())) {
        edges.add(new Edge(callSite, getOrCreate(succ)));
      }
    }
    return edges;
  }

  @Override
  protected Collection<Edge> computeEdgesInto(Method callee) {
    Collection<Edge> edges = new ArrayList<>();
    if (!(callee instanceof WALAMethod)) {
      return edges;
    }

    IMethod calleeMethod = ((WALAMethod) callee).getDelegate();
    if (ignore(calleeMethod)) {
      return edges;
    }

    Map<IMethod, CGNode> expanded = expandedNodes();
    for (CGNode node : cg.getNodes(calleeMethod.getReference())) {
      for (Iterator<CGNode> preds = cg.getPredNodes(node); preds.hasNext(); ) {
        CGNode pred = preds.next();
        if (expanded.get(pred.getMethod()) != pred) {
          continue;
        }

        for (Iterator<CallSiteReference> sites = cg.getPossibleSites(pred, node);
            sites.hasNext(); ) {
          CallSiteReference ref = sites.next();
          for (SSAAbstractInvokeInstruction i : pred.getIR().getCalls(ref)) {
            edges.add(new Edge(new WALAStatement(i, getOrCreate(pred)), getOrCreate(node)));
          }
        }
      }
    }
    return edges;
  }

  @Override
  protected Collection<Method> computeEntryPoints() {
    Collection<Method> entryPoints = new ArrayList<>();
    for (CGNode e : cg.getEntrypointNodes()) {
      entryPoints.add(getOrCreate(e));
    }
    return entryPoints;
  }

  @Override
  protected Collection<Method> computeReachableMethods() {
    Set<Method> reachable = new LinkedHashSet<>();
    for (CGNode node : expandedNodes().values()) {
      for (Iterator<CGNode> succs = cg.getSuccNodes(node); succs.hasNext(); ) {
        CGNode succ = succs.next();
        if (!ignore(succ.getMethod())) {
          reachable.add(getOrCreate(succ));
        }
      }
    }
    return reachable;
  }
}
