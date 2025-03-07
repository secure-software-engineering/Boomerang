package boomerang.flowfunction;

import boomerang.scope.DeclaredMethod;

public class FlowFunctionUtils {
  public static boolean isSystemArrayCopy(DeclaredMethod method) {
    return method.getName().equals("arraycopy")
        && method.getDeclaringClass().getFullyQualifiedName().equals("java.lang.System");
  }
}
