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
package test.core;

import boomerang.options.BoomerangOptions;

/*
 * Note: If you modify this class, you most likely want to modify its "copy"
 *       in the boomerangPDS module as well. In an ideal world this would be
 *       part of the TestingFramework class, which is part of the testCore
 *       maven module. However, this class needs access to the BoomerangOptions
 *       class (which in turn refers to other classes) that is part of the
 *       boomerangPDS maven module. Hence, this would lead to a cyclic
 *       dependency testCore <-> boomerangPDS.
 * Note 2: If you are not in the mood to keep these two classes in sync,
 *         refactor it into a single class in a new boomerangOptions module.
 *         That would be the cleaner solution but it's not worth the effort
 *         atm, IMHO (note that the BoomerangOptions class or, more likely, an
 *         appropriate abstraction of that class has to be moved to that
 *         boomerangOptions maven module as well).
 */
public class BoomerangTestingOptionsBuilder {
  private static final String DEFAULT_OTF_CALLGRAPH_OPTION = "false";

  public static BoomerangOptions.OptionsBuilder create() {
    BoomerangOptions.OptionsBuilder builder = BoomerangOptions.builder();
    String enableOnTheFlyCallGraph =
        System.getProperty("enableOnTheFlyCallGraph", DEFAULT_OTF_CALLGRAPH_OPTION);
    switch (enableOnTheFlyCallGraph) {
      case "true":
        builder.enableOnTheFlyCallGraph(true);
        builder.enableAllowMultipleQueries(true);
        break;
      case "false":
        builder.enableOnTheFlyCallGraph(false);
        break;
      default:
        throw new IllegalArgumentException(
            "Illegal value for the enableOnTheFlyCallGraph option: "
                + enableOnTheFlyCallGraph
                + " (valid values: true, false)");
    }
    return builder;
  }
}
