package jda.modules.security.def;

import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;

/**
 * Represents an un-authenticated user of the application. 
 * 
 * @author dmle
 *
 */
@DClass(schema=DCSLConstants.SECURITY_SCHEMA,serialisable=false)
public class LoginUser {
  @DAttr(name="login",id=true,type=Type.String,optional=false,length=25)
  private String login;
  @DAttr(name="password",type=Type.StringMasked,optional=false,length=25)
  private String password;
  
  @DAttr(name="role",type=Type.Domain,length=20)
  private Role role;

  // constructor methods
  public LoginUser(String login, String pwd, Role role) {
    this.login = login;
    this.password = pwd;
    this.role = role;
  }

  public LoginUser(String login, String pwd) {
    this(login, pwd,null);
  }
  
  public String getLogin() {
    return login;
  }

  public void setLogin(String login) {
    this.login = login;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setRole(Role role) {
    this.role = role;
  }

  public Role getRole() {
    return role;
  }

  public String toString() {
    return "LoginUser("+login+","+role+")";
  }
}
