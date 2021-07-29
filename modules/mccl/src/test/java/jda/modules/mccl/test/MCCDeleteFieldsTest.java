/**
 * 
 */
package jda.modules.mccl.test;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.mccl.conceptualmodel.MCCModel;


/**
 * 
 * @overview
 *  Test delete fields to MCC
 *  
 * @author dmle
 *
 */
public class MCCDeleteFieldsTest {

  /**
   * @requires 
   *  
   * @effects 
   */
  @Test
  public void run() {
    // NOTE: Windows need to change path separator to '\\'
    //Path rootSrcPath = ToolkitIO.getPath("/home","dmle","projects","domainapp","modules","mccl","src/example/java");
    String rootSrcPathStr = System.getProperty("rootSrcPath");
    
    Path rootSrcPath;
    if (rootSrcPathStr == null) {
      rootSrcPath = ToolkitIO.getPath("/home","ducmle","projects","jda","modules","mccl","src", "test", "java");
    } else {
      rootSrcPath = ToolkitIO.getPath(rootSrcPathStr);
    }
    
    // load MCCModel with some MCCs
    String dcls = "org.jda.example.courseman.modulesupdate.student.model.Student";
    
    String[][] mccMap = new String[][] {
        { dcls,
          "org.jda.example.courseman.modulesupdate.student.ModuleStudent"
        },
        { "org.jda.example.courseman.modulesupdate.enrolmentmgmt.model.EnrolmentMgmt",
          "org.jda.example.courseman.modulesupdate.enrolmentmgmt.ModuleEnrolmentMgmt"
        },
    };
   
   System.out.println("Test data:");
   print2DimArray(mccMap);

   System.out.println("Creating MCCModel...");
   
   SourceModel srcModel = new SourceModel(rootSrcPath.toString());

   MCCModel mccModel = MCCModel.load(srcModel, rootSrcPath, mccMap);
    
   // introduce field deletions to MCCModel...
   // ASSUME: this is executed after MCCAddFieldsTest
   Collection<String> delFields = Arrays.asList("test1", "test2");

   System.out.println("Executing onDeleteDomainFields...");
   System.out.println("   deleted fields: " + delFields);
   
   mccModel.onDeleteDomainFields(dcls, delFields);
   
   System.out.println("...ok");
  }

  /**
   * @effects 
   * 
   * @version 
   * @param mccMap 
   * 
   */
  private static void print2DimArray(String[][] mccMap) {
    for (String[] e : mccMap) {
      System.out.printf("%s%n", Arrays.toString(e));
    }
    System.out.println();
  }
}
