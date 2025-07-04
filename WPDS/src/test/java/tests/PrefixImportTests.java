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
package tests;

import de.fraunhofer.iem.Location;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import wpds.impl.PAutomaton;
import wpds.impl.PrefixImport;
import wpds.impl.Transition;
import wpds.interfaces.State;

public class PrefixImportTests {

  PAutomaton<StringLoc, StringState> autA =
      new PAutomaton<>() {
        @Override
        public StringState createState(StringState d, StringLoc loc) {
          return new StringState(d + "_" + loc) {
            @Override
            public boolean generated() {
              return true;
            }
          };
        }

        @Override
        public boolean isGeneratedState(StringState d) {
          return d.generated();
        }

        @Override
        public StringLoc epsilon() {
          return new StringLoc("EPS");
        }
      };

  PAutomaton<StringLoc, StringState> autB =
      new PAutomaton<>() {
        @Override
        public StringState createState(StringState d, StringLoc loc) {
          return new StringState(d + "_" + loc) {
            @Override
            public boolean generated() {
              return true;
            }
          };
        }

        @Override
        public boolean isGeneratedState(StringState d) {
          return d.generated();
        }

        @Override
        public StringLoc epsilon() {
          return new StringLoc("EPS");
        }
      };

  @Test
  public void prefixCombine() {
    autA.addTransition(t("A1", "f", "INITA"));
    autA.addTransition(t("A3", "g", "INITA"));
    autA.addTransition(t("A4", "h", "A3"));

    autB.addTransition(t("A1", "f", "B2"));
    autB.addTransition(t("B2", "i", "INITB"));

    new PrefixImport<>(autA, autB, t("A1", "f", "INITA"));
    Assertions.assertTrue(autB.getTransitions().contains(t("A3", "g", "B2")));
    Assertions.assertTrue(autB.getTransitions().contains(t("A4", "h", "A3")));
  }

  @Test
  public void prefixCombineDouble() {
    autA.addTransition(t("A1", "f", "A2"));
    autA.addTransition(t("A2", "i", "INITA"));
    autA.addTransition(t("A3", "g", "INITA"));
    autA.addTransition(t("A4", "k", "A3"));

    autB.addTransition(t("A1", "f", "B2"));
    autB.addTransition(t("B2", "i", "INITB"));

    new PrefixImport<>(autA, autB, t("A1", "f", "A2"));
    Assertions.assertTrue(autB.getTransitions().contains(t("A3", "g", "INITB")));
    Assertions.assertTrue(autB.getTransitions().contains(t("A4", "k", "A3")));
  }

  private Transition<StringLoc, StringState> t(String start, String label, String target) {
    return new Transition<StringLoc, StringState>(
        new StringState(start), new StringLoc(label), new StringState(target));
  }

  class StringState implements State {
    private final String rep;

    public StringState(String string) {
      rep = string;
    }

    public boolean generated() {
      return false;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((rep == null) ? 0 : rep.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      StringState other = (StringState) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (rep == null) {
        return other.rep == null;
      } else return rep.equals(other.rep);
    }

    @Override
    public String toString() {
      return rep;
    }

    private PrefixImportTests getOuterType() {
      return PrefixImportTests.this;
    }
  }

  class StringLoc implements Location {

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + getOuterType().hashCode();
      result = prime * result + ((rep == null) ? 0 : rep.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) return true;
      if (obj == null) return false;
      if (getClass() != obj.getClass()) return false;
      StringLoc other = (StringLoc) obj;
      if (!getOuterType().equals(other.getOuterType())) return false;
      if (rep == null) {
        return other.rep == null;
      } else return rep.equals(other.rep);
    }

    private final String rep;

    public StringLoc(String string) {
      rep = string;
    }

    @Override
    public String toString() {
      return rep;
    }

    private PrefixImportTests getOuterType() {
      return PrefixImportTests.this;
    }

    @Override
    public boolean accepts(Location other) {
      return this.equals(other);
    }
  }
}
