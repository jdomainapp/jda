package jda.modules.helpviewer.model.print;

import static java.lang.annotation.ElementType.TYPE;

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
import jda.modules.mccl.syntax.MCCLConstants.PageFormat;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;


/**
 * @overview
 *  Describes print-specific configuration settings for an application module.
 *   
 * @author dmle
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={TYPE})
@Documented
public @interface PrintDesc {
  /**
   * The print layout used for the GUI 
   * <p>Default {@link MCCLConstants.PageFormat#Portrait}
   */
  PageFormat pageFormat() default PageFormat.Portrait;

  /**
   * The print paper size
   * 
   * <p>Default: {@link MCCLConstants.PaperSize#A4}
   */
  PaperSize paperSize() default PaperSize.A4;

  /**
   * The user-defined width for the print content; which can either be an absolute width
   * in pixels (e.g. "600") or a relative width (e.g. "100%")
   * <br>Default: {@link MCCLConstants#DEFAULT_PRINT_WDITH}
   */
  String width() default MCCLConstants.DEFAULT_PRINT_WIDTH;

  /**
   * The document builder class, which must be a sub-type of {@link DocumentBuilder}, 
   * that implements the document export logic used for the objects
   * of this class.
   *  
   * <br>Default: {@link DefaultHtmlDocumentBuilder} 
   * @version 2.7.4
   */
  Class<? extends DocumentBuilder> docBuilderType() default DefaultHtmlDocumentBuilder.class;

  /**
   * If the domain class used to generate the document content from objects of this differs from 
   * the domain class of this, specify it here. Otherwise, leave this as the default setting.
   * 
   * <br>Default: {@link CommonConstants#NullType}
   */
  Class docDataClass() default Null.class;

  /**
   * The name of the template file used to generate the document content from objects of this
   * 
   * <br>Default: {@link CommonConstants#NullString}
   */
  String docTemplate() default CommonConstants.NullString;

  /** 
   * a {@link Select} annotation which specifies the domain attributes  
   * of the domain class of this whose values will appear in the exported document
   * 
   * Default: @Select() (i.e. all attributes will be exported) */
  Select ref() default @Select();

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
  
}
