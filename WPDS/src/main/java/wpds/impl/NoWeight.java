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

import javax.annotation.Nonnull;

public class NoWeight implements Weight {

  private static final NoWeight INSTANCE = new NoWeight();

  private NoWeight() {
    /* Singleton */
  }

  @Nonnull
  public static NoWeight getInstance() {
    return INSTANCE;
  }

  @Nonnull
  @Override
  public Weight extendWith(@Nonnull Weight other) {
    return other;
  }

  @Nonnull
  @Override
  public Weight combineWith(@Nonnull Weight other) {
    return other;
  }

  @Override
  public String toString() {
    return "";
  }
}
