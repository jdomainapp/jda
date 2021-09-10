package jda.modules.sccl.conceptualmodel;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.json.JsonObject;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.ClassExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MemberValuePair;
import com.github.javaparser.ast.expr.Name;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.NormalAnnotationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.printer.PrettyPrinterConfiguration;

import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ParserToolkit;
import jda.modules.dodm.dom.DOM;
import jda.modules.dodm.dsm.DSM;
import jda.modules.dodm.osm.postgresql.PostgreSQLOSM;
import jda.modules.mccl.conceptmodel.Configuration.Language;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig.ConnectionType;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.sccl.syntax.DSDesc;
import jda.modules.sccl.syntax.OrgDesc;
import jda.modules.sccl.syntax.SecurityDesc;
import jda.modules.sccl.syntax.SysSetUpDesc;
import jda.modules.sccl.syntax.SystemDesc;
import jda.modules.setup.model.SetUpConfig;

/**
 * @overview Represents a Software Configuration Class (SCC). This class is used
 *           as the base for defining the configuration for a class of software,
 *           which is constructed from a set of MCCs of the software modules,
 *           which are themselves constructed from a domain model.
 * 
 * @author Duc Minh Le (ducmle)
 *
 * @version 5.4
 */
public class SCC {
  private static final Class<SystemDesc> SD = SystemDesc.class;

  private static final PrettyPrinterConfiguration printCfg;

  static {
    printCfg = new PrettyPrinterConfiguration();

    printCfg.setEndOfLineCharacter("\n");
    printCfg.setIndent("  ");
  }

  /**
   * the domain-specific configuration data for the software
   */
  private Map<String, Object> templateData = new HashMap<>();

  private CompilationUnit ast;
  private ClassOrInterfaceDeclaration sccNode;

  private String domainName;
  private String swcName;
  private List<String> mccClsFQNs;
  private String moduleMainClsFQN;

  // v5.4.1
  private String fqn;

  /** the root output dir for saving {@link #getSourceCode()} */
  private File outputSrcFile;

  private void intDefaultData() {
    templateData.put("appName", domainName);
    templateData.put("appLogo", domainName + "logo.jpg");
    templateData.put("appLanguage", Language.English);

    // orgDesc
    templateData.put("orgName", domainName.toUpperCase());
    templateData.put("orgAddress", "");
    templateData.put("orgLogo", domainName + ".png");
    templateData.put("orgUrl", "http://" + domainName + ".com");
    // dsDesc
    templateData.put("dsType", "postgresql");
    templateData.put("dsUrl",
        "//localhost:5432/" + domainName.toLowerCase() + "ds");
    templateData.put("dsUser", "user");
    templateData.put("dsPassword", "password");
    templateData.put("dsConnectionType", ConnectionType.Client);

    // securityDesc
    templateData.put("securityIsEnable", Boolean.FALSE);
  }

  private void updateTemplateData(Map<String, Object> newTemplateData) {
    for (Map.Entry<String, Object> entry : newTemplateData.entrySet()) {
      String key = entry.getKey();
      if (templateData.containsKey(key)) {
        templateData.put(key, entry.getValue());
      }
    }
  }

  // /**
  // * @effects
  // * return the last segment of <tt>fqn</tt>
  // */
  // private String getClassSimpleName(String fqn) {
  // int idx = fqn.lastIndexOf(".");
  //
  // if (idx > -1) {
  // return fqn.substring(idx + 1);
  // } else {
  // return fqn;
  // }
  // }

  public SCC(String domainName, List<String> mccClsFQNs,
      String moduleMainClsFQN, String swcName,
      Map<String, Object> newTemplateData) {
    // get the shared class pool instance (singleton)
    /*
     * ducmle: moved to bottom intDefaultData(); if (newTemplateData != null &&
     * !newTemplateData.isEmpty()) { updateTemplateData(newTemplateData); }
     */
    ast = ParserToolkit.createJavaParserForClass(swcName, Modifier.PUBLIC);

    sccNode = ast.getClassByName(swcName).get();

    this.domainName = domainName;
    this.swcName = swcName;
    this.mccClsFQNs = mccClsFQNs;
    this.moduleMainClsFQN = moduleMainClsFQN;

    intDefaultData();
    if (newTemplateData != null && !newTemplateData.isEmpty()) {
      updateTemplateData(newTemplateData);
    }
  }

