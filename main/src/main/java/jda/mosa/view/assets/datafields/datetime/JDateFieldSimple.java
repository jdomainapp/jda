package jda.mosa.view.assets.datafields.datetime;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.lang.annotation.Annotation;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.ConstraintViolationException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.query.QueryDef;
import jda.modules.ds.viewable.JDataSource;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.controller.assets.helper.DataValidator;
import jda.mosa.view.assets.datafields.DataFieldFactory;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JCounterField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.JTextField;
import jda.mosa.view.assets.datafields.list.JComboField;
import jda.util.events.ValueChangeListener;

/**
 * @overview
 *  A data field that enables a user to easily pick day, month, year
 *  elements of a <tt>Date</tt>
 *  
 *  <p>The day, month, and year elements are synchronised with the local {@link Calendar}. 
 *  
 * @author dmle
 *
 */
public class JDateFieldSimple<C> extends JBindableField {
  /**
   * @overview
   *  The pseudo domain constraint used to initialise the year field
   */
  public static final DAttr dcYear = new DAttr() {
    
    @Override
    public Class<? extends Annotation> annotationType() {
      return DAttr.class;
    }
    
    @Override
    public boolean unique() {
      // TODO Auto-generated method stub
      return false;
    }
    
    @Override
    public Type type() {
      return Type.Integer;
    }
    
    @Override
    public boolean serialisable() {
      // TODO Auto-generated method stub
      return false;
    }
    
    @Override
    public boolean optional() {
      // TODO Auto-generated method stub
      return true;
    }
    
    @Override
    public String name() {
      return "year";
    }
    
    @Override
    public boolean mutable() {
      return true;
    }
    
    @Override
    public double min() {
      // TODO Auto-generated method stub
      return 0;
    }
    
    @Override
    public double max() {
      // TODO Auto-generated method stub
      return CommonConstants.DEFAULT_MAX_VALUE;
    }
    
    @Override
    public int length() {
      return 5;
    }
    
    @Override
    public boolean id() {
      // TODO Auto-generated method stub
      return false;
    }
    
    @Override
    public Format format() {
      // TODO Auto-generated method stub
      return null;
    }
    
    @Override
    public Select filter() {
      // TODO Auto-generated method stub
      return null;
    }
    
    @Override
    public String[] derivedFrom() {
      // TODO Auto-generated method stub
      return null;
    }
    
    @Override
    public boolean defaultValueFunction() {
      // TODO Auto-generated method stub
      return false;
    }
    
    @Override
    public String defaultValue() {
      // TODO Auto-generated method stub
      return null;
    }
    
    @Override
    public boolean autoIncrement() {
      // TODO Auto-generated method stub
      return false;
    }
    
    @Override
    public boolean auto() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public String sourceAttribute() {
      // TODO Auto-generated method stub
      return CommonConstants.NullString;
    }

    @Override
    public boolean sourceQuery() {
      return false;
    }

    @Override
    public boolean sourceQueryHandler() {
      return false;
    }

    @Override // v3.3
    public boolean virtual() {
      return false;
    }

    @Override
    public boolean cid() {
      // TODO Auto-generated method stub
      return false;
    }

    @Override
    public String ccid() {
      // TODO Auto-generated method stub
      return CommonConstants.NullString;
    }
  };

  private DatePanel datePanel;

  private Format format;

  private Calendar cal;

  /**v3.0: cases mappings from years to their calendar maps */
  private static HashMap<Integer,Map> calYearMap = new HashMap();

  public JDateFieldSimple(DataValidator validator, Configuration config,
      Object val, 
      JDataSource dataSource,
      DAttr domainConstraint, 
      DAttr boundConstraint,
      Boolean editable,
      Boolean autoValidation) throws ConstraintViolationException {
    super(validator, config, val, dataSource, domainConstraint, boundConstraint, editable);
    cal = Calendar.getInstance();
  }

