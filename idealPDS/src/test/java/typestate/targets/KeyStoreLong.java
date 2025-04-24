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
import java.io.FileInputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import test.TestMethod;

@SuppressWarnings("unused")
public class KeyStoreLong {

  @TestMethod
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

  @TestMethod
  public void test4() throws GeneralSecurityException, IOException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    KeyStore x = ks;
    FileInputStream fis = null;
    ks.load(fis, null);
    Assertions.mustBeInAcceptingState(ks);
    Assertions.mustBeInAcceptingState(x);
  }

  @TestMethod
  public void test2() throws KeyStoreException {
    KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
    ks.aliases();
    Assertions.mustBeInErrorState(ks);
  }

  @TestMethod
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

  @TestMethod
  public void catchClause() {
    try {
      final KeyStore keyStore = KeyStore.getInstance("JKS");
      // ... Some code
      int size = keyStore.size(); // Hit !
      Assertions.mustBeInErrorState(keyStore);
    } catch (KeyStoreException e) {
      e.printStackTrace();
    }
  }
}
