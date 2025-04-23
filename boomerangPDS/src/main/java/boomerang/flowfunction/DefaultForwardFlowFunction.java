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

import boomerang.ForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Field;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.solver.ForwardBoomerangSolver;
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

public class DefaultForwardFlowFunction implements IForwardFlowFunction {

  private final DefaultForwardFlowFunctionOptions options;
  private Strategies strategies;

  public DefaultForwardFlowFunction(DefaultForwardFlowFunctionOptions options) {
    this.options = options;
  }

  @Override
  public Set<Val> returnFlow(Method method, Statement curr, Val value) {
    Set<Val> out = new LinkedHashSet<>();
    if (curr.isThrowStmt() && !options.throwFlows()) {
      return Collections.emptySet();
    }
    if (curr.isReturnStmt()) {
      if (curr.getReturnOp().equals(value)) {
        out.add(value);
      }
    }
    if (!method.isStatic()) {
      if (method.getThisLocal().equals(value)) {
        out.add(value);
      }
    }
    for (Val param : method.getParameterLocals()) {
      if (param.equals(value)) {
        out.add(value);
      }
    }
    if (value.isStatic()) {
      out.add(value);
    }
    return out;
  }

  @Override
  public Set<Val> callFlow(Statement callSite, Val fact, Method callee) {
    if (!callSite.containsInvokeExpr()) {
      throw new RuntimeException("Call site does not contain an invoke expression.");
    }
    if (callee.isStaticInitializer()) {
      return Collections.emptySet();
    }
    Set<Val> out = new LinkedHashSet<>();
    InvokeExpr invokeExpr = callSite.getInvokeExpr();
    if (invokeExpr.isInstanceInvokeExpr()) {
      if (invokeExpr.getBase().equals(fact) && !callee.isStatic()) {
        out.add(callee.getThisLocal());
      }
    }
    int i = 0;
    List<Val> parameterLocals = callee.getParameterLocals();
    for (Val arg : invokeExpr.getArgs()) {
      if (arg.equals(fact) && parameterLocals.size() > i) {
        out.add(parameterLocals.get(i));
      }
      i++;
    }
    if (fact.isStatic()) {
      out.add(fact.withNewMethod(callee));
    }
    return out;
  }

  @Override
  public Set<State> normalFlow(ForwardQuery query, Edge nextEdge, Val fact) {
    Statement nextStmt = nextEdge.getStart();
    Set<State> out = new LinkedHashSet<>();
    if (killFlow(nextStmt, fact)) {
      return out;
    }
    if (!nextStmt.isFieldWriteWithBase(fact)) {
      // always maintain data-flow if not a field write // killFlow has
      // been taken care of
      if (!options.trackReturnOfInstanceOf()
          || !(query.getType().isNullType() && nextStmt.isInstanceOfStatement(fact))) {
        out.add(new Node<>(nextEdge, fact));
      }
    } else {
      out.add(new ExclusionNode<>(nextEdge, fact, nextStmt.getWrittenField()));
    }
    if (nextStmt.isAssignStmt()) {
      Val leftOp = nextStmt.getLeftOp();
      Val rightOp = nextStmt.getRightOp();
      if (rightOp.equals(fact)) {
        if (nextStmt.isFieldStore()) {
          Pair<Val, Field> ifr = nextStmt.getFieldStore();
          if (options.trackFields()) {
            if (options.includeInnerClassFields() || !ifr.getY().isInnerClassField()) {
              out.add(new PushNode<>(nextEdge, ifr.getX(), ifr.getY(), PDSSystem.FIELDS));
            }
          }
        } else if (nextStmt.isStaticFieldStore()) {
          StaticFieldVal sf = nextStmt.getStaticField();
          if (options.trackFields()) {
            strategies.getStaticFieldStrategy().handleForward(nextEdge, rightOp, sf, out);
          }
        } else if (leftOp.isArrayRef()) {
          Pair<Val, Integer> arrayBase = nextStmt.getArrayBase();
          if (options.trackFields()) {
            strategies.getArrayHandlingStrategy().handleForward(nextEdge, arrayBase, out);
          }
        } else {
          out.add(new Node<>(nextEdge, leftOp));
        }
      }
      if (nextStmt.isFieldLoad()) {
        Pair<Val, Field> ifr = nextStmt.getFieldLoad();
        if (ifr.getX().equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(nextEdge, leftOp, ifr.getY());
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      } else if (nextStmt.isStaticFieldLoad()) {
        StaticFieldVal sf = nextStmt.getStaticField();
        if (fact.isStatic() && fact.equals(sf)) {
          out.add(new Node<>(nextEdge, leftOp));
        }
      } else if (rightOp.isArrayRef()) {
        Pair<Val, Integer> arrayBase = nextStmt.getArrayBase();
        if (arrayBase.getX().equals(fact)) {
          NodeWithLocation<Edge, Val, Field> succNode =
              new NodeWithLocation<>(nextEdge, leftOp, Field.array(arrayBase.getY()));
          out.add(new PopNode<>(succNode, PDSSystem.FIELDS));
        }
      } else if (rightOp.isCast()) {
        if (rightOp.getCastOp().equals(fact)) {
          out.add(new Node<>(nextEdge, leftOp));
        }
      } else if (rightOp.isInstanceOfExpr()
          && query.getType().isNullType()
          && options.trackReturnOfInstanceOf()) {
        if (rightOp.getInstanceOfOp().equals(fact)) {
          out.add(new Node<>(nextEdge, fact.withSecondVal(leftOp)));
        }
      } else if (nextStmt.isPhiStatement()) {
        Collection<Val> phiVals = nextStmt.getPhiVals();
        if (phiVals.contains(fact)) {
          out.add(new Node<>(nextEdge, nextStmt.getLeftOp()));
        }
      }
    }

    return out;
  }

  protected boolean killFlow(Statement curr, Val value) {
    if (curr.isThrowStmt() || curr.isCatchStmt()) {
      return true;
    }

    if (curr.isAssignStmt()) {
      // Kill x at any statement x = * during propagation.
      if (curr.getLeftOp().equals(value)) {
        // But not for a statement x = x.f
        if (curr.isFieldLoad()) {
          Pair<Val, Field> ifr = curr.getFieldLoad();
          return !ifr.getX().equals(value);
        }
        return true;
      }
      if (curr.isStaticFieldStore()) {
        StaticFieldVal sf = curr.getStaticField();
        return value.isStatic() && value.equals(sf);
      }
    }
    return false;
  }

  @Override
  public Collection<State> callToReturnFlow(ForwardQuery query, Edge edge, Val fact) {
    if (FlowFunctionUtils.isSystemArrayCopy(edge.getStart().getInvokeExpr().getDeclaredMethod())) {
      return systemArrayCopyFlow(edge, fact);
    }
    return normalFlow(query, edge, fact);
  }

  protected Collection<State> systemArrayCopyFlow(Edge edge, Val value) {
    Statement callSite = edge.getStart();
    if (value.equals(callSite.getInvokeExpr().getArg(0))) {
      Val arg = callSite.getInvokeExpr().getArg(2);
      return Collections.singleton(new Node<>(edge, arg));
    }
    return Collections.emptySet();
  }

  @Override
  public void setSolver(
      ForwardBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements) {
    this.strategies =
        new Strategies(
            options.getStaticFieldStrategy(),
            options.getArrayStrategy(),
            solver,
            fieldLoadStatements,
            fieldStoreStatements);
  }
}
