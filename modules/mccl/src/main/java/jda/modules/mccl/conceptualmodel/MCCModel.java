package jda.modules.mccl.conceptualmodel;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.github.javaparser.ast.body.FieldDeclaration;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.parser.ClassAST;
import jda.modules.dcsl.parser.Dom;
import jda.modules.dcsl.parser.SourceModel;
import jda.modules.mccl.util.DomainConstants;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class MCCModel {
  
  private static final boolean debug = Toolkit.getDebug(MCCModel.class);
  
  /** all the {@link MCC}s mapped by FQNs of their corresponding {@link ClassAST} */
  private Map<String, MCC> mccMap;

  // v5.4.1: 
  //private SourceModel sourceModel;
//  public MCCModel(SourceModel sourceModel) {
//    this.sourceModel = sourceModel;
//    mccMap = new HashMap<>();
//  }
  private Dom sourceModel;

  public MCCModel(Dom sourceModel) {
    this.sourceModel = sourceModel;
    mccMap = new HashMap<>();
  }
  
  /**
   * @effects 
   *  Generates and return an {@link MCC} that reflects the domain class 
   *  whose FQN is <tt>pkgName + className</tt> and 
   *  whose source file is <tt>javaSrcFile</tt>.
   *  
   *  <p>The {@link MCC} is named <tt>'Module' + className</tt> and imports the necessary domain-specific packages <tt>domainPkgs</tt>.
   *  
   *  <p>If succeeds, writes the {@link MCC} to a file at the designated <tt>mccOutputRootDir</tt>.
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClass</tt> or if this class has 
   *  no domain attributes;<br>
   *  NotPossibleException if failed to create a method or failed to add a method to the class 
   *   
   * @version 
   * - 1.0<br>
   * - 5.2c: improved to support MCC of sub-class<br>
   * - 5.4.1: added mccPkgName
   */
  public MCC genMCC( 
      String pkgName, String className, String javaSrcFile, 
      String mccPkgName,
      String mccOutputRootDir) throws NotFoundException, NotPossibleException {
    ClassAST dcls = sourceModel.loadClassIfNotExists(pkgName, className, javaSrcFile); // v5.2c: new ClassAST(className, javaSrcFile);
    
    // v5.2c:
    ClassAST superCls = dcls.getSuperClass(sourceModel);
    boolean isSubCls = (superCls != null);
    
    // create m's header
    String mccName = getModuleName(className);
    MCC m = new MCC(mccName, dcls);

    if (isSubCls) {
      // dcls is a sub-class: look up MCC of super class and make it the super class of dcls's MCC
      String supName = superCls.getName(), supPkg = superCls.getPackageDeclaration();
      String supMCCPkg = getSuperModulePkg(supPkg, mccPkgName); // v5.4.1
      String supMCCName = getModuleName(supName);
      String supMCCFqn = supMCCPkg + "." + supMCCName;
      // TODO ? (genMCC): use Class.forName here to check?
      
      m.addImport(supMCCFqn);
      
      m.setSuperType(supMCCName);
    }

    // create m's ModuleDesc
    m.createModuleDesc();
    
    // create m's view fields (i.e. view field configs)
    m.createViewFields();
    
    // set mcc's package
    String dclsPkg = dcls.getPackageDeclaration();
    String mccPkg = getModulePkg(dclsPkg, mccPkgName);
    
    /* moved to method
    String mccPkg;
    if (dclsPkg != null) {
      mccPkg = dclsPkg.substring(0, dclsPkg.lastIndexOf(".")) // exclude ".model" 
                    + "." + "modules";                        // replace it by ".modules"
    } else {
      // no package
      mccPkg = "modules";
    }
    */
    
    m.setPackageName(mccPkg);
    
    // write m to file
    m.save(mccOutputRootDir);
    
    // add m to this
    mccMap.put(dcls.getFqn(), m);
    
    if (debug)
      System.out.println(m);
    
    return m;  
  }
  
  /**
   * @effects 
   *  Generates and return an {@link MCC} that reflects the domain class 
   *  whose FQN is <tt>pkgName + className</tt> and 
   *  whose source file is <tt>javaSrcFile</tt>.
   *  
   *  <p>The {@link MCC} is named <tt>'Module' + className</tt> and imports the necessary domain-specific packages <tt>domainPkgs</tt>.
   *  
   *  <p>If succeeds, writes the {@link MCC} to a file at the designated <tt>mccOutputRootDir</tt>.
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClass</tt> or if this class has 
   *  no domain attributes;<br>
   *  NotPossibleException if failed to create a method or failed to add a method to the class 
   *   
   * @version 
   * - 1.0<br>
   * - 5.2c: improved to support MCC of sub-class<br>
   */
  public MCC genMCC( 
      String pkgName, String className, String javaSrcFile, String mccOutputRootDir) throws NotFoundException, NotPossibleException {
    
    // v5.4.1: redirect
    return genMCC(pkgName, className, javaSrcFile, null, mccOutputRootDir);
    
//    ClassAST dcls = sourceModel.loadClass(pkgName, className, javaSrcFile); // v5.2c: new ClassAST(className, javaSrcFile);
//    
//    // v5.2c:
//    ClassAST superCls = dcls.getSuperClass(sourceModel);
//    boolean isSubCls = (superCls != null);
//    
//    // create m's header
//    String mccName = getModuleName(className);
//    MCC m = new MCC(mccName, dcls);
//
//    if (isSubCls) {
//      // dcls is a sub-class: look up MCC of super class and make it the super class of dcls's MCC
//      String supName = superCls.getName(), supPkg = superCls.getPackageDeclaration();
//      String supMCCPkg = getModulePkg(supPkg);
//      String supMCCName = getModuleName(supName);
//      String supMCCFqn = supMCCPkg + "." + supMCCName;
//      // TODO ? (genMCC): use Class.forName here to check?
//      
//      m.addImport(supMCCFqn);
//      
//      m.setSuperType(supMCCName);
//    }
//
//    // create m's ModuleDesc
//    m.createModuleDesc();
//    
//    // create m's view fields (i.e. view field configs)
//    m.createViewFields();
//    
//    // set mcc's package
//    String dclsPkg = dcls.getPackageDeclaration();
//    String mccPkg = getModulePkg(dclsPkg);
//    
//    /* moved to method
//    String mccPkg;
//    if (dclsPkg != null) {
//      mccPkg = dclsPkg.substring(0, dclsPkg.lastIndexOf(".")) // exclude ".model" 
//                    + "." + "modules";                        // replace it by ".modules"
//    } else {
//      // no package
//      mccPkg = "modules";
//    }
//    */
//    
//    m.setPackageName(mccPkg);
//    
//    // write m to file
//    m.save(mccOutputRootDir);
//    
//    // add m to this
//    mccMap.put(dcls.getFqn(), m);
//    
//    if (debug)
//      System.out.println(m);
//    
//    return m;
  }

  /**
   * @requires 
   *  the {@link MCC} corresponding to <tt>domainClsFQN</tt> is in this.
   *  
   * @effects 
   *  Handle the event that new domain fields (<tt>newFields</tt>) of the domain class <tt>domainClsFQN</tt> have
   *  been added. 
   *  
   *  <p>Update the {@link MCC} corresponding to <tt>domainClsFQN</tt> in this and other {@link MCC}s
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClsFQN</tt> or if this class has 
   *  no domain attributes or no domain attributes with the specified names;<br>
   *  NotPossibleException if failed to create a method or failed to add a method to the class 
   *   
   * @version 1.0
   */
  public void onAddDomainFields(
      String domainClsFQN, /** FQN of the domain class */
      Collection<String> newFields /** new domain fields of domain class */
      ) throws NotFoundException, NotPossibleException {
    // retrieve dcls and mcc from this
    MCC m = mccMap.get(domainClsFQN);

    if (m == null) {
      throw new NotFoundException(DomainConstants.DomainMesg.MCC_OF_DOMAIN_CLASS_NOT_FOUND, new Object[] {domainClsFQN});
    }
    
    ClassAST dcls = m.getDomainClass();
    
    // get new domain fields in dcls
    Collection<FieldDeclaration> newDFields = dcls.getDomainFieldsByName(newFields);
    
    if (newFields == null) {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {newFields.toString(), dcls.getName()});
    }
    
    m.addViewFields(newDFields);
    
    // write mcc back to file
    if (debug) System.out.printf("%s: new view fields added -> saving changes...%n", m.getName());

    m.save();
    
    onAddDomainFields_UpdateScopes(dcls, newFields);
  }
  
  /**
   * Invoked by {@link #onAddDomainFields(String, Collection)}
   * 
   * @effects 
   *  add to the scopes of the MCCs of this references to each new view field name in <tt>newFields</tt>.
   */
  private void onAddDomainFields_UpdateScopes(ClassAST c,
      Collection<String> newFields) {

    String dclsName; MCC m;
    boolean updated, scopeChanged;
    for (Entry<String, MCC> e : mccMap.entrySet()) {
      dclsName = e.getKey();
      m = e.getValue();
      updated = false;
      if (m.isModelMatching(c)) {
        // m.modelDesc.model = c
        updated = m.addToStateScope(c, newFields);
      } else {
        // m.modelDesc.model != c
        // see if any Child matches c
        updated = m.addToContainmentScopes(c, newFields);
      }
      
      if (updated) {
        // save changes
        if (debug) System.out.printf("%s: scopes changed -> saving changes...%n", m.getName());
        
        m.save();
      }
    }
  }

  /**
   * @requires 
   *  the {@link MCC} corresponding to <tt>domainClsFQN</tt> is in this /\
   *  <tt>updatedFieldsNameMap</tt> map new-field-name -> old-field-name in domain class <tt>domainClsFQN</tt>
   *  
   * @effects 
   *  Handle the event that some existing domain fields of the domain class <tt>domainClsFQN</tt> have
   *  been updated with new names and/or new data types. 
   *  
   *  <p>Update the {@link MCC} corresponding to <tt>domainClsFQN</tt> in this and other {@link MCC}s
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClsFQN</tt> or if this class has 
   *  no domain attributes;<br>
   *  NotPossibleException if failed to create a method or failed to add a method to the class 
   *   
   * @version 1.0
   */
  public void onUpdateDomainFields(
      String domainClsFQN, /** FQN of the domain class */
      Map<String,String> updatedFieldsNameMap /** map newFieldName -> oldFieldName of domain class */
      ) throws NotFoundException, NotPossibleException {
    // retrieve dcls and mcc from this
    MCC m = mccMap.get(domainClsFQN);

    if (m == null) {
      throw new NotFoundException(DomainConstants.DomainMesg.MCC_OF_DOMAIN_CLASS_NOT_FOUND, new Object[] {domainClsFQN});
    }
    
    ClassAST dcls = m.getDomainClass();
    
    // update m
    // get updated domain fields in dcls
    Collection<String> newNames = updatedFieldsNameMap.keySet();
    Collection<FieldDeclaration> updatedFields = dcls.getDomainFieldsByName(newNames);
    
    if (updatedFields == null) {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {newNames, dcls.getName()});
    }
    
    m.updateViewFields(updatedFields, updatedFieldsNameMap);
    
    // write mcc back to file
    if (debug) System.out.printf("%s: View fields updated -> saving changes...%n", m.getName());

    m.save();
    
    onUpdateDomainFields_UpdateScopes(dcls, updatedFields, updatedFieldsNameMap);
  }
  
  /**
   * Invoked by {@link #onUpdateDomainFields(String, Map)}
   * 
   * @requires 
   *  <tt>updatedFieldsNameMap</tt> map new-field-name -> old-field-name of <tt>updatedFields</tt>
   * @effects 
   *  update references in the scopes of the MCCs of this to each view field name in <tt>updatedFields   
   */
  private void onUpdateDomainFields_UpdateScopes(ClassAST c,
      Collection<FieldDeclaration> updatedFields,
      Map<String, String> updatedFieldsNameMap) {
    String dclsName; MCC m;
    boolean updated, scopeChanged;
    for (Entry<String, MCC> e : mccMap.entrySet()) {
      dclsName = e.getKey();
      m = e.getValue();
      updated = false;
      if (m.isModelMatching(c)) {
        // m.modelDesc.model = c
        updated = m.updateStateScope(c, updatedFieldsNameMap);
      } else {
        // m.modelDesc.model != c
        // see if any Child matches c
        updated = m.updateContainmentScopes(c, updatedFieldsNameMap);
      }
      
      if (updated) {
        // save changes
        if (debug) System.out.printf("%s: scopes changed -> saving changes...%n", m.getName());
        
        m.save();
      }
    }
  }

  /**
   * @requires 
   *  the {@link MCC} corresponding to <tt>domainClsFQN</tt> is in this.
   *  
   * @effects 
   *  Handle the event that some domain fields (<tt>delFields</tt>) of the domain class <tt>domainClsFQN</tt> have
   *  been deleted. 
   *  
   *  <p>Update the {@link MCC} corresponding to <tt>domainClsFQN</tt> in this and other {@link MCC}s
   *  
   *  <p>Throws NotFoundException if failed to obtain a handle for <tt>domainClsFQN</tt> or if this class has 
   *  no domain attributes or no domain attributes with the specified names;<br>
   *  NotPossibleException if failed to change the class or an MCC for some reasons. 
   *   
   * @version 1.0 
   */
  public void onDeleteDomainFields(String domainClsFQN,
      Collection<String> delFields) throws NotFoundException, NotPossibleException {
    // retrieve dcls and mcc from this
    MCC m = mccMap.get(domainClsFQN);

    if (m == null) {
      throw new NotFoundException(DomainConstants.DomainMesg.MCC_OF_DOMAIN_CLASS_NOT_FOUND, new Object[] {domainClsFQN});
    }
    
    ClassAST dcls = m.getDomainClass();
    
//    // get new domain fields in dcls
//    Collection<FieldDeclaration> newDFields = dcls.getDomainFieldsByName(newFields);
//    
//    if (newFields == null) {
//      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_NOT_FOUND, new Object[] {newFields.toString(), dcls.getName()});
//    }
//    
//    m.addViewFields(newDFields);
    
    // remove the corresponding view fields in m
    m.deleteViewFields(delFields);
    
    // write mcc back to file
    if (debug) System.out.printf("%s: view fields deleted -> saving changes...%n", m.getName());

    m.save();
    
    onDeleteDomainFields_UpdateScopes(dcls, delFields);
  }
  
  /**
   * Invoked by {@link #onDeleteDomainFields(String, Collection)}
   * 
   * @effects 
   *  remove from the scopes of the MCCs of this references to each view field name in <tt>delFields</tt>.
   */
  private void onDeleteDomainFields_UpdateScopes(ClassAST c,
      Collection<String> delFields) {

    String dclsName; MCC m;
    boolean updated, scopeChanged;
    for (Entry<String, MCC> e : mccMap.entrySet()) {
      dclsName = e.getKey();
      m = e.getValue();
      updated = false;
      if (m.isModelMatching(c)) {
        // m.modelDesc.model = c
        updated = m.removeFromStateScope(c, delFields);
      } else {
        // m.modelDesc.model != c
        // see if any Child matches c
        updated = m.removeFromContainmentScopes(c, delFields);
      }
      
      if (updated) {
        // save changes
        if (debug) System.out.printf("%s: scopes changed -> saving changes...%n", m.getName());
        
        m.save();
      }
    }
  }
  
  /**
   * @effects 
   *  adds to {@link #mccMap} (c,m), where <tt>c = dcls.getFqn()</tt.
   */
  private void put(ClassAST dcls, MCC m) {
    mccMap.put(dcls.getFqn(), m);
  }
  
  /**
   * @effects 
   *  return the MCC's name generated from <tt>className</tt> of the domain class.
   */
  private String getModuleName(String className) {
    return "Module" + className;
  }


  /**
   * @requires dclsPkg is the package of dcls
   * @effects 
   *  return FQN of the package of the MCC of the domain class <tt>dclsPkg</tt>
   * @version 
   * - 5.2: created<br>
   * - 5.4.1: added mccPkgName
   */
  private String getModulePkg(String dclsPkg, String mccPkgName) {
    
    if (mccPkgName != null) { // mccPkgName is specified, use it
      return mccPkgName;
    } else { // mccPkgName is NOT specified, use a name relative to dclsPkg
      String mccPkg;
      if (dclsPkg != null) {
        String parent = dclsPkg.substring(0, dclsPkg.lastIndexOf("."));
        mccPkg = parent+"." + "modules";

//        mccPkg = dclsPkg.substring(0, dclsPkg.lastIndexOf(".")) // exclude ".model" 
//                      + "." + "modules";                        // replace it by ".modules"
      } else {
        // no package
        mccPkg = "modules";
      }
      
      
      return mccPkg;
      
    }
  }
  
  private String getSuperModulePkg(String dclsPkg, String mccPkgName) {
    
    if (mccPkgName != null) { // mccPkgName is specified, use it
      return mccPkgName;
    } else { // mccPkgName is NOT specified, use a name relative to dclsPkg
      return getModulePkg(dclsPkg, null);
    }
  }
  
  /**
   * @param srcModel 
   * @requires 
   *  <tt>mccMap = [ [c1,m1],...[cn,mn] ]</tt> where 
   *  <tt>ci</tt> is FQN of a domain class and 
   *  <tt>mi</tt> is FQN of the corresponding module class
   * @effects 
   * 
   */
  public static MCCModel load(SourceModel srcModel, Path rootSrcPath, String[][] mccMap) throws IllegalArgumentException {
    if (mccMap == null || mccMap.length == 0) return null;

    MCCModel model = null;
    
    String c, m;
    for (String[] mccEntry : mccMap) {
      if (mccEntry.length != 2)
        throw new IllegalArgumentException("Invalid MCC map input (expecting a 2-element array): " + Arrays.toString(mccEntry));
      
      c = mccEntry[0];
      m = mccEntry[1];
      
      String[] cels = c.split("\\.");
      String cname = cels[cels.length-1];
      String[] mels = m.split("\\.");
      String mname = mels[mels.length-1];
      
      String rootSrcPathStr = rootSrcPath.toString();
      Path dclsFile = ToolkitIO.getPath(rootSrcPathStr, cels);
      Path mccFile = ToolkitIO.getPath(rootSrcPathStr, mels);
      
      ClassAST dcls = new ClassAST(cname, dclsFile.toString() + ToolkitIO.FILE_JAVA_EXT);
      MCC mcc = new MCC(mname, mccFile.toString() + ToolkitIO.FILE_JAVA_EXT, dcls);
      
      if (model == null) model = new MCCModel(srcModel);
      
      model.put(dcls, mcc);
    }
    
    return model;
  }

  /**
   * @effects 
   *  if this contains an MCC named <tt>fqn</tt>
   *    remove it
   *  else
   *    do nothing 
   */
  public void removeMCC(String fqn) {
    mccMap.remove(fqn);
  }

//   v5.4.1
//  /**
//   * @effects 
//   *  if {@link #sourceModel} is initialised
//   *    return it
//   *  else
//   *    return null
//   */
//  public SourceModel getSourceModel() {
//    return sourceModel;
//  }
}
