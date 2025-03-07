package boomerang.scope;

import com.google.common.collect.Sets;
import de.fraunhofer.iem.Location;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public abstract class Method implements Location {

  private static Method epsilon;
  private Collection<Val> returnLocals;

  protected Method() {}

  public static Method epsilon() {
    if (epsilon == null)
      epsilon =
          new Method() {

            @Override
            public boolean isStaticInitializer() {
              return false;
            }

            @Override
            public boolean isParameterLocal(Val val) {
              return false;
            }

            @Override
            public List<Type> getParameterTypes() {
              return Collections.emptyList();
            }

            @Override
            public Type getParameterType(int index) {
              throw new RuntimeException("Epsilon method has no parameters");
            }

            @Override
            public Type getReturnType() {
              throw new RuntimeException("Epsilon method has no return type");
            }

            @Override
            public boolean isThisLocal(Val val) {
              return false;
            }

            @Override
            public Collection<Val> getLocals() {
              return Collections.emptySet();
            }

            @Override
            public Val getThisLocal() {
              throw new RuntimeException("Epsilon method has no this local");
            }

            @Override
            public List<Val> getParameterLocals() {
              return Collections.emptyList();
            }

            @Override
            public boolean isStatic() {
              return false;
            }

            @Override
            public boolean isDefined() {
              return false;
            }

            @Override
            public boolean isPhantom() {
              return true;
            }

            @Override
            public List<Statement> getStatements() {
              return Collections.emptyList();
            }

            @Override
            public WrappedClass getDeclaringClass() {
              throw new RuntimeException("Epsilon method has no declaring class");
            }

            @Override
            public ControlFlowGraph getControlFlowGraph() {
              throw new RuntimeException("Epsilon method has no control flow graph");
            }

            @Override
            public String getSubSignature() {
              return "EPS";
            }

            @Override
            public String getName() {
              return "EPS";
            }

            @Override
            public boolean isConstructor() {
              return false;
            }

            @Override
            public String toString() {
              return "METHOD_EPS";
            }
          };
    return epsilon;
  }

  public abstract boolean isStaticInitializer();

  public abstract boolean isParameterLocal(Val val);

  public abstract List<Type> getParameterTypes();

  public abstract Type getParameterType(int index);

  public abstract Type getReturnType();

  public abstract boolean isThisLocal(Val val);

  public abstract Collection<Val> getLocals();

  public abstract Val getThisLocal();

  public abstract List<Val> getParameterLocals();

  public abstract boolean isStatic();

  public abstract boolean isDefined();

  public abstract boolean isPhantom();

  public abstract List<Statement> getStatements();

  public abstract WrappedClass getDeclaringClass();

  public abstract ControlFlowGraph getControlFlowGraph();

  public abstract String getSubSignature();

  public abstract String getName();

  public Val getParameterLocal(int i) {
    return getParameterLocals().get(i);
  }

  public abstract boolean isConstructor();

  public Collection<Val> getReturnLocals() {
    if (returnLocals == null) {
      returnLocals = Sets.newHashSet();
      for (Statement s : getStatements()) {
        if (s.isReturnStmt()) {
          returnLocals.add(s.getReturnOp());
        }
      }
    }
    return returnLocals;
  }

  @Override
  public boolean accepts(Location other) {
    return this.equals(other);
  }
}
