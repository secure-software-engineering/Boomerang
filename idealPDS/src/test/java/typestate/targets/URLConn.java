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
package typestate.targets;

import assertions.Assertions;
import java.io.IOException;
import java.net.HttpURLConnection;
import test.TestMethod;

@SuppressWarnings("unused")
public class URLConn {

  @TestMethod
  public void test1() throws IOException {
    HttpURLConnection httpURLConnection =
        new HttpURLConnection(null) {

          @Override
          public void connect() {}

          @Override
          public boolean usingProxy() {
            return false;
          }

          @Override
          public void disconnect() {}
        };
    httpURLConnection.connect();
    httpURLConnection.setDoOutput(true);
    Assertions.mustBeInErrorState(httpURLConnection);
    httpURLConnection.setAllowUserInteraction(false);
    Assertions.mustBeInErrorState(httpURLConnection);
  }

  @TestMethod
  public void test2() throws IOException {
    HttpURLConnection httpURLConnection =
        new HttpURLConnection(null) {

          @Override
          public void connect() {}

          @Override
          public boolean usingProxy() {
            return false;
          }

          @Override
          public void disconnect() {}
        };
    httpURLConnection.setDoOutput(true);
    httpURLConnection.setAllowUserInteraction(false);

    httpURLConnection.connect();
    Assertions.mustBeInAcceptingState(httpURLConnection);
  }
}
