package org.jda.example.restfstool.rfsgen;

import jda.modules.restfstool.RFSSoftware;

/**
 * @overview The software generator for a domain software.
 * 
 * @usage
 *  <pre> {@link DomainRFSGen}.main(sccFQN)</pre>
 *  
 * @author ducmle
 */
public class DomainRFSGen {
    
    public static void main(String[] args) throws ClassNotFoundException {
      if (args == null || args.length < 1) {
        System.out.println("Usage: " + DomainRFSGen.class.getSimpleName() + " <FQN-of-SCC>");
        System.exit(1);
      }
      String sccName = args[0];
      
      Class scc = Class.forName(sccName);

      new RFSSoftware(scc)
        .init()
        .generate()
        .run();
    }
}
