package jda.modules.swtool.test;

import org.junit.Before;
import org.junit.Test;

import jda.modules.sccl.conceptualmodel.SCC;
import jda.modules.swtool.SwGenByCount;

/**
 * @overview
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version
 */
public class SwGenByCountCourseMan {
  private SwGenByCount swgen;
  private SCC scc;
  private String swcFqn;
  
  @Before
  public void initSwGenInstance() {
    int count = 2;
    
    swgen = new SwGenByCount("CourseMan",
        // rootsrcpath
        "/data/projects/jda/modules/swtool/src/test/java",
        // output path
        "/data/projects/jda/modules/swtool/target/test-classes",        
        // seed domain model package
        "org.jda.example.courseman.model",
        // target domain model package
        "org.jda.example.courseman.model" + count,
        // modules package
        "org.jda.example.courseman.modules",
        // software package
        "org.jda.example.courseman.software",
        // how many model copies
        count);
  }
  
  @Test
  public void testGenPhase1() {
    // generate everything up to the SCC
    scc = swgen.genDomainModel()
         .genMCCs()
         .genMainMCC()
         .genSCC()
         .getSCC();
    
  }
  
  public void testGenPhase2() {
    // generate 2nd
    swcFqn = swgen
         .genSwClass(scc)
         .getSwcFqn();
    
  }
  
  public void testExec() {
    // execute
    swgen.executeSw(swcFqn);
  }
}
