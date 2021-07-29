package jda.modules.jdatool.modulegen;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.mdkt.compiler.InMemoryJavaCompiler;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.common.types.Tuple2;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.report.Output;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.mccl.conceptmodel.module.ModuleType;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.extra.Hidden;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JListField;

/**
 * 
 * @overview 
 *  Generator of a module class. Logically, basically represents a generator function that takes 
 *  as input three parameters: (1) a domain class, (2) a {@link ModuleDescGen}, and (3) a 
 *  {@link AttributeDescGen}, and produce as output a {@link Class} that serves as a module 
 *  class. 
 *  
 *  <p>This module class is named <tt>ModuleC</tt>, where <tt>C</tt> is the name of the domain class, 
 *  has a {@link ModuleDescriptor} annotated to the class header and a {@link AttributeDesc} annotated 
 *  to each of its fields. A field is either a <tt>title</tt> field or a field that is mapped to 
 *  a domain attribute of the domain class. 
 *  
 * @author dmle
 *
 * @version 3.2
 */
public class ModuleClassGen {
  /**
   * @overview 
   *
   * @author dmle
   */
  private static class CodeSegment {
    
    private static final String charSetName = "UTF-8";

    private StringBuffer buffer;
    
    /**
     * Cache file content that has been loaded
     */
    private static Map<String,Collection<String>> fileCache;
    
    public CodeSegment() {
      buffer = new StringBuffer();
    }
    
    public CodeSegment append(String...items) {
      for (String item : items) {
        buffer.append(item);
      }
      
      return this;
    }

    /**
     * @modifies this
     * @effects 
     *  add each {@link CodeSegment} in <tt>codeSegs</tt> to end of this
     */
    public void append(CodeSegment...codeSegs) {
      for (CodeSegment codeSeg : codeSegs)
        buffer.append(codeSeg.buffer);
    }

    /**
     * @modifies this
     * @effects 
     *  Read source code lines from file <tt>inputFile</tt>, which is stored relative
     *  to this class, and adds them to this
     */
    public void load(String inputFile, Tuple2<String,String>...paramValPairs) throws NotPossibleException {
      
      // cache the file content to reduce overhead
      if (fileCache == null) fileCache = new HashMap();
      
      Collection<String> lines = fileCache.get(inputFile);
      
      if (lines == null) {
        try {
          lines = ToolkitIO.readTextFileWithEncoding(ToolkitIO.getFileInputStream(ModuleClassGen.class, inputFile), charSetName);
          
          fileCache.put(inputFile, lines);
        } catch (IOException e) {
          throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_READ_FILE, e, new Object[] {inputFile});
        }
      }
      
      if (lines != null) {
        for (String line : lines) {
          append(line, NL);
        }
        
        String param, val;
        if (paramValPairs != null) {
          // parameter, val pairs are specified: replace them in the source code
          for (Tuple2<String,String> paramValPair: paramValPairs) {
            param = paramValPair.getFirst();
            val = paramValPair.getSecond();
            setVar(buffer, param, val);
          }
        }
      }

    }
    
