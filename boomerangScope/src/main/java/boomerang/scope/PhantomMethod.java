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

import java.util.Collection;
import java.util.List;

public abstract class PhantomMethod extends Method {

  @Override
  public boolean isParameterLocal(Val val) {
    return false;
  }

  @Override
  public boolean isThisLocal(Val val) {
    return false;
  }

  @Override
  public Collection<Val> getLocals() {
    throw new RuntimeException("Locals in phantom method are not available");
  }

  @Override
  public Val getThisLocal() {
    throw new RuntimeException("this local in phantom method is not available");
  }

  @Override
  public List<Val> getParameterLocals() {
    throw new RuntimeException("Parameter locals in phantom method are not available");
  }

  @Override
  public boolean isDefined() {
    return false;
  }

  @Override
  public boolean isPhantom() {
    return true;
  }

  @Override
  public List<Statement> getStatements() {
    throw new RuntimeException("Statements in phantom method are not available");
  }

  @Override
  public ControlFlowGraph getControlFlowGraph() {
    throw new RuntimeException("Control flow graph in phantom method is not available");
  }
}
