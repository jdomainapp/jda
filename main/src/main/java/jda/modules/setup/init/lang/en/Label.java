package jda.modules.setup.init.lang.en;

import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.conceptmodel.view.Style;

/**
 * Represents English labels.
 * 
 * @author dmle
 * 
 */
@DClass(schema="app_config_en")
public class Label extends jda.modules.mccl.conceptmodel.view.Label {
  
  // v3.1: not used
//  public static final Label Desktop = new Label("Desktop");
//  public static final Label MenuBar = new Label("Menu bar");
//  public static final Label ToolBar = new Label("Tool bar");
//  public static final Label StatusBar = new Label("Status bar");
//
//  // / menu bar regions
//  public static final Label File = new Label("File");
//  public static final Label Tools = new Label("Tools");
//  public static final Label Options = new Label("Options");
//  public static final Label Help = new Label("Help");
//
//  // Tools menu
//  public static final Label SearchToolBar = new Label("Find...");
//  public static final Label ToolReport = new Label("Report");

  private static int idCounter;

  public Label(String value) {
    super(value);
  }
  
  public Label(Integer id, Integer typeId, String value,
      Style style // v2.7
      ) {
    super(id,typeId,value, style);
  }

  @Override
  protected int nextTypeId(Integer currID) {
    if (currID == null) { // generate one
      idCounter++;
      return idCounter;
    } else { // update
      int num;
      num = currID.intValue();

      if (num > idCounter)
        idCounter = num;

      return currID;
    }
  }
  
  @Override
  public String toString() {
    return "Label_en<"+getId()+","+getTypeId()+","+getValue()+">";
  }

}
