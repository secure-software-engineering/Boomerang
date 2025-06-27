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
package typestate;

import assertions.Assertions;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import test.ExpectedTestParameters;
import test.IDEalTestRunnerInterceptor;
import test.TestConfig;
import typestate.impl.statemachines.KeyStoreStateMachine;

@ExtendWith(IDEalTestRunnerInterceptor.class)
@TestConfig(stateMachine = KeyStoreStateMachine.class)
public class KeyStoreTest {

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test1() throws GeneralSecurityException, IOException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

    java.io.FileInputStream fis = null;
    try {
      fis = new FileInputStream("keyStoreName");
      ks.load(fis, null);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
    Assertions.mustBeInAcceptingState(ks);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 2)
  public void test4() throws GeneralSecurityException, IOException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    KeyStore x = ks;
    FileInputStream fis = null;
    ks.load(fis, null);
    Assertions.mustBeInAcceptingState(ks);
    Assertions.mustBeInAcceptingState(x);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test2() throws KeyStoreException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.aliases();
    Assertions.mustBeInErrorState(ks);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void test3()
      throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

    java.io.FileInputStream fis = null;
    try {
      fis = new java.io.FileInputStream("keyStoreName");
      ks.load(fis, null);
    } finally {
      if (fis != null) {
        fis.close();
      }
    }
    ks.aliases();
    Assertions.mustBeInAcceptingState(ks);
  }

  @Test
  @ExpectedTestParameters(expectedSeedCount = 1, expectedAssertionCount = 1)
  public void catchClause() {
    try {
      final KeyStore keyStore = KeyStore.getInstance("JKS");
      // ... Some code
      int size = keyStore.size(); // Hit !
      Assertions.mustBeInErrorState(keyStore);
    } catch (KeyStoreException ignored) {
    }
  }
}
