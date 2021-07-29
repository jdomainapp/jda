package jda.modules.security.def;

/**
   * @overview 
   *  Represents a definition of an permission on a domain class 
   *  
   * @author dmle
   */
  public class DomainClassPerm {
    private Class domainCls;
    private String attrib;
    private PermType type;
    public DomainClassPerm(Class c, PermType type) {
      this(c,null,type);
    }

    public DomainClassPerm(Class c, String attrib, PermType type) {
      this.domainCls = c;
      this.attrib = attrib;
      this.type = type;
    }

    public Class getDomainCls() {
      return domainCls;
    }
//
//    public void setDomainCls(Class domainCls) {
//      this.domainCls = domainCls;
//    }

    public String getAttrib() {
      return attrib;
    }

//    public void setAttrib(String attrib) {
//      this.attrib = attrib;
//    }

    public PermType getType() {
      return type;
    }
//
//    public void setType(PermDefs.PermType type) {
//      this.type = type;
//    }
  }