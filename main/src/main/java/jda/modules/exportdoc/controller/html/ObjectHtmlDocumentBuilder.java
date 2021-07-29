package jda.modules.exportdoc.controller.html;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import javax.script.ScriptEngine;
import javax.script.ScriptException;

import jda.modules.common.collection.CollectionToolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.util.ScriptingToolKit;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.Controller;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.view.assets.JDataContainer;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  To export a single object to an HTML document from a pre-defined HTML template. The template
 *  must be designed with variables that are mapped to the attribute names of the object's domain class. 
 *  
 * @author dmle
 */
public class ObjectHtmlDocumentBuilder extends BasicHtmlDocumentBuilder {

  /**a shared scripting engine used to evaluate scripts embedded in HTML documents or templates
   * @version 3.3
   * */
  public static ScriptEngine scriptEngine;

  public ObjectHtmlDocumentBuilder(DocumentExportController exportCtl,
      Configuration appConfig) throws NotPossibleException {
    super(exportCtl, appConfig);
  }

  @Override
  public void buildContent(DODMBasic dodm, JDataContainer dataContainer,
      PropertySet printCfg, DataDocument doc) throws NotPossibleException {
    Class docDataCls = printCfg.getPropertyValue("docDataClass", Class.class);

    DSMBasic dsm = dodm.getDsm();
    
    /*
     *  read the current object of the container AND simply insert its state into the document template
     *  if docDataCls is specified then use it to wrap the object for exporting  
     */
    
    try {
      // title table model (if any)
      //Dimension pgSize = doc.getPageSize(); //PAGE_SIZE;
      
      Object currObj = dataContainer.getController().getCurrentObject();
      Object dataObj;
      if (currObj != null) {
        if (docDataCls != null) {
          // create a wrapper object
          dataObj = dsm.newInstance(docDataCls, new Object[] {currObj});
        } else {
          dataObj = currObj;
        }
        
        buildPageContent(dodm, printCfg, doc, 
            dataContainer,  // v3.0
            dataObj);
      } else {
        // no object
      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_DOCUMENT,
          e, "Không thể tạo nội dung văn bản {0} cho {1}", doc.getName(), dataContainer.toString());
    }
  }
  
