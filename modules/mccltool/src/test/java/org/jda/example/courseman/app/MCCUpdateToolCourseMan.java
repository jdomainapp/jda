package org.jda.example.courseman.app;

import java.nio.file.Path;
import java.util.Arrays;

import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.mccl.conceptualmodel.MCCModel;
import jda.modules.mccltool.MCCUpdateTool;

/**
 * @overview 
 *  Implements the tool interface for clients to use.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 1.0
 */
public class MCCUpdateToolCourseMan {

  /**
   * @requires 
   *  -DrootSrcPath neq null /\ args.length = 3 /\
   *  args[0] = command: add, update /\ 
   *  args[1] = fully qualified name of a domain class (e.g. Student) /\
   *  args[2] = update-spec, which depends on command: 
   *      (1) add: comma-separated list of new field names (e.g. "test1,test2")
   *      (2) update: comma-sparated list of (new-field-name,old-field-name) pairs (e.g. "(testA,test1),(testB,test2)")
   *      (3) delete: comma-separated list of deleted field names (e.g. "test1,test2") 
   *  
   * @effects 
   *  execute {@link MCCUpdateToolCourseMan} on a test {@link MCCModel}.
   */
  public static void main(String[] args) throws Exception {
    String rootSrcPath = System.getProperty("rootSrcPath");
    
    if (rootSrcPath == null || args == null || args.length < 3) {
      System.out.println("Usage: " + MCCUpdateToolCourseMan.class + 
          " -DrootSrcPath=<path-to-root-source-dir> <command> <domain-class-FQN> <update-spec>" +
          "\nWhere: \n"
          + "  command: add, update \n"
          + "  domain-class-FQN: the FQN of domain class \n"
          + "  update-spec depends on command: \n"
          + "    (1) add: comma-separated list of new field names (e.g. \"test1,test2\")\n"
          + "    (2) update: comma-sparated list of (new-field-name,old-field-name) pairs (e.g. \"(testA,test1),(testB,test2)\")\n" 
          + "    (3) delete: comma-separated list of new field names (e.g. \"test1,test2\")");
      System.exit(0);
    }

    String cmd = args[0];
    String clsFQN = args[1];
    String updateSpec = args[2];
    
//    MCCUpdateTool tool = new MCCUpdateTool(rootSrcPath, cmd, clsFQN, updateSpec);
    
    // for KSE 2017 test
    //tool.createKSE2017TestMCCModel();
    MCCModel mccModel = createKSE2017TestMCCModel(rootSrcPath); 

    MCCUpdateTool tool = MCCUpdateTool.getInstance(mccModel, rootSrcPath, cmd, clsFQN, updateSpec);
    
    tool.exec();
  }
  
  /**
   * @effects 
   *  create a test {@link MCCModel} for KSE 2017 paper.
   */
  private static MCCModel createKSE2017TestMCCModel(String rootSrcPath) {
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
     
     System.out.println("Creating KSE-2017 MCCModel...");
     System.out.println("--- MCCs ---");
     print2DimArray(mccMap);
     
     Path path = ToolkitIO.getPath(rootSrcPath);
     
     SourceModel srcModel = new SourceModel(rootSrcPath);
     
     MCCModel mccModel = MCCModel.load(srcModel, path, mccMap);    
     
     return mccModel;
  }
  
  /**
   * @effects 
   * 
   */
  private static void print2DimArray(String[][] mccMap) {
    for (String[] e : mccMap) {
      System.out.printf("%s%n", Arrays.toString(e));
    }
    System.out.println();
  }
}
