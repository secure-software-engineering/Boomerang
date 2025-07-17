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

import de.fraunhofer.iem.wildcard.Wildcard;

public class WildcardField extends PredefinedField implements Wildcard {

  private static WildcardField instance;

  private WildcardField() {
    super("*");
  }

  public static WildcardField getInstance() {
    if (instance == null) {
      instance = new WildcardField();
    }
    return instance;
  }
}
