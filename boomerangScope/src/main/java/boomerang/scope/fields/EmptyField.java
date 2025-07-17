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
package boomerang.scope.fields;

import de.fraunhofer.iem.Empty;

public class EmptyField extends PredefinedField implements Empty {

  private EmptyField(String name) {
    super(name);
  }

  public static EmptyField getEmptyField() {
    return new EmptyField("{}");
  }

  public static EmptyField getEpsilonField() {
    return new EmptyField("eps_f");
  }
}
