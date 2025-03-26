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
package wpds.impl;

import org.jspecify.annotations.NonNull;

public class NoWeight implements Weight {

  private static final NoWeight INSTANCE = new NoWeight();

  private NoWeight() {
    /* Singleton */
  }

  @NonNull
  public static NoWeight getInstance() {
    return INSTANCE;
  }

  @NonNull
  @Override
  public Weight extendWith(@NonNull Weight other) {
    return other;
  }

  @NonNull
  @Override
  public Weight combineWith(@NonNull Weight other) {
    return other;
  }

  @Override
  public String toString() {
    return "";
  }
}
