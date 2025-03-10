<p align="center">
<img src="https://github.com/secure-software-engineering/Boomerang/blob/master/BoomerangLogo.png">
</p> 

## Boomerang
Synchronized Pushdown Systems enable precise data-flow analysis.
SPDS relies on two pushdown systems, one system models field-sensitivity, the other one context-sensitivity. The results of both systems are then synchronized and yield a field- and context-sensitive data-flow analysis.

This repository contains:
- a Java implementation of [Synchronized Pushdown Systems](https://digital.ub.uni-paderborn.de/hs/content/titleinfo/3030984).
- an implementation of [Boomerang](boomerangPDS)
- [IDEal](idealPDS) based on a Weighted Pushdown System.

## Examples

Boomerang code examples can be found [here](https://github.com/CodeShield-Security/SPDS/tree/master/boomerangPDS/src/main/java/boomerang/example). Code examples for IDEal are given [here](https://github.com/CodeShield-Security/SPDS/tree/master/idealPDS/src/main/java/inference/example).


## Maven dependency
The projects are released on [Maven Central](https://central.sonatype.com/artifact/de.fraunhofer.iem/SPDS) and can be included as a dependency in `.pom` files (replace `x.y.z` with the latest version).
- Boomerang can be included with the following dependency:

```.xml
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>boomerangPDS</artifactId>
  <version>x.y.z</version>
</dependency>
```

- IDEal can be included with the following dependency:

```.xml
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>idealPDS</artifactId>
  <version>x.y.z</version>
</dependency>
```

## Notes on the Test Cases

The projects Boomerang and IDEal contain JUnit test suites. As for JUnit, the test methods are annotated with @Test and can be run as normal JUnit tests.
However, these methods are *not* executed but only statically analyzed. When one executes the JUnit tests, the test method bodies are supplied as input to Soot 
and a static analysis is triggered. All this happens in JUnit's @Before test time. The test method itself is never run, may throw NullPointerExceptions or may not even terminate.

If the static analysis succeeded, JUnit will officially label the test method as skipped. However, the test will not be labeled as Error or Failure. 
Even though the test was skipped, it succeeded. Note, JUnit outputs a message:

``org.junit.AssumptionViolatedException: got: <false>, expected: is <true>``

This is ok! The test passed!
