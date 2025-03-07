package test.cases.hashmap;

import test.TestMethod;
import test.core.QueryMethods;

@SuppressWarnings("unused")
public class AllAliasLongTarget {

  @TestMethod
  public void test() {
    TreeNode<Object, Object> a = new TreeNode<>(0, new Object(), new Object(), null);
    TreeNode<Object, Object> t = new TreeNode<>(0, null, new Object(), a);
    TreeNode.balanceDeletion(t, a);
    // t.balanceInsertion(t, t);
    t.treeify(new TreeNode[] {a, t});
    // t.moveRootToFront(new TreeNode[]{a,t},a);
    QueryMethods.queryFor(t);
  }
}
