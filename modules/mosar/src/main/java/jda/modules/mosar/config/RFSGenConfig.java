package jda.modules.mosar.config;

import jda.modules.mosar.software.backend.BEApp;
import jda.modules.mosar.software.frontend.FEApp;
import jda.modules.mosarfrontend.common.anotation.template_desc.AppTemplateDesc;
import jda.modules.sccl.syntax.SystemDesc;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Duc Minh Le (ducmle)
 * @version 5.4.1
 * @overview Represents the configuration for RFSGen. It is created from {@link RFSGenDesc}.
 */
public class RFSGenConfig {
	

    private ExecSpec execSpec;

    private StackSpec stackSpec;

    private Class[] domainModel;

    private GenerationMode genMode;

    private String feOutputPath;

    /**
     * front-end project path
     */
    private String feProjPath;

    /**
     * front-end project name
     */
    private String feProjName;

    /**
     * front-end template
     */
    private FEPlatform fePlatform;
    private AppTemplateDesc feTemplate;
    private SystemDesc systemDesc;

    /**
     * front-end's shared resources for project
     */
    private String feProjResource;

    private long feServerPort;

    private Class<? extends FEApp> feAppClass;
    private boolean feThreaded;

    private LangPlatform beLangPlatform;
    private String beTargetPackage;
    private String beOutputPath;
    private String bePackage;
    private long beServerPort;

    private Class<? extends BEApp> beAppClass;

    private boolean beThreaded;

    private Class scc;

    private Class mccMain;

    private List<Class> mccFuncs;

    /**
     * is the union of {@link #mccFuncs} and {@link #mccMain}
     */
    private Class[] mccs;

    /**
     * @effects this is needed for mapping from annotation
     */
    public RFSGenConfig() {
    }

    /**
     * @effects return stackSpec
     */
    public StackSpec getStackSpec() {
        return stackSpec;
    }

    /**
     * @effects set stackSpec = stackSpec
     */
    public void setStackSpec(StackSpec stackSpec) {
        this.stackSpec = stackSpec;
    }

    /**
     * @effects return execSpec
     */
    public ExecSpec getExecSpec() {
        return execSpec;
    }

    /**
     * @effects set execSpec = execSpec
     */
    public void setExecSpec(ExecSpec execSpec) {
        this.execSpec = execSpec;
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
     * @version
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
     * @effects this.domainModel = model
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
     */
    public String getFeOutputPath() {
        return this.feOutputPath;
    }

    /**
     * @effects return feProjPath
     */
    public String getFeProjPath() {
        return feProjPath;
    }

    /**
     * @effects set feProjPath = feProjPath
     */
    public void setFeProjPath(String feProjPath) {
        this.feProjPath = feProjPath;
    }

    /**
     * @effects return feProjName
     */
    public String getFeProjName() {
        return feProjName;
    }

    /**
     * @effects set feProjName = feProjName
     */
    public void setFeProjName(String feProjName) {
        this.feProjName = feProjName;
    }

    /**
     * @effects return feProjResource
     */
    public String getFeProjResource() {
        return feProjResource;
    }

    /**
     * @effects set feProjResource = feProjResource
     */
    public void setFeProjResource(String feProjResource) {
        this.feProjResource = feProjResource;
    }

    /**
     * @effects return beAppClass
     */
    public Class<? extends BEApp> getBeAppClass() {
        return beAppClass;
    }

    /**
     * @effects set beAppClass = beAppClass
     */
    public void setBeAppClass(Class<? extends BEApp> beAppClass) {
        this.beAppClass = beAppClass;
    }

    /**
     * @effects
     */
    public void setSCC(Class scc) {
        this.scc = scc;
    }

    /**
     * @effects
     * @version
     */
    public void setMCCMain(Class mccMain) {
        this.mccMain = mccMain;
    }

    /**
     * @effects
     * @version
     */
    public void setMCCFuncs(Class[] mccFuncs) {
        if (this.mccFuncs == null) {
            this.mccFuncs = new ArrayList<>();
        }
        for (Class mccFunc : mccFuncs) this.mccFuncs.add(mccFunc);
    }

    /**
     * @effects
     * @version
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
     * @effects add mcc to {@link #mccFuncs}
     */
    public void addMCCFunc(Class mcc) {
        if (mccFuncs == null) {
            mccFuncs = new ArrayList<>();
        }

        mccFuncs.add(mcc);
    }

    /**
     * @effects return {@link #bePackage}
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

    /**
     * @effects return feServerPort
     */
    public long getFeServerPort() {
        return feServerPort;
    }

    /**
     * @effects set feServerPort = feServerPort
     */
    public void setFeServerPort(long feServerPort) {
        this.feServerPort = feServerPort;
    }

    /**
     * @effects return beServerPort
     */
    public long getBeServerPort() {
        return beServerPort;
    }

    /**
     * @effects set beServerPort = beServerPort
     */
    public void setBeServerPort(long beServerPort) {
        this.beServerPort = beServerPort;
    }

    /**
     * @effects return feAppClass
     */
    public Class<? extends FEApp> getFeAppClass() {
        return feAppClass;
    }

    /**
     * @effects set feAppClass = feAppClass
     */
    public void setFeAppClass(Class<? extends FEApp> feAppClass) {
        this.feAppClass = feAppClass;
    }

    /**
     * @effects return isFeThreaded
     */
    public boolean getFeThreaded() {
        return feThreaded;
    }

    /**
     * @effects set isFeThreaded = isFeThreaded
     */
    public void setFeThreaded(boolean isFeThreaded) {
        this.feThreaded = isFeThreaded;
    }

    /**
     * @effects return isBeThreaded
     */
    public boolean getBeThreaded() {
        return beThreaded;
    }

    /**
     * @effects set isBeThreaded = isBeThreaded
     */
    public void setBeThreaded(boolean isBeThreaded) {
        this.beThreaded = isBeThreaded;
    }

    /**
     * @effects return {@link #execSpec} includes {@link ExecSpec#Compile}
     */
    public boolean isExecSpecCompile() {
        return execSpec.isCompile();
    }


    public FEPlatform getFePlatform() {
        return fePlatform;
    }

    public void setFePlatform(FEPlatform fePlatform) {
        this.fePlatform = fePlatform;
    }

    public AppTemplateDesc getFeTemplate() {
        return feTemplate;
    }

    public void setFeTemplate(AppTemplateDesc feTemplate) {
        this.feTemplate = feTemplate;
    }

    public SystemDesc getSystemDesc() {
        return systemDesc;
    }

    public void setSystemDesc(SystemDesc systemDesc) {
        this.systemDesc = systemDesc;
    }
}
