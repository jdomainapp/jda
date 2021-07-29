/**
 *
 * A scrollable record set with GUI objects for user interaction 
 * 
 * @author Duc M Le  <a href="mailto:dmle@doc.ic.ac.uk"><i>dmle@doc.ic.ac.uk</i></a>
 * @version 1.0
 * Department of Computing, Imperial College
 */
package jda.mosa.view.assets.propeditor;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class RecordScroller {
  private Collection objects;
  private JComboBox searchList;
  private JButton prev;
  private JButton first;
  private JButton next;
  private JButton last;
  private JPanel main;
  private JLabel label;
  private JButton add;

  private Object current;
  private Object firstObj;
  private Object lastObj;
  private Object[] array;
  private int count = -1;

  private boolean stringable;
  private boolean updatable;
  private CommandHandler handler;
  private List listeners;

  public static enum RecordEvent {
    NEW_ITEM, //
    ITEM_CHANGED, //
  }
  
  public RecordScroller() {
    this(false);
  }

  /**
   * Create a new <code>RecordScroller</code>
   * 
   * @param objects
   *            a sorted collection of objects to scroll
   * @param stringable
   *            boolean value set to true of the objects support toString()
   *            method, otherwise it should be set to false. The difference is
   *            that for if objects are stringable then their string
   *            representation will be displayed on the indicator bar, otherwise
   *            only the record indices are displayed
   */
  public RecordScroller(Collection objects, boolean stringable) {
    this(objects, stringable, false);
  }

  public RecordScroller(boolean stringable, boolean updatable) {
    this(null, stringable, updatable);
  }

  public RecordScroller(Collection objects, boolean stringable,
      boolean updatable) {
    this.objects = objects;
    this.stringable = stringable;
    this.updatable = updatable;
    listeners = new LinkedList();
    init();
  }

  public RecordScroller(boolean stringable) {
    this(null, stringable);
  }

  public void setData(Collection objects, boolean stringable) {
    this.objects = objects;
    this.stringable = stringable;
    initData();
    updateState(false);
  }

  /**
   * Display data starting with a selected index (starts from 0)
   * 
   * @param objects
   * @param stringable
   * @param selectedIndex
   */
  public void setData(Collection objects, boolean stringable, int selectedIndex) {
    this.objects = objects;
    this.stringable = stringable;
    initData();
    moveTo(selectedIndex, false);
    // updateState();
  }

  /**
   * Change the display label next to the control
   */
  public void setLabel(String lbl) {
    label.setText(lbl);
  }

  private void init() {
    main = new JPanel();
    main.setLayout(new FlowLayout());

    label = new JLabel("Record scroller:");
    first = new JButton("<<");
    prev = new JButton("<");
    next = new JButton(">");
    last = new JButton(">>");

    if (updatable)
      add = new JButton("+");

    Font ebf = first.getFont();
    Font nbf = new Font(ebf.getName(), Font.BOLD, ebf.getSize() - 2);
    first.setFont(nbf);
    prev.setFont(nbf);
    next.setFont(nbf);
    last.setFont(nbf);
    if (add != null)
      add.setFont(nbf);

    // main.setPreferredSize(new Dimension(100, 50));

    // initialise handlers
    handler = new CommandHandler();
    initData();

    first.addActionListener(handler);
    prev.addActionListener(handler);
    next.addActionListener(handler);
    last.addActionListener(handler);
    if (add != null)
      add.addActionListener(handler);

    searchList.addItemListener(handler);
    searchList.setEditable(true);

    main.add(label);
    main.add(first);
    main.add(prev);
    main.add(searchList);
    main.add(next);
    main.add(last);
    if (add != null)
      main.add(add);
    updateState(false);

  }

  private DefaultComboBoxModel slmodel;

  private void initData() {
    if (slmodel == null) {
      slmodel = new DefaultComboBoxModel();
    } else {
      slmodel.removeAllElements();
    }

    if (searchList == null)
      searchList = new JComboBox(slmodel);

    if (objects != null && objects.size() > 0) {
      array = objects.toArray();
      firstObj = array[0];
      lastObj = array[array.length - 1];
      current = firstObj;
      count = 0;

      if (stringable) {
        for (int i = 0; i < array.length; i++) {
          slmodel.addElement(array[i]);
        }
      } else {
        // display record numbers instead
        // Integer[] indices = new Integer[array.length];
        for (int j = 0; j < array.length; j++) {
          // indices[j] = new Integer(j + 1);
          slmodel.addElement(new Integer(j + 1));
        }
      }

    } else {
      array = null;
      firstObj = null;
      lastObj = null;
      current = null;
      count = -1;
    }
  }

  private void updateState(boolean raiseEvent) {
    if (array != null) {
      if (!searchList.isEnabled())
        searchList.setEnabled(true);

      int numObjs = array.length;

      if (count == 0) {
        first.setEnabled(true);
        last.setEnabled(true);
        prev.setEnabled(false);
        if (numObjs > 1) {
          next.setEnabled(true);
          // last.setEnabled(true);
        } else {
          next.setEnabled(false);
          // last.setEnabled(false);
        }
      } else if (count == array.length - 1) {
        first.setEnabled(true);
        prev.setEnabled(true);
        next.setEnabled(false);
        last.setEnabled(false);
      } else if (count > 0 && count < numObjs) {
        first.setEnabled(true);
        prev.setEnabled(true);
        next.setEnabled(true);
        last.setEnabled(true);
      }
      searchList.setSelectedIndex(count);
      // highlight the text
      // searchList.set
      // notify listeners
      if (raiseEvent)
        notifyListeners();
    } else {
      searchList.setEnabled(false);
      first.setEnabled(false);
      prev.setEnabled(false);
      next.setEnabled(false);
      last.setEnabled(false);
    }
  }

  /**
   * Return the currently selected object
   * 
   * @return
   */
  public Object getSelectedObject() {
    return current;
  }

  /**
   * Return the scrollable record component created by this class for display in
   * the user application
   * 
   * @return
   */
  public JComponent getUI() {
    return main;
  }

  /**
   * Register an {@link ActionListener} object to be notified when the user has
   * moved to a new record
   * 
   * The handling code should cast the source object of the {@link ActionEvent}
   * object to <code>RecordScroller</code> and use the
   * <code>getSelectedObject()</code> method to access the currently active
   * record
   * 
   * @param listener
   */
  public void addListener(ActionListener listener) {
    listeners.add(listener);
  }

  public void addNew(Object obj, boolean moveToNew) {
    if (obj != null) {
      // check duplicate
      if (objects == null) {
        objects = new LinkedList();
        objects.add(obj);
      } else if (!objects.contains(obj)) {
        objects.add(obj);
      }
      // array = objects.toArray();
      initData();
      if (moveToNew) {
        if (!stringable) {
          count = objects.size() - 1;
          lastObj = array[array.length - 1];
          current = lastObj;
          moveTo(count, true);
        }
      }         
    }
  }

  private void notifyListeners() {
    ActionEvent e = new ActionEvent(this, RecordEvent.ITEM_CHANGED.ordinal(), "NewRecord");
    for (Iterator lit = listeners.iterator(); lit.hasNext();) {
      ActionListener l = (ActionListener) lit.next();
      l.actionPerformed(e);
    }
  }

  private void invokeAddNew() {
    ActionEvent e = new ActionEvent(this, RecordEvent.NEW_ITEM.ordinal(), "AddItem");
    for (Iterator lit = listeners.iterator(); lit.hasNext();) {
      ActionListener l = (ActionListener) lit.next();
      l.actionPerformed(e);
    }
  }

  class CommandHandler extends KeyAdapter implements ActionListener,
      ItemListener {
    public CommandHandler() {
    }

    public void actionPerformed(ActionEvent e) {
      Object src = e.getSource();
      if (src == first) {
        // scroll first
        current = firstObj;
        count = 0;
        updateState(true);
      } else if (src == prev) {
        count = count - 1;
        current = array[count];
        updateState(true);
      } else if (src == next) {
        count++;
        current = array[count];
        updateState(true);
      } else if (src == last) {
        count = array.length - 1;
        current = lastObj;
        updateState(true);
      } else if (src == add) {
        // ask caller to provide the new object
        // then add this as a new item
        invokeAddNew();
      }
    }

    public void itemStateChanged(ItemEvent ie) {
      int state = ie.getStateChange();
      // int evid = ie.getID();

      if (state == ItemEvent.SELECTED) {
        Object item = ie.getItem();
        // System.out.println("combobox selected item " + item);
        moveTo(item, false);
      }
    }

    public void keyPressed(KeyEvent e) {
      // search the selected item
      String k = e.getKeyChar() + "";

      if (stringable) {
        for (int i = 0; i < array.length; i++) {
          String objStr = array[i] + "";
          if (objStr.startsWith(k)) {
            current = array[i];
            count = i;
            updateState(true);
            break;
          }
        }
      } else {
        try {
          int i = Integer.parseInt(k);
          if (i >= 0 && i < array.length) {
            current = array[i];
            count = i;
            updateState(true);
          }
        } catch (NumberFormatException ex) {
          // wrong index
        }
      }
    }
  }

  private void moveTo(Object item, boolean raiseEvent) {
    if (stringable) {
      for (int i = 0; i < array.length; i++) {
        if (item.equals(array[i])) {
          current = item;
          count = i;
          updateState(raiseEvent);
          break;
        }
      }
    } else {
      if (item instanceof String && item.toString().equals("")) {
        // ignore
      } else {
        int i = ((Integer) item).intValue() - 1;
        if (i >= 0 && i < array.length) {
          count = i;
          current = array[i];
          updateState(raiseEvent);
        }
      }
    }
  }

  private void moveTo(int index, boolean raiseEvent) {
    if (array == null) return;
    
    if (index < 0 || index >= array.length) {
      index = 0;
    }

    count = index;
    current = array[index];
    updateState(raiseEvent);
  }

  public void clear() {
    objects = null;
    initData();
    updateState(false);
  }

  public static void main(String[] args) {
    JFrame test = new JFrame("test");

    test.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    Collection col = new LinkedList();

    Collections.addAll(col, "item1", "item2",// ,
        "item3", "item4", "item5");
    // RecordScroller rs = new RecordScroller(col, false);
    RecordScroller rs = new RecordScroller(true);

    // test.setPreferredSize(new Dimension(200,200));
    test.getContentPane().add(rs.getUI());

    test.pack();
    test.setVisible(true);

    try {
      Thread.sleep(2000);
    } catch (Exception ex) {
    }

    rs.setData(col, true, 4);
  }

}
