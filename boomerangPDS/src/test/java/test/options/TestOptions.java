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
package test.options;

import boomerang.flowfunction.DefaultFlowFunctionFactory;
import boomerang.solver.Strategies;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TestOptions {

  String[] expectedAllocSites();

  Class<?> flowFunctionFactory() default DefaultFlowFunctionFactory.class;

  Strategies.ArrayStrategy arrayStrategy() default Strategies.ArrayStrategy.INDEX_SENSITIVE;

  Strategies.StaticFieldStrategy staticFieldStrategy() default
      Strategies.StaticFieldStrategy.SINGLETON;

  boolean trackStaticFieldAtEntryPointToClinit() default false;

  int maxFieldDepth() default -1;

  int maxCallDepth() default -1;

  int timeout() default -1;

  boolean fieldSensitivity() default true;

  boolean contextSensitivity() default true;

  boolean callSummaries() default false;

  boolean fieldSummaries() default false;
}
