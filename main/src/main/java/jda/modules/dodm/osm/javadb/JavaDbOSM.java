package jda.modules.dodm.osm.javadb;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.mccl.conceptmodel.dodm.OsmConfig;

public class JavaDbOSM extends JavaDbOSMBasic {

  private static final boolean debug = Toolkit.getDebug(JavaDbOSM.class);
  
  public JavaDbOSM(OsmConfig config, DOMBasic dom)
      throws DataSourceException {
    super(config, dom);
  }
}
