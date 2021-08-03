package jda.modules.restfstool.config;

import jda.modules.restfstool.backend.BESpringApp;

/**
 * @overview 
 *  Represents the configuration for RFSGen. It is created from {@link RFSGenDesc}. 
 *  
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4.1
 */
public class RFSGenConfig {

  private Class[] domainModel;

  private LangPlatform langPlatform;
  private String beTargetPackage;
  private String beOutputPath;
  private GenerationMode genMode;

  private String feOutputPath;

  private Class<? extends BESpringApp> beAppClass;

  private Class scc;

  private Class mccMain;

  private Class[] mccFuncs;
  
  
  /**
   * @effects 
   *
   * @version 
   */
  public RFSGenConfig() {
  }

  /**
   * @effects return langPlatform
   */
  public LangPlatform getLangPlatform() {
    return langPlatform;
  }
  /**
   * @effects set langPlatform = langPlatform
   */
  public void setLangPlatform(LangPlatform langPlatform) {
    this.langPlatform = langPlatform;
  }
  /**
   * @effects return targetPackage
   */
  public String getBeTargetPackage() {
    return beTargetPackage;
  }
  /**
   * @effects set targetPackage = targetPackage
   */
  public void setBeTargetPackage(String targetPackage) {
    this.beTargetPackage = targetPackage;
  }
  /**
   * @effects return outputPath
   */
  public String getBeOutputPath() {
    return beOutputPath;
  }
  /**
   * @effects set outputPath = outputPath
   */
  public void setBeOutputPath(String outputPath) {
    this.beOutputPath = outputPath;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public GenerationMode getGenMode() {
    return genMode;
  }

  
  /**
   * @effects set genMode = genMode
   */
  public void setGenMode(GenerationMode genMode) {
    this.genMode = genMode;
  }

  /**
   * @effects 
   *  this.domainModel = model
   */
  public void setDomainModel(Class[] model) {
    this.domainModel = model;
  }

  /**
   * @effects return domainModel
   */
  public Class[] getDomainModel() {
    return domainModel;
  }

  
  /**
   * @effects set frontEndOutputPath = frontEndOutputPath
   */
  public void setFeOutputPath(String frontEndOutputPath) {
    this.feOutputPath = frontEndOutputPath;
  }

  /**
   * @effects 
   * 
   */
  public String getFeOutputPath() {
    return this.feOutputPath;
  }

  /**
   * @effects 
   * 
   */
  public Class<? extends BESpringApp> getBEAppClass() {
    return beAppClass;
  }

  /**
   * @effects return beAppClass
   */
  public Class<? extends BESpringApp> getBeAppClass() {
    return beAppClass;
  }

  /**
   * @effects set beAppClass = beAppClass
   */
  public void setBeAppClass(Class<? extends BESpringApp> beAppClass) {
    this.beAppClass = beAppClass;
  }

  /**
   * @effects 
   * 
   */
  public void setSCC(Class scc) {
    this.scc = scc;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public void setMCCMain(Class mccMain) {
    this.mccMain = mccMain;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public void setMCCFuncs(Class[] mccFuncs) {
    this.mccFuncs = mccFuncs;
  }

  /**
   * @effects 
   * 
   * @version 
   * 
   */
  public Class getSCC() {
    return scc;
  }

  /**
   * @effects return mccMain
   */
  public Class getMCCMain() {
    return mccMain;
  }

  /**
   * @effects return mccFuncs
   */
  public Class[] getMCCFuncs() {
    return mccFuncs;
  }
  
  
}
