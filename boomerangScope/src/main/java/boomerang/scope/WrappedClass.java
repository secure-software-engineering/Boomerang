package boomerang.scope;

import java.util.Collection;

public interface WrappedClass {

  Collection<Method> getMethods();

  boolean hasSuperclass();

  WrappedClass getSuperclass();

  Type getType();

  boolean isApplicationClass();

  String getFullyQualifiedName();

  boolean isPhantom();
}
