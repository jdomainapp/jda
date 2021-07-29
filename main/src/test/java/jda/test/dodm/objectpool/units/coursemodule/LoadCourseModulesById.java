package jda.test.dodm.objectpool.units.coursemodule;

import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.Module;

public class LoadCourseModulesById extends DODMEnhancedTester {
  
  @Test
  public void doTest() throws Exception {
    method(this.getClass().getSimpleName()+".doTest()");
    Class c = Module.class;

    Map<Oid,Object> objs = loadObjectsWithOid(c);
    
    if (objs != null) {
      System.out.printf("%s objects:%n", c.getSimpleName());
      for (Entry<Oid,Object> e : objs.entrySet()) {
        System.out.printf("  %s -> %s %n", e.getKey(), e.getValue());
      }
    } else {
      System.out.println("No objects");
    }
  }
}
