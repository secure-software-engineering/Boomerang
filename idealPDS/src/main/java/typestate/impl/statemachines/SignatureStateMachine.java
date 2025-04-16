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
package typestate.impl.statemachines;

import boomerang.WeightedForwardQuery;
import boomerang.scope.ControlFlowGraph.Edge;
import boomerang.scope.DeclaredMethod;
import boomerang.scope.Statement;
import java.util.Collection;
import java.util.Collections;
import typestate.TransitionFunction;
import typestate.finiteautomata.MatcherTransition;
import typestate.finiteautomata.MatcherTransition.Parameter;
import typestate.finiteautomata.MatcherTransition.Type;
import typestate.finiteautomata.State;
import typestate.finiteautomata.TypeStateMachineWeightFunctions;

public class SignatureStateMachine extends TypeStateMachineWeightFunctions {

  private static final String INIT_SIGN = "initSign";
  private static final String INIT_VERIFY = "initVerify";
  private static final String SIGN = "sign";
  private static final String UPDATE = "update";
  private static final String VERIFY = "verify";
  private static final String GET_INSTANCE = "getInstance";

  public enum States implements State {
    NONE,
    UNINITIALIZED,
    SIGN_CHECK,
    VERIFY_CHECK,
    ERROR;

    @Override
    public boolean isErrorState() {
      return this == ERROR;
    }

    @Override
    public boolean isInitialState() {
      return this == UNINITIALIZED;
    }

    @Override
    public boolean isAccepting() {
      return this == SIGN_CHECK || this == VERIFY_CHECK;
    }
  }

  public SignatureStateMachine() {
    addTransition(
        new MatcherTransition(
            States.NONE, GET_INSTANCE, Parameter.This, States.UNINITIALIZED, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.UNINITIALIZED, INIT_SIGN, Parameter.This, States.SIGN_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.UNINITIALIZED, INIT_VERIFY, Parameter.This, States.VERIFY_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.UNINITIALIZED, SIGN, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.UNINITIALIZED, VERIFY, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.UNINITIALIZED, UPDATE, Parameter.This, States.ERROR, Type.OnCall));

    addTransition(
        new MatcherTransition(
            States.SIGN_CHECK, INIT_SIGN, Parameter.This, States.SIGN_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.SIGN_CHECK, INIT_VERIFY, Parameter.This, States.VERIFY_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.SIGN_CHECK, SIGN, Parameter.This, States.SIGN_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.SIGN_CHECK, VERIFY, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.SIGN_CHECK, UPDATE, Parameter.This, States.SIGN_CHECK, Type.OnCall));

    addTransition(
        new MatcherTransition(
            States.VERIFY_CHECK, INIT_SIGN, Parameter.This, States.SIGN_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.VERIFY_CHECK, INIT_VERIFY, Parameter.This, States.VERIFY_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.VERIFY_CHECK, SIGN, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.VERIFY_CHECK, VERIFY, Parameter.This, States.VERIFY_CHECK, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.VERIFY_CHECK, UPDATE, Parameter.This, States.VERIFY_CHECK, Type.OnCall));

    addTransition(
        new MatcherTransition(States.ERROR, INIT_SIGN, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(
            States.ERROR, INIT_VERIFY, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(States.ERROR, SIGN, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(States.ERROR, VERIFY, Parameter.This, States.ERROR, Type.OnCall));
    addTransition(
        new MatcherTransition(States.ERROR, UPDATE, Parameter.This, States.ERROR, Type.OnCall));
  }

  /*
  // TODO: [ms] re-enable
  private Set<SootMethod> constructor() {
    List<SootClass> subclasses = getSubclassesOf("java.security.Signature");
    Set<SootMethod> out = new HashSet<>();
    for (SootClass c : subclasses) {
      for (SootMethod m : c.getMethods())
        if (m.isPublic() && m.getName().equals("getInstance")) out.add(m);
    }
    return out;
  }
  */

  @Override
  public Collection<WeightedForwardQuery<TransitionFunction>> generateSeed(Edge edge) {
    Statement unit = edge.getStart();
    if (unit.containsInvokeExpr()) {
      DeclaredMethod method = unit.getInvokeExpr().getDeclaredMethod();
      if (method.getName().equals("getInstance")
          && method.getSubSignature().contains("Signature")) {
        return getLeftSideOf(edge);
      }
    }
    return Collections.emptySet();
  }

  @Override
  protected State initialState() {
    return States.UNINITIALIZED;
  }
}
