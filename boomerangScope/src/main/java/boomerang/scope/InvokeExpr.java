package boomerang.scope;

import java.util.List;

public interface InvokeExpr {

  Val getArg(int index);

  List<Val> getArgs();

  boolean isInstanceInvokeExpr();

  Val getBase();

  DeclaredMethod getMethod();

  boolean isSpecialInvokeExpr();

  boolean isStaticInvokeExpr();
}
