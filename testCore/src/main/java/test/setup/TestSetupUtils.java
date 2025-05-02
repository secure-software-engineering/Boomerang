/**
 * ***************************************************************************** 
 * Copyright (c) 2018 Fraunhofer IEM, Paderborn, Germany
 * <p>
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * <p>
 * SPDX-License-Identifier: EPL-2.0
 * <p>
 * Contributors:
 *   Johannes Spaeth - initial API and implementation
 * *****************************************************************************
 */
package test.setup;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Collection;
import java.util.stream.Stream;
import org.apache.commons.io.file.SimplePathVisitor;

public class TestSetupUtils {

  public static final String APP_CLASSES = "app_classes";
  public static final String JDK_CLASSES = "jdk_classes";

  private TestSetupUtils() {}

  public static Path loadTestClasses(
      String classpath, String testClassName, Collection<String> excludeList) {
    Path path = Path.of(classpath);

    try (Stream<Path> stream = Files.walk(path)) {
      Path outputDir = Paths.get(APP_CLASSES);
      Files.createDirectories(outputDir);

      Stream<File> classPathFiles = stream.filter(Files::isRegularFile).map(Path::toFile);

      // Filter for excluded classes. Additionally, exclude all classes from different packages
      // because CHA would add edges to methods from those test classes
      String packageName = testClassName.substring(0, testClassName.lastIndexOf("."));
      Stream<File> testClassFiles =
          classPathFiles.filter(c -> !isExcludedFile(c, classpath, packageName, excludeList));

      testClassFiles.forEach(
          f -> {
            Path relativePath = path.relativize(f.toPath());
            Path targetPath = outputDir.resolve(relativePath.toString());

            try {
              Files.createDirectories(targetPath.getParent());
              Files.copy(f.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            } catch (IOException e) {
              throw new RuntimeException(
                  "Could not copy file to target directory: " + e.getMessage());
            }
          });

      return outputDir;
    } catch (IOException e) {
      throw new RuntimeException("Could not read classpath: " + e.getMessage());
    }
  }

  private static boolean isExcludedFile(
      File file, String classpath, String packageName, Collection<String> excludeList) {
    // Remove the classpath and the .class ending
    String path = file.getPath().replace("/", ".").replace("\\", ".");
    String formattedClassPath = classpath.replace("/", ".").replace("\\", ".");
    String formattedPath = path.replace(formattedClassPath, "").replace(".class", "").substring(1);

    return !formattedPath.startsWith(packageName) || excludeList.contains(formattedPath);
  }

  /**
   * Load the JDK classes from the include list and store them in the directory {@link
   * #JDK_CLASSES}.
   *
   * @param includeList the classes to load from the JDK
   * @return the path to directory that contains the loaded class files
   */
  public static Path loadJDKFiles(Collection<String> includeList) {
    try (FileSystem fs =
        FileSystems.newFileSystem(URI.create("jrt:/"), java.util.Collections.emptyMap())) {
      Path outputDir = Paths.get(JDK_CLASSES);
      Files.createDirectories(outputDir);

      for (String className : includeList) {
        Path rootPath = fs.getPath("/modules/java.base/");
        String pathInJrt = rootPath + "/" + className.replace('.', '/') + ".class";
        Path jrtPath = fs.getPath(pathInJrt);

        Path relativePath = rootPath.relativize(jrtPath);
        Path targetPath = outputDir.resolve(relativePath.toString());

        try {
          Files.createDirectories(targetPath.getParent());
          Files.copy(jrtPath, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
          throw new RuntimeException("Could not copy file to target directory: " + e.getMessage());
        }
      }

      return outputDir;
    } catch (IOException e) {
      throw new RuntimeException("Could not read classes from JDK: " + e.getMessage());
    }
  }

  public static File[] getFilesInDirectory(Path directory) {
    try (Stream<Path> stream = Files.walk(directory)) {
      return stream.filter(Files::isRegularFile).map(Path::toFile).toArray(File[]::new);
    } catch (IOException e) {
      throw new RuntimeException("Could not walk directory: " + e.getMessage());
    }
  }

  public static void deleteDirectory(String directory) {
    Path dir = Paths.get(directory);

    try {
      Files.walkFileTree(
          dir,
          new SimplePathVisitor() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                throws IOException {
              Files.delete(file);
              return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc)
                throws IOException {
              Files.delete(dir);
              return FileVisitResult.CONTINUE;
            }
          });
    } catch (IOException e) {
      throw new RuntimeException("Could not delete directory: " + e);
    }
  }
}
