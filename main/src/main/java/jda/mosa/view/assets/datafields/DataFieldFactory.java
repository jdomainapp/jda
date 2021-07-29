package jda.mosa.view.assets.datafields;

import java.lang.reflect.Constructor;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.ds.viewable.JSingleValueDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.RegionDataField;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.datafields.chooser.JImageChooserField;
import jda.mosa.view.assets.datafields.datetime.JDateFieldSimple;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.mosa.view.assets.datafields.text.JPasswordField;
import jda.util.events.InputHandler;

/**
 * @overview
 *  A factory class to create data field
 * @author dmle
 */
public class DataFieldFactory {
  private DataFieldFactory() {}
  
  public static JTextField createTextField(DataValidator validator, 
      Configuration config, DAttr dc, Object val, 
      boolean editable) {
    return //new JTextField(schema, val, dc, editable);
        (JTextField) createSingleValuedDataField(validator, config, dc,  
            JTextField.class, val, editable);
  }
  
  public static JTextField createTextField(DataValidator validator, 
      Configuration config, 
      DAttr dc, //DomainConstraint boundConstraint, 
      Object val, 
      boolean editable, boolean autoValidation) {
    return 
        //new JTextField(schema, val, dc, boundConstraint, editable, autoValidation);
        (JTextField) createSingleValuedDataField(validator, config, dc,  
            JTextField.class, val, false, editable, autoValidation);
  }

  public static JTextField createTextField(DataValidator validator, 
      Configuration config, 
      DAttr dc) {
    return //new JTextField(schema, dc);
        (JTextField) createSingleValuedDataField(validator, config, dc, JTextField.class, null, true);
  }

//  public static JTextField createTextField(DataValidator validator, boolean editable) {
//    return new JTextField(schema, null, null, editable);
//  }

  public static JDataField createPasswordField(DataValidator validator, 
      Configuration config, 
      DAttr dc, Object val) {
    //return new JPasswordField(schema, val, dc, true);
    return createSingleValuedDataField(validator, config, dc, JPasswordField.class, val, true, true, true);
  }

  public static JSpinnerField createSpinnerField(DataValidator validator, 
      Configuration config, 
      DAttr domainConstraint, 
      Object val, 
      JDataSource dataSource, //List values, 
      boolean editable) throws ConstraintViolationException {
    //return new JSpinnerField(schema, val, values, domainConstraint, null, editable);
    return (JSpinnerField) createMultiValuedDataField(validator, config,  
        null, // field config
        null, // data container
        null, // input helper
        domainConstraint, null, 
      JSpinnerField.class, dataSource, val, editable);
  }
  
  /////////////////////////////////// CORE METHODS ////////////////////////////////////
  
  /**
   * This is a generic factory method that uses other more specific factory methods
   * of this class to create a <b>multi-valued</tt> data field.  
   * 
   * @effects 
   *  return a multi-valued <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   */
  public static JDataField createMultiValuedDataField(DataValidator validator,
      Configuration config, 
      DAttr co, DAttr boundConstraint, 
      Class displayClass, // the data field class that is used 
      JDataSource dataSource, //List values,        // allowed values
      Object val)
      //, Boolean editable) 
      throws 
      ConstraintViolationException, NotFoundException {
    return createMultiValuedDataField(validator, config,  
        co, boundConstraint, displayClass, dataSource, val, true);
  }
  
  public static JDataField createMultiValuedDataField(DataValidator validator,
      Configuration config, 
      DAttr co, 
      //DomainConstraint boundConstraint, 
      Class displayClass, // the data field class that is used 
      //JDataSource dataSource,        // allowed values
      Object val, 
      Boolean editable) 
      throws 
      ConstraintViolationException, NotFoundException {
    return createMultiValuedDataField(validator, config,  
        co, null, displayClass, null, val, true);
  }
  
  public static JDataField createMultiValuedDataField(DataValidator validator,
      Configuration config, 
      DAttr co, DAttr boundConstraint, 
      Class displayClass, // the data field class that is used 
      JDataSource dataSource, //List values,        // allowed values
      Object val,
      Boolean editable) 
      throws 
      ConstraintViolationException, NotFoundException {
    return createMultiValuedDataField(validator, config,  
        null, // field config
        null, // data container
        null, // input helper
        co, boundConstraint, displayClass, dataSource, val, editable);
  }
  
