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
package typestate.impl.statemachines;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import java.util.Collection;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class PrintWriterStateMachine extends TypeStateMachineWeightFunctions {

  public enum States implements State {
    NONE,
    CLOSED,
    ERROR;

    @Override
    public boolean isErrorState() {
      return this == ERROR;
    }

    @Override
    public boolean isInitialState() {
      return this == NONE;
    }

    @Override
    public boolean isAccepting() {
      return this == CLOSED;
    }
  }

  private static final String CLOSE_METHODS = ".* close.*";
  private static final String READ_METHODS = ".* (read|flush|write).*";
  private static final String TYPE = "java.io.PrintWriter";

  public PrintWriterStateMachine() {
    addTransition(
        new MatcherTransition(
            States.NONE, CLOSE_METHODS, Parameter.This, States.CLOSED, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.CLOSED, CLOSE_METHODS, Parameter.This, States.CLOSED, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.CLOSED, READ_METHODS, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.ERROR, READ_METHODS, Parameter.This, States.ERROR, Type.OnCall));
  }

  //    private Set<SootMethod> closeMethods() {
  //        return selectMethodByName(getSubclassesOf("java.io.PrintWriter"), "close");
  //    }
  //
  //    private Set<SootMethod> readMethods() {
  //        List<SootClass> subclasses = getSubclassesOf("java.io.PrintWriter");
  //        Set<SootMethod> closeMethods = closeMethods();
  //        Set<SootMethod> out = new HashSet<>();
  //        for (SootClass c : subclasses) {
  //            for (SootMethod m : c.getMethods())
  //                if (m.isPublic() && !closeMethods.contains(m) && !m.isStatic())
  //                    out.add(m);
  //        }
  //        return out;
  //    }

  @Override
  public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(Edge stmt) {
    return generateThisAtAnyCallSitesOf(stmt, TYPE, CLOSE_METHODS);
  }

  @Override
  public State initialState() {
    return States.CLOSED;
  }
}
