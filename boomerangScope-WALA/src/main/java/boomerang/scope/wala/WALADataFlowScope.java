/**
 * ***************************************************************************** Copyright (c) 2020
 * CodeShield GmbH, Paderborn, Germany. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * <p>SPDX-License-Identifier: EPL-2.0
 *
 * <p>Contributors: Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package boomerang.scope.wala;

import boomerang.scope.DataFlowScope;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Method;

public class WALADataFlowScope {
  public static DataFlowScope make() {
    return new DataFlowScope() {
      @Override
      public boolean isExcluded(DeclaredMethod method) {
        return false;
      }

      @Override
      public boolean isExcluded(Method method) {
        return false;
      }
    };
  }

  public static DataFlowScope APPLICATION_ONLY =
      new DataFlowScope() {
        @Override
        public boolean isExcluded(DeclaredMethod method) {
          return !method.getDeclaringClass().isApplicationClass();
        }

        @Override
        public boolean isExcluded(Method method) {
          return !method.getDeclaringClass().isApplicationClass() || method.isPhantom();
        }
      };
}
