/**
 * ***************************************************************************** 
 * Copyright (c) 2025 Fraunhofer IEM, Paderborn, Germany. This program and the
 * accompanying materials are made available under the terms of the Eclipse
 * Public License 2.0 which is available at http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.flowfunction;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.Strategies;
import com.google.common.collect.Multimap;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import sync.pds.solver.SyncPDSSolver.PDSSystem;
import sync.pds.solver.nodes.ExclusionNode;
import sync.pds.solver.nodes.Node;
import sync.pds.solver.nodes.NodeWithLocation;
import sync.pds.solver.nodes.PopNode;
import sync.pds.solver.nodes.PushNode;
import wpds.interfaces.State;

public class DefaultBackwardFlowFunction implements IBackwardFlowFunction {

  private final DefaultBackwardFlowFunctionOptions options;
  private Strategies strategies;

  public DefaultBackwardFlowFunction(DefaultBackwardFlowFunctionOptions options) {
    this.options = options;
  }

  @Override
  public Collection<Val> returnFlow(Method callee, Statement returnStmt, Val returnedVal) {
    Set<Val> out = Sets.newHashSet();
    if (!callee.isStatic()) {
      if (callee.getThisLocal().equals(returnedVal)) {
        out.add(returnedVal);
      }
    }
    for (Val param : callee.getParameterLocals()) {
      if (param.equals(returnedVal)) {
        out.add(returnedVal);
      }
    }
    if (callee.isStatic()) {
      out.add(returnedVal);
    }
    return out;
  }

  @Override
  public Collection<Val> callFlow(Statement callSite, Val fact, Method callee, Statement calleeSp) {
    if (!callSite.containsInvokeExpr()) {
      throw new RuntimeException("Call site does not contain an invoke expression.");
    }
    InvokeExpr invokeExpr = callSite.getInvokeExpr();
    Set<Val> out = Sets.newHashSet();
    if (invokeExpr.isInstanceInvokeExpr()) {
      if (invokeExpr.getBase().equals(fact) && !callee.isStatic()) {
        out.add(callee.getThisLocal());
      }
    }
    List<Val> parameterLocals = callee.getParameterLocals();
    int i = 0;
    for (Val arg : invokeExpr.getArgs()) {
      if (arg.equals(fact) && parameterLocals.size() > i) {
        Val param = parameterLocals.get(i);
        out.add(param);
      }
      i++;
    }

    if (callSite.isAssignStmt() && calleeSp.isReturnStmt()) {
      if (callSite.getLeftOp().equals(fact)) {
        out.add(calleeSp.getReturnOp());
      }
    }
    if (fact.isStatic()) {
      out.add(fact.withNewMethod(callee));
    }
    return out;
  }

  @Override
  public Collection<State> normalFlow(Edge currEdge, Val fact) {
    Statement curr = currEdge.getTarget();
    if (options.allocationSite().getAllocationSite(curr.getMethod(), curr, fact).isPresent()) {
      return Collections.emptySet();
    }
    if (curr.isThrowStmt()) {
      return Collections.emptySet();
    }
    Set<State> out = Sets.newHashSet();

    boolean leftSideMatches = false;
    if (curr.isAssignStmt()) {
      Val leftOp = curr.getLeftOp();
      Val rightOp = curr.getRightOp();
      if (leftOp.equals(fact)) {
        leftSideMatches = true;
        if (curr.isFieldLoad()) {
          if (options.trackFields()) {
            Pair<Val, Field> ifr = curr.getFieldLoad();
            if (options.includeInnerClassFields() || !ifr.getY().isInnerClassField()) {
              out.add(new PushNode<>(currEdge, ifr.getX(), ifr.getY(), PDSSystem.FIELDS));
            }
          }
        } else if (curr.isStaticFieldLoad()) {
          if (options.trackFields()) {
            strategies
                .getStaticFieldStrategy()
                .handleBackward(currEdge, curr.getLeftOp(), curr.getStaticField(), out);
          }
        } else if (rightOp.isArrayRef()) {
          Pair<Val, Integer> arrayBase = curr.getArrayBase();
          if (options.trackFields()) {
            strategies.getArrayHandlingStrategy().handleBackward(currEdge, arrayBase, out);
          }
        } else if (rightOp.isCast()) {
          out.add(new Node<>(currEdge, rightOp.getCastOp()));
        } else if (curr.isPhiStatement()) {
          Collection<Val> phiVals = curr.getPhiVals();
          for (Val v : phiVals) {
            out.add(new Node<>(currEdge, v));
          }
        } else {
          if (curr.isFieldLoadWithBase(fact)) {
            out.add(new ExclusionNode<>(currEdge, fact, curr.getLoadedField()));
          } else {
            out.add(new Node<>(currEdge, rightOp));
          }
        }
      }
      if (curr.isFieldStore()) {
        Pair<Val, Field> ifr = curr.getFieldStore();
        Val base = ifr.getX();
        if (base.equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(currEdge, rightOp, ifr.getY());
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      } else if (curr.isStaticFieldStore()) {
        StaticFieldVal staticField = curr.getStaticField();
        if (fact.isStatic() && fact.equals(staticField)) {
          out.add(new Node<>(currEdge, rightOp));
        }
      } else if (leftOp.isArrayRef()) {
        Pair<Val, Integer> arrayBase = curr.getArrayBase();
        if (arrayBase.getX().equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(currEdge, rightOp, Field.array(arrayBase.getY()));
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      }
    }
    if (!leftSideMatches) out.add(new Node<>(currEdge, fact));
    return out;
  }

  @Override
  public Collection<State> callToReturnFlow(Edge edge, Val fact) {
    if (FlowFunctionUtils.isSystemArrayCopy(edge.getTarget().getInvokeExpr().getMethod())) {
      return systemArrayCopyFlow(edge, fact);
    }
    return normalFlow(edge, fact);
  }

  @Override
  public void setSolver(
      BackwardBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements) {
    this.strategies =
        new Strategies(
            options.staticFieldStrategy(),
            options.arrayStrategy(),
            solver,
            fieldLoadStatements,
            fieldStoreStatements);
  }

  protected Collection<State> systemArrayCopyFlow(Edge edge, Val fact) {
    Statement callSite = edge.getTarget();
    if (fact.equals(callSite.getInvokeExpr().getArg(2))) {
      Val arg = callSite.getInvokeExpr().getArg(0);
      return Collections.singleton(new Node<>(edge, arg));
    }
    return Collections.emptySet();
  }
}
