package tests;

import wpds.impl.Weight;

import javax.annotation.Nonnull;

public interface NumWeightInterface extends Weight {
    @Nonnull
    @Override
    Weight extendWith(@Nonnull Weight other);

    @Nonnull
    @Override
    Weight combineWith(@Nonnull Weight other);
}
