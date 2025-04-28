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
package boomerang.scope.sootup.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import java.util.Objects;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.java.core.JavaSootClass;
import sootup.java.core.types.JavaClassType;

public class JimpleUpStaticFieldRef extends StaticFieldVal {

  private final JStaticFieldRef delegate;

  public JimpleUpStaticFieldRef(JStaticFieldRef delegate, Method method) {
    this(delegate, method, null);
  }

  private JimpleUpStaticFieldRef(
      JStaticFieldRef delegate, Method method, ControlFlowGraph.Edge unbalanced) {
    super(method, unbalanced);

    this.delegate = delegate;
  }

  @Override
  public WrappedClass getDeclaringClass() {
    JavaSootClass sootClass =
        SootUpFrameworkScope.getInstance()
            .getSootClass((JavaClassType) delegate.getFieldSignature().getDeclClassType());
    return new JimpleUpWrappedClass(sootClass);
  }

  @Override
  public Field getField() {
    return new JimpleUpField(
        SootUpFrameworkScope.getInstance().getSootField(delegate.getFieldSignature()));
  }

  @Override
  public Type getType() {
    return new JimpleUpType(delegate.getType());
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleUpStaticFieldRef(delegate, m, stmt);
  }

  @Override
  public Val withNewMethod(Method callee) {
    return new JimpleUpStaticFieldRef(delegate, callee);
  }

  @Override
  public String getVariableName() {
    return delegate.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleUpStaticFieldRef that = (JimpleUpStaticFieldRef) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), delegate);
  }

  @Override
  public String toString() {
    return getVariableName();
  }
}
