package jda.modules.msacommon.model;

/**
 * @overview Represents module description, which includes path-id, module name and distribution .jar file.
 */
public class ModuleDesc {
  private final String pid;
  private final String module;
  private final String jarFile;

  public ModuleDesc(String pid, String module, String jarFile) {
    this.pid = pid;
    this.module = module;
    this.jarFile = jarFile;
  }

  public String getPid() {
    return pid;
  }

  public String getModule() {
    return module;
  }

  public String getJarFile() {
    return jarFile;
  }
}
