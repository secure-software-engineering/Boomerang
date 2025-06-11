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
package test.core;

import boomerang.Query;
import boomerang.scope.AnalysisScope;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.FrameworkScope;
import java.util.Collection;
import java.util.Collections;

public class PreAnalysis extends AnalysisScope {

  private final ValueOfInterestInUnit f;

  public PreAnalysis(FrameworkScope frameworkScope, ValueOfInterestInUnit f) {
    super(frameworkScope);
    this.f = f;
  }

  @Override
  protected Collection<? extends Query> generate(Edge seed) {
    if (f.test(seed).isPresent()) {
      return Collections.singleton(f.test(seed).get());
    }
    return Collections.emptySet();
  }
}
