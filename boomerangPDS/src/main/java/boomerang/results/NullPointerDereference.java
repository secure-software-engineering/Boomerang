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
package boomerang.results;

import boomerang.Query;
import boomerang.pathtracking.DataFlowPathWeight;
import boomerang.pathtracking.DataFlowPathWeightImpl;
import boomerang.pathtracking.PathConditionWeight;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.IInstanceFieldRef;
import boomerang.scope.IfStatement;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.ValCollection;
import boomerang.scope.fields.EmptyField;
import boomerang.solver.ForwardBoomerangSolver;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sync.pds.solver.nodes.GeneratedState;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.PAutomaton;
import wpds.impl.Transition;
import wpds.impl.WeightedPAutomaton;

/**
 * TODO This class requires a complete revisit. It is not clear what is supposed to happen and if it
 * works with the refactored scopes in 3.0.0+
 */
public class NullPointerDereference implements AffectedLocation {
  public static final int RULE_INDEX = 0;

  private static final Logger LOGGER = LoggerFactory.getLogger(NullPointerDereference.class);
  private final ForwardBoomerangSolver<?> solver;
  private final ControlFlowGraph.Edge statement;
  private final Val variable;
  private final PAutomaton<Statement, INode<Val>> openingContext;
  private final PAutomaton<Statement, INode<Val>> closingContext;
  private final List<PathElement> dataFlowPath;
  private final ControlFlowGraph.Edge sourceStatement;
  private final Val sourceVariable;
  private final Query query;

  private final boolean trackDataFlowPath;
  private final boolean pruneContradictoryDataFlowPath;
  private final boolean pruneImplicitFlows;

  public NullPointerDereference(
      ForwardBoomerangSolver<?> solver,
      Query query,
      ControlFlowGraph.Edge statement,
      Val variable,
      PAutomaton<Statement, INode<Val>> openingContext,
      PAutomaton<Statement, INode<Val>> closingContext,
      List<PathElement> dataFlowPath,
      boolean trackDataFlowPath,
      boolean pruneContradictoryDataFlowPath,
      boolean pruneImplicitFlows) {
    this.solver = solver;
    this.query = query;
    this.sourceStatement = query.cfgEdge();
    this.sourceVariable = query.var();
    this.statement = statement;
    this.variable = variable;
    this.openingContext = openingContext;
    this.closingContext = closingContext;
    this.dataFlowPath = dataFlowPath;

    this.trackDataFlowPath = trackDataFlowPath;
    this.pruneContradictoryDataFlowPath = pruneContradictoryDataFlowPath;
    this.pruneImplicitFlows = pruneImplicitFlows;
  }

  /**
   * The variable that contains "null" and which provokes at {@link #getStatement() the statement} a
   * NullPointerException.
   *
   * @return the variable that contains a null pointer
   */
  public Val getVariable() {
    return variable;
  }

  @Override
  public List<PathElement> getDataFlowPath() {
    return dataFlowPath;
  }

  @Override
  public String getMessage() {
    return "Potential **null pointer** dereference";
  }

  @Override
  public int getRuleIndex() {
    return RULE_INDEX;
  }

  /**
   * The statement at which a null pointer occurred.
   *
   * <p>A null pointer can occur at three different types of statements: y = x.toString(); or y =
   * lengthof(x); or y = x.f;
   *
   * @return the statement where the respective {@link #getVariable() getVariable} is null
   */
  public ControlFlowGraph.Edge getStatement() {
    return statement;
  }

  /**
   * The source statement of the data-flow, i.e., the statement that assigns null to a variable.
   *
   * <p>Examples are: x = null or x = System.getProperty(...).
   *
   * @return The source statement of the data-flow/null pointer.
   */
  public ControlFlowGraph.Edge getSourceStatement() {
    return sourceStatement;
  }

  /**
   * The source variable at the source statement. At a statement x = null or x = System.getProperty,
   * this will be the variable x.
   *
   * @return The source variable of the data-flow propagation
   */
  public Val getSourceVariable() {
    return sourceVariable;
  }

  /**
   * Returns the method of the statement at which the null pointer occurs.
   *
   * @return The SootMethod of the null pointer statement
   */
  public Method getMethod() {
    return getStatement().getStart().getMethod();
  }

  /**
   * The opening context of a NullPointer provides the call stack under which the null pointer
   * occurs.
   *
   * <pre>
   * main(){
   * 	Object x = null;
   * 	foo(x); //call site context "c1"
   * 	Object y = new Object();
   * 	foo(y); //call site context "c2"
   * }
   * foo(Object z){
   * 	z.toString() // Variable z is null here under context c1, but *not* under c2)
   * }
   * </pre>
   *
   * In the example above, z is null under the calling context of call site c1.
   *
   * <p>In the case of branching, there can be multiple call site contexts leading to a null
   * pointer. Therefore, the opening context is represented as an automaton (or graph). The edges of
   * the automaton are labeled by the call sites, the nodes are labeled by variables or by variables
   * at a context. For the example above, the automaton contains a transition with label foo(x)
   *
   * @return The automaton representation of the opening context.
   */
  public PAutomaton<Statement, INode<Val>> getOpeningContext() {
    return openingContext;
  }

