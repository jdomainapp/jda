package jda.modules.restfstool.test.gensw;

import javax.json.JsonObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.swtool.RFSSwGenByCount;

/**
 * @overview
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class RFSSwGenByCountCourseMan {
  /** number of domain model copies */
  private int count;
  private RFSSwGenByCount swgen;
  private JsonObject rfsgenConfig;
  private SCC scc;
  private String swcFqn;
  
  private static Logger logger = LoggerFactory.getLogger("RESTFSTool");
  
  public static void main(String[] args) {
    int count = 2;
    
    // generate count number of SCCs
    // SCC1, ..., SCCn
    for (int i = 1; i <= count; i++) {
      RFSSwGenByCountCourseMan rfsGenTest = new RFSSwGenByCountCourseMan(i);
      rfsGenTest.init();
      rfsGenTest.testGenPhase1();
      
      // IMPORTANT: ONLY EXECUTE THIS PHASE
      // AFTER REFRESHING THE TARGET PROJECT IN THE IDE
      rfsGenTest.testGenPhase2();
    }
  }
  
  /**
   * @effects 
   *  initialises this with the number of domain model copies <code>count</code>
   */
  public RFSSwGenByCountCourseMan(int count) {
    this.count = count;
  }

  
  public void init() throws NotPossibleException {
    rfsgenConfig = ToolkitIO.readJSonObjectFile(RFSSwGenByCountCourseMan.class, "rfsgenconfig.json");
    
    swgen = new RFSSwGenByCount("CourseMan",
        // rootsrcpath
        "/data/projects/jda/modules/restfstool/src/example/java",
        // seed domain model package
        "jda.modules.restfstool.test.performance.model",
        // software package
        "jda.modules.restfstool.test.performance.software",
        // how many model copies
        count, 
        rfsgenConfig);
  }
  
  public void testGenPhase1() {
    // generate everything up to the SCC
    logger.info("Phase 1: generating the domain model...");
    swgen.genDomainModel();
  }
  
  /**
   * @requires
   *  {@link #testGenPhase1()} has been executed AND 
   *  generated domain model has been compiled into the class path 
   *  (e.g. by refreshing the target package in the IDE)  
   */
  public void testGenPhase2() {
    // generate everything up to the SCC
    logger.info("Phase 2: generating MCCs and SCC...");
    scc = swgen.genMCCs()
         .genMainMCC()
         .genSCC()
         .getSCC();
  }
}
