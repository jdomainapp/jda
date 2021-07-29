package jda.util;

import jda.modules.mccl.conceptmodel.controller.LAName;
import jda.mosa.controller.ControllerBasic.DataController;
import jda.mosa.controller.assets.datacontroller.DataPanelController;
import jda.mosa.controller.assets.datacontroller.ObjectTableController;
import jda.mosa.view.assets.layout.BasicTwoColumnLayoutBuilder;

/**
 * @overview 
 *
 * @author Duc Minh Le (ducmle)
 *
 * @version 
 */
public class SysConstants {

  // MCCL ///
  public static final LAName NullCommand = LAName.Null;
  // v3.0
  public static final Class<? extends DataController> DEFAULT_TABLE_DATA_CONTROLLER = ObjectTableController.class
      .asSubclass(DataController.class);
  public static final Class<? extends DataController> DEFAULT_PANEL_DATA_CONTROLLER = DataPanelController.class
  .asSubclass(DataController.class);
  // v2.7.4
  public static final Class DEFAULT_LAYOUT_BUILDER = BasicTwoColumnLayoutBuilder.class;

}
