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
package boomerang.guided;

import boomerang.utils.MethodWrapper;
import com.google.common.base.Objects;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Specification {
  private static final String ON_SELECTOR = "ON";
  private static final String GO_SELECTOR = "GO";
  private static final String BACKWARD = "{B}";
  private static final String FORWARD = "{F}";

  public enum QueryDirection {
    FORWARD,
    BACKWARD
  }

  private final Collection<MethodWithSelector> methodAndQueries;

  private Specification(Collection<String> spec) {
    this.methodAndQueries = spec.stream().map(this::parse).collect(Collectors.toSet());
  }

  private MethodWithSelector parse(String input) {
    Set<QuerySelector> on = new LinkedHashSet<>();
    Set<QuerySelector> go = new LinkedHashSet<>();

    // Handle arguments
    Pattern arguments = Pattern.compile("\\((.*?)\\)");
    Matcher argumentMatcher = arguments.matcher(input);
    if (argumentMatcher.find()) {
      String group = argumentMatcher.group(1);
      String[] args = group.split(",");
      for (int i = 0; i < args.length; i++) {
        createQuerySelector(args[i], Parameter.of(i), on, go);
      }
    }

    // Handle base variable
    Pattern base = Pattern.compile("<(.*?):");
    Matcher baseMatcher = base.matcher(input);
    if (baseMatcher.find()) {
      String group = baseMatcher.group(1);
      createQuerySelector(group, Parameter.base(), on, go);
    }

    // Handle return
    String[] s = input.split(" ");
    createQuerySelector(s[1], Parameter.returnParam(), on, go);

    String sootMethod =
        input
            .replace(FORWARD, "")
            .replace(BACKWARD, "")
            .replace(ON_SELECTOR, "")
            .replace(GO_SELECTOR, "");

    // Assert parsing successful
    long backwardQueryCount =
        on.stream().filter(x -> x.direction == QueryDirection.BACKWARD).count()
            + go.stream().filter(x -> x.direction == QueryDirection.BACKWARD).count();
    long forwardQueryCount =
        on.stream().filter(x -> x.direction == QueryDirection.FORWARD).count()
            + go.stream().filter(x -> x.direction == QueryDirection.FORWARD).count();
    if (input.length()
        != sootMethod.length()
            + ((long) on.size() * ON_SELECTOR.length()
                + (long) go.size() * GO_SELECTOR.length()
                + backwardQueryCount * BACKWARD.length()
                + forwardQueryCount * FORWARD.length())) {
      throw new RuntimeException("Parsing Specification failed. Please check your specification");
    }

    // To avoid reworking the complete parsing process, we keep the soot signature and transform
    // it into a general method descriptor
    MethodWrapper methodWrapper = parseSootSignature(sootMethod);
    return new MethodWithSelector(methodWrapper, on, go);
  }

  private MethodWrapper parseSootSignature(String signature) {
    String declaringClass;
    String returnType;
    String name;
    List<String> paramList;

    Pattern pattern1 = Pattern.compile("<([^:]+):");
    Matcher matcher1 = pattern1.matcher(signature);
    if (matcher1.find()) {
      declaringClass = matcher1.group(1).trim();
    } else {
      throw new IllegalArgumentException(
          "Could not extract declaring class from signature: " + signature);
    }

    Pattern pattern2 = Pattern.compile(":\\s+([^\\s]+)\\s+[^\\s]+\\(");
    Matcher matcher2 = pattern2.matcher(signature);
    if (matcher2.find()) {
      returnType = matcher2.group(1).trim();
    } else {
      throw new IllegalArgumentException(
          "Could not extract return type from signature: " + signature);
    }

    Pattern pattern3 = Pattern.compile("\\s+([^\\s]+)\\(");
    Matcher matcher3 = pattern3.matcher(signature);
    if (matcher3.find()) {
      name = matcher3.group(1).trim();
    } else {
      throw new IllegalArgumentException("Could not extract name from signature: " + signature);
    }

    Pattern pattern4 = Pattern.compile("\\(([^)]*)\\)");
    Matcher matcher4 = pattern4.matcher(signature);
    if (matcher4.find()) {
      String params = matcher4.group(1).trim();

      paramList = new ArrayList<>();
      if (!params.isEmpty()) {
        String[] paramArray = params.split(",");

        for (String param : paramArray) {
          paramList.add(param.trim());
        }
      }
    } else {
      throw new IllegalArgumentException(
          "Could not extract parameters from signature: " + signature);
    }

    return new MethodWrapper(declaringClass, name, returnType, paramList);
  }

  private void createQuerySelector(
      String arg, Parameter p, Set<QuerySelector> on, Set<QuerySelector> go) {
    if (arg.startsWith(ON_SELECTOR)) {
      on.add(
          new QuerySelector(
              arg.contains(FORWARD) ? QueryDirection.FORWARD : QueryDirection.BACKWARD, p));
    }
    if (arg.startsWith(GO_SELECTOR)) {
      go.add(
          new QuerySelector(
              arg.contains(FORWARD) ? QueryDirection.FORWARD : QueryDirection.BACKWARD, p));
    }
  }

  public static class MethodWithSelector {

    private final MethodWrapper method;
    private final Collection<QuerySelector> on;
    private final Collection<QuerySelector> go;

    public MethodWithSelector(
        MethodWrapper method, Collection<QuerySelector> on, Collection<QuerySelector> go) {
      this.method = method;
      this.on = on;
      this.go = go;
    }

    public Collection<QuerySelector> getOn() {
      return on;
    }

    public Collection<QuerySelector> getGo() {
      return go;
    }

    public MethodWrapper getMethod() {
      return method;
    }
  }

  public static class Parameter {
    private final int value;

    private Parameter(final int newValue) {
      value = newValue;
    }

    public static Parameter returnParam() {
      return new Parameter(-2);
    }

    public static Parameter base() {
      return new Parameter(-1);
    }

    public static Parameter of(int integer) {
      if (integer < 0) {
        throw new RuntimeException("Parameter index must be > 0");
      }
      return new Parameter(integer);
    }

    public int getValue() {
      return value;
    }

    @Override
    public boolean equals(Object o) {
      if (this == o) {
        return true;
      }
      if (o == null || getClass() != o.getClass()) {
        return false;
      }
      Parameter parameter = (Parameter) o;
      return value == parameter.value;
    }

    @Override
    public int hashCode() {
      return Objects.hashCode(value);
    }
  }

  public static class QuerySelector {
    QuerySelector(QueryDirection direction, Parameter argumentSelection) {
      this.direction = direction;
      this.argumentSelection = argumentSelection;
    }

    QueryDirection direction;
    Parameter argumentSelection;
  }

  public static Specification loadFrom(String filePath) throws IOException {
    return new Specification(Files.lines(Paths.get(filePath)).collect(Collectors.toSet()));
  }

  public static Specification create(String... spec) {
    return new Specification(Set.of(spec));
  }

  public Collection<MethodWithSelector> getMethodAndQueries() {
    return methodAndQueries;
  }
}
