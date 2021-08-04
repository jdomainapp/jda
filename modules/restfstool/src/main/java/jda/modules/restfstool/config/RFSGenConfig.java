package jda.modules.restfstool.config;

import java.util.ArrayList;
import java.util.List;

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

  private LangPlatform beLangPlatform;
  private String beTargetPackage;
  private String beOutputPath;
  private GenerationMode genMode;

  private String feOutputPath;

  private Class<? extends BESpringApp> beAppClass;

  private Class scc;

  private Class mccMain;

  private List<Class> mccFuncs;

  /** is the union of {@link #mccFuncs} and {@link #mccMain}*/
  private Class[] mccs;

  private String bePackage;
  
  /**
   * @effects 
   *  this is needed for mapping from annotation
   */
  public RFSGenConfig() {
  }

  /**
   * @effects return langPlatform
   */
  public LangPlatform getBeLangPlatform() {
    return beLangPlatform;
  }
  /**
   * @effects set langPlatform = langPlatform
   */
  public void setBeLangPlatform(LangPlatform langPlatform) {
    this.beLangPlatform = langPlatform;
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
    if (this.mccFuncs == null) {
      this.mccFuncs = new ArrayList<>();
    }
    for (Class mccFunc : mccFuncs) this.mccFuncs.add(mccFunc);
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
    if (mccFuncs != null)
      return mccFuncs.toArray(new Class[mccFuncs.size()]);
    else
      return null;
  }

//  /**
//   * @effects 
//   *  sets this.mccs = mccs
//   */
//  public void setMCCs(Class[] mccs) {
//    this.mccs = mccs;
//  }

  /**
   * @effects 
   *  add mcc to {@link #mccFuncs}
   */
  public void addMCCFunc(Class mcc) {
    if (mccFuncs == null) {
      mccFuncs = new ArrayList<>();
    }
    
    mccFuncs.add(mcc);
  }

  /**
   * @effects 
   *  return {@link #bePackage}
   */
  public String getBePackage() {
    return bePackage;
  }

  /**
   * @effects set bePackage = bePackage
   */
  public void setBePackage(String bePackage) {
    this.bePackage = bePackage;
  }
  
}
