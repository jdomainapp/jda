package jda.test.util.io;

import jda.modules.common.io.ToolkitIO;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class SystemDependentPathTest {
  public static void main(String[] args) {
    String path = "C:\\test\\dir1\\dir2"; // "/home/test/dir1/dir2";
    String depPath = ToolkitIO.getSysDependentPath(path);
    
    System.out.printf("path: %s%n  system-dependent path: %s%n", path, depPath);
  }
}
