package boomerang.scope;

public interface IfStatement {
  enum Evaluation {
    TRUE,
    FALSE,
    UNKNOWN
  }

  Statement getTarget();

  Evaluation evaluate(Val val);

  boolean uses(Val val);
}
