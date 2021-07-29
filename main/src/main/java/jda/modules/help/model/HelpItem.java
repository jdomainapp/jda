package jda.modules.help.model;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.modules.mccl.conceptmodel.view.Region;

@DClass(schema=DCSLConstants.CONFIG_SCHEMA)
public class HelpItem {
  @DAttr(name="id",type=Type.Long,id=true,auto=true,mutable=false,optional=false)
  private long id;

  @DAttr(name="region", type=Type.Domain,length=30,optional=false,serialisable=false)
  @DAssoc(ascName="region-has-helpitem",role="helpitem",
  ascType=AssocType.One2One,endType=AssocEndType.One,
  associate=@Associate(type=Region.class,cardMin=1,cardMax=1,determinant=true))
	private Region region;
	
  @DAttr(name="description", type=Type.String, length=255,optional=false)
	private String description;
	
	@DAttr(name="helpContent", type=Type.Domain, optional=false)
	@DAssoc(ascName="helpcontent-has-helpitems",role="helpitems",
	    ascType=AssocType.One2Many,endType=AssocEndType.Many,
	    associate=@Associate(type=HelpContent.class,cardMin=1,cardMax=1),
	    dependsOn=true)
	private HelpContent helpContent;
	
	public HelpItem(Long id, Region region, String desc, HelpContent helpContent) {
		this.region = region;
		this.description = desc;
		this.helpContent = helpContent;
		this.id = nextId(id);
	}
	
	public HelpItem(Region region, String desc, HelpContent helpContent) {
    this(null, region, desc, helpContent);
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

  public Region getRegion() {
		return region;
	}

	public void setRegion(Region region) {
		this.region = region;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String desc) {
		this.description = desc;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((region == null) ? 0 : region.hashCode());
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
		HelpItem other = (HelpItem) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (region == null) {
			if (other.region != null)
				return false;
		} else if (!region.equals(other.region))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "HelpContent [region=" + region + ", desc=" + description + "]";
	}

  public HelpContent getHelpContent() {
    return helpContent;
  }

  public void setHelpContent(HelpContent helpContent) {
    this.helpContent = helpContent;
  }
}
