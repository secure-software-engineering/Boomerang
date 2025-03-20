package boomerang.scope.test.targets;

import java.util.Arrays;

public class ArrayTarget {

    public static void main(String[] args) {
        singleArrayLoad(new int[]{1, 2});
        singleArrayStore();

        multiArrayStore();
    }

    public static void singleArrayLoad(int[] arr) {
        int i = arr[1];

        System.out.println(i);
    }

    public static void singleArrayStore() {
        int[] arr = new int[2];
        arr[0] = 1;

        System.out.println(Arrays.toString(arr));
    }

    public static void multiArrayStore() {
        int[][] arr = new int[2][2];
        arr[0][0] = 1;

        System.out.println(Arrays.deepToString(arr));
    }
}
