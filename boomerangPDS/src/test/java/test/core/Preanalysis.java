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
package test.core;

import boomerang.Query;
import boomerang.scope.AnalysisScope;
import boomerang.scope.CallGraph;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Collection;
import java.util.Collections;

public class Preanalysis extends AnalysisScope {

  private final ValueOfInterestInUnit f;

  public Preanalysis(CallGraph cg, ValueOfInterestInUnit f) {
    super(cg);
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
