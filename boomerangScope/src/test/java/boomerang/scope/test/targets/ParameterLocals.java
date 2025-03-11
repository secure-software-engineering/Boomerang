package boomerang.scope.test.targets;

public class ParameterLocals {

    public static void main(String[] args) {
        ParameterLocals parameterLocals = new ParameterLocals();

        parameterLocals.noParameters();
        parameterLocals.oneParameter(10);

        A a = new A();
        parameterLocals.twoParameters(20, a);
    }

    public void noParameters() {}

    public void oneParameter(@SuppressWarnings("unused") int i) {}

    public void twoParameters(@SuppressWarnings("unused")int i, @SuppressWarnings("unused") A a) {}
}
