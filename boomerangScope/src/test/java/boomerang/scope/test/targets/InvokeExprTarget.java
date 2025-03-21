package boomerang.scope.test.targets;

public class InvokeExprTarget {

    public static void main(String[] args) {
        instanceInvokeExpr();
    }

    public static void instanceInvokeExpr() {
        int i = 10;
        A a = new A();

        a.methodCall(i);
    }

    public static void alias() {
        int i = 10;
        A a = new A();

        if (Math.random() > 0.5) {
            i = 10000;
        }

        a.methodCall(i);
    }
}