  /**
   * This is a generic factory method that uses other more specific factory methods
   * of this class to create a <b>multi-valued</tt> data field.  
   * 
   * @effects 
   *  return a multi-valued <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   *  
   * @version 
   * <br> - 5.3: added more specific default view fields and upgraded some default view fields
   */
  public static JDataField createMultiValuedDataField(DataValidator validator,
      Configuration config, 
      RegionDataField fieldConfig,  // v2.7
      JDataContainer dataContainer, // v2.7
      InputHandler inputHelper,  // v2.7
      DAttr co, DAttr boundConstraint, 
      Class displayClass, // the data field class that is used 
      JDataSource dataSource, //List values,        // allowed values
      Object val, Boolean editable) throws 
      ConstraintViolationException, NotFoundException {
    JDataField df = null;
    Type type = null;
    if (co != null) {
      type = co.type();
    }
    
    if (displayClass == null) {
      /*v2.7: support boolean-type field
      // use default
      displayClass = JSpinnerField.class;
      */
      if (type != null) {
        if (type.isBoolean()) {
          displayClass = JBooleanField.class;
        } else {
          displayClass = JComboField.class;
        }
      } else {
        displayClass = JComboField.class; //v5.3: JSpinnerField.class;
      } 
    }
    
    Constructor cons;
    try {
      cons = displayClass.getConstructor(DataValidator.class, // domain validator
          Configuration.class, // configuration
          Object.class,   // initial value
          JDataSource.class, //List.class,      // values          
          DAttr.class, // domain constraint
          DAttr.class, // bound constraint
          Boolean.class            // editable
          );
      df = (JDataField) cons.newInstance(validator, config, val,
          dataSource, co, boundConstraint, editable);
      
      /*v2.7: perform post-creation tasks */
      postCreate(df, fieldConfig, dataContainer, inputHelper, boundConstraint, dataSource);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e, 
          new Object[] {"createMultiValuedDataField", displayClass, ""});
    }
    
