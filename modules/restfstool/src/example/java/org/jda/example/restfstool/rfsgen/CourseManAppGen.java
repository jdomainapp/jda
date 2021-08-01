package org.jda.example.restfstool.rfsgen;

import jda.modules.restfstool.RFSGen;
import static org.jda.example.restfstool.rfsgen.Resources.*;

/**
 * The software generator for CourseManApp.
 * @author binh_dh
 */
public class CourseManAppGen {
    
    public static void main(String[] args) {
      
      new RFSGen().run(
          // front-end config + some shared configs
          frontendOutputPath, model, auxModel, scc, mccMain, modules, 
          // back-end config
          backendTargetPackage, backendOutputPath, runCallBack);
    }
}
