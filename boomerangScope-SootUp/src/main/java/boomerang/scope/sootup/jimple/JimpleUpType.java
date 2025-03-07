package boomerang.scope.sootup.jimple;

import boomerang.scope.AllocVal;
import boomerang.scope.Type;
import boomerang.scope.Val;
import boomerang.scope.WrappedClass;
import boomerang.scope.sootup.SootUpFrameworkScope;
import sootup.core.typehierarchy.TypeHierarchy;
import sootup.core.types.ArrayType;
import sootup.core.types.NullType;
import sootup.core.types.PrimitiveType;
import sootup.core.types.ReferenceType;
import sootup.java.core.JavaSootClass;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

public class JimpleUpType implements Type {

  private final sootup.core.types.Type delegate;

  public JimpleUpType(sootup.core.types.Type delegate) {
    this.delegate = delegate;
  }

  @Override
  public boolean isNullType() {
    return delegate instanceof NullType;
  }

  @Override
  public boolean isRefType() {
    return delegate instanceof ReferenceType;
  }

  @Override
  public boolean isArrayType() {
    return delegate instanceof ArrayType;
  }

  @Override
  public Type getArrayBaseType() {
    return new JimpleUpType(delegate).getArrayBaseType();
  }

  @Override
  public WrappedClass getWrappedClass() {
    JavaClassType classType = (JavaClassType) delegate;
    JavaSootClass sootClass = SootUpFrameworkScope.getInstance().getSootClass(classType);
    return new JimpleUpWrappedClass(sootClass);
  }

  @Override
  public boolean doesCastFail(Type targetVal, Val target) {
    JavaClassType targetType = (JavaClassType) ((JimpleUpType) targetVal).getDelegate();
    if (this.getDelegate() instanceof NullType) {
      return true;
    }

    JavaClassType sourceType = (JavaClassType) this.getDelegate();
    JavaSootClass targetClass = SootUpFrameworkScope.getInstance().getSootClass(targetType);
    JavaSootClass sourceClass = SootUpFrameworkScope.getInstance().getSootClass(sourceType);

    if (targetClass instanceof SootUpFrameworkScope.PhantomClass
        || sourceClass instanceof SootUpFrameworkScope.PhantomClass) {
      return false;
    }

    if (target instanceof AllocVal && ((AllocVal) target).getAllocVal().isNewExpr()) {
      boolean castFails =
          SootUpFrameworkScope.getInstance()
              .getView()
              .getTypeHierarchy()
              .isSubtype(targetType, sourceType);
      return !castFails;
    }
    // TODO this line is necessary as canStoreType does not properly work for
    // interfaces, see Java doc.
    if (targetClass.isInterface()) {
      return false;
    }
    boolean castFails =
        SootUpFrameworkScope.getInstance()
                .getView()
                .getTypeHierarchy()
                .isSubtype(targetType, sourceType)
            || SootUpFrameworkScope.getInstance()
                .getView()
                .getTypeHierarchy()
                .isSubtype(sourceType, targetType);
    return !castFails;
  }

  @Override
  public boolean isSubtypeOf(String type) {
    if (delegate.toString().equals(type)) return true;
    if (!(delegate instanceof ReferenceType)) {
      if (delegate instanceof PrimitiveType) {
        return type.equals(delegate.toString());
      }
      return false;
    }

    SootUpFrameworkScope scopeInstance = SootUpFrameworkScope.getInstance();
    JavaClassType superType = scopeInstance.getIdentifierFactory().getClassType(type);
    JavaSootClass superClass = scopeInstance.getSootClass(superType);

    JavaClassType allocatedType = (JavaClassType) delegate;
    JavaSootClass sootClass = scopeInstance.getSootClass(allocatedType);
    TypeHierarchy typeHierarchy = scopeInstance.getView().getTypeHierarchy();
    if (!superClass.isInterface()) {
      return typeHierarchy.isSubtype(sootClass.getType(), superClass.getType());
    }
    // TODO: [ms] check if seperation of interface/class is necessary
    if (typeHierarchy.subclassesOf(superClass.getType()).anyMatch(t -> t == allocatedType)) {
      return true;
    }
    return typeHierarchy.implementersOf(superClass.getType()).anyMatch(t -> t == allocatedType);
  }

  @Override
  public boolean isSupertypeOf(String subTypeStr) {
    if (!(delegate instanceof ReferenceType)) {
      if (delegate instanceof PrimitiveType) {
        return subTypeStr.equals(delegate.toString());
      }
      return false;
    }

    JavaView view = SootUpFrameworkScope.getInstance().getView();
    TypeHierarchy typeHierarchy = view.getTypeHierarchy();

    JavaClassType subType = view.getIdentifierFactory().getClassType(subTypeStr);
    if (!typeHierarchy.contains(subType)) {
      return false;
    }

    JavaClassType thisType = view.getIdentifierFactory().getClassType(delegate.toString());
    if (!typeHierarchy.contains(thisType)) {
      return false;
    }

    return typeHierarchy.isSubtype(subType, thisType);
  }

  @Override
  public boolean isBooleanType() {
    return false;
  }

  public sootup.core.types.Type getDelegate() {
    return delegate;
  }
}