  /**
   * The closing context of a NullPointer provides the call stack via which a variable containing
   * null returns to a caller.
   *
   * <pre>
   * main(){
   * 	Object x;
   *  if(...){
   * 	 	x = returnNull(); //b1
   *  } else {
   *  	x = returnNotNull(); //b2
   *  }
   * 	x.toString() // Variable x is null here when the program executes along branch b1
   * }
   * Object returnNull(){
   * 	Object y = null;
   *  return y;
   * }
   * </pre>
   *
   * In the case above, a null pointer exception occurs when the program executes along branch b1.
   *
   * <p>There can be multiple contexts leading to a null pointer. Therefore, the closing context is
   * represented as an automaton (or graph). The edges of the automaton are labeled by the call
   * sites, the nodes are labeled by variables or by variables at a context. For the example above,
   * the automaton contains a transition with label returnNull(). This indicates, that the null
   * pointer only occurs along branch b1 but not b2.
   *
   * @return The automaton representation of the closing context.
   */
  public PAutomaton<Statement, INode<Val>> getClosingContext() {
    return closingContext;
  }

  @Override
  public String toString() {
    String str = "Null Pointer: \n";
    str += "defined at " + getSourceStatement().getStart().getMethod();
    str += (getVariable() != null ? "\tVariable: " + getVariable() : "");
    str += "\n\tStatement: " + getStatement() + "\n\tMethod: " + getMethod();
    return str;
  }

  public Query getQuery() {
    return query;
  }

  public static boolean isNullPointerNode(Node<ControlFlowGraph.Edge, Val> nullPointerNode) {
    Val fact = nullPointerNode.fact();
    Method m = fact.m();
    // A 'this' variable can never be null.
    if (!m.isStatic() && m.getThisLocal().equals(fact)) {
      return false;
    }
    Statement curr = nullPointerNode.stmt().getStart();
    if (curr.containsInvokeExpr()) {
      if (curr.getInvokeExpr().isInstanceInvokeExpr()) {
        Val invocationBase = curr.getInvokeExpr().getBase();
        if (invocationBase.equals(fact)) {
          return true;
        }
      }
    }
    if (curr.isAssignStmt()) {
      if (curr.isFieldLoad()) {
        IInstanceFieldRef ifr = curr.getFieldLoad();
        if (ifr.getBase().equals(fact)) {
          return true;
        }
      }
      if (curr.getRightOp().isLengthExpr()) {
        Val lengthOp = curr.getRightOp().getLengthOp();
        return lengthOp.equals(fact);
      }
    }
    return false;
  }

  public QueryResults getPotentialNullPointerDereferences() {
    Collection<Node<ControlFlowGraph.Edge, Val>> res = new LinkedHashSet<>();
    for (Transition<Field, INode<Node<ControlFlowGraph.Edge, Val>>> t :
        solver.getFieldAutomaton().getTransitions()) {
      if (!t.getLabel().equals(EmptyField.getInstance())
          || t.getStart() instanceof GeneratedState) {
        continue;
      }
      Node<ControlFlowGraph.Edge, Val> nullPointerNode = t.getStart().fact();
      if (NullPointerDereference.isNullPointerNode(nullPointerNode)
          && solver.getReachedStates().contains(nullPointerNode)) {
        res.add(nullPointerNode);
      }
    }

    Collection<AffectedLocation> resWithContext = new LinkedHashSet<>();
    for (Node<ControlFlowGraph.Edge, Val> r : res) {
      // Context context = constructContextGraph(query, r);
      if (trackDataFlowPath) {
        DataFlowPathWeight dataFlowPath = getDataFlowPathWeight(r);
        if (isValidPath(dataFlowPath)) {
          List<PathElement> p = transformPath(dataFlowPath.getAllStatements(), r);
          resWithContext.add(
              new NullPointerDereference(
                  solver,
                  query,
                  r.stmt(),
                  r.fact(),
                  null,
                  null,
                  p,
                  true,
                  pruneContradictoryDataFlowPath,
                  pruneImplicitFlows));
        }
      } else {
        List<PathElement> dataFlowPath = new ArrayList<>();
        resWithContext.add(
            new NullPointerDereference(
                solver,
                query,
                r.stmt(),
                r.fact(),
                null,
                null,
                dataFlowPath,
                false,
                pruneContradictoryDataFlowPath,
                pruneImplicitFlows));
      }
    }
    return new QueryResults(query, resWithContext);
  }