  /**
   * @param dataObj2 
   * @modifies doc
   * 
   * @effects 
   *  generate a Html page using {@link HtmlPage} as the template and containing the data in <tt>dataObj</tt>
   *  
   *  <br>Add this page to <tt>doc</tt> and to the object pool of <tt>dodm</tt>
   *  
   * @version 
   *  - 3.0: updated to support view-specific configuration (e.g. sorting) that is applied to the object state of dataObj 
   */
  private void buildPageContent(DODMBasic dodm,
      PropertySet printCfg, DataDocument doc,
      JDataContainer dataContainer,   // v3.0
      Object dataObj) throws DataSourceException, NotPossibleException, NotFoundException {

    // create a page from the template
    HtmlPage page = createSimplePageObject(htmlPageTempl);
    
    /* generate content by inserting dataObj's attribute values into the page  
     * support both normal and collection-type  attributes
     * For normal attribute: 
     *  simply replace their template names by the values
     * For collection-type attributes: 
     *  use the row template and use that to generate an HTML row for each object in the collection
     *  then combine all these HTML rows into one string and replace the attribute's template name with it    
     */
    
    DSMBasic dsm = dodm.getDsm();
    
    //TODO: support refAttribNames in containerCfg
    String[] refAttribNames = null;
    
    Map<DAttr,Object> attribVals = dsm.getAttributeValuesAsMap(dataObj, refAttribNames);
    DataController dataCtl = dataContainer.getController();
    
    if (attribVals != null) {
      DAttr attrib;
      String attribName;
      DAttr.Type attribType;
      Object dval, val;
      
      for (Entry<DAttr,Object> e: attribVals.entrySet()) {
        attrib = e.getKey();
        attribName = attrib.name();
        
        attribType = attrib.type();
        dval = e.getValue();
        if (!attribType.isCollection()) {
          // case (1): normal attribute
          // convert val AND support image type
          val = toHtmlFriendlyVal(dodm, dataContainer, attrib, dval);
        } else {
          val = buildCollectionObjectContent(dodm, dataCtl, printCfg, attrib, 
              (Collection)dval);
        }
        
        // write attribute val into page
        try {
          if (val != null) {
            page.setVar(attribName, val.toString());
          } else {
            page.setVar(attribName, "");
          }
        } catch (NotFoundException ex) {
          // ignore attributes that are not found in template
        }
      }
      
      // add pages to DODM
      DOMBasic dom = dodm.getDom();
      dom.addObject(page);
      
      doc.addPage(page); 
    } else {
      // no attribute vals found
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
          new Object[] {dataObj.getClass().getSimpleName()});
    }
  }

  /**
   * @requires dataCtl != null
   * @effects 
   *  write state of each domain object in <tt>attribVal</tt> into a <tt>StringBuffer</tt> 
   *  from a template specified in the print configuration of <tt>attrib</tt> and return it. 
   */
  private StringBuffer buildCollectionObjectContent(DODMBasic dodm, 
      final DataController dataCtl,
      final PropertySet containerPrintCfg,
      final DAttr attrib, 
      final Collection attribVal) throws NotFoundException, NotPossibleException {
    
    final Class domainCls = dataCtl.getDomainClass();
    final String attribName = attrib.name();
    final Class targetDomainCls = attrib.filter().clazz();
    
    PropertySet attribPrintCfg;
    
    // case (2): collection-typed attribute
    // read the HTML row template for this attribute
    if (containerPrintCfg != null)
      attribPrintCfg = containerPrintCfg.getExtension(attribName);
    else
      attribPrintCfg = null;
    
    // requires: print config for this attribute
    if (attribPrintCfg == null) {
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_PRINT_CONFIG_NOT_FOUND, 
          new Object[] {domainCls.getSimpleName(), attribName});
    }
    
    String attribTemplateFile = (String) attribPrintCfg.getPropertyValue(PropertyName.docTemplate, null);
    if (attribTemplateFile == null)
      throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_PRINT_TEMPLATE_NOT_FOUND, 
          new Object[] {domainCls.getSimpleName(), attribName});
    
    // read the template content from file
    StringBuffer attribTemplate = readTemplate(attribTemplateFile);

    // use the template to generate an HTML row for each object in the collection
    // then combine all these HTML rows into one string and replace the attribute's template name with it
    StringBuffer attribContent = new StringBuffer();
    StringBuffer objContent;

    // v3.0: Collection valObjs = (Collection) dval;
    Collection valObjs = null;
    
    // v3.0: check if target data controller is configured with openning all objects 
    // if so then use the objects in the browser buffer instead (e.g. in case sorting is used) 
    /*v3.1: support 'shadow' attribute
    DataController targetDctl = dataCtl.getChildController(attrib);
    */
    DataController targetDctl = dataCtl.getChildControllerWithShadowSupport(attrib);
    
    if (targetDctl != null && targetDctl.isOpenWithAllObjects()) {
      // sorting is applied: use the objects in the browser of the target child data controller
      /*v3.1: FIX NullPointerException bug by initialising valObjs to empty if no objects found 
      valObjs = Toolkit.createCollection(targetDctl.getObjectBuffer());
      */
      Iterator targetViewBuffer = targetDctl.getObjectBuffer(); 
      if (targetViewBuffer != null) {
        valObjs = CollectionToolkit.createCollection(targetViewBuffer);
      } else {
        valObjs = attribVal;  // should be empty
      }
    } else {
      // can use objects dval
      valObjs = attribVal;
    }
    
    // get the print configuration of the top-level module of the referenced domain type
    // NOTE: must use this and NOT the print config of the targetDctl.dataContainer b/c
    // the latter not have all the print field configs needed 
    // PropertySet printCfg = dataCtl.getDataContainer().getContainerPrintConfig(); 
    ControllerBasic targetCtl = Controller.lookUpPrimary(targetDomainCls);
    PropertySet targetPrintCfg = null;
    if (targetCtl != null)
      targetPrintCfg = targetCtl.getApplicationModule().getPrintConfig();
        
    for (Object valObj : valObjs) {
      objContent = buildObjectContent(dodm, targetDctl, targetPrintCfg, valObj, 
          attribPrintCfg, attribTemplate);
      attribContent.append(objContent).append(NL);
    }
    
    if (attribContent.length() == 0)
      return null;
    else
      return attribContent;
  }
  
  /**
   * @param attribPrintCfg 
   * @effects 
   *  write state of <tt>dataObj</tt> into a <tt>StringBuffer</tt> from template <tt>template</tt> and return it.
   *  
   *  <p>throws NotPossibleException if failed to build object content.
   */  
  private StringBuffer buildObjectContent(DODMBasic dodm,
      final DataController dataCtl, // nullable
      final PropertySet containerPrintCfg,  // nullable
      final Object dataObj, 
      PropertySet attribPrintCfg, final StringBuffer contentTemplate)  throws NotPossibleException {
    // check if a specific attribute(s) are configured
    PropertySet refAttribs = attribPrintCfg.getExtension("ref");
    String[] refAttribNames = null;
    if (refAttribs != null) {
      refAttribNames = refAttribs.getPropertyValue("attributes", String[].class, null);
    }
    
    /* generate content by inserting dataObj's attribute values into the page  
     * support both normal and collection-type  attributes
     * For normal attribute: 
     *  simply replace their template names by the values
     * For collection-type attributes: 
     *  use the row template and use that to generate an HTML row for each object in the collection
     *  then combine all these HTML rows into one string and replace the attribute's template name with it    
     */
    
    StringBuffer content = new StringBuffer(contentTemplate);

    DSMBasic dsm = dodm.getDsm();
    Map<DAttr,Object> attribVals = dsm.getAttributeValuesAsMap(dataObj, refAttribNames);
    
    JDataContainer dataContainer = (dataCtl != null) ? dataCtl.getDataContainer() : null;
    
    if (attribVals != null) {
      DAttr attrib;
      String attribName;
      DAttr.Type attribType;
      Object dval, val;
      
      for (Entry<DAttr,Object> e: attribVals.entrySet()) {
        attrib = e.getKey();
        attribName = attrib.name();
        
        attribType = attrib.type();
        dval = e.getValue();
        
        if (!attribType.isCollection()) {
          // case (1): normal attribute
          // convert val AND support image type
          val = toHtmlFriendlyVal(dodm, dataContainer, attrib, dval);
        } else {
          // v3.1: added check 
          if (dataCtl == null)
            throw new NotPossibleException(NotPossibleException.Code.NULL_POINTER_EXCEPTION, 
                new Object[] {DataController.class.getSimpleName(), "domain class: " + dataObj.getClass()});
          
          val = buildCollectionObjectContent(dodm, dataCtl, containerPrintCfg, attrib, 
              (Collection)dval);
        }
        
        try {
          if (val != null) {
            setHtmlVars(content, attribName, val.toString());
          } else {
            setHtmlVars(content, attribName, "");
          }
        } catch (NotFoundException ex) {
          // ignore attributes that are not found in template
        }
      }
      
      // v3.3: added support for post-processing (e.g. to evaluate embedded java script)
      postBuildObjectContent(dataObj, content);
      
      return content;
    } else {
      // no attribute vals found
      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
          new Object[] {dataObj.getClass().getSimpleName()});
    }
  }

  /**
   * @requires 
   *  any embedded Java scripts that require <tt>dataObj</tt> as input data must access it via a global 
   *  input variable named {@link ScriptingToolKit#GlobalScriptInputVarName} 
   *  
   * @modifies content 
   * @effects 
   *  Perform any post-processing needed for <tt>content</tt> that has been pre-processed for <tt>object</tt>
   *  <p>A typical post-processing task is to look for embedded script fragments (e.g. java script) and evaluate them
   *  
   *  <p>throws NotPossibleException if failed to post-build object content (e.g. by failing to execute 
   *  an embedded script)
   *   
   * @version 3.3
   */
  private void postBuildObjectContent(Object dataObj, StringBuffer content) throws NotPossibleException {
    // find embedded java scripts and evaluate them 
    Collection<String> scriptFrags = ScriptingToolKit.extractJjsFragments(content);
    
    if (scriptFrags != null) {
      // has scripts, evaluate them
      
      // scripting engine
      ScriptEngine engine = getScriptEngineInstance();
      
      Object evalResult;
      String scriptFragContent = null;
      for (String scriptFrag : scriptFrags) {
        try {
          scriptFragContent = ScriptingToolKit.extractJjsFragContent(scriptFrag);
          
          evalResult = ScriptingToolKit.evalJjsFragSingleOutput(engine, ScriptingToolKit.GlobalScriptInputVarName, dataObj, scriptFragContent);
        } catch (ScriptException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_EXECUTE_EMBEDDED_SCRIPT, e, 
              new Object[] {scriptFragContent, "domain = " + dataObj});
        }
        
        // replace scriptFrag in content with evalResult
        setHtmlContentKeyValue(content, scriptFrag, evalResult.toString());
      }
    }
  }

  /**
   * @effects 
   *  if {@link #scriptEngine} = null
   *    init it to be a default Java script scripting engine
   *  else
   *    return it 
   * @version 3.3
   */
  private static ScriptEngine getScriptEngineInstance() {
    if (scriptEngine == null) {
      scriptEngine = ScriptingToolKit.getNashornEngine();
    }
    
    return scriptEngine;
  }

