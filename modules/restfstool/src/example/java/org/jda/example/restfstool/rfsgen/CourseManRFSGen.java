package org.jda.example.restfstool.rfsgen;

import jda.modules.restfstool.RFSGen;

/**
 * The software generator for CourseManApp.
 * @author binh_dh
 */
public class CourseManRFSGen {
    
    public static void main(String[] args) {
      Class scc = Resources.scc;
//      new RFSGen().run(
//          // front-end config + some shared configs
//          frontendOutputPath, model, auxModel, scc, mccMain, modules, 
//          // back-end config
//          backendTargetPackage, backendOutputPath, runCallBack);
      
      new RFSGen().run(scc);
    }
}
