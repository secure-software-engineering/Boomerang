package boomerang.scope.soot;

import boomerang.scope.CallGraph;
import boomerang.scope.DataFlowScope;
import boomerang.scope.Field;
import boomerang.scope.FrameworkScope;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimpleStaticFieldVal;
import boomerang.scope.soot.jimple.JimpleVal;
import java.util.Collection;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import soot.Scene;
import soot.SootMethod;
import soot.jimple.IntConstant;

public class SootFrameworkScope implements FrameworkScope {

  protected final Scene scene;
  protected final SootCallGraph sootCallGraph;
  protected DataFlowScope dataFlowScope;

  public SootFrameworkScope(
      @Nonnull Scene scene,
      @Nonnull soot.jimple.toolkits.callgraph.CallGraph callGraph,
      @Nonnull Collection<SootMethod> entryPoints,
      @Nonnull DataFlowScope dataFlowScope) {
    this.scene = scene;

    this.sootCallGraph = new SootCallGraph(callGraph, entryPoints);
    this.dataFlowScope = dataFlowScope;
  }

  @Override
  public Val getTrueValue(Method m) {
    return new JimpleVal(IntConstant.v(1), m);
  }

  @Override
  public Val getFalseValue(Method m) {
    return new JimpleVal(IntConstant.v(0), m);
  }

  @Override
  public Stream<Method> handleStaticFieldInitializers(Val fact) {
    JimpleStaticFieldVal val = ((JimpleStaticFieldVal) fact);
    return ((JimpleField) val.field())
        .getDelegate().getDeclaringClass().getMethods().stream()
            .filter(SootMethod::hasActiveBody)
            .map(JimpleMethod::of);
  }

  @Override
  public StaticFieldVal newStaticFieldVal(Field field, Method m) {
    return new JimpleStaticFieldVal((JimpleField) field, m);
  }

  @Override
  public CallGraph getCallGraph() {
    return sootCallGraph;
  }

  @Override
  public DataFlowScope getDataFlowScope() {
    return dataFlowScope;
  }
}
