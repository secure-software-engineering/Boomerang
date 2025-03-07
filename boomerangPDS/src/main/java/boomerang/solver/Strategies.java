package boomerang.solver;

import boomerang.arrays.ArrayHandlingStrategy;
import boomerang.arrays.ArrayIndexInsensitiveStrategy;
import boomerang.arrays.ArrayIndexSensitiveStrategy;
import boomerang.arrays.IgnoreArrayStrategy;
import boomerang.scope.Field;
import boomerang.scope.Statement;
import boomerang.staticfields.FlowSensitiveStaticFieldStrategy;
import boomerang.staticfields.IgnoreStaticFieldStrategy;
import boomerang.staticfields.SingletonStaticFieldStrategy;
import boomerang.staticfields.StaticFieldHandlingStrategy;
import com.google.common.collect.Multimap;

public class Strategies {

  private final StaticFieldHandlingStrategy staticFieldHandlingStrategy;
  private final ArrayHandlingStrategy arrayHandlingStrategy;

  public enum StaticFieldStrategy {
    IGNORE,
    SINGLETON,
    FLOW_SENSITIVE
  }

  public enum ArrayStrategy {
    DISABLED,
    INDEX_INSENSITIVE,
    INDEX_SENSITIVE
  }

  public Strategies(
      StaticFieldStrategy staticFieldStrategy,
      ArrayStrategy arrayStrategy,
      AbstractBoomerangSolver<?> solver,
      Multimap<Field, Statement> fieldLoadStatements,
      Multimap<Field, Statement> fieldStoreStatements) {
    switch (staticFieldStrategy) {
      case IGNORE:
        staticFieldHandlingStrategy = new IgnoreStaticFieldStrategy();
        break;
      case SINGLETON:
        staticFieldHandlingStrategy =
            new SingletonStaticFieldStrategy(solver, fieldLoadStatements, fieldStoreStatements);
        break;
      case FLOW_SENSITIVE:
      default:
        staticFieldHandlingStrategy = new FlowSensitiveStaticFieldStrategy();
        break;
    }
    switch (arrayStrategy) {
      case DISABLED:
        arrayHandlingStrategy = new IgnoreArrayStrategy();
        break;
      case INDEX_INSENSITIVE:
        arrayHandlingStrategy = new ArrayIndexInsensitiveStrategy();
        break;
      case INDEX_SENSITIVE:
      default:
        arrayHandlingStrategy = new ArrayIndexSensitiveStrategy();
        break;
    }
  }

  public StaticFieldHandlingStrategy getStaticFieldStrategy() {
    return staticFieldHandlingStrategy;
  }

  public ArrayHandlingStrategy getArrayHandlingStrategy() {
    return arrayHandlingStrategy;
  }
}
