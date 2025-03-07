package sparse.eval;

public class PropagationCounter {
  private long forwardPropagation = 0;
  private long backwardPropagation = 0;

  public PropagationCounter() {}

  public void countForwardPropagation() {
    forwardPropagation++;
  }

  public void countBackwardProgragation() {
    backwardPropagation++;
  }

  public long getForwardPropagation() {
    return forwardPropagation;
  }

  public long getBackwardPropagation() {
    return backwardPropagation;
  }
}
