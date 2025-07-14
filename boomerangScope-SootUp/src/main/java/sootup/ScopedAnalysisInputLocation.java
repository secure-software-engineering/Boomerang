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
package sootup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.jspecify.annotations.NonNull;
import sootup.core.frontend.SootClassSource;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SourceType;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.core.views.View;

/**
 * Base class for filtering ClassSources returned from the underlying AnalysisInputLocation you need
 * to override the filter function - e.g. override it in an anonymous class (TODO: remov
 * ScopedAnalysisInputLocation as it is already in an upstream PR - innerclasses need to be kept)
 */
public abstract class ScopedAnalysisInputLocation implements AnalysisInputLocation {

  @NonNull private final AnalysisInputLocation inputLocation;

  public ScopedAnalysisInputLocation(@NonNull AnalysisInputLocation inputLocation) {
    this.inputLocation = inputLocation;
  }

  /** Override this method. */
  protected abstract boolean filter(@NonNull ClassType type);

  @NonNull
  @Override
  public Optional<? extends SootClassSource> getClassSource(
      @NonNull ClassType type, @NonNull View view) {
    if (!filter(type)) {
      return Optional.empty();
    }
    return inputLocation.getClassSource(type, view);
  }

  @NonNull
  @Override
  public Stream<? extends SootClassSource> getClassSources(@NonNull View view) {
    return inputLocation.getClassSources(view).filter(type -> filter(type.getClassType()));
  }

  @NonNull
  @Override
  public SourceType getSourceType() {
    return inputLocation.getSourceType();
  }

  @NonNull
  @Override
  public List<BodyInterceptor> getBodyInterceptors() {
    return inputLocation.getBodyInterceptors();
  }

  public static class AllowlistingScopedAnalysisInputLocation extends ScopedAnalysisInputLocation {

    private final Collection<String> allowlist;

    protected String mapStr(String s) {

      // TODO: adapt from soots: isIncluded()
      //       if (className.equals(pkg)
      //          || ((pkg.endsWith(".*") || pkg.endsWith("$*")) &&
      // className.startsWith(pkg.substring(0, pkg.length() - 1)))) {
      if (!s.endsWith(".*")) {
        throw new IllegalStateException("filter method is not implemented for a specific class!");
      }
      return s.substring(0, s.length() - 2);
    }

    public AllowlistingScopedAnalysisInputLocation(
        @NonNull AnalysisInputLocation inputLocation, Collection<String> allowlist) {
      super(inputLocation);
      this.allowlist = allowlist.stream().map(this::mapStr).collect(Collectors.toList());
    }

    @Override
    protected boolean filter(@NonNull ClassType type) {
      return allowlist.contains(type.getPackageName().toString());
    }
  }

  public static class DenylistingScopedAnalysisInputLocation extends ScopedAnalysisInputLocation {

    private final Collection<String> denylist;

    protected String mapStr(String s) {
      // TODO: adapt from soots: isIncluded()
      //       if (className.equals(pkg)
      //          || ((pkg.endsWith(".*") || pkg.endsWith("$*")) &&
      // className.startsWith(pkg.substring(0, pkg.length() - 1)))) {
      if (!s.endsWith(".*")) {
        throw new IllegalStateException("filter method is not implemented for a specific class!");
      }
      return s.substring(0, s.length() - 2);
    }

    public DenylistingScopedAnalysisInputLocation(
        @NonNull AnalysisInputLocation inputLocation, Collection<String> denylist) {
      super(inputLocation);
      this.denylist = denylist.stream().map(this::mapStr).collect(Collectors.toList());
    }

    @Override
    protected boolean filter(@NonNull ClassType type) {
      return !denylist.contains(type.getPackageName().toString());
    }
  }
}
