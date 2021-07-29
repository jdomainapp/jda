package jda.modules.exportdoc.model;

import static jda.modules.exportdoc.util.table.Table.Prop.MarginPage;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.print.attribute.standard.MediaSizeName;

import jda.modules.common.collection.Map;
import jda.modules.common.types.properties.PropertyName;
import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DCSLConstants;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DOpt;
import jda.modules.dcsl.syntax.Select;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.dcsl.syntax.DOpt.Type;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.exportdoc.util.table.Cell;
import jda.modules.exportdoc.util.table.Row;
import jda.modules.exportdoc.util.table.Table;
import jda.modules.exportdoc.util.table.Table.Prop;
import jda.modules.mccl.syntax.MCCLConstants.PageFormat;
import jda.modules.mccl.syntax.MCCLConstants.PaperSize;
import jda.mosa.view.assets.JDataContainer;
import jda.util.properties.PropertySet;

/**
 * @overview 
 *  Represents a data document, whose content is generated from a collection of domain objects. 
 *  
 * @author dmle
 *
 */
@DClass(serialisable=false)
public class DataDocument<T extends Page> {
  
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,optional=false)
  private int id;
  private static int idCounter;

  /** v3.0: the data container whose data are to be exported into {@link #pages} */
  private JDataContainer dataContainer;

  @DAttr(name="name",type=DAttr.Type.String,length=20)
  private String name;

  @DAttr(name="docTitle",type=DAttr.Type.String,length=100)
  private String docTitle;

  @DAttr(name="pages",type=DAttr.Type.Collection,mutable=false,
      filter=@Select(clazz=Page.class,attributes={"outputFile", "doc"}))
  @DAssoc(ascName="doc-has-pages",role="doc",ascType=AssocType.One2Many,endType=AssocEndType.One,
    associate=@Associate(type=Page.class,cardMin=0,cardMax=DCSLConstants.CARD_MORE))  
  private List<T> pages;
  
  private Map<Prop,Object> props;

  //TODO: make attribute
  private Dimension pageSize;
  private Table<Row<Cell>> titleTable;
  private int pageTopMargin;
  private int pageBottomMargin;
  
  /**the print configuration of this
   * @version 3.1
   * */
  private PropertySet printCfg;
  
  public static final MediaSizeName defaultMediaSize = MediaSizeName.ISO_A4;
  public static final int defaultPageOrientation = java.awt.print.PageFormat.PORTRAIT;
  
  private static final int DEF_PAGE_MARGIN = 50;
  private static final Dimension DEF_PAGE_SIZE = new Dimension(800,1800);

  public DataDocument() {
    idCounter++;
    id=idCounter;
    
    props = new Map<>();

    pageSize = DEF_PAGE_SIZE;
    pageTopMargin = DEF_PAGE_MARGIN;
    pageBottomMargin = DEF_PAGE_MARGIN;
  }
  
  public int getId() {
    return id;
  }
  
  public DataDocument(String name, String docTitle) {
    this.name = name;
    this.docTitle = docTitle;
  }

  public void setDataContainer(JDataContainer dataContainer) {
    this.dataContainer = dataContainer;
  }

  public JDataContainer getDataContainer() {
    return this.dataContainer;
  }
  
  public String getName() {
    return name;
  }

  public String getDocTitle() {
    return docTitle;
  }

  public void setDocTitle(String title) {
    this.docTitle = title;
  }


  public void setName(String name) {
    this.name = name;
  }

  public void setPageSize(int width, int height) {
    this.pageSize.setSize(width, height);
  }

  /**
   * @effects 
   *  return the configured page size for this
   */
  public Dimension getPageSize() {
    return pageSize;
  }

  public Collection<T> getPages() {
    return pages;
  }
  
  @DOpt(type=Type.LinkAdder)
  public void addPage(T page) {
    if (pages == null) pages = new ArrayList();
    
    if (!pages.contains(page))
      pages.add(page);
  }

  @DOpt(type=Type.LinkAdder)
  public void addPage(Collection<T> newPages) {
    if (pages == null) pages = new ArrayList();
    
    for (T page : newPages) {
      if (!pages.contains(page))
        pages.add(page);
    }
  }
  
  public boolean isEmpty() {
    return pages == null;
  }


  public T getFirstPage() {
    if (pages != null && pages.size() > 0)
      return pages.get(0);
    else
      return null;
  }

  public T getLastPage() {
    if (pages != null)
      return pages.get(pages.size()-1);
    else
      return null;
  }

  public T getPageAtRow(int rowIndx) {
    if (pages != null) {
      for (T page: pages) {
        if (page.containsRow(rowIndx)) {
          return page;
        }
      }
    }
    
    return null;
  }


  /**
   * @effects 
   *  if <tt>page</tt> cannot contain more data rows
   *    return true
   *  else
   *    return false
   */
  public boolean isFull(T page) {
    // get the height of the standard content area of a page
    int contentH = getDefaultContentHeight();
    
    // compare total data row height (plus data header height, if used) against contentH
    // TODO: to plus one row to the total height  
    int dataHeight = page.getAggregatedDataHeight();
    if (dataHeight >= contentH)
      return true;
    else
      return false;
  }
  
  /**
   * @effects 
   *  return the height of the standard content area of a page (i.e. page.height - (pageTopMargin + pageBottomMargin))
   */
  private int getDefaultContentHeight() {
    return ((int) pageSize.getHeight()) - (pageTopMargin + pageBottomMargin); 
  }

  public void setTitleTable(Table<Row<Cell>> titleTable) {
    this.titleTable = titleTable;
  }

  public Table<Row<Cell>> getTitleTable() {
    return titleTable;
  }
  
  /**
   * @effects 
   *  clear this.content
   */
  public void clear() {
    pages = null;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()+" (" + id + ")";
  }

  // PROPERTIES
  public int getPageMargin() {
    //return printCfg.getPageMargin();
    return getProperty().getIntegerValue(MarginPage, DEF_PAGE_MARGIN);
  }
  
  public void setProperty(Object...propValPairs) {
    props.put(propValPairs);
  }
  
  public Map<Prop,Object> getProperty() {
    return props;
  }

  /**
   * @effects
   *  if {@link #printCfg} != null
   *    return the value of page orientation in {@link #printCfg}
   *  else 
   *    return {@link #defaultPageOrientation} 
   */
  public int getPageOrientation() {
    if (printCfg != null) {
      PageFormat pf = printCfg.getPropertyValue(PropertyName.pageFormat, PageFormat.class, PageFormat.Portrait);
      
      return pf.getOrientation();
    } else {
      return defaultPageOrientation;
    }
  }

  /**
   * @effects
   *  if {@link #printCfg} != null 
   *    return the page media size in {@link #printCfg}
   *  else
   *    return {@link #defaultMediaSize}
   */
  public MediaSizeName getMediaSizeName() {
    if (printCfg != null) {
      PaperSize paperSize = printCfg.getPropertyValue(PropertyName.paperSize, PaperSize.class, PaperSize.A4);

      // convert to standard media size
      return paperSize.getMediaSize();
    } else {
      return defaultMediaSize;
    }
  }

  /**
   * @effects 
   *  sets this.<tt>printCfg</tt> to <tt>printCfg</tt>
   */
  public void setPrintConfig(PropertySet printCfg) {
    this.printCfg = printCfg;
  }
}
