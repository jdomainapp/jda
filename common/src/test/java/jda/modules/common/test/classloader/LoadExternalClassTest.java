package jda.modules.common.test.classloader;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.javac.JavaC;

import java.io.File;
import java.util.Map;

/**
 * @version 1.0
 * @overview
 */
public class LoadExternalClassTest {
  public static void main(String[] args) {
    String folderPath =
        //"/data/projects/jda/examples/temp/target/classes";
        "/tmp/jda-eg-temp-5.4-SNAPSHOT";
    File folder = new File(folderPath);
    assert folder.exists();

    try {
      System.out.printf("Loading classes from folder: %s%n", folder);

      Map<String, Class<?>> loadedClasses = JavaC.loadClasses(folder);

      loadedClasses.entrySet().forEach(e -> {
        System.out.printf("... loaded: %s -> %s%n", e.getKey(), e.getValue());
      });
    } catch(NotFoundException e) {
      e.printStackTrace();
    }
  }
}
