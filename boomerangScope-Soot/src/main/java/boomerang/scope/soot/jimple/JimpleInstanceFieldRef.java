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
package boomerang.scope.soot.jimple;

import boomerang.scope.Field;
import boomerang.scope.InstanceFieldRef;
import boomerang.scope.Val;

// TODO May be removed
public class JimpleInstanceFieldRef implements InstanceFieldRef {

  private final soot.jimple.InstanceFieldRef delegate;
  private final JimpleMethod m;

  public JimpleInstanceFieldRef(soot.jimple.InstanceFieldRef ifr, JimpleMethod m) {
    this.delegate = ifr;
    this.m = m;
  }

  public Val getBase() {
    return new JimpleVal(delegate.getBase(), m);
  }

  public Field getField() {
    return new JimpleField(delegate.getField());
  }
}
