# Boomerang Setup

Boomerang's purpose is the computation of points-to information for a variable on-demand. Starting at a specific statement, it traverses the program and its data-flow paths backwards until it finds an allocation site for the desired variable. While doing that, it computes relevant alias information.

In the following sections, we give an overview of relevant constructs and API calls. We highly recommend to take a look at the [Examples](./../boomerang/examples.md) to see the best way to combine these constructs.

## Backward Queries

Boomerang uses *backward queries* to compute relevant points-to information. A **BackwardQuery** consists of a statement `s` and a variable `v`. `s` is the starting statement where the backwards analysis starts and `v` is the data-flow fact to solve for.

Backward queries can be easily constructed. However, due to Boomerang's scope implementation, we need to specify the corresponding control-flow graph edge with the starting statement `s` as target (see the [Boomerang Scopes](./../general/boomerang_scopes.md)). With that, we can construct a backward query as follows:

```java
public void createBackwardQuery(ControlFlowGraph.Edge, edge, Val fact) {
    BackwardQuery query = BackwardQuery.make(edge, fact);
}
```

## Running Boomerang

Boomerang requires a [FrameworkScope](./../general/framework_scopes.md) and a set [options](./../boomerang/options.md). With that, we can solve a backward query as follows:

```java
public void solveQuery(
        BackwardQuery query,
        FrameworkScope scope,
        BoomerangOptions options) {
    Boomerang solver = new Boomerang(scope, options);
    BackwardBoomerangResults<NoWeight> results = solver.solve(query);
}
```

The call to `solve` solves the query and returns a wrapper for the results.

// TODO Use a box

!!! Important: A `Boomerang` instance can be used to solve exactly one query. If you want to solve multiple queries with the same instance, you have to set [allowMultipleQueries]() in the options to `true` and you have to call `unregisterAllListeners()` after each call to `solve`. This may look like this:

```java
public void solveQueries(
        Collection<BackwardQuery> queries,
        FrameworkScope scope,
        BoomerangOptions options) {
    Boomerang solver = new Boomerang(scope, options);
    
    for (BackwardQuery query : queries) {
        BackwardBoomerangResults<NoWeight> results = solver.solve(query);
        // <Process or store the results>
        solver.unregisterAllListeners();
    }
}
```

## Extracting Allocation Sites

After running Boomerang, we can use the results to compute the allocation sites, i.e. the objects the query variable points to. An allocation site `AllocVal` is wrapped into a `ForwardQuery` object. Note that the computed allocation sites heavily depend on the used [AllocationSite](./../boomerang/allocation_sites.md) definition. We can extract the corresponding `AllocVal` objects as follows:

```java
public void extractAllocationSites(BackwardBoomerangResults<NoWeight> results) {
    // Compute the allocation sites
    Collection<ForwardQuery> allocationSites = results.getAllocationSites().keySet();
        
    for (ForwardQuery query : allocationSites) {
        // This is a single allocation site
        AllocVal allocVal = query.getAllocVal();
        System.out.println(
                "Query variable points to "
                + allocVal.getAllocVal()
                + " @ statement"
                + allocVal.getAllocStatement()
                + " @ line "
                + allocVal.getAllocStatement().getLineNumber()
                + " in method "
                + allocVal.getAllocStatement().getMethod());   
    }
}
```

## Extracting Aliases

Beside the allocation sites, we can use the results to compute the aliases for the query variable. An alias is represented by an `AccessPath` that holds the base variable and the field chain. For example, an alias `x.f.g` is represented by an `AccessPath` with the base `x` and the field chain `[f, g]. We can compute the access paths as follows:

```java
public void extractAliases(BackwardBoomerangResults<NoWeight> results) {
    Collection<AccessPath> aliases = results.getAllAliases();
    
    System.out.println("Found the following aliases:")
    for (AccessPath alias : aliases) {
        // 'toCompactString()' transforms the access path into a basic String, e.g. x.f.g
        System.out.println(alias.toCompactString());
    }
    
}
```

// TODO Aliases at specific statement
