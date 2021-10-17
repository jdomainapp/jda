package jda.modules.mosartool;

import jda.modules.mosar.software.RFSoftware;

/**
 * @overview The software generator for a domain software.
 * 
 * @usage
 *  <pre> {@link RFSGenRunTool}.main(sccFQN)</pre>
 *  
 * @author ducmle
 */
public class RFSGenRunTool {
    
    public static void main(String[] args) throws ClassNotFoundException {
      if (args == null || args.length < 1) {
        System.out.println("Usage: " + RFSGenRunTool.class.getSimpleName() + " <FQN-of-SCC>");
        System.exit(1);
      }
      String sccName = args[0];
      
      Class scc = Class.forName(sccName);

      new RFSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