  /**
   * @effects initialise this as a <b>non-bounded</b> date field 
   */
  public JDateFieldSimple(DataValidator validator, Configuration config,
      Object val, 
      DAttr domainConstraint, 
      Boolean editable,
      Boolean autoValidation) throws ConstraintViolationException {
    this(validator, config, val, null, domainConstraint, null, editable, autoValidation);
  }

  /**
   * @effects initialise this as an auto-validated, <b>bounded</b> date field 
   */
  public JDateFieldSimple(DataValidator validator, Configuration config,
      Object val, 
      JDataSource dataSource, 
      DAttr domainConstraint, 
      DAttr boundConstraint, Boolean editable) throws ConstraintViolationException {
    this(validator, config, val, dataSource, domainConstraint, boundConstraint, 
        editable,
        //autoValidation
        true
        );
  }
  
  @Override
  protected JComponent createDisplayComponent(DataFieldInputHelper tfh) {
    // initialise format 
    DAttr.Format formatSpec;
    String formatString;
    
    DAttr dconstraint = getDomainConstraint();  // v5.1c:
    DAttr boundConstraint = getBoundConstraint(); // v5.1c:
    
    if (boundConstraint != null) {
      formatSpec = boundConstraint.format();
    } else {
      formatSpec = dconstraint.format();
    }
    
    if (formatSpec.isNull()) {
      formatString = null;
    } else {
      formatString = formatSpec.getFormatString();
    }
    
    if (formatString == CommonConstants.NullString) {
      // no format string
      formatString = null;
    }
    
    // use the data type to determine the value format
    Locale currentLocale = Locale.getDefault();
    if (formatString != null)
      format = new SimpleDateFormat(formatString, currentLocale);
    else
      format = new SimpleDateFormat();
    
    // create date panel and use that as the display component
    datePanel = new DatePanel(getValidator(), getConfiguration(), this);

    // v5.1c: display = datePanel;
    setGUIComponent(datePanel);

    // v3.1: added this
    setEditable(// v5.1c: //editable
        getEditable());

    return datePanel; //v5.1c: display;
  }

  @Override
  public void setEditable(boolean state) {
    super.setEditable(state);

    datePanel.setEditable(state);
  }

  @Override
  protected void setStyle(Component[] comps, Style style) {
    for (Component comp : comps) {
      if (comp instanceof JDataField) {
        // a stricter check to avoid setting style on the date separator
        setStyle(((JDataField) comp).getGUIComponent(), style);
      }
    }
  }

  @Override
  protected void loadBoundedData() throws NotPossibleException {
    JDataSource dataSource = getDataSource(); // v5.1c
    
    if (dataSource != null) {
      // only one data value to be loaded and displayed
      loadBoundedDataSingle();
    }
  }

  @Override
  protected void deleteBoundedData() {
    deleteBoundedDataSingle();
  }

  @Override
  public Object getValue() throws ConstraintViolationException {
    if (!isBounded() &&  
        !isValidated()    //v5.1c: validated
        ) {
      DAttr dconstraint = getDomainConstraint();  // v5.1c:
      
      /* v5.1c: 
      Object v = value;
      value = validateValue(v, dconstraint);
      */
      Object v = getValueDirectly();
      setValueDirectly(validateValue(v, dconstraint));      
    }

    return getValueDirectly(); // v5.1c: value;
  }
  
  @Override
  public boolean isSupportValueFormatting() {
    return true;
  }

  @Override
  public String getFormattedValue(Object val) {
    if (format != null && !val.equals(Nil)) {
      String formatted = format.format(val);
      return formatted;
    } else {
      return (val != null) ? val.toString() : null;
    }
  }

  @Override
  protected void setDisplayValue(Object dispVal) {
    Date dateVal = null;
    
    if (dispVal instanceof Date) {  // dispVal can be Nil
      dateVal = (Date) dispVal;
    }
    
    if (dateVal != null)
      datePanel.setDate(dateVal);
    else
      datePanel.resetToNull();
    
    // v2.7.4
    if (!//v5.1c: validated
        isValidated()
        ) //v5.1c: validated = true;
      setIsValidated(true);
    
    updateGUI(false);
  }
  
