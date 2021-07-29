package jda.test.util.datetime;

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JFormattedTextField;

import jda.modules.common.Toolkit;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMToolkit;

public class MonthOfYearFormat {
  public static void main(String[] args) {
    Date date = Toolkit.getCurrentDateZeroTime();
    
    DAttr.Format format = DAttr.Format.MonthOfYear;
    
    String str = DODMToolkit.dateToString(date, format);
    
    System.out.printf("Date: %s %n  -> formatted (%s)= %s", date, format.getFormatString(), str);
    
    SimpleDateFormat dateFormat = new SimpleDateFormat(format.getFormatString());
    
    JFormattedTextField tf = new JFormattedTextField(dateFormat);
  }
}
