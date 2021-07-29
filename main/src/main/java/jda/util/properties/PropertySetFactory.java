package jda.util.properties;

import static java.lang.System.out;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.types.properties.PropertyDesc;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.helpviewer.model.print.PrintDesc;
import jda.modules.helpviewer.model.print.PrintFieldDesc;
import jda.modules.mccl.conceptmodel.view.StyleName;
import jda.modules.mccl.syntax.ModuleDescriptor;
import jda.modules.mccl.syntax.view.AttributeDesc;
import jda.modules.mccl.syntax.view.ViewDesc;
import jda.util.SysConstants;
import jda.util.properties.PropertySet.PropertySetType;

/**
 * @overview
 *  A factory class for {@link PropertySet}
 *  
 * @author dmle
 *
 */
public class PropertySetFactory {

  private static final Class<PrintDesc> PrintDescCls = PrintDesc.class;
  private static final Class<AttributeDesc> AttributeDescCls = AttributeDesc.class;
  private static final Class<PrintFieldDesc> PrintfDescCls = PrintFieldDesc.class;
  
  private static final List<String> DefaultAnnotationTypeMethods;
  private static final String[] ExcludedAttributeDescMembers;
  
  private static final boolean debug = Toolkit.getDebug(PropertySetFactory.class);
  
  static {
    DefaultAnnotationTypeMethods = new ArrayList<>();
    Collections.addAll(DefaultAnnotationTypeMethods, 
        "toString", "hashCode", "annotationType");
    
    ExcludedAttributeDescMembers = new String[] {
        "controllerDesc", "modelDesc"
    };
  }
  
  
  /**
   * @effects <pre>
   *  if no view-specific <tt>Property</tt>s are defined in <tt>moduleCls</tt>
   *    return <tt>null</tt>
   *  else
   *    create and return <tt>PropertySet</tt> whose name is <tt>name</tt>, <tt>type = {@link PropertySetType#ViewConfig}</tt> 
   *    and whose <tt>Property</tt>s are defined from the {@link ViewDesc} meta attribute of <tt>moduleCls</tt> (if any) and 
   *    from the {@link AttributeDesc} of the data fields of this class (if any).
   *    </pre>
   * @version 3.1
   */
  public static PropertySet createViewConfigPropertySet(
      DODMBasic dodm,
      String name, 
      Class moduleCls,
      boolean serialised) throws NotPossibleException, DataSourceException {
    PropertySet viewPropSet = null; 

    DSMBasic dsm = dodm.getDsm();
    ModuleDescriptor moduleDesc = dsm.getModuleDescriptorObject(moduleCls);
        
    ViewDesc viewDesc = moduleDesc.viewDesc();

    //Property p; String k; Object v; Class rt; DomainConstraint.Type type;
    PropertyDesc[] viewPropDescs = viewDesc.props();
    
    if (viewPropDescs.length > 0) {
      // view props specified
      viewPropSet = createPropertySet(dodm, serialised, PropertySetType.ViewConfig, viewPropDescs);
    }
    
    // extract properties from the domain attributes (if any)
    // v3.3: TODO: use getAttributesNoDuplicates (similar to method createPrintConfigPropertySet) ??
    Collection<Field> attributes = dsm.getAnnotatedSerialisableFields(moduleCls, AttributeDescCls);
    
    if (attributes != null) {
      if (viewPropSet == null)
        viewPropSet = new PropertySet(name, PropertySetType.ViewConfig);
      
      String fname;
      AttributeDesc attribDesc;
      PropertySet attribCfg;
      for (Field field : attributes) {
        fname = field.getName();
        attribDesc = field.getAnnotation(AttributeDescCls);

        if (attribDesc != null) {
          /*
           * create a nested PropertSet for attribDesc
           * if attribDesc.props.length > 0 
           *    create a level-2 nested PropertySet element for attribDesc.props
           */
          attribCfg = new PropertySet(fname, PropertySetType.AttributeViewConfig, viewPropSet);
          addToSet(dodm, attribCfg, AttributeDescCls, attribDesc, null);
          
          viewPropSet.addExtension(attribCfg);
        }
      }
    }
    
    return viewPropSet;
  }
  
