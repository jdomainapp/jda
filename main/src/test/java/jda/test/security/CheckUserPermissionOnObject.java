package jda.test.security;

import java.util.Collection;
import java.util.Iterator;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.expression.Op;
import jda.modules.dodm.DODMBasic;
import jda.modules.security.def.DomainUser;
import jda.test.model.basic.Student;

public class CheckUserPermissionOnObject extends TestMainSecurity {
  @Test
  public void doTest() throws NotFoundException, DataSourceException {
    DODMBasic schema = instance.getDODM();
    
    // register all classes
    instance.registerClasses();
    instance.loadAllObjectsFromSource();
    
    // domain users
    String[] loginNamePatterns = {
      "%duc%",
      "%anh%",
      "%kieu%",
      "%linh%"
    };
    
    for (String login : loginNamePatterns) {
      // load a domain user
      Class<DomainUser> c = DomainUser.class;
      String attrib = "login";
      Op op = Op.MATCH;
      String val = login;
      DomainUser user = schema.getDom().getObject(c, attrib, op, val);
      
      System.out.printf("%nUser %s:%n", user);

      printUserPermissions(user);
      
      // load Student objects
      Class<Student> cs = Student.class;
      
      // check user permission on object(s)
      Collection<Student> students = getStudentObjects();
      Iterator<Student> sit = students.iterator();
      Student s;
      boolean permit;
      System.out.printf("Permissions on %s%n", cs);

      while (sit.hasNext()) {
        s = sit.next();
        permit = isObjectPermitted(user, cs, s);
        System.out.printf("  -%s-> Object %s%n", 
            (permit? "ALLOWED" : "NOT-ALLOWED"), 
            s);
      }
    }
  }
}