  /**
   * This method is invoked to update this.value from the user date input.
   * 
   * @effects <pre>
   *  create a Date object from the values of day, month, year
   *  if succeeded
   *    set this.value = Date object
   *  else
   *    set this.value = null
   *    
   *  call {@link #fireValueChanged()}
   *  </pre>
   */
  private void setValueOnStateChanged(Object dayObj, Object monthObj, Object yearObj) {
    if (dayObj != null && monthObj != null && yearObj != null) {
      int day, month, year = -1;
      
      day = (Integer) dayObj;
      month = (Integer) monthObj;
      try {
        year = Integer.parseInt(yearObj.toString());
      } catch (NumberFormatException e) {
        // invalid year
      }
      
      if (year > -1) {
        cal.clear();
        month = month - 1;  // 0-based
        cal.set(year, month, day);
        
        Date newDate = cal.getTime();
        
        //debug: System.out.printf("%s.%s: new date: %s%n", JDateFieldSimple.class.getSimpleName(), "setValueOnStateChanged", newDate);

        setValueDirectly(newDate); // v5.1c: value = newDate;
      } 
      else {
        // invalid date
        setValueDirectly(null); // v5.1c: value = null;
      }
    } 
    else {
      // invalid date
      setValueDirectly(null); // v5.1c: value = null;
    }

    fireValueChanged();
    
    setIsValidated(false); // validated = false;
  } 

  @Override
  public boolean isMouseClickConsumableByValueChanged() {
    return true;
  }
  
  /**
   * @overview
   *  A {@link JPanel} that contains day, month and year {@link JDataField}s. 
   *  
   * @author dmle
   *
   */
  private static class DatePanel extends JPanel implements ValueChangeListener {
    
    private static final List<Integer> months = Arrays.asList(
        1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12
    );
    
    private JComboField<Integer> dayField;
    private JComboField<Integer> monthField;
    private JTextField<Integer> yearField;
    
    private JDateFieldSimple dateField;
    
    private DayDataSource dayDs;
    
    private Calendar cal;
    
    public DatePanel(DataValidator dataValidator, 
        Configuration config, 
        JDateFieldSimple dateField) {

      // v2.7.4: use gaps = 0 to squeeze components together 
      super(new FlowLayout(FlowLayout.LEFT,2,0));
      // v2.7.4
      setBorder(
          null
          //BorderFactory.createLineBorder(Color.BLACK)
          );
      // debug
      //setBackground(Color.LIGHT_GRAY);

      this.dateField = dateField;
      
      // create field data sources
      JDataSource monthDs = createMonthDataSource();
      
      cal = Calendar.getInstance();
      
      // use days of current month
      int currMonth = Toolkit.getCurrentMonth(cal);
      // auto-validated year field
      int defaultYear = cal.get(Calendar.YEAR);
      
      // initialise calendar map with months and empty day lists
      Map<Integer,List> calMap = initCalendarMap(defaultYear);
      
      // initialise fields
      yearField = (JTextField<Integer>) 
//          DataFieldFactory.createTextField(dataValidator, config, null);
          DataFieldFactory.createSingleValuedDataField(dataValidator, config, 
              dcYear, JCounterField.class, defaultYear, true);
      
      yearField.addValueChangeListener(this);
      
      monthField = (JComboField<Integer>) createDateField(config, dataValidator, monthDs);
      monthField.addValueChangeListener(this);
      
      List days = calMap.get(currMonth);
      if (days.isEmpty()) {
        // not yet initialised 
        Toolkit.getDaysOfMonth(cal, currMonth-1, days);
      }
      dayDs = createDayDataSource(days);
      dayField = (JComboField<Integer>) createDateField(config, dataValidator, dayDs);
      dayField.addValueChangeListener(this);

      Font valFont = dayField.getTextFont();
      Font sepFont = valFont.deriveFont(Font.PLAIN, 20);
      //TODO: should not need to do this (combo field's text is bold but valFont does not indicate this)
      //valFont = valFont.deriveFont(Font.BOLD);
      
      // make year field the same height and font as the other two combo fields
      yearField.setTextFont(valFont);
      yearField.setAlignX(AlignmentX.Center);
      yearField.setTextLength(5);

      Dimension sz = dayField.getPreferredSize();

      // v3.0: increase dayfield's width to fix a display bug in that the size of day field is not enough to display 2-digit
      // values (e.g. 10)
      sz.setSize(sz.getWidth()+5, sz.getHeight());
      dayField.setPreferredSize(sz);
      
      Dimension yez = yearField.getPreferredSize();
      yearField.setPreferredSize(new Dimension((int)yez.getWidth(), (int)sz.getHeight()));

      add(dayField);
      JLabel sep = new JLabel("/", SwingConstants.CENTER);
      sep.setFont(sepFont);
      //sep.setPreferredSize(new Dimension(5,100));
      add(sep);
      add(monthField);
      sep = new JLabel("/", SwingConstants.CENTER);
      sep.setFont(sepFont);
      add(sep);
      add(yearField);
    }

