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
package boomerang;

import boomerang.callgraph.CallerListener;
import boomerang.callgraph.ObservableICFG;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.solver.AbstractBoomerangSolver;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.ForwardBoomerangSolver;
import boomerang.util.DefaultValueMap;
import com.google.common.base.Joiner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import sync.pds.solver.nodes.SingleNode;
import wpds.impl.Transition;
import wpds.impl.Weight;
import wpds.impl.WeightedPAutomaton;
import wpds.interfaces.WPAStateListener;

public class QueryGraph<W extends Weight> {
  private static final Logger LOGGER = LoggerFactory.getLogger(QueryGraph.class);
  private final ObservableICFG<Statement, Method> icfg;
  private final Multimap<Query, QueryEdge> sourceToQueryEdgeLookUp = HashMultimap.create();
  private final Multimap<Query, QueryEdge> targetToQueryEdgeLookUp = HashMultimap.create();
  private final Set<Query> roots = new LinkedHashSet<>();
  private final DefaultValueMap<ForwardQuery, ForwardBoomerangSolver<W>> forwardSolvers;
  private final Multimap<Query, AddTargetEdgeListener> edgeAddListener = HashMultimap.create();
  private final DefaultValueMap<BackwardQuery, BackwardBoomerangSolver<W>> backwardSolver;

  public QueryGraph(WeightedBoomerang<W> weightedBoomerang) {
    this.forwardSolvers = weightedBoomerang.getSolvers();
    this.backwardSolver = weightedBoomerang.getBackwardSolvers();
    this.icfg = weightedBoomerang.icfg;
  }

  public void addRoot(Query root) {
    this.roots.add(root);
  }

  public void addEdge(Query parent, Node<Edge, Val> node, Query child) {
    QueryEdge queryEdge = new QueryEdge(parent, node, child);
    sourceToQueryEdgeLookUp.put(parent, queryEdge);
    if (targetToQueryEdgeLookUp.put(child, queryEdge)) {
      for (AddTargetEdgeListener l : Lists.newArrayList(edgeAddListener.get(child))) {
        l.edgeAdded(queryEdge);
      }
    }
    getSolver(parent)
        .getCallAutomaton()
        .registerListener(new SourceListener(new SingleNode<>(node.fact()), parent, child, null));
  }

  private AbstractBoomerangSolver<W> getSolver(Query query) {
    if (query instanceof BackwardQuery) {
      return backwardSolver.get(query);
    }
    return forwardSolvers.get(query);
  }

  public void unregisterAllListeners() {
    this.edgeAddListener.clear();
  }

  public Set<Query> getNodes() {
    Set<Query> nodes = new LinkedHashSet(sourceToQueryEdgeLookUp.keySet());
    nodes.addAll(targetToQueryEdgeLookUp.keySet());
    return nodes;
  }

  private class SourceListener extends WPAStateListener<Edge, INode<Val>, W> {

    private final Query child;
    private final Query parent;
    private final Method callee;

    public SourceListener(INode<Val> state, Query parent, Query child, Method callee) {
      super(state);
      this.parent = parent;
      this.child = child;
      this.callee = callee;
    }

    @Override
    public void onOutTransitionAdded(
        Transition<Edge, INode<Val>> t,
        W w,
        WeightedPAutomaton<Edge, INode<Val>, W> weightedPAutomaton) {
      if (t.getStart() instanceof GeneratedState && callee != null) {
        Edge callSiteLabel = t.getLabel();
        getSolver(child)
            .allowUnbalanced(
                callee,
                (parent instanceof BackwardQuery
                    ? callSiteLabel.getTarget()
                    : callSiteLabel.getStart()));
      }
      if (t.getTarget() instanceof GeneratedState) {
        getSolver(parent)
            .getCallAutomaton()
            .registerListener(
                new SourceListener(t.getTarget(), parent, child, t.getLabel().getMethod()));
      }

      if (weightedPAutomaton.isUnbalancedState(t.getTarget())) {
        registerEdgeListener(new UnbalancedContextListener(child, parent, t));
      }
    }

    @Override
    public void onInTransitionAdded(
        Transition<Edge, INode<Val>> t,
        W w,
        WeightedPAutomaton<Edge, INode<Val>, W> weightedPAutomaton) {}

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + ((callee == null) ? 0 : callee.hashCode());
      result = prime * result + ((child == null) ? 0 : child.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      SourceListener other = (SourceListener) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
      if (callee == null) {
        if (other.callee != null) return false;
      } else if (!callee.equals(other.callee)) return false;
      if (child == null) {
        if (other.child != null) return false;
      } else if (!child.equals(other.child)) return false;
      if (parent == null) {
        return other.parent == null;
      } else return parent.equals(other.parent);
    }

    private QueryGraph getEnclosingInstance() {
      return QueryGraph.this;
    }
  }

