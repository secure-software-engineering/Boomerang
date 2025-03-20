package boomerang.scope.test.targets;

import java.util.Arrays;

public class AssignmentTarget {

    public static void main(String[] args) {
        arrayAllocation();
        constantAssignment();
    }

    public static void arrayAllocation() {
        int[] arr = new int[]{1, 2};

        System.out.println(Arrays.toString(arr));
    }

    public static void constantAssignment() {
        int i = 10;
        long l = 1000;
        String s = "test";

        System.out.println(i + l + " " + s);
    }
}