    return df;
  }
  
  /**
   * A short-cut method to create simple {@link JDataField} for presenting read-only values. 
   * 
   * @effects 
   *  return a <b>non-bounded, non-masked, single-valued</b> <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   *  
   * @version 5.2
   */
   public static JDataField createSimpleValuedDataField(
       //DataValidator validator,
       Configuration config, 
       //DAttr co, 
       Class displayClass, // the data field class that is used 
       Object val, boolean editable) throws 
       ConstraintViolationException, NotFoundException {
     return createSingleValuedDataField(
         //validator,
         null,
         config, 
         //co,
         null,
         displayClass, val, 
         false, 
         editable, 
         false);
   }
  
 /**
  * @effects 
  *  return a <b>non-bounded, non-masked, single-valued</b> <tt>JDataField</tt> object suitable for the parameters. 
  *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
  *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
  */
  public static JDataField createSingleValuedDataField(DataValidator validator,
      Configuration config, 
      DAttr co, 
      Class displayClass, // the data field class that is used 
      Object val, boolean editable) throws 
      ConstraintViolationException, NotFoundException {
    return createSingleValuedDataField(validator,  config, co, displayClass, val, false, 
        editable, true);
  }
  
  /**
   * This is a generic factory method that uses other more specific factory methods
   * of this class to create an <b>unbounded, single-valued</tt> data field.  
   * 
   * @effects 
   *  return an unbounded, single-valued <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   */
  public static JDataField createSingleValuedDataField(DataValidator validator,
      Configuration config, 
      DAttr co, //DomainConstraint boundConstraint, 
      Class displayClass, // the data field class that is used 
      Object val, boolean masked, boolean editable, boolean autoValidation) throws 
      ConstraintViolationException, NotFoundException {
    return createSingleValuedDataField(validator, config, 
        null, // field config
        null, // data container
        null, // input helper
        co, null, displayClass, 
        null, val, 
        masked, editable, autoValidation);
  }
  
  /**
   * @effects 
   *  return a bounded, single-valued <tt>JDataField</tt> object suitable that is bounded 
   *  to a {@link JSingleValueDataSource}.
   *   
   *  <p>Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   */
  public static JDataField createSingleValuedDataField (
      DataValidator validator, 
      Configuration config, 
      DAttr co,
      DAttr boundConstraint, 
      Class displayClass,
      JSingleValueDataSource dataSource, 
      Object initVal, 
      boolean editable) throws ConstraintViolationException, NotFoundException {
    return createSingleValuedDataField(
        validator, 
        config, 
        null, //fieldConfig, 
        null, // dataContainer, 
        null, // inputHelper, 
        co, boundConstraint, 
        displayClass, dataSource, initVal, 
        false, //masked, 
        editable, 
        true // autoValidation
        );
  }

  /**
   * This is a generic factory method that uses other more specific factory methods
   * of this class to create a <b>bounded, single-valued</tt> data field.  
   * 
   * @effects 
   *  return a non-bounded, single-valued <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   */
  public static JDataField createSingleValuedDataField(DataValidator validator,
      Configuration config, 
      RegionDataField fieldConfig,  // v2.7
      JDataContainer dataContainer, // v2.7
      InputHandler inputHelper,  // v2.7
      DAttr co,  
      Class displayClass, // the data field class that is used
      Object val, boolean masked, boolean editable, boolean autoValidation) throws 
      ConstraintViolationException, NotFoundException {
    return createSingleValuedDataField(validator, config, fieldConfig, dataContainer, inputHelper, 
        co, null, displayClass, null, 
        val, masked, editable, autoValidation);
  }
  
  /**
   * This is a generic factory method that uses other more specific factory methods
   * of this class to create a <b>bounded, single-valued</tt> data field.  
   * 
   * @effects 
   *  return a bounded, single-valued <tt>JDataField</tt> object suitable for the parameters. 
   *  Throw <tt>ConstraintViolationException</tt> if input is invalid, 
   *  <tt>NotFoundException</tt> if a non-supported <tt>displayClass</tt> is specified.
   */
  public static JDataField createSingleValuedDataField(DataValidator validator,
      Configuration config, 
      RegionDataField fieldConfig,  // v2.7
      JDataContainer dataContainer, // v2.7
      InputHandler inputHelper,  // v2.7
      DAttr co, DAttr boundConstraint, 
      Class displayClass, // the data field class that is used
      JDataSource dataSource, // for bounded field
      Object val, boolean masked, boolean editable, boolean autoValidation) throws 
      ConstraintViolationException, NotFoundException {
    JDataField df = null;
    Type type = null;
    if (co != null) {
      type = co.type();
    }
    
    if (displayClass == null) {
      if (masked)
        displayClass = JPasswordField.class;
      else if (type != null) {  // v5.3: added date field
        if (type.isImage() && co.mutable()) {
          displayClass = JImageChooserField.class;
        } else if (type.isDate()) {
          displayClass = JDateFieldSimple.class;
        } else {
          // use default
          displayClass = JTextField.class;
        }
      }
      else {
        // use default
        displayClass = JTextField.class;
      }
    }
    
    Constructor cons;
    try {
      if (boundConstraint != null) {
        cons = displayClass.getConstructor(DataValidator.class, // domain validator
            Configuration.class, // configuration
            Object.class, // initial value
            JDataSource.class, // bounded data source
            DAttr.class, // domain constraint
            DAttr.class, // bound constraint
            Boolean.class, // editable
            Boolean.class // auto-validation
            );
        df = (JDataField) cons.newInstance(validator, config, val, dataSource, co, boundConstraint, 
            editable, autoValidation);
      } else {
        cons = displayClass.getConstructor(DataValidator.class, // domain validator
            Configuration.class, // config
            Object.class, // initial value
            DAttr.class, // domain constraint
            Boolean.class, // editable
            Boolean.class // auto-validation            
            );
        df = (JDataField) cons.newInstance(validator, config, val, co, editable, autoValidation);
      }
      
      /*v2.7: perform post-creation tasks */
      postCreate(df, fieldConfig, dataContainer, inputHelper, boundConstraint, dataSource);

    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_PERFORM, e, 
          new Object[] {"", displayClass, "Không thể tạo đối tượng trường dữ liệu"});
    }
    
    return df;
  }

  private static void postCreate(JDataField df, 
      RegionDataField fieldConfig,
      JDataContainer dataContainer,
      InputHandler inputHelper, 
      DAttr boundConstraint, 
      JDataSource dataSource) {
    
    /*v2.7: perform post-creation tasks */
    df.setDataFieldConfig(fieldConfig);
    
    // sets the data container as the container of the data field
    df.setParentContainer(dataContainer);

    df.initField();
    
    //TODO: check if this can be removed 
//    if (fieldConfig != null) {
//      // set width, height (if specified)
//      Integer width = fieldConfig.getWidth();
//      Integer height = fieldConfig.getHeight();
//      if (width != null & height != null) {
//        df.setPreferredSize(new Dimension(width, height));
//      }
//    }
    
    // register the overall mouse listener
    if (inputHelper != null) {
      df.addMouseListener(inputHelper);
      df.addKeyListener(inputHelper);
      df.addFocusListener(inputHelper);
      
      // v2.7.4
      df.addValueChangeListener(inputHelper);
    }      

    if (boundConstraint != null) {
      JBindableField bdf = (JBindableField) df;
      // register data field as listener of data source
      dataSource.addBoundedComponent(bdf);      
    }  
  }
}
