package boomerang.scope.soot.jimple;

import boomerang.scope.ControlFlowGraph;
import boomerang.scope.Method;
import boomerang.scope.Statement;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import soot.Local;
import soot.SootMethod;
import soot.util.Chain;

/**
 * Class that wraps a {@link SootMethod} with an existing body. All operations provide their
 * corresponding information.
 */
public class JimpleMethod extends Method {

  private final SootMethod delegate;

  protected static Interner<JimpleMethod> INTERNAL_POOL = Interners.newWeakInterner();
  protected ControlFlowGraph cfg;
  private List<Val> parameterLocalCache;
  private Collection<Val> localCache;

  protected JimpleMethod(SootMethod delegate) {
    this.delegate = delegate;
    if (!delegate.hasActiveBody()) {
      throw new RuntimeException(
          "Trying to build a Jimple method for " + delegate + " without active body present");
    }
  }

  public static JimpleMethod of(SootMethod m) {
    return INTERNAL_POOL.intern(new JimpleMethod(m));
  }

  @Override
  public boolean isStaticInitializer() {
    return delegate.isStaticInitializer();
  }

  @Override
  public boolean isParameterLocal(Val val) {
    if (val.isStatic()) return false;
    if (!delegate.hasActiveBody()) {
      throw new RuntimeException("Soot Method has no active body");
    }

    List<Val> parameterLocals = getParameterLocals();
    return parameterLocals.contains(val);
  }

  @Override
  public List<Type> getParameterTypes() {
    List<Type> types = new ArrayList<>();

    for (soot.Type type : delegate.getParameterTypes()) {
      types.add(new JimpleType(type));
    }
    return types;
  }

  @Override
  public Type getParameterType(int index) {
    return new JimpleType(delegate.getParameterType(index));
  }

  @Override
  public Type getReturnType() {
    return new JimpleType(delegate.getReturnType());
  }

  @Override
  public boolean isThisLocal(Val val) {
    if (val.isStatic()) return false;
    if (!delegate.hasActiveBody()) {
      throw new RuntimeException("Soot Method has no active body");
    }
    if (delegate.isStatic()) return false;
    Val parameterLocals = getThisLocal();
    return parameterLocals.equals(val);
  }

  @Override
  public Collection<Val> getLocals() {
    if (localCache == null) {
      localCache = Sets.newHashSet();
      Chain<Local> locals = delegate.getActiveBody().getLocals();
      for (Local l : locals) {
        localCache.add(new JimpleVal(l, this));
      }
    }
    return localCache;
  }

  @Override
  public Val getThisLocal() {
    return new JimpleVal(delegate.getActiveBody().getThisLocal(), this);
  }

  @Override
  public List<Val> getParameterLocals() {
    if (parameterLocalCache == null) {
      parameterLocalCache = Lists.newArrayList();
      for (Local v : delegate.getActiveBody().getParameterLocals()) {
        parameterLocalCache.add(new JimpleVal(v, this));
      }
    }
    return parameterLocalCache;
  }

  @Override
  public boolean isStatic() {
    return delegate.isStatic();
  }

  @Override
  public boolean isDefined() {
    return true;
  }

  @Override
  public boolean isPhantom() {
    return false;
  }

  @Override
  public List<Statement> getStatements() {
    return getControlFlowGraph().getStatements();
  }

  @Override
  public WrappedClass getDeclaringClass() {
    return new JimpleWrappedClass(delegate.getDeclaringClass());
  }

  @Override
  public ControlFlowGraph getControlFlowGraph() {
    if (cfg == null) {
      cfg = new JimpleControlFlowGraph(this);
    }
    return cfg;
  }

  @Override
  public String getSubSignature() {
    return delegate.getSubSignature();
  }

  @Override
  public String getName() {
    return delegate.getName();
  }

  @Override
  public boolean isConstructor() {
    return delegate.isConstructor();
  }

  public SootMethod getDelegate() {
    return delegate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    JimpleMethod that = (JimpleMethod) o;
    return Objects.equals(delegate, that.delegate);
  }

  @Override
  public int hashCode() {
    return Objects.hash(delegate);
  }

  @Override
  public String toString() {
    return delegate != null ? delegate.toString() : "METHOD_EPS";
  }
}
