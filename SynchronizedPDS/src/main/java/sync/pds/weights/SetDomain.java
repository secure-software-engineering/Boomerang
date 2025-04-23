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
package sync.pds.weights;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import sync.pds.solver.nodes.Node;
import wpds.impl.Weight;

public interface SetDomain<N, Stmt, Fact> extends Weight {

  @NonNull Collection<Node<Stmt, Fact>> getNodes();

  @NonNull Collection<Node<Stmt, Fact>> elements();
}
