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
package typestate.finiteautomata;

import boomerang.scope.DeclaredMethod;
import java.util.regex.Pattern;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MatcherTransition extends TransitionImpl {
  private static final Logger LOGGER = LoggerFactory.getLogger(MatcherTransition.class);
  @NonNull private final Type type;
  @NonNull private final Parameter param;
  @NonNull private final Pattern methodMatcher;
  private final boolean negate;

  public enum Type {
    OnCall,
    None,
    OnCallToReturn,
    OnCallOrOnCallToReturn
  }

  public enum Parameter {
    This,
    Param1,
    Param2
  }

  public MatcherTransition(
      @NonNull State from,
      @NonNull String methodMatcher,
      @NonNull Parameter param,
      @NonNull State to,
      @NonNull Type type) {
    this(from, methodMatcher, false, param, to, type);
  }

  public MatcherTransition(
      @NonNull State from,
      @NonNull String methodMatcher,
      boolean negate,
      @NonNull Parameter param,
      @NonNull State to,
      @NonNull Type type) {
    super(from, to);
    this.methodMatcher = Pattern.compile(methodMatcher);
    this.negate = negate;
    this.type = type;
    this.param = param;
  }

  public boolean matches(@NonNull DeclaredMethod declaredMethod) {
    boolean matches = methodMatcher.matcher(declaredMethod.getSubSignature()).matches();
    if (matches) {
      LOGGER.debug(
          "Found matching transition at call site {} for {}", declaredMethod.getInvokeExpr(), this);
    }
    return negate != matches;
  }

  @NonNull
  public Type getType() {
    return type;
  }

  @NonNull
  public Parameter getParam() {
    return param;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    result = prime * result + ((methodMatcher == null) ? 0 : methodMatcher.hashCode());
    result = prime * result + (negate ? 1231 : 1237);
    result = prime * result + ((param == null) ? 0 : param.hashCode());
    result = prime * result + ((type == null) ? 0 : type.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (!super.equals(obj)) return false;
    if (getClass() != obj.getClass()) return false;
    MatcherTransition other = (MatcherTransition) obj;
    if (methodMatcher == null) {
      if (other.methodMatcher != null) return false;
    } else if (!methodMatcher.equals(other.methodMatcher)) return false;
    if (negate != other.negate) return false;
    if (param != other.param) return false;
    return type == other.type;
  }
}