    private JDataSource createMonthDataSource() {
      return new JDataSource() {
        @Override
        public Iterator iterator() {
          return months.iterator();
        }

        @Override
        public boolean isEmpty() {
          return false;
        }
      };
    }

    /**
     * @requires 
     *  month is a valid month 
     */
    private DayDataSource createDayDataSource(final List days) {
      /*v3.0: use calendar map 
      days = new ArrayList<>();
      
      int count;
      if (month != null) {
        //TODO: get the actual days of the specified month
        if (month == 2) {
          count = 28;
        } else {
          count = 31;
        }
      } else {
        count = 31;
      }
      
      for (int i = 1; i <= count; i++) days.add(i);

      return new JDataSource() {
        @Override
        public Iterator iterator() {
          return days.iterator();
        }

        @Override
        public boolean isEmpty() {
          return false;
        }
      }; 
      */

      DayDataSource dayDs = new DayDataSource(days);
      
      return dayDs;
    }
    

    /**
     * @requires 
     *  month != null /\ year != null 
     * @effects
     *  update this.dayField to display the correct days of the specified month and/or year
     */
    private void updateDayField(Integer month, Integer year) {
      // debug: System.out.println("updateDayField()...");

      List days;
      Map<Integer,List> calMap = calYearMap.get(year);
      if (calMap == null) {
        calMap = initCalendarMap(year);
      }

      days = calMap.get(month);

      if (days.isEmpty()) {
        // not yet initialised 
        if (year != Toolkit.getCurrentYear(cal)) {
          cal.set(Calendar.YEAR, year);
        }
        Toolkit.getDaysOfMonth(cal, month-1, days);
      }
      
      dayDs.setDays(days);
      
      // currently selected day (if any)
      Integer currDay = (Integer) dayField.getValue();
      
      // reload data
      dayField.reloadBoundedData();
      
      // reset to display previously selected day
      if (currDay != null)
        dayField.setValue(currDay);
    }
    
    /**
     * @modifies calMap
     * @effects 
     *  initialise <tt>calMap<tt> whose keys are months of <tt>year</tt>
     *  <br>caches <tt>calMap</tt>
     */
    private Map<Integer,List> initCalendarMap(int year) {
      Map<Integer,List> calMap = new HashMap();

      for (Integer m : months) calMap.put(m, new ArrayList());
      
      calYearMap.put(year, calMap);
      
      return calMap;
    }
    
    /**
     * @effects 
     *  register <tt>ml</tt> to the components of this
     */
    @Override
    public void addMouseListener(MouseListener ml) {
      // only the yearField needs to be listened to
      yearField.addMouseListener(ml);
    }

    /**
     * @effects 
     *  register <tt>kl</tt> to the components of this
     */
    @Override
    public void addKeyListener(KeyListener kl) {
      // only the yearField needs to be listened to
      yearField.addKeyListener(kl);
    }  

    /**
     * @effects 
     *  register <tt>fl</tt> to the components of this
     */
    @Override
    public void addFocusListener(FocusListener fl) {
      // only the yearField needs to be listened to
      yearField.addFocusListener(fl);
    }  
    