  public String toString() {
    String s = "";
    int level = 0;
    for (Query root : roots) {
      s += "Root:" + root + "\n";
      s += visit(root, "", ++level, new LinkedHashSet<>());
    }
    return s;
  }

  public void registerEdgeListener(AddTargetEdgeListener l) {
    if (edgeAddListener.put(l.getTarget(), l)) {
      ArrayList<QueryEdge> edges = Lists.newArrayList(targetToQueryEdgeLookUp.get(l.getTarget()));
      for (QueryEdge edge : edges) {
        l.edgeAdded(edge);
      }
      if (edges.isEmpty()) {
        l.noParentEdge();
      }
    }
  }

  private interface AddTargetEdgeListener {
    Query getTarget();

    void edgeAdded(QueryEdge queryEdge);

    void noParentEdge();
  }

  private class UnbalancedContextListener implements AddTargetEdgeListener {

    private final Transition<Edge, INode<Val>> transition;
    private final Query parent;
    private final Query child;

    public UnbalancedContextListener(Query child, Query parent, Transition<Edge, INode<Val>> t) {
      this.child = child;
      this.parent = parent;
      this.transition = t;
    }

    @Override
    public Query getTarget() {
      return parent;
    }

    @Override
    public void edgeAdded(QueryEdge parentOfParent) {
      Query newParent = parentOfParent.getSource();
      getSolver(newParent)
          .getCallAutomaton()
          .registerListener(
              new SourceListener(
                  new SingleNode<>(parentOfParent.getNode().fact()), newParent, child, null));
    }

    @Override
    public void noParentEdge() {
      if (child instanceof BackwardQuery) {
        Method callee = transition.getTarget().fact().m();
        icfg.addCallerListener(
            new CallerListener<Statement, Method>() {
              @Override
              public Method getObservedCallee() {
                return callee;
              }

              @Override
              public void onCallerAdded(Statement callSite, Method method) {
                getSolver(child).allowUnbalanced(callee, callSite);
              }
            });
      }
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getEnclosingInstance().hashCode();
      result = prime * result + ((child == null) ? 0 : child.hashCode());
      result = prime * result + ((parent == null) ? 0 : parent.hashCode());
      result = prime * result + ((transition == null) ? 0 : transition.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      UnbalancedContextListener other = (UnbalancedContextListener) obj;
      if (!getEnclosingInstance().equals(other.getEnclosingInstance())) return false;
      if (child == null) {
        if (other.child != null) return false;
      } else if (!child.equals(other.child)) return false;
      if (parent == null) {
        if (other.parent != null) return false;
      } else if (!parent.equals(other.parent)) return false;
      if (parent == null) {
        return other.transition == null;
      } else return transition.equals(other.transition);
    }

    private QueryGraph getEnclosingInstance() {
      return QueryGraph.this;
    }
  }

  private String visit(Query parent, String s, int i, Set<Query> visited) {
    for (QueryEdge child : sourceToQueryEdgeLookUp.get(parent)) {
      if (visited.add(child.getTarget())) {
        for (int j = 0; j <= i; j++) {
          s += " ";
        }
        s += i;
        s += child + "\n";
        s += visit(child.getTarget(), "", ++i, visited);
      } else {
      }
    }
    return s;
  }

  public String toDotString() {
    String s = "digraph {\n";
    TreeSet<String> trans = new TreeSet<String>();
    for (Entry<Query, QueryEdge> target : sourceToQueryEdgeLookUp.entries()) {
      String v = "\t\"" + escapeQuotes(target.getKey().toString()) + "\"";
      v += " -> \"" + escapeQuotes(target.getValue().getTarget().toString()) + "\"";
      trans.add(v);
    }

    s += Joiner.on("\n").join(trans);
    s += "}\n";
    return s;
  }

  private String escapeQuotes(String string) {
    return string.replace("\"", "");
  }

  private static class QueryEdge {
    private final Query source;
    private final Query target;
    private final Node<ControlFlowGraph.Edge, Val> node;

    public QueryEdge(Query source, Node<ControlFlowGraph.Edge, Val> node, Query target) {
      this.source = source;
      this.node = node;
      this.target = target;
    }

    public Node<Edge, Val> getNode() {
      return node;
    }

    public Query getSource() {
      return source;
    }

    public Query getTarget() {
      return target;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((source == null) ? 0 : source.hashCode());
      result = prime * result + ((node == null) ? 0 : node.hashCode());
      result = prime * result + ((target == null) ? 0 : target.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      QueryEdge other = (QueryEdge) obj;
      if (source == null) {
        if (other.source != null) return false;
      } else if (!source.equals(other.source)) return false;
      if (target == null) {
        if (other.target != null) return false;
      } else if (!target.equals(other.target)) return false;
      if (node == null) {
        return other.node == null;
      } else return node.equals(other.node);
    }
  }

  public boolean isRoot(Query q) {
    return roots.contains(q);
  }
}
