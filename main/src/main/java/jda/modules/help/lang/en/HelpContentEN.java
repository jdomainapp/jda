package jda.modules.help.lang.en;

import java.util.List;

import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.help.model.AppHelp;
import jda.modules.help.model.HelpContent;
import jda.modules.help.model.HelpItem;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;

@DClass(schema = DCSLConstants.CONFIG_SCHEMA + "_en")
public class HelpContentEN extends HelpContent {

  public HelpContentEN(ApplicationModule module, String overview,
      String titleDesc, AppHelp appHelp, List<HelpItem> helpItems) {
    super(module, overview, titleDesc, appHelp, helpItems);
  }

  public HelpContentEN(Long id, ApplicationModule module, String overview,
      String titleDesc, AppHelp appHelp, List<HelpItem> helpItems) {
    super(id, module, overview, titleDesc, appHelp, helpItems);
  }

  @Override
  public String toString() {
    return "HelpContentEN [" + getId() + ", " + getOverview() + ", "
        + getTitleDesc() + "]";
  }
}
