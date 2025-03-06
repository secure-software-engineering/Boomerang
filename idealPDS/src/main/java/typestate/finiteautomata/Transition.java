package typestate.finiteautomata;

import javax.annotation.Nonnull;

public interface Transition {
  @Nonnull
  State from();

  @Nonnull
  State to();
}
