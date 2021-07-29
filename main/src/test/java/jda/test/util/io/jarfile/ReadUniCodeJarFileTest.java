package jda.test.util.io.jarfile;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;


/**
 * @overview
 *
 * @author dmle
 *
 * @version 
 */
public class ReadUniCodeJarFileTest {
  
  public static void main(String[] args) {
    if (args == null || args.length < 1) {
      System.out.println("Missing jar-file path!");
      System.exit(0);
    }
    
    String charSetName = "UTF-8";
    String jarFilePath = args[0];
    
    try {
      System.out.println("Reading jar file entry: " + jarFilePath);
      
      Collection<String> content = ToolkitIO.readJarTextFileEntry(jarFilePath, charSetName);

      System.out.println("File content:\n");
      StringBuffer contentSb = new StringBuffer();
      int numLines = content.size();
      int lineNo = 0;
      for (String line : content) {
        System.out.println(line);
        
        contentSb.append(line);
        
        if (lineNo < numLines-1)
          contentSb.append("\n");
        
        lineNo++;
      }
      
      String outputFileName = "outputFile.txt";
      File outputFile = new File(outputFileName);
      ToolkitIO.writeUTF8TextFile(outputFile, contentSb.toString(), true);
      
      System.out.println("\nWritten to: " + outputFile.getAbsolutePath() + "\n");
    } catch (IllegalArgumentException | IOException | NotPossibleException e) {
      e.printStackTrace();
    }
    
  }
}
