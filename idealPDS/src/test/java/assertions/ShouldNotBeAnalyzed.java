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
package assertions;

import boomerang.scope.Statement;
import test.Assertion;

public class ShouldNotBeAnalyzed implements Assertion {

  private final Statement statement;
  private boolean unsound;

  public ShouldNotBeAnalyzed(Statement statement) {
    this.statement = statement;
    this.unsound = false;
  }

  public Statement getStatement() {
    return statement;
  }

  @Override
  public String toString() {
    return "Method should not be included in analysis: " + statement.getMethod();
  }

  @Override
  public boolean isUnsound() {
    return unsound;
  }

  @Override
  public boolean isImprecise() {
    return false;
  }

  @Override
  public String getAssertedMessage() {
    return toString();
  }

  public void hasBeenAnalyzed() {
    unsound = true;
  }
}