  /**
   * @effects <pre>
   *  if no print-specific <tt>Property</tt>s are defined in <tt>printableModuleCls</tt>
   *    return <tt>null</tt>
   *  else
   *    create and return <tt>PropertySet</tt> whose name is <tt>name</tt>, <tt>type = {@link PropertySetType#PrintConfig}</tt> 
   *    and whose <tt>Property</tt>s are defined from the {@link PrintDesc} meta attribute of <tt>printableModuleCls</tt> (if it is specified) and 
   *    from the data fields of this class <b>that are specified with {@link PrintFieldDesc}</b>.
   *    </pre>
   */
  public static PropertySet createPrintConfigPropertySet(
      DODMBasic dodm,
      String name, 
      Class printableModuleCls) throws NotPossibleException {
    PropertySet printCfg = null; 
    
    PrintDesc printDesc = (PrintDesc) printableModuleCls.getAnnotation(PrintDescCls);

    //Property p; String k; Object v; Class rt; DomainConstraint.Type type;
    
    if (printDesc != null) {
      printCfg = new PropertySet(name, PropertySetType.PrintConfig);
      
      // create a Property element from each property of this descriptor
      addToSet(dodm, printCfg, PrintDescCls, printDesc, null);
    }

    // extract print config Properties from the domain attributes (if any)
    /* v3.3: read attributes with preference given to those from moduleDescrCls if it is 
     * a sub-type
    List<Field> attributes = dodm.getDsm().getAttributes(printableModuleCls, AttributeDescCls);
    */
    Map<Field,AttributeDesc> attributes = dodm.getDsm().getAnnotatedFieldsNoDups(printableModuleCls, AttributeDescCls);
    
    if (attributes != null) {
      if (printCfg == null)
        printCfg = new PropertySet(name, PropertySetType.PrintConfig);
      
      String fname;
      //String labelStr;
      //String labelId;
      //AttributeDesc attribDesc;
      PrintFieldDesc printfDesc;
      PropertySet printfCfg;
      //for (Field field : attributes) {
      for (Entry<Field,AttributeDesc> entry : attributes.entrySet()){
        Field field = entry.getKey();
        fname = field.getName();
        printfDesc = field.getAnnotation(PrintfDescCls);

        // there is no need to define Propertys for the field, because that can be looked-up  
        // at run-time
        if (printfDesc != null) {
          /*
           * create a nested PropertSet for printfDesc
           * if printfDesc.printConfig != null 
           *    create a level-2 nested PropertySet element for printfDesc.printConfig
           */
          printfCfg = new PropertySet(fname, PropertySetType.PrintFieldConfig, printCfg);
          addToSet(dodm, printfCfg, PrintfDescCls, printfDesc, null);
          
          printCfg.addExtension(printfCfg);
        }
      }
    }
    
    return printCfg;
  }
  
  /**
   * @effects <pre>
   *  if no print-specific <tt>Property</tt>s are defined in <tt>printableModuleCls</tt>
   *    return <tt>null</tt>
   *  else
   *    create and return <tt>PropertySet</tt> whose <tt>name = {@link PropertySetType#PrintConfig}</tt> 
   *    and whose <tt>Property</tt>s are defined from the {@link PrintDesc} meta attribute of <tt>printableModuleCls</tt> (if it is specified) and 
   *    from <b>all data fields</b> of this class.
   *    </pre>
   */
  private static PropertySet createPrintConfigExtensionPropertySet(DODMBasic schema, 
      String name, 
      Class printableModuleCls,
      PropertySet extensionOf
      ) throws NotPossibleException {
    PropertySet printCfg = null; 
    
    PrintDesc printDesc = (PrintDesc) printableModuleCls.getAnnotation(PrintDescCls);

    if (printDesc != null) {
      printCfg = new PropertySet(name, PropertySetType.PrintConfig, extensionOf);
      
      // create a Property element from each property of this descriptor
      addToSet(schema, printCfg, PrintDescCls, printDesc, null);
    }

    // extract print config Properties from the domain attributes (if any)
    Collection<Field> attributes = schema.getDsm().getAnnotatedSerialisableFields(printableModuleCls, AttributeDescCls);
    
    if (attributes != null) {
      if (printCfg == null)
        printCfg = new PropertySet(name, PropertySetType.PrintConfig, extensionOf);
      
      String fname;
      AttributeDesc attribDesc;
      PrintFieldDesc printfDesc;
      PropertySet printfCfg;
      for (Field field : attributes) {
        fname = field.getName();
        attribDesc = field.getAnnotation(AttributeDescCls);
        printfDesc = field.getAnnotation(PrintfDescCls);
        
        // create Propertys from both attribDesc and printDesc (if specified)
        printfCfg = new PropertySet(fname, PropertySetType.PrintFieldConfig, printCfg);

        // add attribute desc
        addToSet(schema, printfCfg, AttributeDescCls, attribDesc, ExcludedAttributeDescMembers);
        
        // add print field desc (if specified)
        if (printfDesc != null) {
          addToSet(schema, printfCfg, PrintfDescCls, printfDesc, null);
        }
        
        printCfg.addExtension(printfCfg);
      }
    }
    
    if (printCfg != null)
      extensionOf.addExtension(printCfg);
    
    return printCfg;
  }

