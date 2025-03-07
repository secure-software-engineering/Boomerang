package boomerang.flowfunction;

import boomerang.options.DefaultAllocationSite;
import boomerang.options.IAllocationSite;
import boomerang.solver.Strategies;

public class DefaultBackwardFlowFunctionOptions {

  private final BackwardFlowFunctionBuilder builder;

  public DefaultBackwardFlowFunctionOptions(BackwardFlowFunctionBuilder builder) {
    this.builder = builder;
  }

  public IAllocationSite allocationSite() {
    return builder.allocationSite;
  }

  public Strategies.StaticFieldStrategy staticFieldStrategy() {
    return builder.staticFieldStrategy;
  }

  public Strategies.ArrayStrategy arrayStrategy() {
    return builder.arrayStrategy;
  }

  public boolean trackFields() {
    return builder.trackFields;
  }

  public boolean includeInnerClassFields() {
    return builder.includeInnerClassFields;
  }

  public static BackwardFlowFunctionBuilder builder() {
    return new BackwardFlowFunctionBuilder();
  }

  public static DefaultBackwardFlowFunctionOptions DEFAULT() {
    return new BackwardFlowFunctionBuilder().build();
  }

  public static class BackwardFlowFunctionBuilder {

    private IAllocationSite allocationSite;
    private Strategies.StaticFieldStrategy staticFieldStrategy;
    private Strategies.ArrayStrategy arrayStrategy;
    private boolean trackFields;
    private boolean includeInnerClassFields;

    private BackwardFlowFunctionBuilder() {
      this.allocationSite = new DefaultAllocationSite();
      this.staticFieldStrategy = Strategies.StaticFieldStrategy.SINGLETON;
      this.arrayStrategy = Strategies.ArrayStrategy.INDEX_INSENSITIVE;
      this.trackFields = true;
      this.includeInnerClassFields = true;
    }

    public BackwardFlowFunctionBuilder withAllocationSite(IAllocationSite allocationSite) {
      this.allocationSite = allocationSite;
      return this;
    }

    public BackwardFlowFunctionBuilder withStaticFieldStrategy(
        Strategies.StaticFieldStrategy staticFieldStrategy) {
      this.staticFieldStrategy = staticFieldStrategy;
      return this;
    }

    public BackwardFlowFunctionBuilder withArrayStrategy(Strategies.ArrayStrategy arrayStrategy) {
      this.arrayStrategy = arrayStrategy;
      return this;
    }

    public BackwardFlowFunctionBuilder enableTrackFields(boolean trackFields) {
      this.trackFields = trackFields;
      return this;
    }

    public BackwardFlowFunctionBuilder enableIncludeInnerClassFields(
        boolean includeInnerClassFields) {
      this.includeInnerClassFields = includeInnerClassFields;
      return this;
    }

    public DefaultBackwardFlowFunctionOptions build() {
      return new DefaultBackwardFlowFunctionOptions(this);
    }
  }
}
