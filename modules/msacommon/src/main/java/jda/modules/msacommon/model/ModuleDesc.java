package jda.modules.msacommon.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * @overview Represents module description, which includes path-id, module name and distribution .jar file.
 */
@JsonIgnoreProperties()
public class ModuleDesc {
  private String pid;
  private String module;

  private String parentModule;

  private String domainClsName;
  private String jarFile;
  /** equivalent service of {@link #module}*/
  private String service;

  public ModuleDesc() {
    //
  }

  public ModuleDesc(String pid, String module, String jarFile) {
    this(pid, module, module, jarFile);
  }

  public ModuleDesc(String pid, String module, String domainClsName, String jarFile) {
    this.pid = pid;
    this.module = module;
    this.domainClsName = domainClsName;
    this.jarFile = jarFile;
  }

  public String getPid() {
    return pid;
  }

  public String getModule() {
    return module;
  }

  public String getDomainClsName() {
    return domainClsName;
  }

  public String getJarFile() {
    return jarFile;
  }

  public void setService(String servName) {
    this.service = servName;
  }

  public String getService() {
    return this.service;
  }

  public void setParentModule(String parentModule) {
    this.parentModule = parentModule;
  }

  public String getParentModule() {
    return this.parentModule;
  }
}
