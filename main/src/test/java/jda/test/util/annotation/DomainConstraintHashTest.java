package jda.test.util.annotation;

import java.util.Arrays;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.test.model.basic.Student;
import jda.util.SwTk;

public class DomainConstraintHashTest {
  public static void main(String[] args) throws DataSourceException {
    Configuration config = SwTk.createMemoryBasedConfiguration("");
    DODMBasic schema = DODMBasic.getInstance(config);
    
    DSMBasic dsm = schema.getDsm();
    DOMBasic dom = schema.getDom();

    schema.registerClass(Student.class);
    
    String attribName = "name";
    DAttr attrib = dsm.getDomainConstraint(Student.class, attribName);
    int hash = attrib.hashCode();
    
    System.out.printf("Attribute %s; hash = %d%n", attribName, hash);
    
    String val = "S2014";
    hash = val.hashCode();
    System.out.printf("Value %s; hash = %d%n", val, hash);
    
    // array of values (computed from hash of values)
    Object[] vals = {"S2014", 1, 2L, 3.2D};
    hash = hashOfValues(vals);
    System.out.printf("Array of vals: %s;%n hash = %d%n", Arrays.toString(vals), hash);

    /*
    // tuple
    Tuple2<DomainConstraint,Comparable> t = new Tuple2(attrib,val);
    hash = t.hashCode();
    System.out.printf("Tuple2: %s;%n hash = %d%n", t, hash);
    */

    /*
    // list of tuples
    List<Tuple2<DomainConstraint,Comparable>> l = new ArrayList<Tuple2<DomainConstraint,Comparable>>();
    l.add(t);
    hash = l.hashCode();
    System.out.printf("List of Tuple2: %s;%n hash = %d%n", l, hash);
    */

    /*
    // array 
    Object[] arr = {val};
    hash = arr.hashCode();
    System.out.printf("Array of vals: %s;%n hash = %d%n", Arrays.toString(arr), hash);
    */
  }
  
  private static int hashOfValues(Object[] vals) {
    int hashCode = 1;
    for (Object e : vals)
        hashCode = 31*hashCode + (e==null ? 0 : e.hashCode());
    
    return hashCode;
  }
}
