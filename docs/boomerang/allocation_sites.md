# Defining Allocation Sites

Boomerang provides an interface that allows the definition of individual allocation sites. An allocation site is a value that should be considered as a points-to object.


## Allocation Site Interface

To define an individual allocation site, we have to implement the `IAllocationSite` interface and override its method `getAllocationSite(...)` that returns an optional `AllocVal`. 
An `AllocVal` represents an allocation site and acts as a wrapper for the allocation site statement and value.
If the optional is present, the `AllocVal` is added to the resulting allocation sites.

When performing a backward analysis, Boomerang calls this method on each statement on each data-flow path. 
It provides three parameters to the method `getAllocationSite`:
- Method: The current method
- Statement: The current statement that may contain an allocation site
- Val: The current propagated data-flow fact

These parameters necessitate two checks that should be part of each allocation site implementation:
1) Check whether the statement is an assignment
2) Check whether the left operand of the assignment is equal to the propagated data-flow fact

The first point is relevant because an allocation site is defined as an assignment.
The second aspect is relevant to avoid returning statements that are not relevant to the points-to analysis.
Boomerang propagates only data-flow facts that are relevant to or alias with the query variable.
Therefore, one can exclude irrelevant assignments with the second check.

To this end, a self-defined allocation site should have at least the following code:

```java
public class ExtendedAllocationSite implements IAllocationSite {

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
        // Check for assignments        
        if (!statement.isAssignStmt()) {
            return Optional.empty();
        }
        
        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();
        // Check for correct data-flow fact
        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }
        
        // rightOp is a potential allocation site
        ...
    }
}
```




Last, to use our self-defined allocation site, we need to add it to the options:

```java
BoomerangOptions options = 
                    BoomerangOptions.builder()
                            .withAllocationSite(new ExtendedAllocationSite())
                            ...
                            .build();
```

## Simple Allocation Site

To show how an implementation of the `IAllocationSite` interface may look like, we consider the following simple example:

Assume our program requires *constants* and *new expressions* as allocation sites. 
Then, the interface implementation may look like this:

```java
public class SimpleAllocationSite implements IAllocationSite {

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
        // Check for assignments        
        if (!statement.isAssignStmt()) {
            return Optional.empty();
        }

        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();
        // Check for correct data-flow fact
        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }

        // Constant allocation sites: var = <constant>
        if (rightOp.isConstant()) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);
            return Optional.of(allocVal);
        }

        // New expressions: var = new java.lang.Object
        if (rightOp.isNewExpr()) {
            AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);
            return Optional.of(allocVal);
        }
        
        return Optional.empty();
    }
}
```

## Allocation Site with DataFlowScope

In many cases, we are interested in finding an allocation site to analyze it.
However, a common scenario where Boomerang cannot find an allocation site occurs when a data-flow path ends because we have a function call that is not part of the application.
For example, using the `SimpleAllocationSite` from the previous section, Boomerang would not find an allocation site in the following program:

```java
String s = System.getProperty("property");  // Most precise allocation site
...
queryFor(s);                                
```

Boomerang does not compute an allocation site because `System.getProperty("property")` is not a *constant* or a *new expression*.
Additionally, we may be interested in analyzing only our own application, that is, we do not load the JDK class `java.lang.System` and exclude it in the `DataFlowScope`.
In this case, Boomerang returns an empty results set because the data-flow path ends at the call `System.getProperty("property")`.

To cover these scenarios, we can include the `DataFlowScope` in the allocation site implementation.
For example, we can extend the [DefaultAllocationSite](https://github.com/secure-software-engineering/Boomerang/blob/develop/boomerangPDS/src/main/java/boomerang/options/DefaultAllocationSite.java) as follows:

```java
public class ExtendedDataFlowScope extends DefaultAllocationSite {
    
    private final DataFlowScope dataFlowScope;
    
    public ExtendedDataFlowScope(DataFlowScope dataFlowScope) {
        this.dataFlowScope = dataFlowScope;
    }

    @Override
    public Optional<AllocVal> getAllocationSite(Method method, Statement statement, Val fact) {
        // Check for assignments        
        if (!statement.isAssignStmt()) {
            return Optional.empty();
        }

        Val leftOp = statement.getLeftOp();
        Val rightOp = statement.getRightOp();
        // Check for correct data-flow fact
        if (!leftOp.equals(fact)) {
            return Optional.empty();
        }
        
        // Check for function calls that would end the data-flow path
        // If the function call is not excluded, Boomerang can continue with the analysis
        if (statement.containsInvokeExpr()) {
            InvokeExpr invokeExpr = statement.getInvokeExpr();
            DeclaredMethod declaredMethod = invokeExpr.getDeclaredMethod();
            
            if (dataFlowScope.isExcluded(declaredMethod)) {
                // rightOp is the invoke expression
                AllocVal allocVal = new AllocVal(leftOp, statement, rightOp);
                return Optional.of(allocVal);
            }
        }
        
        // If the statement does not contain a function call, we continue with the default behavior
        return super.getAllocationSite(method, statement, fact);
    }
}
```

With this implementation, we cover function calls that would end the analysis, and we can conclude that the allocation site cannot be computed precisely.
For example, having `System.getProperty("property")` as allocation site indicates that the query variable points to some object that depends on some system variables at runtime.
