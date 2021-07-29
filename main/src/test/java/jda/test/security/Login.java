package jda.test.security;

import org.junit.Test;

import jda.modules.common.exceptions.DataSourceException;
import jda.modules.security.def.DomainUser;

public class Login extends TestMainSecurity {
  @Test
  public void login() throws DataSourceException {
    TestMainSecurity tm = (TestMainSecurity) instance;
    
    instance.registerClasses();

    String user = "anhtq";
    String pwd = user;
    
    DomainUser duser = tm.login(user, pwd);
    
    System.out.printf("User: %s%n", duser);
    System.out.printf("User permissions: %n");
    
    tm.printUserPermissions(duser);

  }
}
