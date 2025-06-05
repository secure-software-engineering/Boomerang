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
package typestate;

import boomerang.scope.Statement;
import com.google.common.collect.Multimap;
import org.jspecify.annotations.NonNull;
import typestate.finiteautomata.Transition;
import wpds.impl.Weight;

public interface TransitionFunction extends Weight {

  @NonNull Multimap<Transition, Statement> getStateChangeStatements();
}
