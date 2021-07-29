package jda.test.app.courseman.qproc.units;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.oql.def.ObjectJoinExpression;
import jda.modules.oql.def.Query;
import jda.test.app.courseman.qproc.TestQProc;
import jda.test.app.courseman.qproc.query.JoinDefTest;
import jda.test.app.courseman.qproc.query.TranslateClassJoin2ObjectJoinExpressionTest;
import jda.test.app.courseman.qproc.query.JoinDefTest.SupportedCourseMan;
import jda.test.model.extended.City;

public class AutoJoinQueryQProc extends TestQProc {

  @Test
  public void doTest() throws DataSourceException {
    System.out.println(this.getClass().getSimpleName());
    
    
    Class defCls = JoinDefTest.SupportedCourseMan.class;

    // test result
    Object[] expected = null;
    
    //instance.registerClass(City.class);
    
    TranslateClassJoin2ObjectJoinExpressionTest translator = new TranslateClassJoin2ObjectJoinExpressionTest();
    ObjectJoinExpression[] jexps = translator.doTranslation(defCls, SupportedCourseMan.values, expected);
    
    Query query;    
    for (ObjectJoinExpression jexp : jexps) {
      query = new Query(jexp);
      
      executeQuery(query);
      
    }
  }
  
}
