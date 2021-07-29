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
import jda.modules.mccl.conceptmodel.Configuration;

@DClass(schema = DCSLConstants.CONFIG_SCHEMA)
public class AppHelp {
  @DAttr(name="id",type=Type.Long,id=true,auto=true,mutable=false,optional=false)
  private long id;
  @DAttr(name="config", type=Type.Domain, optional=false)
  private Configuration config;
  
  @DAttr(name="helpContents", type= Type.Collection, optional=false, serialisable=false,
      filter=@Select(clazz=HelpContent.class))
  @DAssoc(ascName="apphelp-has-helpcontents",role="apphelp",
  ascType=AssocType.One2Many,endType=AssocEndType.One,
  associate=@Associate(type=HelpContent.class,cardMin=1,cardMax=DCSLConstants.CARD_MORE))
  private List<HelpContent> helpContents;
  
  // derived attribute
  private int helpContentsCount;
  
  public AppHelp(Long id, Configuration config) {
    this (id, config, null);
  }
  
  public AppHelp(Configuration config) {
    this(null, config, null);
  }
  
  public AppHelp(Long id, Configuration config, List<HelpContent> helpContents) {
    this.config = config;
    this.id = nextId(id);
    
    if (helpContents == null) {
      this.helpContents = new ArrayList<>();
      helpContentsCount = 0;
    } else {
      this.helpContents = helpContents;
      helpContentsCount = helpContents.size();
    }
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addHelpContent(HelpContent helpContent) {
    if (!helpContents.contains(helpContent)) {
      helpContents.add(helpContent);
//      helpContent.setAppHelp(this);
    }

    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewHelpContent(HelpContent helpContent) {
    helpContents.add(helpContent);
    helpContentsCount++;
    
    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkAdder)
  public boolean addHelpContent(List<HelpContent> helpContents) {
    for (HelpContent helpContent : helpContents) {
      if (!this.helpContents.contains(helpContent)) {
        this.helpContents.add(helpContent);
//        helpContent.setAppHelp(this);
      }
    }
    
    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkAdderNew)
  public boolean addNewHelpContent(List<HelpContent> helpContents) {
    this.helpContents.addAll(helpContents);
    helpContentsCount += helpContents.size();
    
    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkRemover)
  public boolean removeHelpContent(HelpContent helpContent) {
    boolean removed = helpContents.remove(helpContent);
    
    if (removed) {
      helpContentsCount--;
    }
    
    return false;
  }
  
  @DOpt(type=DOpt.Type.LinkCountGetter)
  public Integer getHelpContentsCount() {
    return helpContentsCount;
  }
  
  @DOpt(type=DOpt.Type.LinkCountSetter)
  public void setHelpContentsCount(int helpContentsCount) {
    this.helpContentsCount = helpContentsCount;
  }
  
  public List<HelpContent> getHelpContents() {
    return helpContents;
  }

  public void setHelpContents(List<HelpContent> helpContents) {
    this.helpContents = helpContents;
    helpContentsCount = helpContents.size();
  }

  private static long nextId(Long currId) {
    // use current time as id
    if (currId == null)
      return System.nanoTime();
    else
      return currId;
  }

  public Configuration getConfig() {
    return config;
  }

  public void setConfig(Configuration config) {
    this.config = config;
  }

  public long getId() {
    return id;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((config == null) ? 0 : config.hashCode());
    result = prime * result
        + ((helpContents == null) ? 0 : helpContents.hashCode());
    result = prime * result + (int) (id ^ (id >>> 32));
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
    AppHelp other = (AppHelp) obj;
    if (config == null) {
      if (other.config != null)
        return false;
    } else if (!config.equals(other.config))
      return false;
    if (helpContents == null) {
      if (other.helpContents != null)
        return false;
    } else if (!helpContents.equals(other.helpContents))
      return false;
    if (id != other.id)
      return false;
    return true;
  }

  @Override
  public String toString() {
    return "AppHelp [id=" + id + ", config=" + config + ", helpContents="
        + helpContents + "]";
  }
}
