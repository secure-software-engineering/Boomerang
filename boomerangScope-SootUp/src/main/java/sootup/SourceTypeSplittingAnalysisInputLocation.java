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
package sootup;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import sootup.core.frontend.SootClassSource;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.model.SourceType;
import sootup.core.transform.BodyInterceptor;
import sootup.core.types.ClassType;
import sootup.core.views.View;

/** Mimics Soots includePackage / excludePackage behaviour */
public abstract class SourceTypeSplittingAnalysisInputLocation implements AnalysisInputLocation {

  @Nonnull
  protected abstract AnalysisInputLocation getInputLocation();

  @Nonnull
  @Override
  public abstract SourceType getSourceType();

  protected abstract boolean filter(@Nonnull ClassType type);

  private static boolean filterConditionCheck(String pkg, String className) {
    if (className.equals(pkg)) {
      return true;
    }
    if (pkg.endsWith(".*") || pkg.endsWith("$*")) {
      return className.startsWith(pkg.substring(0, pkg.length() - 1));
    }
    return false;
  }

  @Nonnull
  @Override
  public Optional<? extends SootClassSource> getClassSource(
      @Nonnull ClassType type, @Nonnull View view) {
    if (filter(type)) {
      return getInputLocation().getClassSource(type, view);
    }
    return Optional.empty();
  }

  @Nonnull
  @Override
  public Stream<? extends SootClassSource> getClassSources(@Nonnull View view) {
    return getInputLocation().getClassSources(view).filter(type -> filter(type.getClassType()));
  }

  @Nonnull
  @Override
  public List<BodyInterceptor> getBodyInterceptors() {
    return getInputLocation().getBodyInterceptors();
  }

  public static class LibraryAnalysisInputLocation
      extends SourceTypeSplittingAnalysisInputLocation {

    @Nonnull private final SourceTypeSplittingAnalysisInputLocation inputLocation;
    private final Collection<String> excludes;

    public LibraryAnalysisInputLocation(
        @Nonnull SourceTypeSplittingAnalysisInputLocation inputLocation,
        Collection<String> excludes) {
      this.inputLocation = inputLocation;
      this.excludes = excludes;
    }

    @Override
    protected AnalysisInputLocation getInputLocation() {
      return inputLocation;
    }

    protected boolean filter(@Nonnull ClassType type) {
      String className = type.getFullyQualifiedName();
      for (String pkg : excludes) {
        if (filterConditionCheck(pkg, className)) {
          return !inputLocation.filter(type);
        }
      }
      return false;
    }

    @Nonnull
    @Override
    public SourceType getSourceType() {
      return SourceType.Library;
    }
  }

  public static class ApplicationAnalysisInputLocation
      extends SourceTypeSplittingAnalysisInputLocation {
    private final Collection<String> includes;
    @Nonnull private final AnalysisInputLocation inputLocation;

    @Override
    protected AnalysisInputLocation getInputLocation() {
      return inputLocation;
    }

    public ApplicationAnalysisInputLocation(
        @Nonnull AnalysisInputLocation inputLocation, Collection<String> includes) {
      this.inputLocation = inputLocation;
      this.includes = includes;
    }

    protected boolean filter(@Nonnull ClassType type) {
      String className = type.getFullyQualifiedName();
      for (String pkg : includes) {
        if (filterConditionCheck(pkg, className)) {
          return true;
        }
      }
      return false;
    }

    @Nonnull
    @Override
    public SourceType getSourceType() {
      return SourceType.Application;
    }
  }
}