    /**
     * @effects 
     *  replaces all occurrences of variable whose name is <tt>name</tt> in <tt>content</tt> with <tt>val</tt>;
     *  
     *  throws NotFoundException if no such variable is found
     */
    private void setVar(StringBuffer content, String name, String val) throws NotFoundException {
      //String varName = "{"+name+"}";
      
      int pos;
      int count = 0;
      do {
        pos = content.indexOf(name);
        if (pos < 0 && count == 0) 
          throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, new Object[] {name});
        
        if (pos >-1) content.replace(pos, pos+name.length(), val);
        count++;
      } while (pos > -1);
    }
    
    /**
     * @effects 
     *  replaces all occurrences of variable whose name is <tt>name</tt> in <tt>this.buffer</tt> with <tt>val</tt>;
     *  
     *  throws NotFoundException if no such variable is found
     */
    public void setVar(String name, String val) throws NotFoundException {
      setVar(buffer, name, val);
    }
    
    /**
     * @effects 
     *  clear this
     */
    public void clear() {
      buffer.delete(0, buffer.length());
    }

    @Override
    public String toString() {
      return buffer.toString();
    }
  }  /**end {@link CodeSegment} */
  
  /** endof source code line, i.e. <tt>"; \n"</tt>*/
  private static final String EOL = "; \n";
  private static final String NL = "\n";

  private static final String File_Standard_Import = "StandardImport.temp";
  private static final String File_Module_Descriptor = "ModuleDescriptor_Functional.temp";
  private static final String File_Module_Main_Descriptor= "ModuleDescriptor_Main.temp";
  private static final String File_Attrib_Descriptor = "AttributeDescriptor.temp";
  
  private static final boolean debug = Toolkit.getDebug(ModuleClassGen.class);
  
  // v5.4:
  private static InMemoryJavaCompiler javaCompiler;
  static {
    System.out.println("Initialising " + ModuleClassGen.class);
    javaCompiler = InMemoryJavaCompiler.newInstance();
//    javaCompiler.useParentClassLoader(ModuleClassGen.class.getClassLoader());
  }
  
  private DODMBasic dodm;
  
  /**
   * A source code buffer that is progressively used by {@link #generateModuleClass()} to generate
   * {@link #moduleClass} 
   */
  private CodeSegment moduleClassSeg;
  private CodeSegment packageSeg;
  private CodeSegment importSeg;
  private CodeSegment moduleDescSeg;
  private CodeSegment classHeaderSeg;
  private CodeSegment classBodySeg;

  /** output of {@link #generateModuleClass()} */
  private Class moduleClass;

  public ModuleClassGen(DODMBasic dodm) throws NotPossibleException {
    this.dodm = dodm;
  }
  
  /**
   * @effects 
   *  A specific generator for <b>main</b> module class of the application.
   *  
   *  <p>This differs from other module classes in that it does not need a domain class.
   */
  public Class generateMainModuleClass(final String appName, String packageName) throws NotPossibleException {
    String moduleClassName = "Module"+appName;
    String moduleTitle = appName;

    return generateModuleClass(null, ModuleType.DomainMain, appName, moduleTitle, moduleClassName, packageName);
  }

  /**
   * @effects 
   *  create and return a {@link Class} that defines the module class for {@link #domainCls}, that is 
   *  named after this domain class and that is placed in the package named <tt>packageName</tt>
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  public final Class generateFunctionalModuleClass(final Class domainCls, 
      final String packageName) throws NotPossibleException {
    String domainClsSimpleName = domainCls.getSimpleName();
    String moduleClassName = "Module"+domainClsSimpleName;
    String moduleName = moduleClassName;
    String moduleTitle = "Module: " + domainClsSimpleName;
    return generateModuleClass(domainCls, ModuleType.DomainData, 
        moduleName, moduleTitle, moduleClassName, packageName);
  }

  /**
   * @effects 
   *  create and return a {@link Class} that defines the module class for {@link #domainCls}, that is 
   *  named after this domain class and that is placed in the package named <tt>packageName</tt>
   *  
   *  <p>Throws NotPossibleException if fails.
   */
  private final Class generateModuleClass(final Class domainCls, 
      final ModuleType moduleType, 
      final String moduleName,
      final String moduleTitle,
      final String moduleClassName, final String packageName) throws NotPossibleException {
    // data validation
    Map<Field,DAttr> attribs = null;
    if (domainCls != null) {
      DSMBasic dsm = dodm.getDsm();
      // cannot use dsm.getDomainAttributes() here because domainCls is not yet registered!
      attribs = dsm.getAnnotatedFieldsNoDups(domainCls, DSMBasic.DC);
      if (attribs == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_DOMAIN_ATTRIBUTES_IN_DOMAIN_CLASS,
            new Object[] {domainCls.getName()});
      }
    }
    
    // reset resources
    reset();
    
    // the generator phases:
    
    /* (1): gen module class header */
    String moduleClassFQName = packageName+"." + moduleClassName;
    createModuleClass(domainCls, packageName, moduleClassName);
    
    /* (2): gen module descriptor */
    addModuleDescriptor(domainCls, moduleType, moduleName, moduleTitle);

    // 
    boolean hasStateEventSourceField = false; // 5.3
    if (domainCls != null) {
      /* (3): gen module class body */
      // the title field
      addViewAttributeForTitle(domainCls);
      
      // other fields for non-main modules
      //for (Field attrib: attribs) {
      for (Entry<Field,DAttr> entry : attribs.entrySet()){
        Field attrib = entry.getKey();
        DAttr dc = entry.getValue();
        boolean result = addViewAttributeFor(domainCls, attrib, dc);
        if (!hasStateEventSourceField && result == true) 
          hasStateEventSourceField = true;
      }      
    }
    
    // v5.3: post-processing moduleDescSeg based on result of classBodySeg
    if (!moduleType.equals(ModuleType.DomainMain)) {
      boolean isDataFieldStateListener = hasStateEventSourceField;
      moduleDescSeg.setVar("{isDataFieldStateListener}", isDataFieldStateListener+"");
    }
    
    /* (4) */
    // merge all segments
    moduleClassSeg.append(
        packageSeg, importSeg,  
        moduleDescSeg,
        classHeaderSeg.append("{\n"),
          classBodySeg.append("\n}")
        );
    
    if (debug)
      System.out.println("\n===>  Module class: \n" + moduleClassSeg);
    
    moduleClass = compileClass(moduleClassFQName, moduleClassSeg.toString());
    
    return moduleClass;
  }
  
  /**
   * @effects 
   *  initialise the declaration of {@link #moduleClass} using  
   *  the source code lines for class name <tt>moduleClassName</tt> and package <tt>packageName</tt>
   */
  private void createModuleClass(final Class domainCls, String packageName, String moduleClassName) throws NotPossibleException {
    packageSeg.append("package ", packageName, EOL);
    importSeg.load(File_Standard_Import);

    // add import for the domain class (if specified)
    //String domainPkg = domainCls.getPackage().getName();
    if (domainCls != null) {
      String domainClsName = domainCls.getName();
      importSeg.append("import ", domainClsName, EOL);
    }
    
    classHeaderSeg.append("public class ", moduleClassName);
  }
  
  /**
   * @effects 
   *  initialise {@link #moduleDescSeg} using {@link #domainCls} as the model 
   *  @version 
   *  - 5.1c: added support for ControllerDesc.props
   */
  private void addModuleDescriptor(final Class domainCls, final ModuleType moduleType, final String moduleName, final String moduleTitle) {
    String moduleDescFile;
    if (moduleType.equals(ModuleType.DomainMain)) {
      // main module
      moduleDescFile = File_Module_Main_Descriptor;
      
      moduleDescSeg.load(moduleDescFile, 
          Tuple2.newTuple2("{moduleName}", moduleName), 
          Tuple2.newTuple2("{moduleTitle}", moduleTitle)); 
    } else {
      // functional module
      moduleDescFile = File_Module_Descriptor;
      
      // 2 cases: abstract vs. non-abstract
      /* v5.2: FIXED this to use DClass.editable
      boolean isAbstract = (domainCls != null) ? DSMBasic.isAbstract(domainCls) : false;
      */
      boolean isDomainClsEditable = (domainCls != null) ? DSMBasic.isEditable(domainCls) : false; 
          
      boolean modelEditable = true; // default 
      if (//v5.2: isAbstract
          !isDomainClsEditable
          ) {
        // make editable=false
        modelEditable = false;
      }
      /* v5.2: removed
      // v5.1c: added support for DClass(domainCls).mutable
//      else {
//        modelEditable = DSMBasic.isEditable(domainCls);
//      }
      */
      
      // v5.1c: support ControllerDesc.props
      String controllerProps = MCCLConstants.DEFAULT_CONTROLLER_DESC_PROPS_AS_STRING;
      
      if (hasManyManyAssociations(domainCls)) {
        // v5.1c: add actions that handle many-many associations
        controllerProps = "{\n" + 
            "      // custom Create object command: to create {@link Enrolment} from the course modules\n" + 
            "      @PropertyDesc(name=PropertyName.controller_dataController_create,\n" + 
            "          valueIsClass=domainapp.controller.datacontroller.command.manyAssoc.CreateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,\n" + 
            "          valueType=Class.class),\n" + 
            "      // custom Update object command: to update {@link Enrolment} from the course modules\n" + 
            "      @PropertyDesc(name=PropertyName.controller_dataController_update,\n" + 
            "          valueIsClass=domainapp.controller.datacontroller.command.manyAssoc.UpdateObjectAndManyAssociatesDataControllerCommand.class, valueAsString=CommonConstants.NullValue,\n" + 
            "          valueType=Class.class)\n" + 
            "    }";
      }
        
      moduleDescSeg.load(moduleDescFile, 
          Tuple2.newTuple2("{moduleName}", moduleName), 
          Tuple2.newTuple2("{moduleTitle}", moduleTitle), 
          Tuple2.newTuple2("{domainCls}", domainCls.getSimpleName()), 
          Tuple2.newTuple2("{editable}", modelEditable+"")
          // v5.1c:
          ,Tuple2.newTuple2("{controllerProps}", controllerProps) 
          );
      
    }
  }

  /**
   * @effects 
   *  add view attribute for the title field of module class 
   * @version 
   * - 5.0: added support for property visible
   */
  private void addViewAttributeForTitle(final Class domainCls) {
    String objectFormTitle = domainCls.getSimpleName();
    int width = MCCLConstants.DEFAULT_FIELD_WIDTH, height = MCCLConstants.DEFAULT_FIELD_HEIGHT;

    addViewAttributeFor(String.class, "title", objectFormTitle, MCCLConstants.DEFAULT_DISPLAY_CLASS, 
        false, width, height, true, MCCLConstants.DEFAULT_CONTROLLER_DESC_AS_STRING,
        // v5.1c:
        MCCLConstants.DEFAULT_SELECT_AS_STRING, 
        MCCLConstants.DEFAULT_LOAD_OID_WITH_BOUND_VALUE,
        MCCLConstants.DEFAULT_DISPLAY_OID_WITH_BOUND_VALUE,
        MCCLConstants.DEFAULT_IS_STATE_EVENT_SOURCE
        );
  }
  
  /**
   * @requires <tt>attrib</tt> is a domain attribute of <tt>domainCls</tt>
   * @effects 
   *  add to {@link #classBodySeg} an {@link AttributeDesc}-annotated field definition for <tt>attrib</tt>,
   *  the <tt>label<tt> of whose annotation is the same as <tt>attrib.name</tt>
   *  
   * @version 
   * - 3.3: to ignore virtual attributes <br>
   * - 5.0: added support for properties: {@link AttributeDesc}.visible,type<br>
   * - 5.1c: added support for many-many associative attribute <br>
   * - 5.3: return boolean (whether or not this attribute is a state-event-source (e.g. combofield)
   */
  private boolean addViewAttributeFor(Class domainCls, Field attrib, DAttr dc) {
    Class dataType = attrib.getType();
    String attribName = attrib.getName();
    String label = attribName;
    // v5.0: use the default display class
    Class displayCls = MCCLConstants.DEFAULT_DISPLAY_CLASS;  
    String controllerDesc = MCCLConstants.DEFAULT_CONTROLLER_DESC_AS_STRING;
    // end 5.0
    
    // v5.1c: 
    String ref = MCCLConstants.DEFAULT_SELECT_AS_STRING; 
    boolean isStateEventSource = MCCLConstants.DEFAULT_IS_STATE_EVENT_SOURCE;
    // end 5.1c
    
    
    // v5.3: support bound attributes
    // e.g. "@Select(clazz=CourseModule.class, attributes={\"code\"})"
    boolean loadOidWithBoundValue = MCCLConstants.DEFAULT_LOAD_OID_WITH_BOUND_VALUE, 
            displayOidWithBoundValue = MCCLConstants.DEFAULT_DISPLAY_OID_WITH_BOUND_VALUE;
    
    if (dc.type().isDomainType()) {
      // if the associate class has candidate identifier then pick one and use the attribute(s)
      // of this identifier as bound attribute
      String[] candIdAttribNames = dodm.getDsm().getCandidateIdAttribNames(dataType);
      if (candIdAttribNames != null) { // has candidate identifer
        String refStr = "@Select(clazz=%s, attributes={%s})";
        String attribNameStr = "\"" + String.join("\",\"", candIdAttribNames) + "\"";
        ref = String.format(refStr, dataType.getName()+".class", attribNameStr);
        loadOidWithBoundValue = true;
        displayOidWithBoundValue = true;
      }
    }
    
    // v3.3: to ignore virtual attributes
    //DAttr dc = getDomainConstraint(domainCls, attrib);
    if (// v5.1: dc.virtual()
        isAttributeNotViewable(dc)
        ) {
      // virtual attribute
      return false;
    }
    
    /* editable:
     * editable = false in the following cases:
     *  (1) (already supported by default) DomainConstraint.mutable=false
     *  (2) Association.type=one-to-one /\ Association.associate.determinant=true 
     */
    boolean attribEditable = true;
    if (isDeterminedByAssociate(domainCls, attrib)) {
      attribEditable = false;
    }
    
    /* width, height:
     * if attribute type results in the use of JTextField as display class
     *  make width at least as long as the label
     *  make height the default height
     */
    int width = MCCLConstants.DEFAULT_FIELD_WIDTH, height = MCCLConstants.DEFAULT_FIELD_HEIGHT;
    if (isTextFieldDisplayFor(domainCls, attrib, dc)) {
      int labelLength = label.length();
      if (dc.length() < labelLength) {
        // set both width and height
        width = labelLength;
        height = MCCLConstants.STANDARD_FIELD_HEIGHT;
      }
    }
    
    /*
     * 5.0: visible, type, editable: depends on whether annotation @Hidden is assigned
     *  
     */
    boolean visible = true;
    Hidden hiddenAno = getAttribAnnotation(attrib, Hidden.class);
    if (hiddenAno != null) {
      attribEditable = false;
      displayCls = JTextField.class;
      visible = false; // not visible, but still available for setting attribute value
    }

    // SPECIAL CASES:
    // special case (1):
    Output outputAno = getAttribAnnotation(attrib, Output.class);
    if (outputAno != null && dc.type().isCollection()) {
      // attrib is an output attribute (of a report) and is collection-typed: use a special controller desc for it
      controllerDesc = "@ControllerDesc( \n" + 
          "openPolicy=OpenPolicy.L \n" + 
          "// a special open command to open the objects recorded in the report's output attribute \n" + 
          ",props={ \n" + 
          "  @PropertyDesc(name=PropertyName.controller_dataController_open, \n" + 
          "      valueIsClass=OpenObjectsInBufferDataControllerCommand.class, valueAsString=CommonConstants.NullValue, \n" + 
          "      valueType=Class.class), \n" + 
          "})";
    } else {
      // (v5.1c) special case (2):
      DAssoc dassoc = getAttribAnnotation(attrib, DAssoc.class);
      if (dassoc != null && dassoc.ascType().equals(AssocType.Many2Many)) {
      //if (isAttribOfManyManyAssociation(domainCls, attrib)) {
        // attrib is an associative attribute for a many-many association
        displayCls = JListField.class;
        //TODO: ref = "@Select(clazz="+dassoc.associate().type().getSimpleName()+".class,attributes={\"name\"})";
        width = 100;
        height = 5;
        isStateEventSource = true;
      } else if (isAttribTypeListOrCombo(dc)) {
        isStateEventSource=true;
      }
    }    
    
    addViewAttributeFor(dataType, attribName, label, displayCls, attribEditable, width, height, visible, controllerDesc,
        // v5.1c:
        ref, 
        // v5.3:
        loadOidWithBoundValue,
        displayOidWithBoundValue,
        // end 5.3
        isStateEventSource
        );
    
    return isStateEventSource;
  }

