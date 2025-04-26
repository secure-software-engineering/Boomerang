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
package boomerang.flowfunction;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.IArrayRef;
import boomerang.scope.IInstanceFieldRef;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.solver.BackwardBoomerangSolver;
import boomerang.solver.Strategies;
import com.google.common.collect.Multimap;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
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
    Set<Val> out = new LinkedHashSet<>();
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
    Set<Val> out = new LinkedHashSet<>();
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
  public Collection<State> normalFlow(Edge currEdge, Edge nextEdge, Val fact) {
    Statement nextStmt = nextEdge.getTarget();
    if (options
        .allocationSite()
        .getAllocationSite(nextStmt.getMethod(), nextStmt, fact)
        .isPresent()) {
      return Collections.emptySet();
    }
    if (nextStmt.isThrowStmt()) {
      return Collections.emptySet();
    }
    Set<State> out = new LinkedHashSet<>();

    boolean leftSideMatches = false;
    if (nextStmt.isAssignStmt()) {
      Val leftOp = nextStmt.getLeftOp();
      Val rightOp = nextStmt.getRightOp();
      if (leftOp.equals(fact)) {
        leftSideMatches = true;
        if (nextStmt.isFieldLoad()) {
          if (options.trackFields()) {
            IInstanceFieldRef ifr = nextStmt.getFieldLoad();
            if (options.includeInnerClassFields() || !ifr.getField().isInnerClassField()) {
              out.add(new PushNode<>(nextEdge, ifr.getBase(), ifr.getField(), PDSSystem.FIELDS));
            }
          }
        } else if (nextStmt.isStaticFieldLoad()) {
          if (options.trackFields()) {
            strategies
                .getStaticFieldStrategy()
                .handleBackward(currEdge, nextStmt.getLeftOp(), nextStmt.getStaticField(), out);
          }
        } else if (rightOp.isArrayRef()) {
          IArrayRef arrayBase = nextStmt.getArrayBase();
          if (options.trackFields()) {
            strategies.getArrayHandlingStrategy().handleBackward(nextEdge, arrayBase, out);
          }
        } else if (rightOp.isCast()) {
          out.add(new Node<>(nextEdge, rightOp.getCastOp()));
        } else if (nextStmt.isPhiStatement()) {
          Collection<Val> phiVals = nextStmt.getPhiVals();
          for (Val v : phiVals) {
            out.add(new Node<>(nextEdge, v));
          }
        } else {
          if (nextStmt.isFieldLoadWithBase(fact)) {
            out.add(new ExclusionNode<>(nextEdge, fact, nextStmt.getLoadedField()));
          } else {
            out.add(new Node<>(nextEdge, rightOp));
          }
        }
      }
      if (nextStmt.isFieldStore()) {
        IInstanceFieldRef ifr = nextStmt.getFieldStore();
        Val base = ifr.getBase();
        if (base.equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(nextEdge, rightOp, ifr.getField());
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      } else if (nextStmt.isStaticFieldStore()) {
        StaticFieldVal staticField = nextStmt.getStaticField();
        if (fact.isStatic() && fact.equals(staticField)) {
          out.add(new Node<>(nextEdge, rightOp));
        }
      } else if (leftOp.isArrayRef()) {
        IArrayRef arrayBase = nextStmt.getArrayBase();
        if (arrayBase.getBase().equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(nextEdge, rightOp, Field.array(arrayBase.getIndex()));
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      }
    }
    if (!leftSideMatches) out.add(new Node<>(nextEdge, fact));
    return out;
  }

  @Override
  public Collection<State> callToReturnFlow(Edge currEdge, Edge nextEdge, Val fact) {
    if (FlowFunctionUtils.isSystemArrayCopy(
        nextEdge.getTarget().getInvokeExpr().getDeclaredMethod())) {
      return systemArrayCopyFlow(nextEdge, fact);
    }
    return normalFlow(currEdge, nextEdge, fact);
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
