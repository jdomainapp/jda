package jda.modules.mccltool;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jda.modules.common.ModuleToolable;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.mccl.conceptualmodel.MCCModel;

/**
 * @overview 
 *  Implements the tool interface for clients to use.
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 1.0
 */
public class MCCUpdateTool implements ModuleToolable {

  private String rootSrcPath;
  private String command;
  private String clsFQN;
  private String updateSpec;
  private MCCModel mccModel;

  private static MCCUpdateTool instance;
  
  public static MCCUpdateTool getInstance(MCCModel mccModel, 
      String rootSrcPath, /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      String command,
      String clsFQN,
      String updateSpec) {
    if (instance == null) {
      instance = new MCCUpdateTool(mccModel, rootSrcPath, command, clsFQN, updateSpec);
    } else { // update
      instance.mccModel = mccModel;
      instance.rootSrcPath = rootSrcPath;
      instance.command = command;
      instance.clsFQN = clsFQN;
      instance.updateSpec = updateSpec;
    }
    
    return instance;
  }
  
  private MCCUpdateTool(
      MCCModel mccModel, 
      String rootSrcPath, /**e.g. /home/dmle/projects/domainapp/modules/mccl/src/example/java */
      String command,
      String clsFQN,
      String updateSpec
      ) {
    this.mccModel = mccModel;
    this.rootSrcPath = rootSrcPath;
    this.command = command;
    this.clsFQN = clsFQN;
    this.updateSpec = updateSpec;
  }
  
  /* (non-Javadoc)
   * @see domainapp.module.ModuleToolable#exec()
   */
  /**
   * @requires 
   *  {@link #mccModel} has been initialised with the necessary MCCs./\ 
   *  update-spec depends on command: 
   *      (1) add: comma-separated list of new field names (e.g. "test1,test2")
   *      (2) update: comma-sparated list of (new-field-name,old-field-name) pairs (e.g. "(testA,test1),(testB,test2)")
   *      (3) delete: comma-separated list of deleted field names (e.g. "test1,test2")     
   * 
   * @effects 
   *  executes this
   */
  @Override
  public Object exec(Object...args) throws NotPossibleException {
    // output dir of the generated source files
    if (command.equals("add")) {
      String[] fields = updateSpec.split(",");
      Collection<String> newFields = Arrays.asList(fields);
      
      System.out.println("\nRunning OnAddDomainFields...");
      System.out.println("   Domain class: " + clsFQN);
      System.out.println("   New fields: " + newFields);
      
      mccModel.onAddDomainFields(clsFQN, newFields);
      
      System.out.println("...ok");
    } else if (command.equals("update")) {
      Map<String,String> updatedFields = new HashMap<>();
      String[] fieldPairs = updateSpec.split(";");

      for (String fieldPair : fieldPairs) {
        String[] els = fieldPair.split(",");
        if (els.length != 2)
          throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"field name pair = '" + fieldPair + "'"});
        
        String newField = els[0].substring(1);  // remove the leading '('
        String oldField = els[1].substring(0, els[1].length()-1); // remove the trailing ')'
        updatedFields.put(newField, oldField);
      }
      
      System.out.println("\nRunning OnUpdateDomainFields...");
      System.out.println("   Domain class: " + clsFQN);
      System.out.println("   Updated fields (new-name -> old-name): " + updatedFields);
      
      mccModel.onUpdateDomainFields(clsFQN, updatedFields);
      
      System.out.println("...ok");
    } else if (command.equals("delete")) {
      String[] fields = updateSpec.split(",");
      Collection<String> delFields = Arrays.asList(fields);
      
      System.out.println("\nRunning OnDeleteDomainFields...");
      System.out.println("   Domain class: " + clsFQN);
      System.out.println("   Deleted fields: " + delFields);
      
      mccModel.onDeleteDomainFields(clsFQN, delFields);
      
      System.out.println("...ok");
    } else {
      // invalid command
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, new Object[] {"command = '" + command + "'"});
    }
    
    return true;
  }
//  /**
//   * @requires 
//   *  -DrootSrcPath neq null /\ args.length = 3 /\
//   *  args[0] = command: add, update /\ 
//   *  args[1] = fully qualified name of a domain class (e.g. Student) /\
//   *  args[2] = update-spec, which depends on command: 
//   *      (1) add: comma-separated list of new field names (e.g. "test1,test2")
//   *      (2) update: comma-sparated list of (new-field-name,old-field-name) pairs (e.g. "(testA,test1),(testB,test2)")
//   *      (3) delete: comma-separated list of deleted field names (e.g. "test1,test2") 
//   *  
//   * @effects 
//   *  execute {@link MCCUpdateTool} on a test {@link MCCModel}.
//   */
//  public static void main(String[] args) throws Exception {
//    String rootSrcPath = System.getProperty("rootSrcPath");
//    
//    if (rootSrcPath == null || args == null || args.length < 3) {
//      System.out.println("Usage: " + MCCUpdateTool.class + 
//          " -DrootSrcPath=<path-to-root-source-dir> <command> <domain-class-FQN> <update-spec>" +
//          "\nWhere: \n"
//          + "  command: add, update \n"
//          + "  domain-class-FQN: the FQN of domain class \n"
//          + "  update-spec depends on command: \n"
//          + "    (1) add: comma-separated list of new field names (e.g. \"test1,test2\")\n"
//          + "    (2) update: comma-sparated list of (new-field-name,old-field-name) pairs (e.g. \"(testA,test1),(testB,test2)\")\n" 
//          + "    (3) delete: comma-separated list of new field names (e.g. \"test1,test2\")");
//      System.exit(0);
//    }
//
//    String cmd = args[0];
//    String clsFQN = args[1];
//    String updateSpec = args[2];
//    
////    MCCUpdateTool tool = new MCCUpdateTool(rootSrcPath, cmd, clsFQN, updateSpec);
//    
//    // for KSE 2017 test
//    //tool.createKSE2017TestMCCModel();
//    MCCModel mccModel = (new KSE2017MCCModel()).createKSE2017TestMCCModel(rootSrcPath); 
//
//    MCCUpdateTool tool = MCCUpdateTool.getInstance(mccModel, rootSrcPath, cmd, clsFQN, updateSpec);
//    
//    tool.exec();
//  }
}