  /**
   * @effects initialise this from content of <tt>srcFile</tt>
   */
  public SCC(String clsName, String srcFile) throws NotFoundException {
    // get the shared class pool instance (singleton)
    try {
      ast = ParserToolkit.createJavaParser(srcFile);

      sccNode = ast.getClassByName(clsName).get();

      this.swcName = sccNode.getNameAsString();

      this.outputSrcFile = new File(srcFile);

    } catch (FileNotFoundException e) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, e,
          new Object[] { srcFile });
    }
  }

  /**
   * @modifies {@link #sccNode}
   * 
   * @requires
   *  <code>cfgData</code> is structured precisely based on <code>anoType</code> (i.e. (property,value) pairs must match)  
   *  
   * @effects
   *  create and attach to this an instance of <code>anoType</code> that is initialised 
   *  from <code>cfgData</code>  
   *  
   * @version 5.4.1
   */
  public void createCfgAnnotation(Class<? extends Annotation> anoType,
      JsonObject cfgData) {
    NormalAnnotationExpr ano = ParserToolkit.createAnoPropValExpr(ast, cfgData, anoType);
    
    sccNode.addAnnotation(ano);
  }

  /**
   * @modifies this.{@link #ast}
   * @effects creates in {@link #ast} a {@link ModuleDescriptor} for the module
   *          class represented by this. The model of this descriptor is set to
   *          {@link #dcls}
   */
  public void createSystemDesc() {
    /**
     * create SystemDesc( appName="CourseMan",
     * splashScreenLogo=""coursemanapplogo.jpg", language=Language.English,
     * orgDesc=OrgDesc(name="",address="",logo="", url=""),
     * dsDesc=DSDesc(type="",dsUrl="",user="",password="",dsmType=DSM.class,domType=DOM.class,osmType=PostgreSQLOSM.class,connType=ConnectionType.Client),
     * modules={}, sysModules={},
     * setUpDes=SysSetUpDesc(setUpConfigType=SetUpConfig.class),
     * securityDesc=SecurityDesc(isEnabled=false) )
     **/
    NormalAnnotationExpr systemDesc = sccNode
        .addAndGetAnnotation(SystemDesc.class);

    NodeList<MemberValuePair> props = new NodeList<>();
    systemDesc.setPairs(props);

    // prop: appName
    props.add(new MemberValuePair("appName",
        new StringLiteralExpr((String) templateData.get("appName"))));
    props.add(new MemberValuePair("splashScreenLogo",
        new StringLiteralExpr((String) templateData.get("appLogo"))));

    // prop: language
    ast.addImport(Language.class);
    FieldAccessExpr fieldAccessExpr = new FieldAccessExpr(
        new NameExpr("Language"),
        new SimpleName(((Language) templateData.get("appLanguage")).name())
            .asString());
    MemberValuePair language = new MemberValuePair("language", fieldAccessExpr);
    props.add(language);

    // prop: orgDesc
    NodeList<MemberValuePair> orgDescProps = new NodeList<>();
    orgDescProps.add(new MemberValuePair("name",
        new StringLiteralExpr((String) templateData.get("orgName"))));
    orgDescProps.add(new MemberValuePair("address",
        new StringLiteralExpr((String) templateData.get("orgAddress"))));
    orgDescProps.add(new MemberValuePair("logo",
        new StringLiteralExpr((String) templateData.get("orgLogo"))));
    orgDescProps.add(new MemberValuePair("url",
        new StringLiteralExpr((String) templateData.get("orgUrl"))));

    NormalAnnotationExpr orgDesc = new NormalAnnotationExpr(
        parseNameAndImport(OrgDesc.class), orgDescProps);
    MemberValuePair orgDescProp = new MemberValuePair("orgDesc", orgDesc);
    props.add(orgDescProp);

    // prop: dsDesc
    NodeList<MemberValuePair> dsDescProps = new NodeList<>();
    dsDescProps.add(new MemberValuePair("type",
        new StringLiteralExpr((String) templateData.get("dsType"))));
    dsDescProps.add(new MemberValuePair("dsUrl",
        new StringLiteralExpr((String) templateData.get("dsUrl"))));
    dsDescProps.add(new MemberValuePair("user",
        new StringLiteralExpr((String) templateData.get("dsUser"))));
    dsDescProps.add(new MemberValuePair("password",
        new StringLiteralExpr((String) templateData.get("dsPassword"))));
    dsDescProps
        .add(new MemberValuePair("dsmType", createClassExprFor(DSM.class)));
    dsDescProps
        .add(new MemberValuePair("domType", createClassExprFor(DOM.class)));
    dsDescProps.add(new MemberValuePair("osmType",
        createClassExprFor(PostgreSQLOSM.class)));

    ast.addImport(ConnectionType.class);
    fieldAccessExpr = new FieldAccessExpr(new NameExpr("ConnectionType"),
        new SimpleName(
            ((ConnectionType) templateData.get("dsConnectionType")).name())
                .asString());
    dsDescProps.add(new MemberValuePair("connType", fieldAccessExpr));

    NormalAnnotationExpr dsDesc = new NormalAnnotationExpr(
        parseNameAndImport(DSDesc.class), dsDescProps);
    MemberValuePair dsDescProp = new MemberValuePair("dsDesc", dsDesc);
    props.add(dsDescProp);

    // prop: modules
    NodeList<Expression> mccClazzes = new NodeList<>();
    mccClazzes.add(createClassExprFor(moduleMainClsFQN));
    for (String mccClsFQN : mccClsFQNs) {
      mccClazzes.add(createClassExprFor(mccClsFQN));
    }

    ArrayInitializerExpr modules = new ArrayInitializerExpr(mccClazzes);
    MemberValuePair moduleDes = new MemberValuePair("modules", modules);
    props.add(moduleDes);

    // prop: systemModules
    ArrayInitializerExpr sysModules = new ArrayInitializerExpr();
    MemberValuePair systemModuleDes = new MemberValuePair("sysModules",
        sysModules);
    props.add(systemModuleDes);

    // prop: setUpDesc
    NodeList<MemberValuePair> setUpDescProps = new NodeList<>();
    setUpDescProps.add(new MemberValuePair("setUpConfigType",
        createClassExprFor(SetUpConfig.class)));

    NormalAnnotationExpr setUpDesc = new NormalAnnotationExpr(
        parseNameAndImport(SysSetUpDesc.class), setUpDescProps);
    MemberValuePair setUpDescProp = new MemberValuePair("setUpDesc", setUpDesc);
    props.add(setUpDescProp);

    // prop: securityDesc
    NodeList<MemberValuePair> securityProps = new NodeList<>();
    securityProps.add(new MemberValuePair("isEnabled", new BooleanLiteralExpr(
        (Boolean) templateData.get("securityIsEnable"))));

    NormalAnnotationExpr securityDesc = new NormalAnnotationExpr(
        parseNameAndImport(SecurityDesc.class), securityProps);
    MemberValuePair securityProp = new MemberValuePair("securityDesc",
        securityDesc);
    props.add(securityProp);
  }

  /**
   * @effects parse <tt>qualifiedName</tt> (import it if needed) and return the
   *          {@link Name} object of <tt>qualifiedName</tt>
   */
  private Name parseName(String qualifiedName) {
    return JavaParser.parseName(qualifiedName);
  }

  // havt start
  private Name parseNameAndImport(Class cls) {
    ast.addImport(cls);
    return JavaParser.parseName(cls.getSimpleName());
  }

  private ClassExpr createClassExprFor(Class cls) {
    ast.addImport(cls);
    ClassOrInterfaceType type = JavaParser
        .parseClassOrInterfaceType(cls.getSimpleName());
    return new ClassExpr(type);
  }

  private ClassExpr createClassExprFor(String clsFQN) {
    ast.addImport(clsFQN);
    String simpleClsName = clsFQN.substring(clsFQN.lastIndexOf('.') + 1);
    ClassOrInterfaceType type = JavaParser
        .parseClassOrInterfaceType(simpleClsName);
    return new ClassExpr(type);
  }

  // havt end

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#toString()
   */
  /**
   * @effects
   * 
   * @version
   */
  @Override
  public String toString() {
    return String.format("SCC(%s): %n %s", sccNode.getNameAsString(),
        ast.toString(printCfg));
  }

  /**
   * @modifies this.{@link #ast}
   * @effects changes package declaration of {@link #ast} to pkgName.
   */
  public void setPackageName(String pkgName) {
    ast.setPackageDeclaration(pkgName);
  }


  /**
   * @effects 
   *  return the package name that was set by {@link #setPackageName(String)}
   * @version 5.4.1
   */
  public String getPackage() {
    return ParserToolkit.getPackageDeclaration(ast);
  }
  
  /**
   * @effects return {@link #ast}.toString
   */
  public String getSourceCode() {
    return ast.toString();
  }

  /**
   * @modifies this.{@link #outputSrcFile}
   * @effects write source code of {@link #ast} to a file in a sub-package
   *          directory of <tt>mccOutputRootDir</tt> that corresponds to the
   *          package name of this (overwritting existing content, if any).
   * 
   *          <p>
   *          sets this.{@link #outputSrcFile} = the file.
   */
  public void save(String mccOutputRootDir) throws NotPossibleException {

    String mccSrc = getSourceCode();
    String mccPkg = ParserToolkit.getPackageDeclaration(ast);
    String mccFQN = mccPkg + "." + swcName;

    // write to file
    boolean overwrite = true;
    outputSrcFile = ToolkitIO.writeJavaSourceFile(mccOutputRootDir, mccFQN,
        mccSrc, overwrite);
  }

  /**
   * @effects if {@link #outputSrcFile} != null write source code of
   *          {@link #ast} to a file specified in {@link #outputSrcFile}
   *          (overwritting existing content, if any). else do nothing
   */
  public void save() {
    if (outputSrcFile != null) {
      String mccSrc = getSourceCode();

      boolean overwrite = true;
      ToolkitIO.writeTextFileWithEncoding(outputSrcFile, mccSrc, "UTF-8",
          overwrite);
    }
  }


  /**
   * @requires {@link #outputSrcFile} is set by a constructor or 
   *  by invoking it {@link #save()} 
   *  
   * @effects 
   *  return the output source file that was set by {@link #save()}.
   *  
   * @version 5.4.1
   * 
   */
  public File getOutputSrcFile() {
    return outputSrcFile;
  }
  
  /**
   * @effects return the FQN of the SCC defined by this
   * @version 5.4.1
   * 
   */
  public String getFqn() {
    if (fqn == null) {
      String mccPkg = ParserToolkit.getPackageDeclaration(ast);
      fqn = mccPkg + "." + swcName;
    }

    return fqn;
  }
}