  /**
   * @effects 
   *  create a return an <tt>Annotation-typed</tt> PropertySet which is an extension of 
   *  <tt>set</tt>, whose properties are defined from those of <tt>annotation</tt>
   */
  private static PropertySet createAnnotationTypedExtensionPropertySet(
      DODMBasic schema, String name, Annotation annotation, PropertySet set) {
    
    PropertySet extensionSet = new PropertySet(name, PropertySetType.Annotation, set);
    
    Class annotationCls = annotation.getClass();
        
    addToSet(schema, extensionSet, annotationCls, annotation, null);
    
    set.addExtension(extensionSet);
    
    return extensionSet;
  }

  /**
   * @requires set != null 
   * @effects <pre>
   *  for each property p of <tt>metaAttrib</tt>
   *    add to <tt>set</tt> a <tt>Property</tt> object that is created from p
   *    </pre> 
   */
  private static <T extends Annotation> void addToSet(
      DODMBasic schema, 
      PropertySet set, 
      Class<T> metaAttribCls,
      Annotation metaAttrib,
      String[] excludedMembers) {
    Method[] methods = metaAttribCls.getDeclaredMethods();
    
    Property p; String k; Object v; Class type;
    PropertySet extensionSet;
    ANO: for (Method m : methods) {
      k = m.getName();
      
      // ignore default methods
      if (DefaultAnnotationTypeMethods.contains(k)) continue ANO;
      
      // if excluded members were specified then exclude them
      if (excludedMembers != null) {
        for (String exl : excludedMembers) if (exl.equals(k)) continue ANO;
      }
      
      try {
        v = m.invoke(metaAttrib, null);

        if (v == CommonConstants.NullType || 
            v == SysConstants.NullCommand || 
            v == CommonConstants.NullString || 
            v == StyleName.Null)
          v = null;
        
        type = m.getReturnType();

        if (v != null && k.equals("printConfig") && type == Class.class) {
          // an print-config extension -> create a print-config PropertySet element
          extensionSet = createPrintConfigExtensionPropertySet(schema, k, (Class)v, set);
        } else if (v != null && Annotation.class.isAssignableFrom(type)) {
          /*  an annotation-typed extension, e.g. 
          ref=@Select(clazz=FamilyRegister.class,
                    attributes={FamilyRegister.AttributeName_number, "issuedDate"}) */
          // treat this as an extension and create a nested PropertySet for it
          extensionSet = createAnnotationTypedExtensionPropertySet(schema, k, (Annotation)v, set);
        } else {
          // a normal property
          p = new Property(k, v, type, set);
          set.addProperty(p);
        }
      } catch (Exception e) {
        // should not happen
      }
    }  
  }

  /**
   * @requires 
   * <tt>propName, propVal != null</tt>
   *  
   * @effects 
   *  create a {@link PropertySet} whose type is {@link PropertySetType#Annotation} and containing one property named <tt>propName</tt> whose value is <tt>propVal</tt>.
   *   
   *  Return this set.
   * @version 5.1
   */
  public static PropertySet createAnoPropertySet(DODMBasic dodm,
      boolean serialised, PropertyName propName,
      Object propVal) throws DataSourceException {
    PropertySetType type = PropertySetType.Annotation;
    PropertySet pset = new PropertySet(null, type);
    
    pset.setProperty(propName, propVal);
    
    createPropertySet(dodm, pset, serialised, 0);
    
    return pset;
  }
  
  /**
   * @effects 
   *  if <tt>propDescs</tt> is not empty
   *    create and return <tt>PropertySet</tt> object whose properties are defined by 
   *    <tt>propDescs</tt>; 
   *  else
   *    return <tt>null</tt>
   * @version 3.0
   */
  public static PropertySet createPropertySet(DODMBasic dodm,
      boolean serialised, 
      PropertyDesc[] propDescs 
      ) throws DataSourceException {
    return createPropertySet(dodm, serialised, PropertySetType.Annotation, propDescs);
  }
  
  /**
   * @effects 
   *  if <tt>propDescs</tt> is not empty
   *    create and return <tt>PropertySet</tt> object whose properties are defined by 
   *    <tt>propDescs</tt>; 
   *  else
   *    return <tt>null</tt>
   * @version 3.1
   */
  public static PropertySet createPropertySet(DODMBasic dodm,
      boolean serialised, 
      PropertySetType type, 
      PropertyDesc[] propDescs 
      ) throws DataSourceException {
    PropertySet pset = null;
    
    if (propDescs.length > 0) {
      pset = new PropertySet(null, type);
      
      Property prop;
      for (PropertyDesc pd : propDescs) {
        prop = Property.createInstance(pd, pset);
        
        pset.addProperty(prop);
      }
      
      createPropertySet(dodm, pset, serialised, 0);
    } 
    
    return pset;
  }
  
