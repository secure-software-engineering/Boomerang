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
package typestate.finiteautomata;

import boomerang.WeightedForwardQuery;
import boomerang.scope.AllocVal;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.pds.solver.WeightFunctions;
import sync.pds.solver.nodes.Node;
import typestate.TransitionFunction;
import typestate.TransitionFunctionImpl;
import typestate.TransitionFunctionOne;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;

public abstract class TypeStateMachineWeightFunctions
    implements WeightFunctions<Edge, Val, Edge, TransitionFunction> {
  private static final Logger LOGGER =
      LoggerFactory.getLogger(TypeStateMachineWeightFunctions.class);
  public Set<MatcherTransition> transition = new HashSet<>();

  public void addTransition(MatcherTransition trans) {
    transition.add(trans);
  }

  @Override
  public TransitionFunction getOne() {
    return TransitionFunctionOne.one();
  }

  public TransitionFunction pop(Node<Edge, Val> curr) {
    LOGGER.trace("Getting pop weights for {} which returns to {}", curr);
    return getOne();
  }

  public TransitionFunction push(Node<Edge, Val> curr, Node<Edge, Val> succ, Edge push) {
    return getMatchingTransitions(
        succ.stmt(),
        succ.fact(),
        push,
        Collections2.filter(
            transition,
            input ->
                input.getType().equals(Type.OnCall)
                    || input.getType().equals(Type.OnCallOrOnCallToReturn)),
        Type.OnCall);
  }

  @Override
  public TransitionFunction normal(Node<Edge, Val> curr, Node<Edge, Val> succ) {
    Statement successor = succ.stmt().getStart();
    if (successor.containsInvokeExpr()) {
      return callToReturn(curr, succ, successor.getInvokeExpr());
    }
    return getOne();
  }

  public TransitionFunction callToReturn(
      Node<Edge, Val> curr, Node<Edge, Val> succ, InvokeExpr invokeExpr) {
    Set<Transition> res = new LinkedHashSet<>();
    if (invokeExpr.isInstanceInvokeExpr()) {
      if (invokeExpr.getBase().equals(succ.fact())) {
        for (MatcherTransition trans : transition) {
          if (trans.matches(invokeExpr.getDeclaredMethod())
              && (trans.getType().equals(Type.OnCallToReturn)
                  || trans.getType().equals(Type.OnCallOrOnCallToReturn))) {
            res.add(trans);
          }
        }
      }
    }
    if (!res.isEmpty()) {
      LOGGER.trace("Typestate transition at {} to {}, [{}]", succ.stmt(), res, Type.OnCallToReturn);
    }
    return (res.isEmpty() ? getOne() : new TransitionFunctionImpl(res, succ.stmt().getStart()));
  }

  private TransitionFunction getMatchingTransitions(
      Edge edge,
      Val node,
      Edge transitionEdge,
      Collection<MatcherTransition> filteredTrans,
      Type type) {
    Statement transitionStmt = transitionEdge.getStart();
    Set<Transition> res = new HashSet<>();
    if (filteredTrans.isEmpty() || !transitionStmt.containsInvokeExpr()) return getOne();
    for (MatcherTransition trans : filteredTrans) {
      if (trans.matches(transitionStmt.getInvokeExpr().getDeclaredMethod())) {
        LOGGER.trace(
            "Found potential transition at {}, now checking if parameter match", transitionStmt);
        Parameter param = trans.getParam();
        if (param.equals(Parameter.This) && edge.getMethod().isThisLocal(node))
          res.add(new TransitionImpl(trans.from(), trans.to()));
        if (param.equals(Parameter.Param1) && edge.getMethod().getParameterLocal(0).equals(node))
          res.add(new TransitionImpl(trans.from(), trans.to()));
        if (param.equals(Parameter.Param2) && edge.getMethod().getParameterLocal(1).equals(node))
          res.add(new TransitionImpl(trans.from(), trans.to()));
      }
    }

    if (res.isEmpty()) return getOne();

    LOGGER.debug("Typestate transition at {} to {}, [{}]", transitionStmt, res, type);
    return new TransitionFunctionImpl(res, transitionStmt);
  }

  /*
  // TODO: [ms] re-enable
  protected List<SootClass> getSubclassesOf(String className) {
    SootClass sootClass = Scene.v().getSootClass(className);
    List<SootClass> list = Scene.v().getActiveHierarchy().getSubclassesOfIncluding(sootClass);
    List<SootClass> res = new LinkedList<>();
    for (SootClass c : list) {
      res.add(c);
    }
    return res;
  }
  */

  protected Collection<WeightedForwardQuery<TransitionFunction>> getLeftSideOf(Edge edge) {
    Statement s = edge.getStart();
    if (s.isAssignStmt()) {
      return Collections.singleton(
          new WeightedForwardQuery<>(
              edge, new AllocVal(s.getLeftOp(), s, s.getRightOp()), initialTransition(edge)));
    }
    return Collections.emptySet();
  }

  protected Collection<WeightedForwardQuery<TransitionFunction>> generateAtAllocationSiteOf(
      Edge edge, Class<?> allocationSuperType) {
    Statement s = edge.getStart();
    if (s.isAssignStmt()) {
      if (s.getRightOp().isNewExpr()) {
        boomerang.scope.Type newExprType = s.getRightOp().getNewExprType();
        if (newExprType.isSubtypeOf(allocationSuperType.getName())) {
          TransitionFunction function =
              new TransitionFunctionImpl(new TransitionImpl(initialState(), initialState()), s);
          return Collections.singleton(
              new WeightedForwardQuery<>(
                  edge, new AllocVal(s.getLeftOp(), s, s.getRightOp()), function));
        }
      }
    }
    return Collections.emptySet();
  }

  public Collection<WeightedForwardQuery<TransitionFunction>> generateThisAtAnyCallSitesOf(
      Edge edge, String declaredType, String declaredMethod) {
    Statement unit = edge.getStart();
    if (unit.containsInvokeExpr()) {
      if (unit.getInvokeExpr().isInstanceInvokeExpr()) {
        Val base = unit.getInvokeExpr().getBase();
        // TODO
        //  Soot, SootUp and Opal use different signatures. Hence, we should move away from
        //  using basic pattern matching to identify correct methods
        String sig = " " + unit.getInvokeExpr().getDeclaredMethod().getName();
        if (sig.matches(declaredMethod)) {
          if (base.getType().isSubtypeOf(declaredType)) {
            return Collections.singleton(
                new WeightedForwardQuery<>(
                    edge, new AllocVal(base, unit, base), initialTransition(edge)));
          }
        }
      }
    }
    return Collections.emptySet();
  }

  @Override
  public String toString() {
    return Joiner.on("\n").join(transition);
  }

  public abstract Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(Edge stmt);

  public TransitionFunction initialTransition(Edge edge) {
    return new TransitionFunctionImpl(
        new TransitionImpl(initialState(), initialState()), edge.getStart());
  }

  protected abstract State initialState();
}
