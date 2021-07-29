/**
 * @overview
 *
 * @author dmle
 */
package jda.test.util.io;

import java.io.File;

import jda.modules.common.io.ToolkitIO;

/**
 * @overview
 *  Get the content of a File object as byte[] array.
 *  
 * @author dmle
 *
 */
public class GetFileAsBytesTest {
  
  public static void main(String[] args) throws Exception {
    String fileName = "test2.pdf";//"test1.txt"; "testErr1.txt"; 
    Class c = GetFileAsBytesTest.class;
    String path = ToolkitIO.getPath(c, fileName);
    if (path != null) {
      File file = new File(path);
      
      byte[] data = ToolkitIO.getFileAsBytes(file);
      
      System.out.printf("File: %s%n  content length (bytes): %d%n", file, data.length);
    } else {
      System.err.printf("File (%s) not found relative to class: %s%n", fileName, c); 
    }
  }
}
