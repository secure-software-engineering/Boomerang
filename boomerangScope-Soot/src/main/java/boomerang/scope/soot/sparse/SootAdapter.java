package boomerang.scope.soot.sparse;

import boomerang.scope.Field;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Val;
import boomerang.scope.soot.jimple.JimpleField;
import boomerang.scope.soot.jimple.JimpleMethod;
import boomerang.scope.soot.jimple.JimpleStatement;
import boomerang.scope.soot.jimple.JimpleStaticFieldVal;
import boomerang.scope.soot.jimple.JimpleVal;
import boomerang.scope.soot.sparse.aliasaware.MStaticFieldRef;
import soot.*;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;

public class SootAdapter {

  public static Statement asStatement(Unit unit, Method method) {
    return JimpleStatement.create((Stmt) unit, method);
  }

  public static Stmt asStmt(Statement stmt) {
    return ((JimpleStatement) stmt).getDelegate();
  }

  public static Type getTypeOfVal(Val val) {
    if (val instanceof JimpleVal) {
      Value value = asValue(val);
      return value.getType();
    } else if (val instanceof JimpleStaticFieldVal) {
      SootField field = asField(val);
      return field.getType();
    } else {
      throw new RuntimeException("Unknown Val");
    }
  }

  public static Value asValue(Val val) {
    if (val instanceof JimpleStaticFieldVal) {
      JimpleStaticFieldVal staticVal = (JimpleStaticFieldVal) val;
      Field field = staticVal.field();
      SootField sootField = ((JimpleField) field).getDelegate();
      SootFieldRef sootFieldRef = sootField.makeRef();
      StaticFieldRef srf = new MStaticFieldRef(sootFieldRef);
      return srf;
    }
    return ((JimpleVal) val).getDelegate();
  }

  public static SootField asField(Val val) {
    Field field = ((JimpleStaticFieldVal) val).field();
    return ((JimpleField) field).getDelegate();
  }

  public static SootMethod asSootMethod(Method m) {
    return ((JimpleMethod) m).getDelegate();
  }
}
