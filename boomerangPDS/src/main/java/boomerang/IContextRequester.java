package boomerang;

import boomerang.scope.Statement;
import java.util.Collection;

public interface IContextRequester {
  Collection<Context> getCallSiteOf(Context child);

  Context initialContext(Statement stmt);
}