//  /**
//   * @effects 
//   *  if cls has candidate identifiers then pick the first one declared in the class and 
//   *  return the attribute name(s) that form this identifier.
//   *  
//   * @version 5.3
//   */
//  private String[] getCandidateIdAttribNames(Class cls) {
//    return dodm.getDsm().getCandidateIdAttribNames(cls);
//  }

  /**
   * @effects 
   *  if dc.type is a list or combo type
   *    return true
   *  else 
   *    return false
   *    
   * @version 5.3
   */
  private boolean isAttribTypeListOrCombo(DAttr dc) {
    DAttr.Type type = dc.type();
    
    return type.isDate() || type.isBoolean() || type.isDomainType();
  }

  /**
   * @effects 
   *  add to {@link #classBodySeg} an {@link AttributeDesc}-annotated field definition for <tt>attrib</tt>,
   *  the <tt>label<tt> of whose annotation is the same as <tt>label</tt> and 
   *  other properties are set to the corresponding parameters  
   *  
   * @version 
   * - 5.0: added support for new parameters (visible, displayCls, controllerDesc<br>
   * - v5.1c: support two more parameters
   * @param isStateEventSource2 
   * @param displayOidWithBoundValue 
   */
  private void addViewAttributeFor(Class dataType, String attribName, String label, 
      Class displayCls, // v5.0
      boolean attribEditable, int width, int height,
      boolean visible // v5.0
      , String controllerDesc // v5.0
      , String ref, 
      boolean loadOidWithBoundValue, // v5.3
      boolean displayOidWithBoundValue, 
      boolean isStateEventSource  // v5.1c
      ) {
    String dataTypeName = dataType.getName();
    
    CodeSegment attribDescSeg = new CodeSegment();
    attribDescSeg.load(File_Attrib_Descriptor, 
        Tuple2.newTuple2("{dataType}", dataTypeName), 
        Tuple2.newTuple2("{fieldName}", attribName), 
        Tuple2.newTuple2("{label}", label), 
        Tuple2.newTuple2("{type}", displayCls.getName() + ".class"), 
        Tuple2.newTuple2("{editable}", attribEditable+""), 
        Tuple2.newTuple2("{width}", width+""), 
        Tuple2.newTuple2("{height}", height+""),
        Tuple2.newTuple2("{visible}", visible+"")
        ,Tuple2.newTuple2("{controllerDesc}", controllerDesc)
        ,Tuple2.newTuple2("{ref}", ref)
        ,Tuple2.newTuple2("{loadOidWithBoundValue}", loadOidWithBoundValue+"")
        ,Tuple2.newTuple2("{displayOidWithBoundValue}", displayOidWithBoundValue+"")
        ,Tuple2.newTuple2("{isStateEventSource}", isStateEventSource+"")
        );
    
    classBodySeg.append(attribDescSeg);
  }
  
  /**
   * @effects 
   *  if attribute.type results in the use of JTextField as display class for it
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt>
   */
  private boolean isTextFieldDisplayFor(Class domainCls, Field attrib, DAttr dc) {
    //DAttr dc = getDomainConstraint(domainCls, attrib);
    
    if (dc.type().isPrimitiveExceptBoolean()) {
      return true;
    } else {
      return false;
    }
  }

