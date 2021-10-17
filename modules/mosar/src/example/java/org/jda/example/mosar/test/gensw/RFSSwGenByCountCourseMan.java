package org.jda.example.mosar.test.gensw;

import java.io.File;

import javax.json.Json;
import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.mosartool.testing.RFSSwGenByCount;
import jda.modules.sccl.conceptualmodel.SCC;

/**
 * @overview
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class RFSSwGenByCountCourseMan {
  /** number of domain model copies */
  private int counter;
  private RFSSwGenByCount swgen;
  private JsonObject rfsgenConfig;
  private SCC scc;
  private String swcFqn;
  
  private static Logger logger = LoggerFactory.getLogger(RFSSwGenByCountCourseMan.class);
  
  public static void main(String[] args) throws NotPossibleException {
    // generate count SCCs
    // SCC1, ..., SCCn
    JsonObject rfsGenConfig = ToolkitIO.readJSonObjectFile(
        RFSSwGenByCountCourseMan.class, "rfsgenconfig.json");
    JsonObject countSpec = rfsGenConfig.getJsonObject("countSpec");
    int min = countSpec.getInt("min"),
        max = countSpec.getInt("max"),
        increment = countSpec.getInt("increment");
    
    if (increment < 1)
      throw new NotPossibleException(NotPossibleException.Code.INVALID_ARGUMENT, 
          new Object[] {"increment = "  + increment});
    
    int counter = min;
    while (counter <= max) {
      logger.info("\n==========\nGenerating config for software variant: " + counter + "\n==========\n");
      
      RFSSwGenByCountCourseMan rfsGenTest = 
          new RFSSwGenByCountCourseMan(rfsGenConfig, counter);
      
      rfsGenTest.init();
      // + create separate modules and software packages for each software
      // + auto-compile the generated classes
      rfsGenTest.gen();
      
      if (min == 1) {
        if (counter == min) {
          if (increment == 1) {
            counter++;
          } else {
            counter = increment;
          }
        } else {
          if (increment == 1) {
            counter++;
          } else {
            counter += increment;
          }
        }
      } else {
        counter += increment;
      }
    }
  }
  
  /**
   * @effects 
   *  initialises this with the number of domain model copies <code>count</code>
   */
  public RFSSwGenByCountCourseMan(JsonObject rfsgenConfig, int counter) {
    this.rfsgenConfig = rfsgenConfig;
    this.counter = counter;
  }

  
  public void init() throws NotPossibleException {
//    rfsgenConfig = ToolkitIO.readJSonObjectFile(RFSSwGenByCountCourseMan.class, "rfsgenconfig.json");

    String domain = rfsgenConfig.getString("domain");
    JsonObject swConfigGen = rfsgenConfig.getJsonObject("swConfigGen");
    JsonObject rfsGenDesc = rfsgenConfig.getJsonObject("rfsGenDesc");
    
    // update rfsGenDesc.fe-courseman with counter
    String feProjName = rfsGenDesc.getString("feProjName");
    feProjName += counter;
    rfsGenDesc = ToolkitIO.createNewJsonObject(rfsGenDesc, 
        "feProjName", 
        Json.createValue(feProjName));
    
    // auto create backend package
    String bePackage = // rfsGenDesc.getString("bePackage");
        swConfigGen.getString("targetDomainModelPkgPrefix");
    bePackage += "." + domain.toLowerCase() + counter;
    rfsGenDesc = ToolkitIO.createNewJsonObject(rfsGenDesc, 
        "bePackage", 
        Json.createValue(bePackage));
    
    // append domain + counter to backend target package
    String beTargetPackage = rfsGenDesc.getString("beTargetPackage");
    beTargetPackage += "." + domain.toLowerCase() + counter;
    rfsGenDesc = ToolkitIO.createNewJsonObject(rfsGenDesc, 
        "beTargetPackage", 
        Json.createValue(beTargetPackage));
    
    // append domain + counter to frontend path
    String feOutputPath = rfsGenDesc.getString("feOutputPath");
    feOutputPath += 
        (!feOutputPath.endsWith(File.separator) ? File.separator : "") 
        + domain.toLowerCase() + counter;
    rfsGenDesc = ToolkitIO.createNewJsonObject(rfsGenDesc, 
        "feOutputPath", 
        Json.createValue(feOutputPath));
    
    swgen = new RFSSwGenByCount(domain,
        // root source path
        swConfigGen.getString("rootSrcPath"),
        // outputPath
        swConfigGen.getString("outputPath"),
        // seed domain model package
        swConfigGen.getString("seedDomainModelPkg"),
        // target domain model package
        bePackage,
        // modules package
        swConfigGen.getString("modulesPkgPrefix")+
          "."+domain.toLowerCase()+counter,
        // software package
        swConfigGen.getString("softwarePkgPrefix")+
          "."+domain.toLowerCase()+counter,
        // how many model copies
        counter, 
        rfsGenDesc);
  }
  
  public void gen() {
    logger.info("Generating RFS software configuration ...");
    swgen.genDomainModel()
    .genMCCs()
    .genMainMCC()
    .genSCC()
    .getSCC();
  }
  
//  public void testGenPhase1() {
//    // generate the domain model
//    logger.info("Generating domain model...");
//    swgen.genDomainModel()
//    .genMCCs()
//    .genMainMCC()
//    .genSCC()
//    .getSCC();
//  }
//  
//  /**
//   * @requires
//   *  {@link #testGenPhase1()} has been executed AND 
//   *  generated domain model has been compiled into the class path 
//   *  (e.g. by refreshing the target package in the IDE)  
//   */
//  public void testGenPhase2() {
//    // generate everything up to the SCC
//    logger.info("Phase 2: generating MCCs and SCC...");
//    scc = swgen.genMCCs()
//         .genMainMCC()
//         .genSCC()
//         .getSCC();
//  }
}
