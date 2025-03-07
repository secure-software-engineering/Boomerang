package boomerang.scope;

public interface DataFlowScope {

  boolean isExcluded(DeclaredMethod method);

  boolean isExcluded(Method method);

  /** Basic dataflow scope that excludes all methods from classes that are not loaded */
  DataFlowScope EXCLUDE_PHANTOM_CLASSES =
      new DataFlowScope() {

        @Override
        public boolean isExcluded(DeclaredMethod method) {
          return method.getDeclaringClass().isPhantom();
        }

        @Override
        public boolean isExcluded(Method method) {
          return method.getDeclaringClass().isPhantom();
        }
      };
}
