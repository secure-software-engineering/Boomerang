# Installation and Setup

## Maven Dependency

Boomerang, IDEal and the scopes are released on Maven Central.
Depending on whether you want to use Boomerang or IDEal, include the following dependencies in your project (replace `x.y.z` with the latest version):

- Boomerang can be included with the following dependency:

```bash
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>boomerangPDS</artifactId>
  <version>x.y.z</version>
</dependency>
```

- IDEal can be included with the following dependency:

```bash
<dependency>
  <groupId>de.fraunhofer.iem</groupId>
  <artifactId>idealPDS</artifactId>
  <version>x.y.z</version>
</dependency>
```

Additionally, Boomerang and IDEal require a concrete scope implementation to run with a static analysis framework.
Depending on the desired framework, include the following dependencies (see the [FrameworkScopes](../general/framework_scopes.md) for more information):

=== "Soot"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-Soot</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

=== "SootUp"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-SootUp</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

=== "Opal"
    ```
    <dependency>
      <groupId>de.fraunhofer.iem</groupId>
      <artifactId>boomerangScope-Opal</artifactId>
      <version>x.y.z</version>
    </dependency>
    ```

## Building the Project locally

Boomerang uses Maven as build tool.
You can compile and install this project on your machine via the command

```bash
mvn clean install -DskipTests
```
