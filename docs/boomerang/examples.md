# Examples

## Taint Analysis
A **Taint Analysis** is a common use case for Boomerang. Our goal is to decide whether a variable points to a specific object (*source*) (e.g. a password) that is unintentionally used as parameter in a method call (*sink*) (e.g. a print statement).

Assume we have the following program:

```java
A a1 = new A();       // Object o
A a2 = a1;            // Create an alias, i.e. a1 and a2 point to o

Object s = source();  // Read some tainted value
a1.f = s;             // Store tainted value in field of o

Object z = a2.f;      // Read the field from o
sink(z);              // Is the tainted value used in the sink?
```

In this program, the variable `s` points to some tainted value that should not be used in a sink. Although `s` aliases with the field `f` of `a1` and we read the field `f` of `a2`, the tainted value `s` is still used in the sink because `a1` and `a2` alias.
