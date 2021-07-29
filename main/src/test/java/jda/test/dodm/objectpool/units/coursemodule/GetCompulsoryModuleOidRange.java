package jda.test.dodm.objectpool.units.coursemodule;

import org.junit.Test;

import jda.modules.common.types.Tuple2;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMEnhancedTester;
import jda.test.model.basic.CompulsoryModule;

public class GetCompulsoryModuleOidRange extends DODMEnhancedTester {  
  @Test
  public void doTest() throws Exception { 
    DODMEnhancedTester me = (DODMEnhancedTester)instance;
    Class cls = CompulsoryModule.class;
    Tuple2<Oid,Oid> idRange = me.getOidRange(cls);
    Oid min = idRange.getFirst();
    Oid max = idRange.getSecond();
    
    System.out.printf("ID range of %s: [%s,%s]%n", cls, min, max);
  }
}
