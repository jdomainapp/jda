package jda.modules.exportdoc.page.model;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import jda.modules.dcsl.syntax.DAssoc;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAssoc.AssocEndType;
import jda.modules.dcsl.syntax.DAssoc.AssocType;
import jda.modules.dcsl.syntax.DAssoc.Associate;
import jda.modules.exportdoc.model.DataDocument;

/**
 * @overview
 *  Represents a page in {@link DataDocument}, whose content is displayed on an object form (like a standard domain object). 
 *  
 * @author dmle
 *
 */
@DClass(serialisable=false)
public abstract class Page {

  protected StringBuffer content;
  
  private InputStream contentInputStream;

  protected Dimension docSize;
  
  @DAttr(name="id",id=true,auto=true,type=DAttr.Type.Integer,mutable=false,optional=false)
  private int id;
  private static int idCounter;

  @DAttr(name="outputFile",type=DAttr.Type.File,optional=false)
  private File outputFile;

  @DAttr(name="contentString",type=DAttr.Type.String, mutable=false,optional=false)
  private String contentString;

  @DAttr(name="doc",type=DAttr.Type.Domain, mutable=false,optional=false)
  @DAssoc(ascName="doc-has-pages",role="pages",ascType=AssocType.One2Many,endType=AssocEndType.Many,
    associate=@Associate(type=DataDocument.class,cardMin=1,cardMax=1,determinant=true))
  private DataDocument doc;

  private List<Integer> rowsCoverage;
  
  // association 
  public Page() {
    idCounter++;
    id=idCounter;
    content = new StringBuffer();
  }

  public abstract void init();
  
  /**
   * @effects 
   *  return the sum of data row heights (plus data header height, if used) 
   */
  public abstract int getAggregatedDataHeight();
  
  public int getId() {
    return id;
  }

  public String getContentString() {
    if (contentString == null && content != null)
      contentString = content.toString();
    
    return contentString;
  }
  
  public InputStream getContentStream() {
    if (content != null) {
      if (contentInputStream == null)
        contentInputStream = new ByteArrayInputStream(content.toString().getBytes());
    } 
    
    return contentInputStream;
  }
  
  public StringBuffer getContent() {
    return content;
  }

  public void setSize(int docWidth, int docHeight) {
    if (docSize == null)
      docSize = new Dimension();
    
    docSize.setSize(docWidth, docHeight);
  }
  
  public File getOutputFile() {
    return outputFile;
  }

  public void setOutputFile(File contentFile) {
    this.outputFile = contentFile;
  }

  /**
   * @effects 
   *  if this has content
   *    return the aggregated (width,height) of the entire content block in pixels
   *  else
   *    return null 
   */
  public Dimension getSize() {
    return docSize;
  }

  /**
   * @effects 
   *  updates row coverage of this to contain data row <tt>rowIndx</tt>
   */
  public void addRowCoverage(int rowIndx) {
    if (rowsCoverage == null) rowsCoverage = new ArrayList<>();
    rowsCoverage.add(rowIndx);
  }
  
  /**
   * @effects 
   *  if the row coverage of this contains <tt>rowIndx</tt>
   *    return true
   *  else
   *    return false
   */
  public boolean containsRow(int rowIndx) {
    return (rowsCoverage != null && rowsCoverage.contains(rowIndx));
  }
  
  /**
   * @effects 
   *  finalise the content of this (ready for display)
   */
  public abstract void finalise();
  
  @Override
  public String toString() {
    return this.getClass().getSimpleName()+"(" + id + ")";
  }

//  public void close() {
//    content = null;
//    contentInputStream = null;
//  }
  
}
