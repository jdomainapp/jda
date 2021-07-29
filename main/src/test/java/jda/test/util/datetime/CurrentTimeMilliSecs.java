package jda.test.util.datetime;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;


/**
 * @author ducminhle
 */
public class CurrentTimeMilliSecs {

 public static void main(String[] args) {
   System.out.printf("time (milli secs): %d%n",System.currentTimeMillis());
   
//   Integer[] intArr = {1,2,3};
//   Object[] numArr = (Object[]) intArr;
//   System.out.println(Arrays.toString(numArr));
   
   Calendar cal = Calendar.getInstance();
   DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
   System.out.printf("Today: %s%n", format.format(cal.getTime()));
   
   String dateStr = "01/01/1974";
   try {
    Date date = format.parse(dateStr);
    System.out.printf("format('%s') = %s%n", dateStr, format.format(date));
  } catch (ParseException e) {
    // TODO Auto-generated catch block
    e.printStackTrace();
  }
   
 }
}
