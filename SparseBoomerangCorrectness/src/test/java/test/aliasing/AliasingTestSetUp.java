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
package test.aliasing;

import boomerang.BackwardQuery;
import boomerang.scope.Method;
import boomerang.sparse.SparsificationStrategy;
import boomerang.util.AccessPath;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import test.TestingFramework;

/**
 * Compares the aliases computed with the sparsification strategies {@link
 * SparsificationStrategy#TYPE_BASED} and {@link SparsificationStrategy#ALIAS_AWARE} against the
 * aliases computed without sparsification.
 */
public abstract class AliasingTestSetUp {

  /**
   * Intermediate locals introduced by the frameworks. Sparsification may skip the statements that
   * assign them, so they are not compared:
   *
   * <ul>
   *   <li>Soot / SootUp: $stackN, $lN (stack locals) and varReplacerN (Boomerang pre-transformer)
   *   <li>Opal: $sN (stack locals)
   * </ul>
   */
  private static final Pattern INTERMEDIATE_LOCAL =
      Pattern.compile("^(\\$stack\\d+|\\$l\\d+|\\$s\\d+|varReplacer\\d+)$");

  private static final List<SparsificationStrategy> SPARSE_STRATEGIES =
      List.of(SparsificationStrategy.TYPE_BASED, SparsificationStrategy.ALIAS_AWARE);

  /** Set if Boomerang without sparsification reports aliases that a sparse analysis may miss */
  protected boolean falsePositiveInDefaultBoomerang = false;

  private SparseCorrectnessTestingFramework framework;

  @BeforeEach
  public void setUp() {
    framework = new SparseCorrectnessTestingFramework();
  }

  @AfterEach
  public void tearDown() {
    framework.cleanUp();
  }

  protected TestingFramework.Framework getFramework() {
    return framework.getFramework();
  }

  protected void runAnalyses(String queryLHS, String targetClass, String targetMethod) {
    runAnalyses(queryLHS, targetClass, targetMethod, true);
  }

  protected void runAnalyses(
      String queryLHS, String targetClass, String targetMethod, boolean ignoreAfterQuery) {
    framework.initialize(targetClass);
    Method method = framework.findMethod(targetMethod);
    BackwardQuery query = framework.createQuery(method, queryLHS);

    Set<String> nonSparseAliases =
        toStrings(framework.getAliases(query, SparsificationStrategy.NONE, ignoreAfterQuery));
    for (SparsificationStrategy strategy : SPARSE_STRATEGIES) {
      Set<String> sparseAliases =
          toStrings(framework.getAliases(query, strategy, ignoreAfterQuery));
      checkResults(strategy, sparseAliases, nonSparseAliases);
    }
  }

  private static Set<String> toStrings(Set<AccessPath> accessPaths) {
    return accessPaths.stream()
        .filter(ap -> !INTERMEDIATE_LOCAL.matcher(ap.getBase().getVariableName()).matches())
        .map(AccessPath::toString)
        .collect(Collectors.toSet());
  }

  private void checkResults(
      SparsificationStrategy strategy, Set<String> sparseAliases, Set<String> nonSparseAliases) {
    String prefix = framework.getFramework() + " " + strategy + ": ";
    if (!falsePositiveInDefaultBoomerang) {
      Assertions.assertTrue(
          sparseAliases.containsAll(nonSparseAliases),
          prefix + "unsound, missing " + difference(nonSparseAliases, sparseAliases));
    }
    Assertions.assertTrue(
        nonSparseAliases.containsAll(sparseAliases),
        prefix + "imprecise, additional " + difference(sparseAliases, nonSparseAliases));
  }

  private static String difference(Set<String> larger, Set<String> smaller) {
    return larger.stream()
        .filter(e -> !smaller.contains(e))
        .sorted()
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
