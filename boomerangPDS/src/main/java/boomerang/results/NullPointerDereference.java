package boomerang.results;

import boomerang.Query;
import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.Pair;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import java.util.List;
import sync.pds.solver.nodes.INode;
import sync.pds.solver.nodes.Node;
import wpds.impl.PAutomaton;

public class NullPointerDereference implements AffectedLocation {
  public static final int RULE_INDEX = 0;

  private final ControlFlowGraph.Edge statement;
  private final Val variable;
  private final PAutomaton<Statement, INode<Val>> openingContext;
  private final PAutomaton<Statement, INode<Val>> closingContext;
  private final List<PathElement> dataFlowPath;
  private final ControlFlowGraph.Edge sourceStatement;
  private final Val sourceVariable;
  private final Query query;

  public NullPointerDereference(ControlFlowGraph.Edge statement) {
    this(null, statement, null, null, null, null);
  }

  public NullPointerDereference(
      Query query,
      ControlFlowGraph.Edge statement,
      Val variable,
      PAutomaton<Statement, INode<Val>> openingContext,
      PAutomaton<Statement, INode<Val>> closingContext,
      List<PathElement> dataFlowPath) {
    this.query = query;
    this.sourceStatement = query.cfgEdge();
    this.sourceVariable = query.var();
    this.statement = statement;
    this.variable = variable;
    this.openingContext = openingContext;
    this.closingContext = closingContext;
    this.dataFlowPath = dataFlowPath;
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
    // A this variable can never be null.
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
        Pair<Val, Field> ifr = curr.getFieldLoad();
        if (ifr.getX().equals(fact)) {
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
}
