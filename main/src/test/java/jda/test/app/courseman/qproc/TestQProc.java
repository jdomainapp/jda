package jda.test.app.courseman.qproc;

import java.util.Collection;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.oql.def.ObjectExpression;
import jda.modules.oql.def.Query;
import jda.mosa.model.Oid;
import jda.test.app.courseman.extended.CourseManExtendedTester;

public class TestQProc extends CourseManExtendedTester {
  //
  public void executeQuery(Query query) throws DataSourceException {
    System.out.printf("executeQuery(%s)%n", query);
    
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class queryCls = ((ObjectExpression) query.getTerm(0)).getDomainClass();
    
    //DBToolKit dbt = schema.getDBManager();
    
    // execute query to get result
    Collection<Oid> oids =  dom.retrieveObjectOids(queryCls, query); //dbt.readObjectIds(queryCls, null, query); 

    if (oids == null) {
      System.out.println("No objects satisfy query");
    } else {
      System.out.println("Matching Oids:");
      for (Oid oid : oids) {
        System.out.printf("  %s%n", oid);
      }
    }  
  }
}
