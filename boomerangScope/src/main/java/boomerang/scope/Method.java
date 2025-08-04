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
package boomerang.scope;

import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class Method implements Location {

  private Collection<Val> returnLocals;

  protected Method() {}

  public abstract boolean isStaticInitializer();

  public abstract boolean isParameterLocal(Val val);

  public abstract List<Type> getParameterTypes();

  public abstract Type getParameterType(int index);

  public abstract Type getReturnType();

  public abstract boolean isThisLocal(Val val);

  public abstract Collection<Val> getLocals();

  public abstract Val getThisLocal();

  public abstract List<Val> getParameterLocals();

  public abstract boolean isStatic();

  public abstract boolean isDefined();

  public abstract boolean isPhantom();

  public abstract List<Statement> getStatements();

  public abstract WrappedClass getDeclaringClass();

  public abstract ControlFlowGraph getControlFlowGraph();

  public abstract String getSubSignature();

  public abstract String getName();

  public Val getParameterLocal(int i) {
    return getParameterLocals().get(i);
  }

  public abstract boolean isConstructor();

  public Collection<Val> getReturnLocals() {
    if (returnLocals == null) {
      returnLocals = new LinkedHashSet<>();
      for (Statement s : getStatements()) {
        if (s.isReturnStmt()) {
          returnLocals.add(s.getReturnOp());
        }
      }
    }
    return returnLocals;
  }

  @Override
  public boolean accepts(Location other) {
    return this.equals(other);
  }
}
