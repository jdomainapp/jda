package jda.modules.helpviewer.model.print;

import static java.lang.annotation.ElementType.FIELD;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.Select;
import jda.modules.exportdoc.controller.DocumentBuilder;
import jda.modules.exportdoc.controller.html.DefaultHtmlDocumentBuilder;
import jda.modules.mccl.syntax.MCCLConstants;

/**
 * @overview
 *  Describes print-specific configuration settings for each GUI field of an application module.
 *  
 *  <p>This is used together with {@link PrintDesc} to specify the print configuration of an 
 *  application module.
 *   
 * @author dmle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={FIELD})
@Documented
public @interface PrintFieldDesc {
  /**
   * Whether or not this field appears on print
   * 
   * <p>Default: <tt>true</tt>
   */
  boolean isVisible() default true;

  /**
   * Whether or not <b>the label</b> of this field appears on print
   * 
   * <p>Default: <tt>true</tt>
   */
  boolean isLabelVisible() default true;

  /**
   * The reference id of this data field. Fields that have the same id appear in the same  
   * print area (e.g. a print table); while those that have different ids appear 
   * in separate print areas.
   * 
   * <p>Default: {@link MCCLConstants#DEFAULT_PRINT_REF_ID}
   */
  String refId() default MCCLConstants.DEFAULT_PRINT_REF_ID;

  /**
   * Applied only to <b>collection-typed attributes</b>.
   * 
   * Specifies the class that defines the print configuration for the domain objects being referenced
   * by this field.
   * 
   * <p>Default: {@link CommonConstants#NullType}
   */
  Class printConfig() default Null.class;

  /**
   * The user-defined width for the print content; which can either be an absolute width
   * in pixels (e.g. "600") or a relative width (e.g. "100%")
   * <br>Default: {@link MCCLConstants#DEFAULT_PRINT_WDITH}
   */
  String width() default MCCLConstants.DEFAULT_PRINT_WIDTH;

  /**
   * Whether or not the print content of this field is surrounded by a border
   * <p>Default: <tt>true</tt>
   */
  boolean border() default true;

  /**
   * The name of the template file used to generate the document content from the value of this.
   * <br>This is typically used by collection-typed attributes, which specifies a template file that defines 
   * the HTML row for each object in the collection. 
   * 
   * <br>Default: {@link CommonConstants#NullString}
   */
  String docTemplate() default CommonConstants.NullString;

//  /**
//   * The template generator which takes as input the base template specified by {@link #docTemplate()}
//   * and generate the actual template.
//   * 
//   * <p><b>NOTE</b>: use this with {@link #docTemplate()} to dynamically generate a template 
//   *  
//   * <br>Default: {@link MetaConstants#NullType}
//   * 
//   */
//  Class docTemplateBuilder() default Null.class;
  
  /**
   * The document builder class, which must be a sub-type of {@link DocumentBuilder}, 
   * that implements the document export logic used for the objects
   * of this class.
   *  
   * <p><b>NOTE</b>: use this with {@link #docTemplate()} to dynamically generate a doc
   *  
   * <br>Default: {@link DefaultHtmlDocumentBuilder} 
   * @version 3.0
   */
  Class<? extends DocumentBuilder> docBuilderType() default DefaultHtmlDocumentBuilder.class;

  /** 
   * a {@link Select} annotation which describes the domain field 
   * of the referenced domain class to which this field refers.
   * Default: @Select() */
  Select ref() default @Select();
  
//  /**
//   * The (optional) print-specific settings for this field that mimics the format of {@link AttributeDesc}
//   * (that is used for the domain attributes).
//   * 
//   * <p>This property is only used for fields that are are not specified with the usual {@link AttributeDesc},  
//   * as they are exclusively used for specifying print configuration settings. 
//   * 
//   * <p><b>IMPORTANT</b>: Either use this OR {@link AttributeDesc}, not both!! 
//   * 
//   * <p>Default: {@link AttributeDesc}()
//   */
//  AttributeDesc attributeDesc() default @AttributeDesc();
}
