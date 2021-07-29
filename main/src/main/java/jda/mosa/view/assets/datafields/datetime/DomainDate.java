package jda.mosa.view.assets.datafields.datetime;

import jda.modules.dcsl.syntax.DAttr;

/**
 * @effects 
 *  Represent the elements of a <tt>Date</tt> for use in the date date field.
 *  
 * @author dmle
 */
public class DomainDate {
  @DAttr(name="day",type=DAttr.Type.Integer,
      optional=false,
      min=1,max=31)
  private int day;

  @DAttr(name="month",type=DAttr.Type.Integer,
      optional=false,
      min=1,max=12)
  private int month;
  
  @DAttr(name="year",type=DAttr.Type.Integer,
      optional=false,
      min=0)
  private int year;
}
