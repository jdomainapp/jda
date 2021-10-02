package jda.modules.mbsl.test.graph;

import org.junit.Test;

import jda.modules.mbsl.model.ActivityModel;
import jda.modules.mbsl.model.graph.ActivityGraph;
import jda.modules.mbsl.test.model.ActivityModelTest;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class AGraphGenTest {
  @Test
  public void testName() throws Exception {
    ActivityModel m = (new ActivityModelTest()).createActivityModel();
    
    System.out.println(m);
    
    ActivityGraph g = m.getGraph();
    
    System.out.println(g);
    
    // print ModuleOpts of the nodes
  }
}
