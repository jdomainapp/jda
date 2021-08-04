package jda.modules.mccl.conceptmodel.module;

import jda.modules.dcsl.syntax.DAttr;

/** application module type*/
public enum ModuleType implements ModType {
  Security,
  System,
  DomainMain,
  DomainData,
  DomainReport,
  /**
   * = {@link #System} + service: 
   * a system module that provides a specific service to other modules (clients)
   */
  SystemService;
  
  @DAttr(name = "name", id = true, type = DAttr.Type.String, length = 20)
  public String getName() {
    return name();
  }

  /**
   * @effects 
   *  if this represents a domain-specific module type
   *    return true
   *  else
   *    return false
   */
  public boolean isDomain() {
    return this == DomainMain ||
        this == DomainData || 
        this == DomainReport ;
  }


  /**
   * @effects 
   *  if this is {@link ModuleType#DomainMain} then return true else return false
   * @version 5.4.1
   */
  public boolean isMain() {
    return this == DomainMain;
  }
  
  /**
   * @effects 
   *  if this has the specified type
   *    return true
   *  else
   *  return false
   *  
   *  @version 
   *  - 5.2: improved to support Composite.
   */
  public boolean isType(ModType type) {
    if (type instanceof ModuleType)
      return this == type;
    else
      return ((ModuleType.Composite) type).hasType(this);
  }
  
  /**
   * @overview 
   *  Represents a composition of two or more {@link ModuleType}s.
   *  
   * @author Duc Minh Le (ducmle)
   *
   * @version 5.2
   */
  public enum Composite implements ModType {
    Sys(System, SystemService),
    Service(SystemService), 
    Domain(DomainData, DomainReport, DomainMain),
    ;

    private ModuleType[] components;
    
    private Composite(ModuleType...components) {
      this.components = components;
    }
    
    /**
     * @effects return the component types of this. 
     */
    public ModuleType[] components() {
      return components;
    }
    
    /**
     * @effects 
     *  if {@link #components} contains <tt>moduleType</tt> as a component
     *    return true
     *  else
     *    return false
     */
    public boolean hasType(ModuleType moduleType) {
      for (ModuleType comp : components) {
        if (comp == moduleType)
          return true;
      }
      
      return false;
    }
  } /** end {@link Composite} */

  /**
   * @effects 
   *  return all the {@link ModuleType}s that are system types.
   *  
   * @version 5.2
   */
  public static ModuleType[] systemTypes() {
    return Composite.Sys.components();
  }
  
  /**
   * @effects 
   *  return all the {@link ModuleType}s that are domain types.
   *  
   * @version 5.2
   */
  public static ModuleType[] domainTypes() {
    return Composite.Domain.components();
  }
}