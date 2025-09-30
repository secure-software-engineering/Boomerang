package boomerang.example.analysis;

import boomerang.scope.DataFlowScope;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;
import boomerang.scope.WrappedClass;
import boomerang.utils.MethodWrapper;

import java.util.Collection;

public class TaintDataFlowScope implements DataFlowScope {

  private final Collection<MethodWrapper> sources;
  private final Collection<MethodWrapper> sinks;

  public TaintDataFlowScope(Collection<MethodWrapper> sources, Collection<MethodWrapper> sinks) {
    this.sources = sources;
    this.sinks = sinks;
  }

  @Override
  public boolean isExcluded(DeclaredMethod declaredMethod) {
    WrappedClass declaringClass = declaredMethod.getDeclaringClass();

    if (declaringClass.isPhantom()) {
      return true;
    }

    return isClassSourceOrSink(declaringClass);
  }

  @Override
  public boolean isExcluded(Method method) {
    WrappedClass declaringClass = method.getDeclaringClass();

    if (declaringClass.isPhantom()) {
      return true;
    }

    return isClassSourceOrSink(declaringClass);
  }

  private boolean isClassSourceOrSink(WrappedClass declaringClass) {
    for (MethodWrapper wrapper : sources) {
      if (wrapper.getDeclaringClass().equals(declaringClass.getFullyQualifiedName())) {
        return true;
      }
    }

    for (MethodWrapper wrapper : sinks) {
      if (wrapper.getDeclaringClass().equals(declaringClass.getFullyQualifiedName())) {
        return true;
      }
    }

    return false;
  }
}
