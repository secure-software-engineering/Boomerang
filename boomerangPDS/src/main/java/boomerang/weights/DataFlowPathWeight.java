package boomerang.weights;

import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.weights.PathConditionWeight.ConditionDomain;
import com.google.common.base.Objects;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public class DataFlowPathWeight implements Weight {

  private static DataFlowPathWeight one;

  private final PathTrackingWeight path;
  private final PathConditionWeight condition;

  private DataFlowPathWeight() {
    path = PathTrackingRepesentativeWeight.one();
    condition = PathConditionWeight.one();
  }

  public DataFlowPathWeight(Node<Edge, Val> path) {
    this.path = new PathTrackingWeightImpl(path);
    this.condition = PathConditionWeight.one();
  }

  public DataFlowPathWeight(Node<Edge, Val> path, Statement callSite, Method callee) {
    this.path = new PathTrackingWeightImpl(path);
    this.condition = new PathConditionWeight(callSite, callee);
  }

  public DataFlowPathWeight(Statement callSite, Method callee) {
    this.path = PathTrackingRepesentativeWeight.one();
    this.condition = new PathConditionWeight(callSite, callee);
  }

  public DataFlowPathWeight(Statement ifStatement, Boolean condition) {
    this.path = PathTrackingRepesentativeWeight.one();
    this.condition = new PathConditionWeight(ifStatement, condition);
  }

  private DataFlowPathWeight(PathTrackingWeightImpl path, PathConditionWeight condition) {
    this.path = path;
    this.condition = condition;
  }

  public DataFlowPathWeight(Val leftOp, ConditionDomain conditionVal) {
    this.path = PathTrackingRepesentativeWeight.one();
    this.condition = new PathConditionWeight(leftOp, conditionVal);
  }

  public DataFlowPathWeight(Val returnVal) {
    this.path = PathTrackingRepesentativeWeight.one();
    this.condition = new PathConditionWeight(returnVal);
  }

  public static DataFlowPathWeight one() {
    if (one == null) one = new DataFlowPathWeight();
    return one;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DataFlowPathWeight that = (DataFlowPathWeight) o;
    return Objects.equal(path, that.path) && Objects.equal(condition, that.condition);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(path, condition);
  }

  public Set<Node<Edge, Val>> getAllStatements() {
    return path.getShortestPathWitness();
  }

  public Map<Statement, ConditionDomain> getConditions() {
    return condition.getConditions();
  }

  public Map<Val, ConditionDomain> getEvaluationMap() {
    return condition.getEvaluationMap();
  }

  @Override
  public String toString() {
    return /*"PATH" + path +*/ " COND: " + condition;
  }

  @Nonnull
  public Weight extendWith(@Nonnull Weight other) {
    return new DataFlowPathWeight(
        (PathTrackingWeightImpl) path.extendWith(((DataFlowPathWeight) other).path),
        (PathConditionWeight) condition.extendWith(((DataFlowPathWeight) other).condition));
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return new DataFlowPathWeight(
        (PathTrackingWeightImpl) path.combineWith(((DataFlowPathWeight) other).path),
        (PathConditionWeight) condition.combineWith(((DataFlowPathWeight) other).condition));
  }
}