//  /**
//   * A specialised method of {@link #getAttribAnnotation(Field, Class)} for {@link DAttr}.
//   * 
//   * @effects 
//   *  return {@link DAttr} attached to <tt>attrib</tt>
//   * @version 5.0: improved to support the new Field-DAttr mapping
//   */
//  private DAttr getDomainConstraint(Class domainCls, Field attrib) {
//    return attrib.getAnnotation(DAttr.class);
//  }


  /**
   * This is a more general form of {@link #getDomainConstraint(Class, Field)}.
   * 
   * @effects 
   *  if exists annotation typed <tt>anoType</tt> assigned to <tt>attrib</tt>
   *    return it
   *  else
   *    return null
   * @version 5.0
   * 
   */
  private <T extends Annotation> T getAttribAnnotation(Field attrib, Class<T> anoType) {
    T ano = attrib.getAnnotation(anoType);
    
    return ano;
  }


  /**
   * @effects 
   *  if attribute <tt>c.attrib</tt> realises an one-one association with the associate end
   *  being the determinant
   *    return <tt>true</tt>
   *  else
   *    return <tt>false</tt> 
   */
  private boolean isDeterminedByAssociate(Class c, Field attrib) {
    DSMBasic dsm = dodm.getDsm();
    DAttr dc = dsm.getDomainConstraint(c,attrib);
    return dsm.isDeterminedByAssociate(c, dc);
  }
  
  /**
   * @param classSource 
   * @requires 
   *  classSource != null /\ 
   *  <tt>clsName</tt> is the fully qualified name of the output class 
   *  
   * @effects 
   *  compile the source code of module class contained in <tt>classSource</tt> using 
   *  <tt>clsName</tt> as the fully-qualified class name, 
   *  and return the {@link Class} output; 
   *  throws NotPossibleException if failed
   */
  private Class compileClass(final String clsName, String classSource) throws NotPossibleException {
    try {
//       Class<?> compiledCls = InMemoryJavaCompiler.compile(clsName, classSource);
      Class<?> compiledCls = javaCompiler.compile(clsName, classSource);
      
      return compiledCls;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_COMPILE_CLASS, e, new Object[] {clsName});
    }
  }

  /**
   * @effects 
   *  re-initialises the state of this ready to run {@link #generateModuleClass()}
   */
  private void reset() {
    if (moduleClassSeg == null) {
      moduleClassSeg = new CodeSegment();
    } else {
      moduleClassSeg.clear();
    }

    if (packageSeg == null) {
      packageSeg = new CodeSegment();
    } else {
      packageSeg.clear();
    }

    if (importSeg == null) {
      importSeg = new CodeSegment();
    } else {
      importSeg.clear();
    }

    if (classHeaderSeg == null) {
      classHeaderSeg = new CodeSegment();
    } else {
      classHeaderSeg.clear();
    }
    
    if (classBodySeg == null) {
      classBodySeg = new CodeSegment();
    } else {
      classBodySeg.clear();
    }
    
    if (moduleDescSeg == null) {
      moduleDescSeg = new CodeSegment();
    } else {
      moduleDescSeg.clear();
    }
    
    moduleClass = null;    
  }

  /**
   * @effects 
   *  if the domain attribute represented by <tt>dc</tt> is not viewable
   *    return true
   *  else
   *    return false
   * @version 5.1
   */
  private boolean isAttributeNotViewable(DAttr dc) {
    return dc.virtual() || dc.sourceQuery();
  }
  

  /**
   * @effects 
   *  if <tt>cls.attrib</tt> is an associative attribute of a many-many association
   *    return true
   *  else
   *    return false
   * @version 5.1c
   */
  private boolean isAttribOfManyManyAssociation(Class domainCls, Field attrib) {
    DAssoc assoc = getAttribAnnotation(attrib, DAssoc.class);
    
    if (assoc != null && assoc.ascType().equals(AssocType.Many2Many)) {
      // it is!
      return true;
    } else {
      return false;
    }
  }

  /**
   * @effects 
   *  if <tt>domainCls</tt> has many-many associative field(s)
   *    return true
   *  else
   *    return false
   * @version 5.1c
   */
  private boolean hasManyManyAssociations(Class domainCls) {
    DSMBasic dsm = dodm.getDsm();
    // cannot use dsm.getDomainAttributes() here because domainCls is not yet registered!
    //attribs = dsm.getAnnotatedFieldsNoDups(domainCls, DSMBasic.DC);
    return dsm.hasAssociation(domainCls, AssocType.Many2Many, null);
  }
  
  /**
   * 
   * @effects 
   *  if {@link #moduleClass} is not null
   *    return  {@link #moduleClass}
   *  else
   *    return <tt>null</tt>
   */
  public Class getModuleClass() {
    return moduleClass;
  }


  /**
   * @effects 
   *  if {@link #moduleClass} is not null
   *    return the source code of {@link #moduleClass}
   *  else
   *    return <tt>null</tt>
   */
  public String getModuleClassSource() {
    if (moduleClass != null) {
      return moduleClassSeg.toString();
    } else {
      return null;
    }
  }
//  
//  @Override
//  public String toString() {
//    return "ModuleClassGen (" + domainCls + ")";
//  }

}
