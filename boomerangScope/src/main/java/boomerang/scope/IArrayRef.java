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
package boomerang.scope;

/**
 * Interface that provides basic methods to deal with array references. An array reference
 * <i>a[i]</i> consists of a base <i>a</i> and an index expression <i>i</i>. Note that the index may
 * be an integer or a {@link Val}. The method {@link #getIndex()} defaults to -1 if <i>i</i> is not
 * an integer constant.
 */
public interface IArrayRef {

  Val getBase();

  Val getIndexExpr();

  int getIndex();

  ArrayVal asArrayVal();
}
