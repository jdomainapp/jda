/**
 * 
 */
package jda.modules.dcsl.parser.statespace.metadef;

import java.lang.annotation.Annotation;
import java.util.Arrays;

import jda.modules.common.CommonConstants;
import jda.modules.common.types.Null;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.Select;

/**
 * @overview 
 *  An implementation of {@link DAttr} to encapsulate property definitions extracted directly from 
 *  the source code.
 *  
 * @author dmle 
 *
 * @version 3.4
 */
public class DAttrDef extends MetaAttrDef implements DAttr {

  private static final Object DefaultSelect = new Select() {
    @Override
    public Class<? extends Annotation> annotationType() {
      return Select.class;
    }

    @Override
    public String[] attributes() {
      return CommonConstants.EmptyArray;
    }

    @Override
    public Class clazz() {
      return Null.class;
    }
    
    @Override
    public String toString() {
      return "@Select()";
    }
  };

  // toStringFormat
  private static final String toStringFormat = 
      "%s: auto=%b, autoIncrement=%b, defaultValue=%s, defaultValueFunction=%b, derivedFrom=%s, filter=%s, format=%s, id=%b, length=%d, max=%f, min=%f, mutable=%b, name=%s, optional=%b, serialisable=%b, sourceAttribute=%s, sourceQuery=%b, sourceQueryHandler=%b, type=%s, unique=%b, virtual=%b";
  
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String toString() {
    return String.format(toStringFormat, 
        this.getClass().getSimpleName(),
        auto(), autoIncrement(), defaultValue(), defaultValueFunction(), Arrays.toString(derivedFrom()), 
        filter(), format(), id(), length(), max(),
        min(), mutable(), name(), optional(), serialisable(),
        sourceAttribute(), sourceQuery(), sourceQueryHandler(), type(), unique(),
        virtual()
        );
  }

  /* (non-Javadoc)
   * @see java.lang.annotation.Annotation#annotationType()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Class<? extends Annotation> annotationType() {
    return DAttr.class;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#auto()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean auto() {
    return (Boolean) propValMap.getOrDefault("auto", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#autoIncrement()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean autoIncrement() {
    return (Boolean) propValMap.getOrDefault("autoIncrement", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#defaultValue()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String defaultValue() {
    return (String) propValMap.getOrDefault("defaultValue", CommonConstants.NullString);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#defaultValueFunction()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean defaultValueFunction() {
    return (Boolean) propValMap.getOrDefault("defaultValueFunction", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#derivedFrom()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String[] derivedFrom() {
    return (String[]) propValMap.getOrDefault("derivedFrom", CommonConstants.EmptyArray);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#filter()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Select filter() {
    return (Select) propValMap.getOrDefault("filter", DefaultSelect);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#format()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Format format() {
    return (Format) propValMap.getOrDefault("format", Format.Nil);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#id()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean id() {
    return (Boolean) propValMap.getOrDefault("id", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#length()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public int length() {
    return (Integer) propValMap.getOrDefault("length", -1);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#max()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public double max() {
    return (Double) propValMap.getOrDefault("max", CommonConstants.DEFAULT_MAX_VALUE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#min()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public double min() {
    return (Double) propValMap.getOrDefault("min", CommonConstants.DEFAULT_MIN_VALUE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#mutable()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean mutable() {
    return (Boolean) propValMap.getOrDefault("mutable", true);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#name()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String name() {
    return (String) propValMap.get("name").toString();
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#optional()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean optional() {
    return (Boolean) propValMap.getOrDefault("optional", Boolean.TRUE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#serialisable()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean serialisable() {
    return (Boolean) propValMap.getOrDefault("serialisable", Boolean.TRUE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#sourceAttribute()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String sourceAttribute() {
    return (String) propValMap.getOrDefault("sourceAttribute", CommonConstants.NullString);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#sourceQuery()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean sourceQuery() {
    return (Boolean) propValMap.getOrDefault("sourceQuery", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#sourceQueryHandler()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean sourceQueryHandler() {
    return (Boolean) propValMap.getOrDefault("sourceQueryHandler", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#type()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public Type type() {
    return (Type) propValMap.get("type");
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#unique()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean unique() {
    return (Boolean) propValMap.getOrDefault("unique", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#virtual()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean virtual() {
    return (Boolean) propValMap.getOrDefault("virtual", Boolean.FALSE);
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#cid()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public boolean cid() {
    // TODO Auto-generated method stub
    return false;
  }

  /* (non-Javadoc)
   * @see domainapp.basics.model.meta.DAttr#ccid()
   */
  /**
   * @effects 
   * 
   * @version 
   */
  @Override
  public String ccid() {
    // TODO Auto-generated method stub
    return null;
  }
}
