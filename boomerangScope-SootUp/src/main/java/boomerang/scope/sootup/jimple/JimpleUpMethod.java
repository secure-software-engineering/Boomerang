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
package boomerang.scope.sootup.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import sootup.core.jimple.basic.Local;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;

public class JimpleUpMethod extends Method {

  protected static Interner<JimpleUpMethod> INTERNAL_POOL = Interners.newWeakInterner();
  private final JavaSootMethod delegate;
  private final JimpleUpControlFlowGraph cfg;

  private Set<Val> localCache;
  private List<Val> parameterLocalCache;

  protected JimpleUpMethod(JavaSootMethod delegate) {
    this.delegate = delegate;

    if (!delegate.hasBody()) {
      throw new RuntimeException("Trying to build a Jimple method without body present");
    }

    cfg = new JimpleUpControlFlowGraph(this);
  }

  public static JimpleUpMethod of(JavaSootMethod method) {
    return INTERNAL_POOL.intern(new JimpleUpMethod(method));
  }

  public JavaSootMethod getDelegate() {
    return delegate;
  }

  @Override
  public boolean isStaticInitializer() {
    return SootUpFrameworkScope.isStaticInitializer(delegate);
  }

  @Override
  public boolean isParameterLocal(Val val) {
    if (val.isStatic()) return false;

    List<Val> parameterLocals = getParameterLocals();
    return parameterLocals.contains(val);
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> result = new ArrayList<>();

    for (sootup.core.types.Type type : delegate.getParameterTypes()) {
      result.add(new JimpleUpType(type));
    }

    return result;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleUpType(delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleUpType(delegate.getReturnType());
  }

  @Override
  public boolean isThisLocal(Val val) {
    if (val.isStatic()) return false;
    if (delegate.isStatic()) return false;

    Val thisLocal = getThisLocal();
    return thisLocal.equals(val);
  }

  @Override
  public Set<Val> getLocals() {
    if (localCache == null) {
      localCache = new HashSet<>();

      for (Local local : delegate.getBody().getLocals()) {
        localCache.add(new JimpleUpVal(local, this));
      }
    }
    return localCache;
  }

  @Override
  public Val getThisLocal() {
    return new JimpleUpVal(delegate.getBody().getThisLocal(), this);
  }

  @Override
  public List<Val> getParameterLocals() {
    if (parameterLocalCache == null) {
      parameterLocalCache = new ArrayList<>();

      for (Local local : delegate.getBody().getParameterLocals()) {
        parameterLocalCache.add(new JimpleUpVal(local, this));
      }
    }
    return parameterLocalCache;
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public boolean isPhantom() {
    return false;
  }

  @Override
  public List<Statement> getStatements() {
    return getControlFlowGraph().getStatements();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    JavaSootClass sootClass =
        SootUpFrameworkScope.getInstance()
            .getSootClass((JavaClassType) delegate.getDeclaringClassType());
    return new JimpleUpWrappedClass(sootClass);
  }

  @Override
  public ControlFlowGraph getControlFlowGraph() {
    return cfg;
  }

  @Override
  public String getSubSignature() {
    return delegate.getSignature().getSubSignature().toString();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean isConstructor() {
    return SootUpFrameworkScope.isConstructor(delegate);
  }

  @Override
  public int hashCode() {
    return Arrays.hashCode(new Object[] {delegate});
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;

    JimpleUpMethod other = (JimpleUpMethod) obj;
    if (delegate == null) {
      return other.delegate == null;
    } else return delegate.equals(other.delegate);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
