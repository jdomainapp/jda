package jda.test.dodm.objectpool.units.browse.withbuffer;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.FIRST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.LAST;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.NEXT;
import static jda.test.dodm.DODMTesterWithBrowse.BrowsingStep.PREV;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import org.junit.Test;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.mosa.model.Oid;
import jda.test.dodm.DODMTesterWithBrowse;
import jda.test.model.basic.Student;

public class BrowseWithIdCollection extends DODMTesterWithBrowse {

  /**
   * @requires 
   *  Basic Student data have been set-up in database
   */
  public Collection<Oid> getStudentOids() {
    DSMBasic schema = instance.getDsm();
    DOMBasic dom = instance.getDom();
    
    Class c = Student.class;
    
    DAttr idAttrib = schema.getIDAttributeConstraints(c)[0];
    
    int currYear = Calendar.getInstance().get(Calendar.YEAR);
    
    String[] idVals = {
        "S"+currYear,
        "S"+(currYear+1),
        "S"+(currYear+2),
        "S"+(currYear+3),
        "S"+(currYear+4),
    };
    
    Oid oid;
    Collection<Oid> oids = new ArrayList();
    for (String idVal : idVals) {
      oid = dom.genObjectId(c, idAttrib, idVal);
      oids.add(oid);
    }
    
    return oids;
  }
  
  @Test
  public void doTest() throws Exception { 
    
    // get Id collection
    Collection<Oid> oids = getStudentOids();
    
    Class c = Student.class;

    // initialise object buffer
    initObjectBuffer(c, oids);
    
    // set up a browsing sequence
    BrowsingStep[] browseSeq = {
        FIRST,
        NEXT,PREV,NEXT,
        NEXT,
        NEXT,PREV,PREV,
        LAST,
        PREV,
        PREV, NEXT, NEXT, 
        //NEXT // -- NotFoundException
    };
    
    Boolean[] expected = {
        FALSE, 
        FALSE, TRUE, TRUE,
        FALSE, 
        FALSE, TRUE, TRUE,
        FALSE,
        TRUE,
        TRUE, TRUE, TRUE
    };
        
    browse(browseSeq, expected);
  }

}
