package jda.mosa.view.assets.datafields.text;

import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.JTextField;

/**
 * @overview
 *  A <b>bounded</b> subtype of {@link JTextField} that has an extra behaviour of loading the bounded data value from the underlying 
 *  data source and displays this value on the field. 
 *  
 *  <p>The data loading behaviour is provided by {@link #loadBoundedData()}. 
 *  
 * @author dmle
 */
public class JTextFieldAuto<C> extends JTextField {
  
  // bounded
  public JTextFieldAuto(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable, autoValidation);
  }

  // bounded
  public JTextFieldAuto(DataValidator validator, Configuration config,  
      C val,
      JDataSource dataSource, // the data source to which this field is bound 
      DAttr domainConstraint, DAttr boundConstraint, Boolean editable) throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable);
  }
  
  // unbounded
  public JTextFieldAuto(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable, Boolean autoValidation) throws ConstraintViolationException  {
    super(validator, config, val, null, domainConstraint, null, editable, autoValidation);
  }

  // unbounded
  public JTextFieldAuto(DataValidator validator, Configuration config, C val, 
      DAttr domainConstraint, Boolean editable) throws ConstraintViolationException {
    // auto-validation=true
    super(validator, config, val, null, domainConstraint, null, editable, true);
  }

  // unbounded
  public JTextFieldAuto(DataValidator validator, Configuration config, 
      DAttr domainConstraint) throws ConstraintViolationException {
    super(validator, config, null, null, domainConstraint, null, true);
  }
  
  @Override
  protected void loadBoundedData() throws NotPossibleException {
    loadBoundedDataSingle();
  }
}
