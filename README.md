<p align="center">
<img src="https://github.com/secure-software-engineering/Boomerang/blob/master/docs/img/BoomerangLogo.png">
</p> 

## Boomerang
Boomerang is an efficient and precise pointer and dataflow analysis framework based on a Synchronized Pushdown Systems (SPDS). SPDS relies on two pushdown systems that model field-sensitivity and context-sensitivity separately. Combining (synchronizing) both systems enables a field-sensitive and context-sensitive analysis that is also flow-sensitive. Detailed information can be found [here](https://digital.ub.uni-paderborn.de/hs/content/titleinfo/3030984).

This repository contains:
- a Java implementation of [Synchronized Pushdown Systems](https://digital.ub.uni-paderborn.de/hs/content/titleinfo/3030984).
- [Boomerang](boomerangPDS) to calculate on-demand points-to and dataflow information using a Synchronized Pushdown System.
- [IDEal](idealPDS), an IDE solver based on a [Weighted Pushdown System](https://www.bodden.de/pubs/sab19context.pdf) that uses Boomerang to compute alias information only when required (i.e. on-demand).
- Implementation of scopes that allows you to run Boomerang and IDEal with the static analysis frameworks Soot and Opal.

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

## Build and Installation
If you plan to install Boomerang and IDEal locally, you can use the following commands to build the project:

- `mvn clean install -DskipTests` Install the projects and skip all tests
- `mvn clean install -DtestSetup=Soot` Install the projects and run the tests with Soot as the underlying framework
- `mvn clean install -DtestSetup=SootUp` Install the projects and run the tests with SootUp as the underlying framework
- `mvn clean install -DtestSetup=Opal` Install the projects and run the tests with Opal as the underlying framework

## Contributing

We hare happy for every contribution from the community! You can simply create a fork and open a pull request. Note that we use the Google style sheet to keep the code clean. To format the code, run the command 

```xml
mvn spotless:apply
```

before commiting your changes.