// v3.0  
//  /**
//   * @param dataObj2 
//   * @modifies doc
//   * 
//   * @effects 
//   *  generate a Html page using {@link HtmlPage} as the template and containing the data in <tt>dataObj</tt>
//   *  
//   *  <br>Add this page to <tt>doc</tt> and to the object pool of <tt>dodm</tt>
//   *  
//   * @version 
//   *  - 3.0: updated to support view-specific configuration (e.g. sorting) that is applied to the object state of dataObj 
//   */
//  protected void buildObjectContent(DODMBasic dodm,
//      PropertySet printCfg, DataDocument doc,
//      JDataContainer dataContainer,   // v3.0
//      Object dataObj) throws DataSourceException, NotPossibleException, NotFoundException {
//    //Dimension pgSize = doc.getPageSize();
//
//    // create a page from the template
//    HtmlPage page = createSimplePageObject(htmlPageTempl);
//    
//    /* generate content by inserting dataObj's attribute values into the page  
//     * support both normal and collection-type  attributes
//     * For normal attribute: 
//     *  simply replace their template names by the values
//     * For collection-type attributes: 
//     *  use the row template and use that to generate an HTML row for each object in the collection
//     *  then combine all these HTML rows into one string and replace the attribute's template name with it    
//     */
//    
//    DSMBasic dsm = dodm.getDsm();
//    Map<DomainConstraint,Object> attribVals = dsm.getAttributeValuesAsMap(dataObj);
//    DataController dataCtl = dataContainer.getController();
//    DataController childDCtl;
//    
//    if (attribVals != null) {
//      DomainConstraint attrib;
//      String attribName;
//      DomainConstraint.Type attribType;
//      Object dval, val;
//      PropertySet printfCfg; // attribute's print config
//      String attribTemplateFile;
//      
//      for (Entry<DomainConstraint,Object> e: attribVals.entrySet()) {
//        attrib = e.getKey();
//        attribType = attrib.type();
//        attribName = attrib.name();
//        dval = e.getValue();
//        
//        if (!attribType.isCollection()) {
//          // case (1): normal attribute
//          // convert val AND support image type
//          val = toHtmlFriendlyVal(dodm, attrib, dval);
//        } else {
//          // case (2): collection-typed attribute
//          // read the HTML row template for this attribute
//          if (printCfg != null)
//            printfCfg = printCfg.getExtension(attribName);
//          else
//            printfCfg = null;
//          
//          // requires: print config for this attribute
//          if (printfCfg == null) {
//            throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_PRINT_CONFIG_NOT_FOUND, 
//                new Object[] {dataObj.getClass().getSimpleName(), attribName});
//          }
//          
//          attribTemplateFile = (String) printfCfg.getPropertyValue(PropertyName.docTemplate, null);
//          if (attribTemplateFile == null)
//            throw new NotFoundException(NotFoundException.Code.ATTRIBUTE_PRINT_TEMPLATE_NOT_FOUND, 
//                new Object[] {dataObj.getClass().getSimpleName(), attribName});
//          
//          // read the template content from file
//          StringBuffer attribTemplate = readTemplate(attribTemplateFile);
//
//          // use the template to generate an HTML row for each object in the collection
//          // then combine all these HTML rows into one string and replace the attribute's template name with it
//          StringBuffer attribContent = new StringBuffer();
//          StringBuffer row;
//
//          // v3.0: Collection valObjs = (Collection) dval;
//          Collection valObjs = null;
//          
//          // v3.0: check if view-specific configuration (e.g. sorting order) is applied to attrib
//          childDCtl = dataCtl.getChildController(attrib);
//          
//          if (childDCtl != null && childDCtl.isSortingOn()) {
//            // sorting is applied: use the objects in the browser of the target child data controller
//            valObjs = Toolkit.createCollection(childDCtl.getObjectBuffer());
//          } else {
//            // can use objects dval
//            valObjs = (Collection) dval;
//          }
//          
//          for (Object valObj : valObjs) {
//            row = buildObjectContent(dodm, valObj, attribTemplate);
//            attribContent.append(row).append(NL);
//          }
//          
//          if (attribContent.length() == 0)
//            val = null;
//          else
//            val = attribContent;
//        }
//        
//        // write attribute val into page
//        try {
//          if (val != null) {
//            page.setVar(attribName, val.toString());
//          } else {
//            page.setVar(attribName, "");
//          }
//        } catch (NotFoundException ex) {
//          // ignore attributes that are not found in template
//        }
//      }
//      
//      // add pages to DODM
//      DOMBasic dom = dodm.getDom();
//      dom.addObject(page);
//      
//      doc.addPage(page); 
//    } else {
//      // no attribute vals found
//      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
//          new Object[] {dataObj.getClass().getSimpleName()});
//    }
//  }
//
//  /**
//   * @effects 
//   *  write state of <tt>valObj</tt> into a <tt>StringBuffer</tt> from template <tt>template</tt> and return it. 
//   */
//  private StringBuffer buildObjectContent(DODMBasic dodm, Object valObj,
//      StringBuffer template) {
//    DSMBasic dsm = dodm.getDsm();
//    Map<DomainConstraint,Object> attribVals = dsm.getAttributeValuesAsMap(valObj);
//
//    StringBuffer content = new StringBuffer(template);
//    
//    if (attribVals != null) {
//      DomainConstraint attrib;
//      String attribName;
//      Object dval, val;
//      DomainConstraint.Type type;
//      
//      for (Entry<DomainConstraint,Object> e: attribVals.entrySet()) {
//        attrib = e.getKey();
//        attribName = attrib.name();
//        type = attrib.type();
//        dval = e.getValue();
//        
//        /* TODO: support both normal and collection-typed attributes */
//        // for collection-typed attributes: use a recursive function
//        // ASSUME: normal attribute
//        // convert val AND support image type
//        val = toHtmlFriendlyVal(dodm, attrib, dval);
//        try {
//          if (val != null) {
//            setHtmlVars(content, attribName, val.toString());
//          } else {
//            setHtmlVars(content, attribName, "");
//          }
//        } catch (NotFoundException ex) {
//          // ignore attributes that are not found in template
//        }
//      }
//      
//      return content;
//    } else {
//      // no attribute vals found
//      throw new NotPossibleException(NotPossibleException.Code.CLASS_NOT_WELL_FORMED, 
//          new Object[] {valObj.getClass().getSimpleName()});
//    }
//    
//  }
}
