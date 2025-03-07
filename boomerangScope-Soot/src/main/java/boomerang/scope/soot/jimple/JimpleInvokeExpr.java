package boomerang.scope.soot.jimple;

import boomerang.scope.DeclaredMethod;
import boomerang.scope.InvokeExpr;
import boomerang.scope.Method;
import boomerang.scope.Val;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import soot.jimple.InstanceInvokeExpr;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;

public class JimpleInvokeExpr implements InvokeExpr {

  private final soot.jimple.InvokeExpr delegate;
  private final Method m;
  private ArrayList<Val> argCache;

  public JimpleInvokeExpr(soot.jimple.InvokeExpr ive, Method m) {
    this.delegate = ive;
    this.m = m;
  }

  @Override
  public Val getArg(int index) {
    if (delegate.getArg(index) == null) {
      return Val.zero();
    }
    return new JimpleVal(delegate.getArg(index), m);
  }

  @Override
  public List<Val> getArgs() {
    if (argCache == null) {
      argCache = Lists.newArrayList();
      for (int i = 0; i < delegate.getArgCount(); i++) {
        argCache.add(getArg(i));
      }
    }
    return argCache;
  }

  @Override
  public boolean isInstanceInvokeExpr() {
    return delegate instanceof InstanceInvokeExpr;
  }

  @Override
  public Val getBase() {
    InstanceInvokeExpr iie = (InstanceInvokeExpr) delegate;
    return new JimpleVal(iie.getBase(), m);
  }

  @Override
  public DeclaredMethod getMethod() {
    return new JimpleDeclaredMethod(this, delegate.getMethodRef());
  }

  @Override
  public boolean isSpecialInvokeExpr() {
    return delegate instanceof SpecialInvokeExpr;
  }

  @Override
  public boolean isStaticInvokeExpr() {
    return delegate instanceof StaticInvokeExpr;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleInvokeExpr that = (JimpleInvokeExpr) o;
    return Objects.equals(delegate, that.delegate) && Objects.equals(m, that.m);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate, m);
  }

  @Override
  public String toString() {
    return delegate.toString();
  }
}