    void setEditable(boolean state) {
      dayField.setEditable(state);
      monthField.setEditable(state);
      yearField.setEditable(state);
    }

    private JComboField createDateField(
        Configuration config, 
        DataValidator dataValidator,
        JDataSource ds) {
      Class<JComboField> displayCls = JComboField.class;
      
      DAttr dcField = null;
      Integer initVal = null;
      JComboField df = (JComboField) 
          DataFieldFactory.createMultiValuedDataField(dataValidator, 
              config, dcField, null, 
              displayCls, ds, initVal, true); 

      df.connectDataSource();
      
      return df;
    }

    /**
     * @requires value != null
     * @effects update day, month, and year fields to <tt>value</tt>
     */
    public void setDate(Date value) {
      cal.setTime(value);
      
      // extract day, month, year
      int day = cal.get(Calendar.DAY_OF_MONTH);
      int month = cal.get(Calendar.MONTH)+1;
      int year = cal.get(Calendar.YEAR);
      
      // current values
      Integer currMonth = (Integer) monthField.getValue();
      Integer currDay = (Integer) dayField.getValue();
      Integer currYear = (Integer) yearField.getValue();
      
      // update month (if needed)
      boolean diffMonth = false, diffYear = false;
      if (currMonth == null || month != (int) currMonth) {
        diffMonth = true;
        monthField.setValue(month);
      }

      // update days if needed
      if (currYear == null || year != (int)currYear) {
        // different year
        diffYear = true;
        yearField.setValue(year);
      } 
      
      if (diffMonth || diffYear) {
        // update day listing
        updateDayField(month, year);
      }
      
      // display field value (if needed)
      if (currDay == null || day != (int) currDay) {
        dayField.setValue(day);
      }
    }

    /**
     * @effects 
     *  reset day, month, and year fields
     */
    public void resetToNull() {
      //System.out.println("reset");
      dayField.reset();
      monthField.reset();
      yearField.reset();
    }

    /**
     * @effects 
     *  populate this.dayField with days of the selected month  
     */
    @Override // ValueChangeListener
    public void fieldValueChanged(javax.swing.event.ChangeEvent e) {
      //System.out.println("state changed");
      JComponent source = (JComponent) e.getSource();

      handleFieldStateChanged(source);
    }

//    @Override // ChangeListener
//    public void stateChanged(javax.swing.event.ChangeEvent e) {
//      JComponent source = (JComponent) e.getSource();
//
//      handleFieldStateChanged(source);
//    }

    /**
     * @effects 
     *  if <tt>source</tt> is month or year field
     *    update other date fields according to the current value of <tt>source</tt>  
     */
    private void handleFieldStateChanged(JComponent source) {
      //debug: System.out.println("handleFieldStateChanged("+source.getClass().getSimpleName()+"("+source.getName()+"))...");

      Integer month = (Integer) monthField.getValue();
      Integer year = (Integer) yearField.getValue();
      
      // update day field
      /* v3.1: only update day field if source is either the month or year field
      if (month != null && year != null) {
        updateDayField(month, year);
      }
      */
      if (source == monthField || source == yearField) {
        if (month != null && year != null) {
          updateDayField(month, year);
        }
      }
      
      dateField.setValueOnStateChanged(dayField.getValue(), 
          // v3.1: monthField.getValue(), yearField.getValue()
          month, year
          );      
    }
    
    /**
     * @overview 
     *   A {@link #JDataSource} that represents the days of a month. The days 
     *   change according to the month. 
     *    
     * @author dmle
     */
    private static class DayDataSource extends JDataSource {
      
      private List days;
      
      public DayDataSource(List days) {
        this.days = days;
      }

      public void setDays(List days) {
        this.days = days;
      }

      @Override
      public Iterator iterator() {
        return days.iterator();
      }

      @Override
      public boolean isEmpty() {
        return false;
      }
    } // end DayDataSource
  } // end DatePanel
}
