package jda.modules.help.model;

import java.util.ArrayList;
import java.util.List;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.module.ApplicationModule;

@DClass(schema = DCSLConstants.CONFIG_SCHEMA, languageAware = true)
public class HelpContent {
  @DAttr(name = "id", type = Type.Long, id = true, auto = true, mutable = false, optional = false)
  private long id;
  
  @DAttr(name = "module", type = Type.Domain, optional = false)
  private ApplicationModule module;
  
  @DAttr(name = "overview", type = Type.String, optional = false)
  private String overview;
  
  @DAttr(name = "titleDesc", type = Type.String, optional = false)
  private String titleDesc;

  @DAttr(name = "appHelp", type = Type.Domain, optional = false)
  @DAssoc(ascName = "apphelp-has-helpcontents", role = "helpcontents", 
  ascType = AssocType.One2Many, endType = AssocEndType.Many, 
  associate = @Associate(type = AppHelp.class, cardMin = 0, cardMax = 1),
  dependsOn=true)
  private AppHelp appHelp;
  
  @DAttr(name = "helpItems", type = Type.Collection, serialisable = false, optional=false,
      filter=@Select(clazz=HelpItem.class))
  @DAssoc(ascName = "helpcontent-has-helpitems", role = "helpcontent", 
  ascType = AssocType.One2Many, endType = AssocEndType.One, 
  associate = @Associate(type = HelpItem.class, 
  cardMin = 1, cardMax = DCSLConstants.CARD_MORE))
  private List<HelpItem> helpItems;


  // derived attribute
  private int helpItemsCount;

  public HelpContent(ApplicationModule module, String overview,
      String titleDesc, AppHelp appHelp) {
    this (null, module, overview, titleDesc, appHelp, null);
  }

  public HelpContent(ApplicationModule module, String overview, String titleDesc, 
      AppHelp appHelp, List<HelpItem> helpItems) {
    this(null, module, overview, titleDesc, appHelp, helpItems);
  }

  public HelpContent(Long id, ApplicationModule module, String overview,
      String titleDesc, AppHelp appHelp, List<HelpItem> helpItems) {
    this.module = module;
    this.overview = overview;
    this.titleDesc = titleDesc;
    this.appHelp = appHelp;
    this.id = nextId(id);

    if (helpItems == null) {
      this.helpItems = new ArrayList<>();
      helpItemsCount = 0;
    } else {
      this.helpItems = helpItems;
      helpItemsCount = helpItems.size();
    }
  }

  @DOpt(type = DOpt.Type.LinkAdder)
  public boolean addHelpItem(HelpItem helpItem) {
    if (!helpItems.contains(helpItem)) {
      helpItems.add(helpItem);
//      helpItem.setHelpContent(this);
    }

    return false;
  }

  @DOpt(type = DOpt.Type.LinkAdderNew)
  public boolean addNewHelpItem(HelpItem helpItem) {
    helpItems.add(helpItem);
    helpItemsCount++;
    
//    helpItem.setHelpContent(this);

    return false;
  }

  @DOpt(type = DOpt.Type.LinkAdder)
  public boolean addHelpItem(List<HelpItem> helpItems) {
    for (HelpItem helpItem : helpItems) {
      if (!this.helpItems.contains(helpItem)) {
        this.helpItems.add(helpItem);
        
//        helpItem.setHelpContent(this);
      }
    }

    return false;
  }

  @DOpt(type=DOpt.Type.LinkAdderNew)
	public boolean addNewHelpItem(List<HelpItem> helpItems) {
	  this.helpItems.addAll(helpItems);
	  helpItemsCount += helpItems.size();
	  
	  return false;
	}
  
  @DOpt(type=DOpt.Type.LinkRemover)
  public boolean removeHelpItem(HelpItem helpItem) {
    boolean removed = helpItems.remove(helpItem);
    
    if (removed) {
      helpItem.setHelpContent(null);
      helpItemsCount--;
    }
    
    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getHelpItemsCount() {
    return helpItemsCount;
  }
  
  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setHelpItemsCount(int helpItemsCount) {
    this.helpItemsCount = helpItemsCount;
  }
  
  public List<HelpItem> getHelpItems() {
    return helpItems;
  }

  public void setHelpItems(List<HelpItem> helpItems) {
    this.helpItems = helpItems;
    helpItemsCount = helpItems.size();
  }

  public AppHelp getAppHelp() {
    return appHelp;
  }

  public void setAppHelp(AppHelp appHelp) {
    this.appHelp = appHelp;
  }

  private static long nextId(Long currId) {
    // use current time as id
    if (currId == null)
      return System.nanoTime();
    else
      return currId;
  }

  public long getId() {
    return id;
  }

  public ApplicationModule getModule() {
    return module;
  }

  public void setModule(ApplicationModule module) {
    this.module = module;
  }

  public String getOverview() {
    return overview;
  }

  public void setOverview(String overview) {
    this.overview = overview;
  }

  public String getTitleDesc() {
    return titleDesc;
  }

  public void setTitleDesc(String titleDesc) {
    this.titleDesc = titleDesc;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((helpItems == null) ? 0 : helpItems.hashCode());
    result = prime * result + ((module == null) ? 0 : module.hashCode());
    result = prime * result + ((overview == null) ? 0 : overview.hashCode());
    result = prime * result + ((titleDesc == null) ? 0 : titleDesc.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    HelpContent other = (HelpContent) obj;
    if (helpItems == null) {
      if (other.helpItems != null)
        return false;
    } else if (!helpItems.equals(other.helpItems))
      return false;
    if (module == null) {
      if (other.module != null)
        return false;
    } else if (!module.equals(other.module))
      return false;
    if (overview == null) {
      if (other.overview != null)
        return false;
    } else if (!overview.equals(other.overview))
      return false;
    if (titleDesc == null) {
      if (other.titleDesc != null)
        return false;
    } else if (!titleDesc.equals(other.titleDesc))
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "HelpItem [module=" + module + ", overview=" + overview
        + ", titleDesc=" + titleDesc + ", helpItems=" + helpItems + "]";
  }
}
