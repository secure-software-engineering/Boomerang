package boomerang.scope;

import boomerang.util.AccessPath;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class AccessPathParser {

  public static Collection<? extends AccessPath> parseAllFromString(String value, Method m) {
    Set<AccessPath> results = Sets.newHashSet();
    for (String v : value.split(";")) {
      results.add(parseAccessPathFromString(v, m));
    }
    return results;
  }

  private static AccessPath parseAccessPathFromString(String value, Method m) {
    List<String> fieldNames = Lists.newArrayList();
    String baseName;
    boolean overApproximated = value.endsWith("*");
    if (!value.contains("[")) {
      baseName = value;
    } else {
      int i = value.indexOf("[");
      baseName = value.substring(0, i);
      fieldNames =
          Lists.newArrayList(
              value.substring(i + 1, value.length() - (!overApproximated ? 1 : 2)).split(","));
    }

    throw new UnsupportedOperationException("not refactored yet");
  }
  /*
    // TODO: [ms] refactor!
    List<Field> fields = Lists.newArrayList();
    Local base = getLocal(m, baseName);
    soot.Type type = base.getType();
    for (String fieldName : fieldNames) {
      if (type instanceof RefType) {
        RefType refType = (RefType) type;
        SootField fieldByName = refType.getSootClass().getFieldByName(fieldName);
        fields.add(new JimpleField(fieldByName));
        type = fieldByName.getType();
      }
    }
    return new AccessPath( new JimpleVal(base, m), (!overApproximated ? fields : Sets.newHashSet(fields)));
  }

  private static Local getLocal(JimpleMethod m, String baseName) {
    Chain<Local> locals = m.getDelegate().getActiveBody().getLocals();
    for (Local l : locals) {
      if (l.getName().equals(baseName)) return l;
    }
    throw new RuntimeException(
        "Could not find local with name " + baseName + " in method " + m.getDelegate());
  }
  */
}
