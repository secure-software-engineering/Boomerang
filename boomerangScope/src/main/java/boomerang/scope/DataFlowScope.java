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
package boomerang.scope;

public interface DataFlowScope {

  boolean isExcluded(DeclaredMethod method);

  boolean isExcluded(Method method);

  /** Basic dataflow scope that excludes all methods from classes that are not loaded */
  DataFlowScope EXCLUDE_PHANTOM_CLASSES =
      new DataFlowScope() {

        @Override
        public boolean isExcluded(DeclaredMethod method) {
          return method.getDeclaringClass().isPhantom();
        }

        @Override
        public boolean isExcluded(Method method) {
          return method.getDeclaringClass().isPhantom();
        }
      };
}
