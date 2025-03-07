package boomerang.scope;

import java.util.stream.Stream;

public interface FrameworkScope {

  CallGraph getCallGraph();

  DataFlowScope getDataFlowScope();

  Val getTrueValue(Method m);

  Val getFalseValue(Method m);

  Stream<Method> handleStaticFieldInitializers(Val fact);

  StaticFieldVal newStaticFieldVal(Field field, Method m);
}
