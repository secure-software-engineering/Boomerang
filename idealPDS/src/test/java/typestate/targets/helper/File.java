/**
 * ***************************************************************************** Copyright (c) 2018
 * Fraunhofer IEM, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package typestate.targets.helper;

public class File {

  public File open() {
    return this;
  }

  public File close() {
    return this;
  }

  public int hashCode() {
    return 9;
  }

  public void wrappedClose() {
    close();
  }
}
