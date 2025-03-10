package boomerang.weights;

import wpds.impl.Weight;


public class MinDistanceWeightOne implements MinDistanceWeightInterface {

    private static final MinDistanceWeight one =
            new MinDistanceWeight();

    public static MinDistanceWeight one() {
        return one;
    }

    @Override
    public Weight extendWith( Weight o) {
        return null;
    }


    @Override
    public Weight combineWith( Weight o) {
        return null;
    }

    @Override
    public Integer getMinDistance() {
        return 0;
    }
}
