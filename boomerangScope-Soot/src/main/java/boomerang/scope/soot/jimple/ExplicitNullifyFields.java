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
package boomerang.scope.soot.jimple;

import java.util.LinkedHashSet;
import java.util.Set;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.InstanceFieldRef;
import soot.jimple.NullConstant;
import soot.jimple.internal.JAssignStmt;
import soot.jimple.internal.JInstanceFieldRef;
import soot.util.Chain;

public class ExplicitNullifyFields {
  public static void apply() {
    for (SootClass c : Scene.v().getClasses()) {
      for (SootMethod m : c.getMethods()) {
        if (m.hasActiveBody() && m.isConstructor()) {
          apply(m);
        }
      }
    }
  }

  private static void apply(SootMethod cons) {
    Chain<SootField> fields = cons.getDeclaringClass().getFields();
    UnitPatchingChain units = cons.getActiveBody().getUnits();
    Set<SootField> fieldsDefinedInMethod = getFieldsDefinedInMethod(cons);
    for (SootField f : fields) {
      if (fieldsDefinedInMethod.contains(f)) continue;
      if (f.isStatic()) continue;
      if (f.isFinal()) continue;
      if (f.getType() instanceof RefType) {
        units.addFirst(
            new JAssignStmt(
                new JInstanceFieldRef(cons.getActiveBody().getThisLocal(), f.makeRef()),
                NullConstant.v()));
      }
    }
  }

  private static Set<SootField> getFieldsDefinedInMethod(SootMethod cons) {
    Set<SootField> res = new LinkedHashSet<>();
    for (Unit u : cons.getActiveBody().getUnits()) {
      if (u instanceof AssignStmt) {
        AssignStmt as = (AssignStmt) u;
        Value left = as.getLeftOp();
        if (left instanceof InstanceFieldRef) {
          InstanceFieldRef ifr = (InstanceFieldRef) left;
          res.add(ifr.getField());
        }
      }
    }
    return res;
  }
}
