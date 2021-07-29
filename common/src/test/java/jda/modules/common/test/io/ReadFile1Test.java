package jda.modules.common.test.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class ReadFile1Test {
  @Test
  public void main() {
    Class c = ReadFile1Test.class;
    System.out.printf("Class: %s%n", c.getName());

    File path = ToolkitIO.getPackagePath(c);
    System.out.printf("Path: %s%n", path);
    String clsFileName = c.getSimpleName() + ".class";
    String clsFilePath = path + File.separator + clsFileName;
    System.out.printf("Class file: %s%n", clsFilePath);
    String absClsFilePath = ToolkitIO.getPath(c, clsFileName);
    System.out.printf("Absolute class file: %s%n", absClsFilePath);
    
    try {
      InputStream ins = ToolkitIO.getFileInputStream(absClsFilePath);
      System.out.printf("Cls file input stream: %s%n", ins);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }

    // 
    String testFile = ToolkitIO.getPathExt(c, "test.txt");
    System.out.printf("Test file: %s%n", testFile);
    testFile = ToolkitIO.getPathExt(c, "../test1.txt");
    System.out.printf("Test file: %s%n", testFile);
    testFile = ToolkitIO.getPathExt(c, "../../test2.txt");
    System.out.printf("Test file: %s%n", testFile);

  }
}