  /**
   * @requires 
   *  propSet != null /\ dodm != null
   * @effects 
   *  create <tt>PropertySet propSet</tt> in the underlying data source of <tt>dodm</tt> 
   */
  private static void createPropertySet(DODMBasic dodm, PropertySet  propSet, 
      boolean serialised, // v2.8 
      int gapDistance) throws DataSourceException {
    DOMBasic dom = dodm.getDom();
    
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < gapDistance;i++) indent.append(" ");
    
    StringBuffer subIndent = new StringBuffer(indent);
    subIndent.append("  ");
    
    gapDistance = gapDistance + 4;

    // add property set to data source
    if (debug)
      out.printf("%sProperty set: %s%n", indent, propSet.getName());
    
    dom.addObject(propSet, serialised);
    
    if (debug)
      out.printf("%sProperties:%n", indent);
    Collection<Property> props = propSet.getProps();
    for (Property p : props) {
      if (debug)
        out.printf("%s%s: \"%s\" (%s<%s>)%n", subIndent, 
          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
      
      // add property set to data source
      dom.addObject(p, serialised);
    }

    Collection<PropertySet> extents = propSet.getExtensions();
    if (extents != null && !extents.isEmpty()) {
      if (debug)
        out.printf("%sExtension(s):%n", subIndent);
      for (PropertySet pset : extents) {
        // recursive
        createPropertySet(dodm, pset, serialised, gapDistance);
      }
    }
  }
  
  /**
   * @requires 
   *  propSet != null /\ dodm != null
   * @effects 
   *  create <tt>PropertySet propSet</tt> in the underlying data source of <tt>dodm</tt> 
   */
  public static void createPropertySet(DODMBasic dodm, PropertySet  propSet, 
      boolean serialised) throws DataSourceException {
    createPropertySet(dodm, propSet, serialised, 0);
  }

  /**
   * @effects 
   *  dislay content of <tt>propSet</tt> on standard output
   */
  public static void print(PropertySet propSet) {
    print(propSet, 0);
  }
  
  private static void print(PropertySet  propSet, int gapDistance) {
    StringBuffer indent = new StringBuffer();
    for (int i = 0; i < gapDistance;i++) indent.append(" ");
    
    StringBuffer subIndent = new StringBuffer(indent);
    subIndent.append("  ");
    
    gapDistance = gapDistance + 4;

    // add property set to data source
    out.printf("%sProperty set (%d): %s%n", indent, propSet.getId(), propSet.getName());
    
    out.printf("%sProperties:%n", indent);
    Collection<Property> props = propSet.getProps();
    for (Property p : props) {
      out.printf("%s%d. %s: \"%s\" (%s<%s>)%n", 
          subIndent,p.getId(),  
          p.getPkey(), p.getValueAsString(), p.getType(), p.getValue());
    }

    Collection<PropertySet> extents = propSet.getExtensions();
    if (extents != null && !extents.isEmpty()) {
      out.printf("%sExtension(s):%n", subIndent);
      for (PropertySet pset : extents) {
        // recursive
        print(pset, gapDistance);
      }
    }
  }

//  private static Map<Class,DomainConstraint.Type> typeMap;
//  
//  static {
//    typeMap = new HashMap<>();
//    typeMap.put(Boolean.class,Type.Boolean);
//    typeMap.put(Integer.class, Type.Integer);
//    typeMap.put(Long.class, Type.Long);
//    typeMap.put(Float.class, Type.Float);
//    typeMap.put(Double.class, Type.Double);
//    typeMap.put(String.class, Type.String);
//    typeMap.put(ImageIcon.class, Type.Image);
//    typeMap.put(Font.class, Type.Font);
//    typeMap.put(Color.class, Type.Color);
//    typeMap.put(Collection.class, Type.Collection);
//  }
//  
//  /**
//   * @effects
//   *  return the property type equivalent to the value type <tt>valType</tt>; 
//   *  throws NotImplementedException if no suitable property type is found.
//   *  
//   */
//  public static DomainConstraint.Type getPropertyType(Class valType) throws NotImplementedException {
//    Class t;
//    for (Entry<Class,Type> e : typeMap.entrySet()) {
//      t = e.getKey();
//      if (t.isAssignableFrom(valType)) {
//        return e.getValue();
//      }
//    }
//    
//    throw new NotImplementedException(NotImplementedException.Code.DATA_TYPE_NOT_SUPPORTED, 
//        "Không hỗ trợ kiểu dữ liệu: {0}", valType);
//  }
}
