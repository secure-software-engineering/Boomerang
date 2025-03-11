package boomerang.scope.test;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class MethodSignature {

    private final String declaringClass;
    private final String methodName;
    private final String returnType;
    private final List<String> parameters;

    public static final String VOID = "void";

    public MethodSignature(String declaringClass, String methodName) {
        this(declaringClass, methodName, VOID);
    }

    public MethodSignature(String declaringClass, String methodName, String returnType) {
        this(declaringClass, methodName, returnType, Collections.emptyList());
    }

    public MethodSignature(String declaringClass, String methodName, List<String> parameters) {
        this(declaringClass, methodName, VOID, parameters);
    }

    public MethodSignature(
            String declaringClass, String methodName, String returnType, List<String> parameters) {
        this.declaringClass = declaringClass;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameters = parameters;
    }

    public String getDeclaringClass() {
        return declaringClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MethodSignature that = (MethodSignature) o;
        return Objects.equals(declaringClass, that.declaringClass)
                && Objects.equals(methodName, that.methodName)
                && Objects.equals(returnType, that.returnType)
                && Objects.equals(parameters, that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(declaringClass, methodName, returnType, parameters);
    }

    @Override
    public String toString() {
        return declaringClass + " " + returnType + " " + methodName + " " + parameters;
    }
}
