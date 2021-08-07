package jda.modules.setup.init.lang.vi;

import jda.modules.dcsl.syntax.DClass;
import jda.modules.mccl.conceptmodel.view.Style;

/**
 * Represents Vietnamese labels.
 * 
 * @author dmle
 * 
 */
@DClass(schema="app_config_vi")
public class Label extends jda.modules.mccl.conceptmodel.view.Label {

// v3.1: not used  
//  public static final Label Desktop = new Label("Cửa sổ chính");
//  public static final Label MenuBar = new Label("Thanh Menu");
//  public static final Label ToolBar = new Label("Thanh công cụ");
//  public static final Label StatusBar = new Label("Thanh trạng thái");
//
//  // / menu bar regions
//  public static final Label File = new Label("Tệp");
//  public static final Label Tools = new Label("Công cụ");
//  public static final Label Options = new Label("Tùy chọn");
//  public static final Label Help = new Label("Trợ giúp");
//
//  // Tools menu
//  public static final Label SearchToolBar = new Label("Tìm...");
//  public static final Label ToolReport = new Label("Báo cáo");

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
    return "Label_vi<"+getId()+","+getTypeId()+","+getValue()+">";
  }
}
