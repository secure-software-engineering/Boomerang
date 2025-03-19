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
package boomerang.scope.soot.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.StaticFieldVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import java.util.Objects;

public class JimpleStaticFieldVal extends StaticFieldVal {

  private final JimpleField field;

  public JimpleStaticFieldVal(JimpleField field, Method m) {
    this(field, m, null);
  }

  private JimpleStaticFieldVal(JimpleField field, Method m, ControlFlowGraph.Edge unbalanced) {
    super(m, unbalanced);
    this.field = field;
  }

  public Field field() {
    return field;
  }

  @Override
  public Val asUnbalanced(ControlFlowGraph.Edge stmt) {
    return new JimpleStaticFieldVal(field, m, stmt);
  }

  @Override
  public Type getType() {
    return new JimpleType(field.getDelegate().getType());
  }

  @Override
  public Val withNewMethod(Method callee) {
    return new JimpleStaticFieldVal(field, callee);
  }

  @Override
  public String getVariableName() {
    return toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;
    JimpleStaticFieldVal that = (JimpleStaticFieldVal) o;
    return Objects.equals(field, that.field);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), field);
  }

  @Override
  public String toString() {
    return "StaticField: " + field;
  }
}