  private boolean isValidPath(DataFlowPathWeight dataFlowPath) {
    if (!pruneContradictoryDataFlowPath) {
      return true;
    }
    Map<Statement, PathConditionWeight.ConditionDomain> conditions = dataFlowPath.getConditions();
    for (Map.Entry<Statement, PathConditionWeight.ConditionDomain> c : conditions.entrySet()) {
      if (contradiction(c.getKey(), c.getValue(), dataFlowPath.getEvaluationMap())) {
        return false;
      }
    }
    return true;
  }

  private DataFlowPathWeight getDataFlowPathWeight(Node<ControlFlowGraph.Edge, Val> sinkLocation) {
    WeightedPAutomaton<ControlFlowGraph.Edge, INode<Val>, ?> callAut = solver.getCallAutomaton();
    // Iterating over whole set to find the matching transition is not the most elegant solution....
    for (Map.Entry<Transition<ControlFlowGraph.Edge, INode<Val>>, ?> e :
        callAut.getTransitionsToFinalWeights().entrySet()) {
      Transition<ControlFlowGraph.Edge, INode<Val>> t = e.getKey();

      if (t.getLabel().equals(ControlFlowGraph.Edge.epsilon())) {
        continue;
      }

      if (t.getStart().fact().isLocal()
          && !t.getLabel().getMethod().equals(t.getStart().fact().m())) {
        continue;
      }
      if (t.getStart().fact().equals(sinkLocation.fact())
          && t.getLabel().equals(sinkLocation.stmt())) {
        if (e.getValue() instanceof DataFlowPathWeightImpl) {
          return (DataFlowPathWeightImpl) e.getValue();
        }
      }
    }
    return null;
  }

  private boolean contradiction(
      Statement ifStmt,
      PathConditionWeight.ConditionDomain mustBeVal,
      Map<Val, PathConditionWeight.ConditionDomain> evaluationMap) {
    if (ifStmt.isIfStmt()) {
      IfStatement ifStmt1 = ifStmt.getIfStmt();
      for (Transition<Field, INode<Node<ControlFlowGraph.Edge, Val>>> t :
          solver.getFieldAutomaton().getTransitions()) {

        if (!t.getStart().fact().stmt().equals(ifStmt)) {
          continue;
        }
        if (!t.getLabel().equals(EmptyField.getInstance())
            || t.getStart() instanceof GeneratedState) {
          continue;
        }

        Node<ControlFlowGraph.Edge, Val> node = t.getStart().fact();
        Val fact = node.fact();
        switch (ifStmt1.evaluate(fact)) {
          case TRUE:
            if (mustBeVal.equals(PathConditionWeight.ConditionDomain.FALSE)) {
              return true;
            }
            break;
          case FALSE:
            if (mustBeVal.equals(PathConditionWeight.ConditionDomain.TRUE)) {
              return true;
            }
        }
      }
      if (pruneImplicitFlows) {
        for (Map.Entry<Val, PathConditionWeight.ConditionDomain> e : evaluationMap.entrySet()) {

          Val key = e.getKey();
          if (ifStmt1.uses(key)) {
            IfStatement.Evaluation eval = null;
            if (e.getValue().equals(PathConditionWeight.ConditionDomain.TRUE)) {
              // Map first to JimpleVal
              eval = ifStmt1.evaluate(ValCollection.trueVal());
            } else if (e.getValue().equals(PathConditionWeight.ConditionDomain.FALSE)) {
              // Map first to JimpleVal
              eval = ifStmt1.evaluate(ValCollection.falseVal());
            }
            if (eval != null) {
              if (mustBeVal.equals(PathConditionWeight.ConditionDomain.FALSE)) {
                if (eval.equals(IfStatement.Evaluation.FALSE)) {
                  return true;
                }
              } else if (mustBeVal.equals(PathConditionWeight.ConditionDomain.TRUE)) {
                if (eval.equals(IfStatement.Evaluation.TRUE)) {
                  return true;
                }
              }
            }
          }
        }
      }
    }
    return false;
  }

  private List<PathElement> transformPath(
      Collection<Node<ControlFlowGraph.Edge, Val>> allStatements,
      Node<ControlFlowGraph.Edge, Val> sinkLocation) {
    List<PathElement> res = new ArrayList<>();
    int index = 0;
    for (Node<ControlFlowGraph.Edge, Val> x : allStatements) {
      res.add(new PathElement(x.stmt(), x.fact(), index++));
    }
    // TODO The analysis misses
    if (!allStatements.contains(sinkLocation)) {
      res.add(new PathElement(sinkLocation.stmt(), sinkLocation.fact(), index));
    }

    for (PathElement n : res) {
      LOGGER.trace(
          "Statement: {}, Variable {}, Index {}", n.getEdge(), n.getVariable(), n.stepIndex());
    }
    return res;
  }
}
