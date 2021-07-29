package jda.modules.exportdoc.controller;

import static jda.modules.exportdoc.util.table.Table.Prop.AlignX;
import static jda.modules.exportdoc.util.table.Table.Prop.ColSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.PageWidth;
import static jda.modules.exportdoc.util.table.Table.Prop.PreferredHeight;
import static jda.modules.exportdoc.util.table.Table.Prop.PreferredWidth;
import static jda.modules.exportdoc.util.table.Table.Prop.RowSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.TextColor;
import static jda.modules.exportdoc.util.table.Table.Prop.TextFont;
import static jda.modules.exportdoc.util.table.Table.Prop.Visible;
import static jda.modules.exportdoc.util.table.Table.Prop.WrapText;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import jda.modules.common.CommonConstants;
import jda.modules.common.exceptions.ApplicationRuntimeException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.exportdoc.util.table.Cell;
import jda.modules.exportdoc.util.table.HeaderCell;
import jda.modules.exportdoc.util.table.ImageCell;
import jda.modules.exportdoc.util.table.Row;
import jda.modules.exportdoc.util.table.Table;
import jda.modules.exportdoc.util.table.TableCell;
import jda.modules.exportdoc.util.table.TextCell;
import jda.modules.exportdoc.util.table.Table.TableType;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.modules.mccl.conceptmodel.view.Label;
import jda.modules.mccl.conceptmodel.view.Region;
import jda.modules.mccl.conceptmodel.view.Style;
import jda.modules.mccl.syntax.MCCLConstants;
import jda.modules.mccl.syntax.MCCLConstants.AlignmentX;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.model.Oid;
import jda.mosa.view.View;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.GUIToolkit.ImageType;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.mosa.view.assets.datafields.chooser.JChooserDataField;
import jda.mosa.view.assets.datafields.chooser.JImageChooserField;
import jda.mosa.view.assets.panels.DefaultPanel;
import jda.mosa.view.assets.panels.TitlePanel;
import jda.mosa.view.assets.swing.JHtmlLabel;
import jda.mosa.view.assets.tables.JDataTable;
import jda.util.properties.PropertySet;

/**
 * @overview 
 *  Represents a document
 *  
 * @author dmle
 */
public abstract class DocumentBuilder {

  private DocumentExportController exportCtl;

  private Configuration appConfig;
  
  private String docFile;

  private String exportFolderPath;

  private String templateFolderPath;  // v2.7.4

  // constants
  protected static final String DEF_FONT_FAMILY = "arial" ;

  protected static final String DEF_FONT_SIZE = "12" ;

  private static final AlignmentX DEF_HEADER_ALIGN = AlignmentX.Center;

  protected static final String DEF_CELL_ALIGN = AlignmentX.Left.getHtmlName();
  
  private static final Color DEF_CELL_COLOR = Color.BLACK;

  private static final AlignmentX DEF_TITLE_ALIGNMENT = AlignmentX.Center;
  
  protected static final boolean DEF_TEXT_WRAPPING = true;
  
  protected static final int DEF_ROW_HEIGHT = -1; // auto

  public DocumentBuilder(DocumentExportController exportCtl, Configuration appConfig) {
    this.exportCtl = exportCtl;
    this.appConfig = appConfig;
  }
  
  private void setDocFile(String name) {
    // prepare file name + extension (provided by the subtype)
    docFile = exportFolderPath + File.separator + name+ getFileExtension();
  }
  
  protected String getDocFile() {
    return docFile;
  }
  
  protected abstract String getFileExtension();
  
  protected Configuration getApplicationConfiguration() {
    return appConfig;
  }

  /**
   * @requires 
   *  {@link #init(DataDocument)} has been invoked
   */
  protected String getExportFolderPath() {
    return exportFolderPath;
  }

  /**
   * @requires 
   *  {@link #init(DataDocument)} has been invoked
   */
  protected String getTemplateFolderPath() {
    return templateFolderPath;
  }
  
  /**
   * @modifies this
   * 
   * @effects <pre> 
   *  create a {@link DocumentBuilder} whose type is docType containing 
   *    the data values of the data fields of <tt>dataContainer</tt>
   *    and recursively in its sub-containers (if any)
   *  </pre>
   */
  public static <T extends DocumentBuilder> DocumentBuilder createDocumentBuilderInstance(
      Class<T> builderType,
      DocumentExportController exportCtl,
      Configuration appConfig) throws NotPossibleException {
    // create data document
  
    Constructor<? extends DocumentBuilder> cons = null;
    try {
      cons = builderType.getConstructor(DocumentExportController.class, Configuration.class);
      DocumentBuilder docBuilder = cons.newInstance(exportCtl, appConfig);

      return docBuilder;
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, e, 
          "Không thể tạo đối tượng lớp: {0}.{1}({2})", builderType.getSimpleName(), cons);
    }
  }

  /**
   * @effects 
   *  perform the preparation steps before starting the document building process
   *  (which is performed by {@link #buildContent(DODMBasic, JDataContainer, PropertySet, DataDocument)}
   *  
   *  <br>throws NotPossibleException if failed to initialise
   */
  public void init(DataDocument doc,
      PropertySet printCfg
      ) throws NotPossibleException {
    exportFolderPath = appConfig.getExportFolderPath(); //getAppFolder()+File.separator+"export";
    
    templateFolderPath = exportFolderPath+File.separator+"templates";
    
    File exportFolder = new File(exportFolderPath);
    ToolkitIO.createFolderIfNotExists(exportFolder);
    
    File tempFolder = new File(templateFolderPath);
    ToolkitIO.createFolderIfNotExists(tempFolder);
    
    setDocFile(doc.getName());
  }
  
  /**
   * @effects 
   *  create the document content from the domain objects of <tt>dataContainer</tt> using <tt>printCfg</tt> (if specified)
   *  as the view configuration. 
   */
  public abstract void buildContent(DODMBasic schema, JDataContainer dataContainer, PropertySet printCfg, DataDocument doc);
  
  /**
   * @effects 
   *  perform the final steps before terminating
   */
  public void finalise(DataDocument<? extends Page> doc) {}

//  /**
//   * @effects 
//   *  save the content of this to <tt>file</tt>
//   *  
//   *  throws NotPossibleException if failed to do so
//   */
//  protected abstract void save() throws NotPossibleException;

//  /**
//   * @effects 
//   *  return the document content as <tt>InputStream</tt>; or 
//   *  return <tt>null</tt> if no content is available
//   */
//  public abstract InputStream getContentStream();
//
//  /**
//   * @effects 
//   *  return the document content as <tt>String</tt>; or 
//   *  return <tt>null</tt> if no content is available
//   */
//  public abstract String getContentAsString();

  /**
   * @effects 
   *  end document building process and clear the resources that were used
   */
  public abstract void close();
  
//  /**
//   * @effects 
//   *  if this has content
//   *    return the aggregated (width,height) of the entire content block in pixels (without regarding to pagination)
//   *  else
//   *    return null; 
//   */
//  public abstract Dimension getContentSize();
  
  /**
   * The title component of a data container is either its title panel (for top-level container) or 
   * the title label of the container (for child container).
   * 
   * @effects 
   *  create and return a <tt>Table</tt> model for the title component of <tt>dataContainer</tt>
   */
  protected Table createTitleModel(
      JDataContainer dataContainer,
      Dimension pgSize) {
    
    Table<Row<Cell>>  table;
    final boolean border = false;
    int numCols;

    final ControllerBasic ctl = dataContainer.getController().getCreator(); 
    final View gui = ctl.getGUI();

    if (dataContainer.getParentContainer() == null) {
      // top level container
      TitlePanel title = gui.getTitlePanel();
      if (title != null) {
        JLabel left = (JLabel) title.getComponent(TitlePanel.ComponentIndex.Left);
        JLabel centre = (JLabel) title.getComponent(TitlePanel.ComponentIndex.Centre);
        JLabel right = (JLabel) title.getComponent(TitlePanel.ComponentIndex.Right);
    
        // update the number of table columns based on the title components that are used
        numCols = 1;
        Font font; 
        Color color;
        
        if (left != null) {
          if (left.getIcon() != null) {
            numCols++;
          }
          if (left.getText() != null) {
            numCols++;
          }
        }
        if (right != null) {
          if (right.getIcon() != null) {
            numCols++;
          }
          if (right.getText() != null) {
            numCols++;
          }
        }
        
        // create the table
        table = new Table<>(numCols, TableType.NormalTable, 
            exportCtl.getContainerPrintCfg(dataContainer));
        table.setProperty(PageWidth, (int) pgSize.getWidth());

        //PdfPCell cell;
        // left component (if any)
        ImageIcon icon;
        String text;
        
        Object[] propValPairs = {
            ColSpan, 1
        };
        
        table.beginRow();
        
        if (left != null) {
          icon = (ImageIcon) left.getIcon();
          // the logo (if any)
          if (icon != null) {
            createImageCell(table, icon, "title_left", 
                ImageType.JPEG  // TODO: use field configuration option for this
                );
          }
          
          // the text (if any)
          text = left.getText();
          if (text != null) {
            createTextCellFromLabel(table, left, propValPairs);
          }
        }
        
        // the title text cell
        createTextCellFromLabel(table, centre, propValPairs);
    
        // the right component (if any)
        if (right != null) {
          icon = (ImageIcon) right.getIcon();
          // the logo (if any)
          if (icon != null) {
            createImageCell(table, icon, "title_right", 
                ImageType.JPEG  // TODO: use field configuration option for this
                );
          }
          // the text (if any)
          text = right.getText();
          createTextCellFromLabel(table, right, propValPairs);
        }
        
        table.endRow();
      } else {
        // no title panel -> create a single-column table that has an empty cell
        numCols = 1;
        table = new Table<>(numCols, TableType.NormalTable, exportCtl.getContainerPrintCfg(dataContainer));
        table.setProperty(PageWidth, (int) pgSize.getWidth());
        table.beginRow();
        TextCell tc = TextCell.EmptyCell.clone();
        table.addTextCell(tc);
        table.endRow();
      }
    } else {
      // child container: create a single-column table that has one text cell containing the title label
      Region containerCfg = dataContainer.getContainerConfig();
      Region titleCfg = ctl.getSettingsForChild(containerCfg, "title");
      Style titleStyle = ctl.getStyleSettings(titleCfg);
      Label titleLabel = titleCfg.getLabel();
      numCols = 1;
      table = new Table<>(numCols, TableType.NormalTable, exportCtl.getContainerPrintCfg(dataContainer));
      table.setProperty(PageWidth, (int) pgSize.getWidth());
      table.beginRow();
      int colSpan = 1;
      int width = dataContainer.getGUIComponent().getWidth();
      Graphics g = dataContainer.getGUIComponent().getGraphics(); //gui.getGUI().getGraphics();
      AlignmentX alignX = DEF_TITLE_ALIGNMENT;  
      createTextCellFromLabel(table, g, titleLabel, titleStyle, width, alignX, colSpan);
      table.endRow();
    }

    return table;
  }
  
//  /**
//   * This is the main method that is used to generate the Table model of the data and 
//   * styles contained in a data container. 
//   * It supports different ways of generating the Table model.
//   * @effects 
//   *  creates and return a <tt>Table</tt> whose data and view styles are defined based on 
//   *  <tt>dataContainer</tt>
//   */
//  protected Table<Row<Cell>> createContentTable(DomainSchema schema, JDataContainer dataContainer,
//      Region printCfg, Dimension pgSize, boolean withHeaders) throws IOException {
//    Iterator buffer = dataContainer.getController().getObjectBuffer();
//    return createContentTable(schema, dataContainer, buffer, printCfg, pgSize, withHeaders);
//  }
  
  /**
   * @effects 
   *  creates and return a <tt>Table</tt>, 
   *  whose view styles are defined based on <tt>dataContainer</tt>, 
   *  whose table columns (and headers if <tt>withHeaders=true</tt>) are defined from <tt>attribs</tt>, 
   *  and whose data rows are created from the domain objects in <tt>buffer</tt>
   *  (if any). 
   *  
   *  <p>If buffer is null then the table contain a single empty row.
   */
  protected Table<Row<Cell>> createDocModel(DODMBasic schema,
      JDataContainer dataContainer, Iterator buffer,
      Collection<DAttr> attribs,
      PropertySet printCfg, Dimension pgSize, boolean withHeaders) throws IOException {
    Class containerType = dataContainer.getClass();

    Table<Row<Cell>> table = null;
    
    /**
     * supports two cases:
     * if dataContainer is DocumentExportable
     *  invoke the export() method of it
     * else
     *  invoke a generic export function suitable for it
     */
    if (dataContainer instanceof DocumentExportable) {
      table = ((DocumentExportable) dataContainer).export(this, schema, buffer, printCfg, pgSize, withHeaders);
    } else {
      // for now assume all print types are table
      if (DefaultPanel.class.isAssignableFrom(containerType)) {
        DefaultPanel panel = (DefaultPanel) dataContainer;
        table = (buffer != null) ?
                  createDocModelFromNonEmptyPanelContainer(schema, panel, buffer,
                      attribs, 
                      printCfg, pgSize, withHeaders) :
                  createDocModelFromEmptyPanelContainer(schema, panel, 
                      attribs,
                      printCfg, pgSize, withHeaders);
      } else if (JDataTable.class.isAssignableFrom(containerType)) {
        JDataTable dtable = (JDataTable) dataContainer;
        //table = createTableFromTableContainer(schema, dtable, buffer, printCfg, pgSize, withHeaders);
        table = (buffer != null) ?
            createDocModelFromNonEmptyTableContainer(schema, dtable, buffer, 
                attribs,
                printCfg, pgSize, withHeaders) :
            createDocModelFromEmptyTableContainer(schema, dtable, 
                attribs,
                printCfg, pgSize, withHeaders);
      }
    }

    return table;
  }
  
  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
   */  
  private Table<Row<Cell>> createDocModelFromEmptyPanelContainer(
      DODMBasic schema, 
      DefaultPanel panel,
      Collection<DAttr> attribs,
      PropertySet printCfg, /* with print configuration */
      Dimension pgSize, 
      boolean withHeaders) throws IOException {

    final String tablePrintId = (printCfg != null) ? 
        printCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID) : MCCLConstants.DEFAULT_PRINT_REF_ID;

    int colCount = getTableColumns(panel, tablePrintId, printCfg, attribs); //attribs.size(); 
    
    
    // table with enough columns to accommodate the specified attributes
    Table<Row<Cell>> table = new Table(colCount, TableType.NormalTable, printCfg);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    setUpTable(table, panel, printCfg);

    JLabel label;
    JComponent comp;
    //JDataField df; 
    String attribName;
    //Object val;
    JDataContainer dcont;
    
    //Font font; 
    //Color color;
    HeaderCell hc = null;
    //Dimension dim;
    //AlignmentX alignX;
    int //w, h, 
    colSpan//, rowSpan, nrowSpan
    ;
    
    // the table's header rows span (i.e. number of its header rows)
    int headerRowsSpan = 1;
    int nheaderRowsSpan, maxNHeaderRowsSpan = 0; // that of a nested table
    
    Row<Cell> currRow;
    //int rowInd = 0;
    //Object o;
    
    // the nested tables (if any)
    //List<Table> nestedTables = new ArrayList<>();
    List<Integer> nestedCols = new ArrayList<>(); // to record the nested column indices
    
    // the spans of the columns of this table 
    Integer[] nestedColSpans = new Integer[colCount];
    Arrays.fill(nestedColSpans, null);
    
    // record the empty cells for update with column spans later
    //Map<String,TextCell> emptyCells = new HashMap<>();

    // record whether or not a nested cols has got its header constructed
    Boolean[] nestedHeaders = new Boolean[colCount];
    Arrays.fill(nestedHeaders, null);
    
    //List<JComponent> labels = panel.getLabelComponents();
    
    // create table rows from objects in buffer
    int colIndx;

    Table nested;
    Region attribConfig; PropertySet printfCfg = null; PropertySet printRefConfig; boolean printfLabelVisible; boolean printfVisible; String printfId; 
    Class printfType; PropertySet printfRef; String[] printfRefAttribNames = null; Class printfRefCls = null; Collection<DAttr> printfRefAttribs = null;
    PropertySet printContainerConfig = null; JDataContainer printfContainer = null, printfRefContainer=null;
    boolean isContainerType;
    
    // create an empty table
    colIndx = 0;

    currRow = table.beginRow();
    
    //for (JComponent c : labels) {
    for (DAttr attrib: attribs) {
      label = panel.getLabelFor(attrib);
      attribName = attrib.name(); //panel.getAttributeName(comp);
      if (label == null)
        throw new ApplicationRuntimeException(null, "Label not found in {0} for attribute {1}", panel, attribName);

      attribConfig = panel.getComponentConfig(attrib);
      if (printCfg != null)
        printfCfg = printCfg.getExtension(attribName);
      else
        printfCfg = null;
      
      printfRefCls = null; printfRefAttribNames=null; printfRefAttribs=null; printContainerConfig=null; printfContainer=null; printfRefContainer=null;
      printfLabelVisible = true;
      
      // create a table cell based on a combination of print configuration setting 
      // and the configuration setting of data field component
      if (printfCfg != null) {
        printfVisible = printfCfg.getPropertyValue("isVisible", Boolean.class, true);
        // skip invisible components
        if (!printfVisible)
          continue;

        printfId = printfCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID);
        
        // create the headers
        if (withHeaders
            && printfId.equals(tablePrintId)  // same print id as the table
            ) {
          colSpan = 1;  // to be updated below
          hc = createHeaderCell(table, printfCfg, label, colSpan, 1);
          
          // set header's visibility
          printfLabelVisible = printfCfg.getPropertyValue("isLabelVisible", Boolean.class, true);
          hc.setProperty(Visible, printfLabelVisible);
        }
        
        printRefConfig = printfCfg.getExtension("printConfig");

        //display type: 
        printfType = printfCfg.getPropertyValue("type", Class.class);
        if (printfType == null) printfType = attribConfig.getDisplayClassType();
            
        printfRef = printfCfg.getExtension("ref");
        isContainerType = printfType != null && JDataContainer.class.isAssignableFrom(printfType);
        
        if (printfRef != null) {
          // get the referenced attributes (if any)
          printfRefCls = printfRef.getPropertyValue("clazz", Class.class);
        }
        
        if (isContainerType) {
          // a container type
          // create nested table using the specified type and attributes
          // retrieve a data container of the referenced class to use (this is either the 
          // root of the module's GUI (if available) or any container being used by 
          // any other modules
          if (printfRefCls == null) { // not in print config, try the attribute config
            printfRefCls = attrib.filter().clazz();
            if (printfRefCls == CommonConstants.NullType) printfRefCls = null;
          }

          if (printfRefCls == null) {
            throw new ApplicationRuntimeException(null, "A domain class is required but none is specified for attribute {0} (container: {1})", attrib.name(), panel);
          }
          
          // prepare the print config for the container
          printfRefContainer = exportCtl.getDataContainerWithPreference(printfRefCls);
          printContainerConfig = exportCtl.getContainerPrintCfg(printfRefContainer);

          // merge refContainerPrintConfig with printfCfg and printRefConfig if it is specified
          printContainerConfig = printContainerConfig.clone();
          printContainerConfig.mergeProperties(printfCfg, true);
          if (printRefConfig != null) {
            // merge with printRefConfit 
            printContainerConfig.mergeExtent(printRefConfig, true);
          }

          printfContainer = exportCtl.getDataContainer(panel, attribConfig);
          if (printfContainer == null) {
            printfContainer = printfRefContainer;
          }

          if (printfContainer == null) {
            throw new ApplicationRuntimeException(null, "No child data container found in {0} for attribute {1}", panel, attrib.name());
          }
          
          // retrieve the domain attributes involved or all if no specific names were specified
          if (printfRef != null) {
             printfRefAttribNames = printfRef.getPropertyValue("attributes", String[].class);
          } 
          
          if (printfRefAttribNames == null) {// not in print config, get from the data container
            printfRefAttribs = printfContainer.getDomainAttributes(true);
          } else {            
            printfRefAttribs = schema.getDsm().getAttributeConstraints(printfRefCls, printfRefAttribNames);
          }
          
          nested = createDocModel(schema, printfContainer, null, // buffer
              printfRefAttribs, 
              printContainerConfig, pgSize, withHeaders);
          
          //nested.setRefId(printfId);
          // if nested.refId differs from the table: add it as an extension
          if (!nested.getRefId().equals(tablePrintId)) {
            // extension table
            table.addExtension(nested);
          } else {          
            nestedCols.add(colIndx);

            TableCell tc = new TableCell(nested);
            table.addTableCell(tc);
            
            // update the col span of this column (if not done so) 
            if (nestedColSpans[colIndx] == null) {
              int nestedSpan = nested.getColumnsSpan();
              nestedColSpans[colIndx]= nestedSpan;
              
              table.setColumnSpan(colIndx, nestedSpan);
            }
            
            // determine the max header rows span among all the nested tables
            if (withHeaders && nestedHeaders[colIndx] == null) {
              // if table.headers[colIndx] is invisible then subtract by 1
              //nheaderRowsSpan = nested.getHeaderRowsSpan();
              if (hc != null && !printfLabelVisible) {
                nheaderRowsSpan = Math.max(nested.getHeaderRowsSpan()-1,1);   // at least 1               
              } else {
                nheaderRowsSpan = nested.getHeaderRowsSpan();
              }
              if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
            }
            
            if (nestedHeaders[colIndx] == null) 
              nestedHeaders[colIndx] = Boolean.TRUE;        
          }
        } else {
          // a single data field (bounded if a ref attribute is specified)
          if (printfRef != null) {
            printfRefAttribNames = printfRef.getPropertyValue("attributes",
                String[].class);
            // bounded: use the referenced attribute
            DAttr printfRefAttrib = schema.getDsm().getDomainConstraint(
                printfRefCls, printfRefAttribNames[0]);
            printfRefContainer = exportCtl
                .getDataContainerWithPreference(printfRefCls);
            createEmptyCell(schema, table, printfRefContainer, printfRefAttrib);
          } else {
            // non-bounded: use the current attribute
            createEmptyCell(schema, table, panel, attrib);
          }          
          table.setColumnSpan(colIndx, 1);
        }
      } else {
        // no print config -> use the data field config
        comp = (JComponent) label.getLabelFor();

        // skip null or invisible components
        if (comp == null || !comp.isVisible())
          continue;

        // create the headers
        if (withHeaders) {
          colSpan = 1;  // to be updated below
          hc = createHeaderCell(table, printfCfg, label, colSpan, 1);
        }

        if (comp instanceof JDataField) {
          // create an empty cell
          createEmptyCell(schema, table, (JDataField)comp);
          table.setColumnSpan(colIndx, 1);
        } else { // data container
          // add a TableCell from the nested data container
          nestedCols.add(colIndx);
          
          dcont = View.toDataContainer(comp);
          
          if (dcont.isVisible()) {
             // create a table with full header and an empty row
              nested = createDocModel(schema, dcont, null, 
                  dcont.getDomainAttributes(true),
                  exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders //_withHeaders
                  );
            
            TableCell tc = new TableCell(nested);
            table.addTableCell(tc);
            
            // update the col span of this column (if not done so) 
            if (nestedColSpans[colIndx] == null) {
              int nestedSpan = nested.getColumnsSpan();
              nestedColSpans[colIndx]= nestedSpan;
              
              table.setColumnSpan(colIndx, nestedSpan);
              //debug
              //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
            }
            
            // determine the max header rows span among all the nested tables
            if (withHeaders && nestedHeaders[colIndx] == null) {
              nheaderRowsSpan = nested.getHeaderRowsSpan();
              if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
            }
            
            if (nestedHeaders[colIndx] == null) 
              nestedHeaders[colIndx] = Boolean.TRUE;
          }
        }
      }
        
      colIndx++;
    }
    
    if (currRow.isEmpty())
      throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
    
    currRow.setProperty(RowSpan, 1);
    
    table.endRow();

    // update header col and row spans
    Integer cspan;
    //if (withHeaders) {
    if (table.hasHeader()) {
      Row<HeaderCell> headerRow = table.getHeaderRow();
      headerRowsSpan += maxNHeaderRowsSpan;
      table.setHeaderRowsSpan(headerRowsSpan); 

      for (int i = 0; i < headerRow.size(); i++) {
        hc = headerRow.get(i);
        cspan = nestedColSpans[i];
        if (
            cspan == null // not a nested column, update row span
            ) {      
          hc.setProperty(RowSpan, headerRowsSpan);
        } else {  // nested column, update col span
          hc.setProperty(ColSpan, cspan);
        }
      }
    }
    
    //debug
    //System.out.printf("%s%n", table);
    
    return table;
  }

  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
   */  
  private Table<Row<Cell>> createDocModelFromNonEmptyPanelContainer(
      DODMBasic schema, 
      DefaultPanel panel,
      Iterator buffer,
      Collection<DAttr> attribs,
      PropertySet printCfg, /* (Optional) print configuration */
      Dimension pgSize, 
      boolean withHeaders) throws IOException {
  
    final String tablePrintId = (printCfg != null) ? 
        printCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID) : MCCLConstants.DEFAULT_PRINT_REF_ID;
    
    //final int allColCount = attribs.size();
    final int colCount = getTableColumns(panel, tablePrintId, printCfg, attribs); //attribs.size(); 
    
    // table with enough columns to accommodate the specified attributes
    Table<Row<Cell>> table = new Table(colCount, TableType.NormalTable, printCfg);
    table.setProperty(PageWidth, (int) pgSize.getWidth());
    

    setUpTable(table, panel, printCfg);    
    
    JLabel label;
    JComponent comp;
    JDataField df; 
    String attribName;
    //Object val;
    JDataContainer dcont;
    
    //Font font; 
    //Color color;
    HeaderCell hc = null;
    //Dimension dim;
    //AlignmentX alignX;
    int //w, h, 
    colSpan, rowSpan, nrowSpan;
    
    // the table's header rows span (i.e. number of its header rows)
    int headerRowsSpan = 1;
    int nheaderRowsSpan, maxNHeaderRowsSpan = 0; // that of a nested table
    
    Row<Cell> currRow;
    int rowInd = 0;
    Object o;
    
    // the nested tables (if any)
    List<Table> nestedTables = new ArrayList<>();
    List<Integer> nestedCols = new ArrayList<>(); // to record the nested column indices
    
    // the spans of the columns of this table 
    Integer[] nestedColSpans = new Integer[colCount];
    Arrays.fill(nestedColSpans, null);
    
    // record the empty cells for update with column spans later
    Map<String,TextCell> emptyCells = new HashMap<>();

    // record whether or not a nested cols has got its header constructed
    Boolean[] nestedHeaders = new Boolean[colCount];
    Arrays.fill(nestedHeaders, null);
    
    //List<JComponent> labels = panel.getLabelComponents();

    // create table rows from objects in buffer
    int colIndx;

    Table nested;
    Region attribConfig; PropertySet printfCfg = null; PropertySet printRefConfig; boolean printfLabelVisible = true; boolean printfVisible; String printfId; 
    Class printfType; PropertySet printfRef; String[] printfRefAttribNames=null; Class printfRefCls = null; Collection<DAttr> printfRefAttribs = null;
    PropertySet printContainerConfig = null; JDataContainer printfContainer = null, printfRefContainer = null;
    boolean isContainerType;
    
    // create a table with data rows
    while (buffer.hasNext()) {
      o = buffer.next();
      
      // create a data row (update the header if needed)
      colIndx = 0;
      rowSpan = 1;
      nestedTables.clear();
      nestedCols.clear();
      hc = null;
      
      // only create headers once for each table
      if (rowInd > 0 && withHeaders) withHeaders = false;
      
      currRow = table.beginRow();
      //for (JComponent c : labels) {
      for (DAttr attrib: attribs) {
        label = panel.getLabelFor(attrib);
        attribName = attrib.name(); 
        if (label == null)
          throw new ApplicationRuntimeException(null, "Label not found in {0} for attribute {1}", panel, attribName);
        
        //label = (JLabel) c;
        attribConfig = panel.getComponentConfig(attrib); 
        if (printCfg != null)
          printfCfg = printCfg.getExtension(attribName);
        else
          printfCfg = null;
        
        printfRefCls = null; printfRefAttribNames=null; printfRefAttribs=null; printContainerConfig=null; printfContainer=null; printfRefContainer=null;
        printfLabelVisible=true;
        
        // create a table cell based on a combination of print configuration setting 
        // and the configuration setting of the data field component
        if (printfCfg != null) {
          printfVisible = printfCfg.getPropertyValue("isVisible", Boolean.class, true);
          if (!printfVisible) continue;

          printfId = printfCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID);

          // if this is the first data row then create the headers
          if (rowInd == 0 && withHeaders
              && printfId.equals(tablePrintId)  // same print id as the table
              ) {
            colSpan = 1;  // to be updated below
            hc = createHeaderCell(table, printfCfg, label, colSpan, 1);
            
            // set header's visibility
            printfLabelVisible = printfCfg.getPropertyValue("isLabelVisible", Boolean.class, true);
            hc.setProperty(Visible, printfLabelVisible);
          }
          
          printRefConfig = printfCfg.getExtension("printConfig");

          //display type: 
          //TODO: property "type" is not supported????
          printfType = printfCfg.getPropertyValue("type", Class.class);
          if (printfType == null) printfType = attribConfig.getDisplayClassType();
          
          printfRef = printfCfg.getExtension("ref");
          isContainerType = printfType != null && JDataContainer.class.isAssignableFrom(printfType);
          
          if (printfRef != null) {
            // get the referenced attributes (if any)
            printfRefCls = printfRef.getPropertyValue("clazz", Class.class);
          }
          
          if (isContainerType) {
            // a container type
            // create nested table using the specified type and attributes
            // retrieve a data container of the referenced class to use (this is either the 
            // root of the module's GUI (if available) or any container being used by 
            // any other modules
            if (printfRefCls == null) { // not in print config, try the attribute config
              printfRefCls = attrib.filter().clazz();
              if (printfRefCls == CommonConstants.NullType) printfRefCls = null;
            }

            if (printfRefCls == null) {
              throw new ApplicationRuntimeException(null, "A domain class is required but NOT specified for attribute {0} (container: {1})", attrib.name(), panel);
            }
            
            // prepare the print config for the container
            printfRefContainer = exportCtl.getDataContainerWithPreference(printfRefCls);
            printContainerConfig = exportCtl.getContainerPrintCfg(printfRefContainer);

//            if (printContainerConfig == null) {
//              throw new ApplicationRuntimeException(null, "A print configuration is required but NOT specified for data container {0} (domain class: {1})", printfRefContainer, printfRefCls.getSimpleName());
//            }

            // merge refContainerPrintConfig with printfCfg and printRefConfig if it is specified
            if (printContainerConfig == null) {
              printContainerConfig = printfCfg;
              if (printRefConfig != null) {
                // merge with printRefConfit
                printContainerConfig = printContainerConfig.clone();
                printContainerConfig.mergeExtent(printRefConfig, true);
              }
            } else {
              printContainerConfig = printContainerConfig.clone();
              printContainerConfig.mergeProperties(printfCfg, true);
              if (printRefConfig != null) {
                printContainerConfig.mergeExtent(printRefConfig, true);
              }
            }
            
            printfContainer = exportCtl.getDataContainer(panel, attribConfig);
            if (printfContainer == null) {
              printfContainer = printfRefContainer;
            }

            if (printfContainer == null) {
              throw new ApplicationRuntimeException(null, "No child data container found in {0} for attribute {1}", panel, attrib.name());
            }
            
            // retrieve the domain attributes involved or all if no specific names were specified
            if (printfRef != null) {
               printfRefAttribNames = printfRef.getPropertyValue("attributes", String[].class);
            } 
            
            if (printfRefAttribNames == null) {// not in print config, get from the data container
              printfRefAttribs = printfContainer.getDomainAttributes(true);
            } else {            
              printfRefAttribs = schema.getDsm().getAttributeConstraints(printfRefCls, printfRefAttribNames);
            }
            
            Collection bufferObjs = exportCtl.getAssociateObjects(o, attrib);
          
            // create a table regardless of whether its buffer is empty
            // an empty table has a complete header (if not created) and an empty row 
            if (bufferObjs != null && !bufferObjs.isEmpty()) {
              // create  nested table for the container (create with headers only if not done so)
              nested = createDocModel(schema, printfContainer, bufferObjs.iterator(), 
                  printfRefAttribs, printContainerConfig, pgSize, withHeaders 
                  );
            } else {
              // empty buffer -> create a table with full header and an empty row
              nested = createDocModel(schema, printfContainer, null, // buffer
                  printfRefAttribs, printContainerConfig, pgSize, withHeaders);
            }
            
            //nested.setRefId(printfId);
            // if nested.refId differs from the table: add it as an extension
            if (!nested.getRefId().equals(tablePrintId)) {
              // extension table
              table.addExtension(nested);
            } else {
              // not an extension -> add nested inside the body of the current table
              nestedCols.add(colIndx);
              nestedTables.add(nested);
              
              TableCell tc = new TableCell(nested);
              table.addTableCell(tc);
              
              // update the col span of this column (if not done so) 
              if (nestedColSpans[colIndx] == null) {
                int nestedSpan = nested.getColumnsSpan();
                nestedColSpans[colIndx]= nestedSpan;
                
                table.setColumnSpan(colIndx, nestedSpan);
                
                //debug
                //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
              }
              
              // update the row span of the current row
              nrowSpan = nested.getRowsSpan();
              if (rowSpan < nrowSpan) 
                rowSpan = nrowSpan;
              
              // determine the max header rows span among all the nested tables
              //if (rowInd == 0 && withHeaders) {
              if (withHeaders && nestedHeaders[colIndx] == null) {
                // if table.headers[colIndx] is invisible then subtract by 1
                //nheaderRowsSpan = nested.getHeaderRowsSpan();
                if (hc != null && !printfLabelVisible) {
                  nheaderRowsSpan = Math.max(nested.getHeaderRowsSpan()-1,1);   // at least 1               
                } else {
                  nheaderRowsSpan = nested.getHeaderRowsSpan();
                }
                if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
              }
              
              if (nestedHeaders[colIndx] == null) 
                nestedHeaders[colIndx] = Boolean.TRUE;            
            }
          } else {
            // a single data field (bounded if a ref attribute is specified)
            if (printfRef != null) {
              printfRefAttribNames = printfRef.getPropertyValue("attributes",
                  String[].class);
              // bounded: use the referenced attribute
              DAttr printfRefAttrib = schema.getDsm().getDomainConstraint(
                  printfRefCls, printfRefAttribNames[0]);
              printfRefContainer = exportCtl
                  .getDataContainerWithPreference(printfRefCls);
              createCell(schema, table, printfRefContainer, printfRefAttrib,
                  printfCfg, o);
            } else {
              // non-bounded: use the current attribute
              createCell(schema, table, panel, attrib, printfCfg, o);
            }
            table.setColumnSpan(colIndx, 1);
          }
        } else {
          // no print config -> use data field setting
          comp = (JComponent)label.getLabelFor();

          // skip null or invisible components
          if (comp == null || !comp.isVisible())
            continue;

          // if this is the first data row then create the headers
          if (rowInd == 0 && withHeaders) {
            colSpan = 1;  // to be updated below
            hc = createHeaderCell(table, printfCfg, label, colSpan, 1);
          }

          if (comp instanceof JDataField) { // data field
            // add a Cell from the object value 
            df = (JDataField) comp;
            createCell(schema, table, df, o);
            table.setColumnSpan(colIndx, 1);
          } else { // data container
            // add a TableCell from the nested data container
            // starts a new row for the nested container
            nestedCols.add(colIndx);
            
            dcont = View.toDataContainer(comp);
            if (dcont.isVisible()) {
              // get the object buffer for dcont as determined by o
              Collection bufferObjs = exportCtl.getAssociateObjects(o, dcont);
              
              // create a table regardless of whether its buffer is empty
              // an empty table has a complete header (if not created) and an empty row 
              if (bufferObjs != null && !bufferObjs.isEmpty()) {
                // create  nested table for the container (create with headers only if not done so)
                nested = createDocModel(schema, dcont, bufferObjs.iterator(), 
                    dcont.getDomainAttributes(true),
                    exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders // to create headers once with the parent (_withHeaders)
                    );
              } else {
                // empty buffer -> create a table with full header and an empty row
                nested = createDocModel(schema, dcont, null, 
                    dcont.getDomainAttributes(true),
                    exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders//_withHeaders
                    );
              }
              
              nestedTables.add(nested);
              
              TableCell tc = new TableCell(nested);
              table.addTableCell(tc);
              
              // update the col span of this column (if not done so) 
              if (nestedColSpans[colIndx] == null) {
                int nestedSpan = nested.getColumnsSpan();
                nestedColSpans[colIndx]= nestedSpan;
                
                table.setColumnSpan(colIndx, nestedSpan);
                
                //debug
                //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
              }
              
              // update the row span of the current row
              nrowSpan = nested.getRowsSpan();
              if (rowSpan < nrowSpan) 
                rowSpan = nrowSpan;
              
              // determine the max header rows span among all the nested tables
              if (withHeaders && nestedHeaders[colIndx] == null) {
                // header's visibility is not supported here
                nheaderRowsSpan = nested.getHeaderRowsSpan(); 
                if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
              }
              
              if (nestedHeaders[colIndx] == null) 
                nestedHeaders[colIndx] = Boolean.TRUE;
            } else {
              // container not visible -> empty cell
              TextCell tc = TextCell.EmptyCell.clone();
              table.addTextCell(tc);
              // record this so that we can update with a suitable column span later  
              emptyCells.put(colIndx+"_"+rowInd, tc);
            }
          }
        }
        
        colIndx++;
      } // end cells of current row loop
      
      // update the row span of this row and of all the non-nested cells
      currRow.setProperty(RowSpan, rowSpan);
      Cell c;
      if (!nestedCols.isEmpty()) {
        for (int i = 0; i < currRow.size(); i++) {
          if (!nestedCols.contains(i)) {
            c = currRow.get(i);
            c.setProperty(RowSpan, rowSpan);
          }
        }
      }
      
      if (currRow.isEmpty())
        throw new ApplicationRuntimeException(null, "Data row is required but EMPTY: {0}.row[{1}] \nObject: {2}", panel, rowInd, o);

      table.endRow();

      //if RowSpan is higher than some of the nested tables, fill these tables with empty rows up to the RowSpan
      // TODO: can we use column span here?
      if (!nestedTables.isEmpty()) {
        for (Table t : nestedTables) {
          nrowSpan = t.getRowsSpan();
          if (nrowSpan < rowSpan) {
            t.addEmptyRows(rowSpan - nrowSpan);
          }
        }
      }
      
      // update rowInd based on rowspan
      rowInd = rowInd+1;
    } // end object buffer loop

    // update header col and row spans
    Integer cspan;
    //if (withHeaders) {
    if (table.hasHeader()) {
      Row<HeaderCell> headerRow = table.getHeaderRow();
      headerRowsSpan += maxNHeaderRowsSpan;
      table.setHeaderRowsSpan(headerRowsSpan); 

      for (int i = 0; i < headerRow.size(); i++) {
        hc = headerRow.get(i);
        cspan = nestedColSpans[i];
        if (
            cspan == null // not a nested column, update row span
            ) {      
          hc.setProperty(RowSpan, headerRowsSpan);
        } else {  // nested column, update col span
          hc.setProperty(ColSpan, cspan);
        }
      }
    }
    
    // update all empty cells with suitable column span
    if (!emptyCells.isEmpty()) {
      String pos;
      int colAt;
      TextCell tc;
      for (Entry<String,TextCell> e : emptyCells.entrySet()) {
        pos = e.getKey();
        colAt = Integer.parseInt(pos.split("_")[0]);
        cspan = nestedColSpans[colAt];
        if (cspan == null)  // not a nested column
          cspan = 1;
        tc = e.getValue();
        tc.setProperty(ColSpan, cspan);
        
        // debug
        //System.out.printf("Empty cell@col%d: %s (dcont: %s)%n  -> colspan: %d%n", colAt, tc, panel, cspan);
      }
    }
    
    //debug
    //System.out.printf("%s%n", table);
    
    return table;
  }
  
  /**
   * @effects 
   *  return the actual number of columns used to represent the attributes in <tt>attribs</tt>
   *  of <tt>dataContainer</tt> in the <tt>Table</tt> model of this container, with respect 
   *  to the print configuration <tt>printCfg</tt>. 
   *  
   *  <p>The attributes that satisfy the above are those that have the same <tt>refId</tt> in their
   *  print configuration as that of <tt>dataContainer</tt>
   */
  private int getTableColumns(JDataContainer dataContainer,
      String containerRefId, 
      PropertySet printCfg, 
      Collection<DAttr> attribs) {
    
    if (printCfg == null) // no print config -> all attributes are printable
      return attribs.size();
    
    String attribName;
    PropertySet printfCfg;
    
    //String contRefId = printCfg.getPropertyValue("refId", String.class, MetaConstants.DEFAULT_PRINT_REF_ID);
    
    int numCols = 0;
    for (DAttr attrib: attribs) {
      attribName = attrib.name(); 
      printfCfg = printCfg.getExtension(attribName);
      
      if (printfCfg == null     // no print config for this attribute
          || printfCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID).equals(containerRefId)  // OR same print id as the container
          ) {
        // another column
        numCols++;
      }
    }
    
    return numCols;
  }
  
  public void setUpTable(Table table, 
      JDataContainer dataContainer, 
      PropertySet printCfg) {
    boolean border; String tablePrintId, tablePreW; AlignmentX alignX;
    
    alignX = dataContainer.getContainerConfig().getAlignX();
    
    /*
    //debug
    System.out.printf("DocumentBuilder(%s).setUpTable(): %n  data container %s%n  print cfg = %s %n   details %s%n", 
        this.getName(), dataContainer, printCfg, (printCfg != null) ? printCfg.getProps() : "");
    */
    
    if (printCfg != null) {
      border = printCfg.getPropertyValue("border", Boolean.class, true);
      tablePrintId = printCfg.getPropertyValue("refId", String.class, MCCLConstants.DEFAULT_PRINT_REF_ID);
      tablePreW = printCfg.getPropertyValue("width", String.class);
    } else {
      border = MCCLConstants.DEFAULT_PRINT_BORDER;
      tablePrintId = MCCLConstants.DEFAULT_PRINT_REF_ID;
      tablePreW = null;
    }
    
    // table's border
    table.setBorder(border);
    
    // table's print reference id
    table.setRefId(tablePrintId);
  
    // table's horizontal alignment
    table.setAlignX(alignX);
    
    table.setUserDefinedWidth(tablePreW);

    /*// debug
    System.out.printf("DocumentBuilder(%s).setUpTable(%s): %n  user-defined width = %s%n", 
        this.getName(), tablePrintId, 
        table.getProperty().getStringValue(Width,null) + " (" + tablePreW + ")");
        */
    
  }
  
  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>dtable.headers</tt>
   *  and contains <b>all</b> data rows in <tt>dtable.buffer</tt>. The cell styles of each row 
   *  are defined from the corresponding <tt>dtable.cellRenderers</tt>.
   *  
   *  <p>This table typically does not contain any nested tables.
   */
  private Table<Row<Cell>> createDocModelFromEmptyTableContainer(
      DODMBasic schema, 
      JDataTable dtable,
      Collection<DAttr> attribs,
      PropertySet printCfg, 
      Dimension pgSize, boolean withHeaders) throws IOException {

    /**
     * adds the data model of this table to the document
     */
    // v2.5.4: ignore columns that are not visible
    int colCount = dtable.getColumnCount();
    int visibleColCount = dtable.getColumnCount(true); 
    // a table with columns = dtable.columnCount
        
    Table<Row<Cell>> table = new Table<>(visibleColCount, TableType.NormalTable, printCfg);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    setUpTable(table, dtable, printCfg);
    
    // data: 
    // v2.5.4: ignore columns that are not visible
    Font headFont;
    //Component renderer;
    Color headColor;
    int headW, headH;
    Dimension headDim;
    AlignmentX headAlignX;
    JDataField dfeditor;
    
    // header rows span is 1 b/c there are no nested containers
    int headerRowsSpan = 1;
    
    Row<Cell> currRow;
    
    int rowInd = 0, visColInd = 0;
    
    // create an empty table
    currRow = table.beginRow();
    for (int colInd = 0; colInd < colCount; colInd++) {
      // if not visible then skip
      if (!dtable.isColumnVisible(colInd)) {
        continue;
      }
      
      // create table header (if this is the first row)
      if (withHeaders) { 
        headFont = dtable.getHeaderFont(colInd);
        headColor = dtable.getHeaderForeground(colInd);
        headAlignX = DEF_HEADER_ALIGN; //dtable.getHeaderAlignmentX(colInd);
        headDim = dtable.getHeaderPreferredSize(colInd);
        headW = (int) headDim.getWidth();
        headH = (int) headDim.getHeight();
        createHeaderCell(table, 
            dtable.getColumnName(colInd),
            headW, headH, headFont, headColor, null, headAlignX, 1, 1
            );
      }
      
      dfeditor = dtable.getTableCellEditor(colInd);
      createEmptyCell(schema, table, dfeditor);
      table.setColumnSpan(visColInd, 1);
      visColInd++;
    }
  
    // row span of this row is 1 (since there are no nested rows)
    currRow.setProperty(RowSpan, 1);
    
    if (currRow.isEmpty())
      throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");

    table.endRow();
    
    // update the header rows span
    table.setHeaderRowsSpan(headerRowsSpan);
    
    return table;
  }
  
  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>dtable.headers</tt>
   *  and contains <b>all</b> data rows in <tt>dtable.buffer</tt>. The cell styles of each row 
   *  are defined from the corresponding <tt>dtable.cellRenderers</tt>.
   *  
   *  <p>This table typically does not contain any nested tables.
   */
  private Table<Row<Cell>> createDocModelFromNonEmptyTableContainer(
      DODMBasic schema, 
      JDataTable dtable,
      Iterator buffer,
      Collection<DAttr> attribs, 
      PropertySet printCfg, 
      Dimension pgSize, boolean withHeaders) throws IOException {

    /**
     * adds the data model of this table to the document
     */
    // v2.5.4: ignore columns that are not visible
    int colCount = dtable.getColumnCount();
    int visibleColCount = dtable.getColumnCount(true); 
    // a table with columns = dtable.columnCount
//    final String tablePrintId = (printCfg != null) ? 
//        printCfg.getPropertyValue("refId", String.class, MetaConstants.DEFAULT_PRINT_REF_ID) : MetaConstants.DEFAULT_PRINT_REF_ID;
        
    Table<Row<Cell>> table = new Table<>(visibleColCount, TableType.NormalTable, printCfg);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    setUpTable(table, dtable, printCfg);

//    table.setBorder(border);
//    
//    // table's print reference id
//    table.setRefId(tablePrintId);
//    
//    // table's horizontal alignment
//    table.setAlignX(dtable.getContainerConfig().getAlignX());
    
    // data: 
    // v2.5.4: ignore columns that are not visible
    Font headFont;
    //Component renderer;
    Color headColor;
    int headW, headH;
    Dimension headDim;
    AlignmentX headAlignX;
    JDataField dfeditor;
    
    // header rows span is 1 b/c there are no nested containers
    int headerRowsSpan = 1;
    
    Row<Cell> currRow;
    
    int rowInd = 0, visColInd = 0;
    Object o;
    
    while (buffer.hasNext()) {
      o = buffer.next();
      
      currRow = table.beginRow();
      for (int colInd = 0; colInd < colCount; colInd++) {
        // if not visible then skip
        if (!dtable.isColumnVisible(colInd)) {
          continue;
        }
        
        // create table header (if this is the first row)
        if (withHeaders && rowInd==0) { 
          headFont = dtable.getHeaderFont(colInd);
          headColor = dtable.getHeaderForeground(colInd);
          headAlignX = DEF_HEADER_ALIGN; //dtable.getHeaderAlignmentX(colInd);
          headDim = dtable.getHeaderPreferredSize(colInd);
          headW = (int) headDim.getWidth();
          headH = (int) headDim.getHeight();
          createHeaderCell(table, 
              dtable.getColumnName(colInd),
              headW, headH, headFont, headColor, null, headAlignX, 1, 1
              );
        }
        
        dfeditor = dtable.getTableCellEditor(colInd);
        createCell(schema, table, dfeditor, o);
        table.setColumnSpan(visColInd, 1);
        visColInd++;
      }
    
      // row span of this row is 1 (since there are no nested rows)
      currRow.setProperty(RowSpan, 1);
      
      if (currRow.isEmpty())
        throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");

      table.endRow();
      rowInd++;
    } // end row loop
    
    // update the header rows span
    table.setHeaderRowsSpan(headerRowsSpan);
    
    return table;
  }
  
  /**
   * This is the main method that is used to generate the Table model of the data and 
   * styles contained in a data container. 
   * It supports different ways of generating the Table model.
   * @effects 
   *  creates and return a <tt>Table</tt>, 
   *  whose view styles are defined based on <tt>dataContainer</tt>, 
   *  whose table columns (and headers if <tt>withHeaders=true</tt>) are defined from <tt>dataContainer.labels</tt>, 
   *  and whose data rows are created from the domain objects in <tt>buffer</tt>
   *  (if any). 
   *  
   *  <p>If buffer is <tt>null</tt> then the table contain a single empty row.
   */
  protected Table<Row<Cell>> createDocModel(DODMBasic schema, 
      JDataContainer dataContainer,
      Iterator buffer,
      //PropertySet printCfg, 
      Dimension pgSize, boolean withHeaders) throws IOException {
    Class containerType = dataContainer.getClass();

    Table<Row<Cell>> table = null;
    
    /**
     * supports two cases:
     * if dataContainer is DocumentExportable
     *  invoke the export() method of it
     * else
     *  invoke a generic export function suitable for it
     */
    if (dataContainer instanceof DocumentExportable) {
      table = ((DocumentExportable) dataContainer).export(this, schema, buffer, null, pgSize, withHeaders);
    } else {
      // for now assume all print types are table
      if (DefaultPanel.class.isAssignableFrom(containerType)) {
        DefaultPanel panel = (DefaultPanel) dataContainer;
        table = createDocModelFromPanelContainer(schema, panel, buffer, pgSize, withHeaders);
      } else if (JDataTable.class.isAssignableFrom(containerType)) {
        JDataTable dtable = (JDataTable) dataContainer;
        //table = createTableFromTableContainer(schema, dtable, buffer, printCfg, pgSize, withHeaders);
        table = createDocModelFromTableContainer(schema, dtable, buffer, 
                //printCfg, 
                pgSize, withHeaders);        
      }
    }

    return table;
    
  }
  
//  /**
//   * @effects 
//   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
//   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
//   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
//   */  
//  private Table<Row<Cell>> createDocModelFromEmptyPanelContainer(
//      DomainSchema schema, 
//      DefaultPanel panel,
//      PropertySet printCfg, /* with print configuration */
//      Dimension pgSize, 
//      boolean withHeaders) throws IOException {
//  
//    int colCount = panel.getVisibleComponents().size();
//    
//    // table with 2 columns: one for label, one for component
//    Table<Row<Cell>> table = new Table(colCount, TableType.NormalTable, printCfg);
//    table.setProperty(PageWidth, (int) pgSize.getWidth());
//
//    // disable the border of non-top-level tables
//    if (panel.getParent() == null || panel.isNested()) {
//      table.setBorder(false);
//    }
//
//    JLabel label;
//    JComponent comp;
//    JDataField df; String fieldName;
//    Object val;
//    JDataContainer dcont;
//    
//    Font font; 
//    Color color;
//    HeaderCell hc = null;
//    Dimension dim;
//    AlignmentX alignX;
//    int w, h, colSpan, rowSpan, nrowSpan;
//    
//    // the table's header rows span (i.e. number of its header rows)
//    int headerRowsSpan = 1;
//    int nheaderRowsSpan, maxNHeaderRowsSpan = 0; // that of a nested table
//    
//    Row<Cell> currRow;
//    int rowInd = 0;
//    Object o;
//    
//    // the nested tables (if any)
//    //List<Table> nestedTables = new ArrayList<>();
//    List<Integer> nestedCols = new ArrayList<>(); // to record the nested column indices
//    
//    // the spans of the columns of this table 
//    Integer[] nestedColSpans = new Integer[colCount];
//    Arrays.fill(nestedColSpans, null);
//    
//    // record the empty cells for update with column spans later
//    Map<String,TextCell> emptyCells = new HashMap<>();
//
//    // record whether or not a nested cols has got its header constructed
//    Boolean[] nestedHeaders = new Boolean[colCount];
//    Arrays.fill(nestedHeaders, null);
//    
//    List<JComponent> labels = panel.getLabelComponents();
//
//    // create table rows from objects in buffer
//    int colIndx;
//
//    Table nested;
//    PropertySet printfCfg = null; Class printfType; PropertySet printfRef; 
//    String[] printfRefAttribNames; Class printfRefCls; Collection<DomainConstraint> printfRefAttribs;
//    JDataContainer printfRefCont;
//    // create an empty table
//    colIndx = 0;
//
//    currRow = table.beginRow();
//    
//    for (JComponent c : labels) {
//      label = (JLabel) c;
//      comp = (JComponent)label.getLabelFor();
//      fieldName = panel.getAttributeName(comp);
//      if (printCfg != null)
//        printfCfg = printCfg.getExtension(fieldName);
//      
//      // skip null or invisible components
//      if (comp == null || !comp.isVisible())
//        continue;
//
//      // create the headers
//      if (withHeaders) {
//        font = label.getFont();
//        color = label.getForeground();
//        alignX = GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
//        
//        dim = label.getSize();
//        w = (int) dim.getWidth();
//        h = (int) dim.getHeight();
//        colSpan = 1;  // to be updated below
//        hc = createHeaderCell(table, 
//            getLabelText(label),
//            w, h, font, color, null, alignX, colSpan, 1
//            );
//      }
//      
//      // create a table cell based on a combination of print configuration setting 
//      // and the configuration setting of comp
//      if (printfCfg != null) {
//        printfType = printfCfg.getPropertyValue("type", Class.class);
//        printfRef = printfCfg.getExtension("ref");
//
//        printfRefCls = printfRef.getPropertyValue("clazz", Class.class);
//        
//        // the referenced attributes (if any)
//        //TODO: Property: set to null if array-typed value is empty
//        printfRefAttribNames = printfRef.getPropertyValue("attributes", String[].class);
//        
//        // retrieve the domain attributes involved or all if no specific names were specified 
//        printfRefAttribs = schema.getAttributeConstraints(printfRefCls, printfRefAttribNames);
//        
//        // retrieve a data container of the referenced class to use (this is either the 
//        // root of the module's GUI (if available) or any container being used by 
//        // any other modules
//        printfRefCont = exportCtl.getDataContainerWithPreference(printfRefCls);
//        
//        if (printfType != null && JDataContainer.class.isAssignableFrom(printfType)) {
//          // a container type
//          // create nested table using the specified type and attributes
//          nestedCols.add(colIndx);
//
//          nested = createDocModel(schema, printfRefCont, null, // buffer
//              printfRefAttribs, 
//              exportCtl.getContainerPrintCfg(printfRefCont), pgSize, withHeaders);
//          
//          TableCell tc = new TableCell(nested);
//          table.addTableCell(tc);
//          
//          // update the col span of this column (if not done so) 
//          if (nestedColSpans[colIndx] == null) {
//            int nestedSpan = nested.getColumnsSpan();
//            nestedColSpans[colIndx]= nestedSpan;
//            
//            table.setColumnSpan(colIndx, nestedSpan);
//          }
//          
//          // determine the max header rows span among all the nested tables
//          if (withHeaders && nestedHeaders[colIndx] == null) {
//            nheaderRowsSpan = nested.getHeaderRowsSpan();
//            if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
//          }
//          
//          if (nestedHeaders[colIndx] == null) 
//            nestedHeaders[colIndx] = Boolean.TRUE;            
//        } else {
//          // a single data field (bounded if a ref attribute is specified)
//          createEmptyCell(schema, table, printfRefCont, printfRefAttribs.iterator().next());
//          table.setColumnSpan(colIndx, 1);
//        }
//      } else {
//        // no print config -> use the data field config
//        if (comp instanceof JDataField) {
//          // create an empty cell
//          createEmptyCell(schema, table, (JDataField)comp);
//          table.setColumnSpan(colIndx, 1);
//        } else { // data container
//          // add a TableCell from the nested data container
//          nestedCols.add(colIndx);
//          
//          dcont = AppGUI.toDataContainer(comp);
//          
//          if (dcont.isVisible()) {
//            // create a table with full header and an empty row
//            nested = createDocModel(schema, dcont, null, 
//                exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders //_withHeaders
//                );
//            
//            TableCell tc = new TableCell(nested);
//            table.addTableCell(tc);
//            
//            // update the col span of this column (if not done so) 
//            if (nestedColSpans[colIndx] == null) {
//              int nestedSpan = nested.getColumnsSpan();
//              nestedColSpans[colIndx]= nestedSpan;
//              
//              table.setColumnSpan(colIndx, nestedSpan);
//              //debug
//              //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
//            }
//            
//            // determine the max header rows span among all the nested tables
//            if (withHeaders && nestedHeaders[colIndx] == null) {
//              nheaderRowsSpan = nested.getHeaderRowsSpan();
//              if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
//            }
//            
//            if (nestedHeaders[colIndx] == null) 
//              nestedHeaders[colIndx] = Boolean.TRUE;
//          }
//        }
//      }
//        
//      colIndx++;
//    }
//    
//    if (currRow.isEmpty())
//      throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
//    
//    currRow.setProperty(RowSpan, 1);
//    
//    table.endRow();
//
//    // update header col and row spans
//    Integer cspan;
//    //if (withHeaders) {
//    if (table.hasHeader()) {
//      Row<HeaderCell> headerRow = table.getHeaderRow();
//      headerRowsSpan += maxNHeaderRowsSpan;
//      table.setHeaderRowsSpan(headerRowsSpan); 
//
//      for (int i = 0; i < headerRow.size(); i++) {
//        hc = headerRow.get(i);
//        cspan = nestedColSpans[i];
//        if (
//            cspan == null // not a nested column, update row span
//            ) {      
//          hc.setProperty(RowSpan, headerRowsSpan);
//        } else {  // nested column, update col span
//          hc.setProperty(ColSpan, cspan);
//        }
//      }
//    }
//    
//    // update all empty cells with suitable column span
//    if (!emptyCells.isEmpty()) {
//      String pos;
//      int colAt;
//      TextCell tc;
//      for (Entry<String,TextCell> e : emptyCells.entrySet()) {
//        pos = e.getKey();
//        colAt = Integer.parseInt(pos.split("_")[0]);
//        cspan = nestedColSpans[colAt];
//        if (cspan == null)  // not a nested column
//          cspan = 1;
//        tc = e.getValue();
//        tc.setProperty(ColSpan, cspan);
//        
//        // debug
//        //System.out.printf("Empty cell@col%d: %s (dcont: %s)%n  -> colspan: %d%n", colAt, tc, panel, cspan);
//      }
//    }
//    
//    //debug
//    //System.out.printf("%s%n", table);
//    
//    return table;
//  }
//  
//  /**
//   * @effects 
//   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
//   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
//   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
//   */  
//  private Table<Row<Cell>> createDocModelFromNonEmptyPanelContainer(
//      DomainSchema schema, 
//      DefaultPanel panel,
//      Iterator buffer,
//      PropertySet printCfg, /* with print configuration */
//      Dimension pgSize, 
//      boolean withHeaders) throws IOException {
//  
//    int colCount = panel.getVisibleComponents().size();
//    
//    // table with 2 columns: one for label, one for component
//    Table<Row<Cell>> table = new Table(colCount, TableType.NormalTable, printCfg);
//    table.setProperty(PageWidth, (int) pgSize.getWidth());
//
//    // disable the border of non-top-level tables
//    if (panel.getParent() == null || panel.isNested()) {
//      table.setBorder(false);
//    }
//
//    JLabel label;
//    JComponent comp;
//    JDataField df; String fieldName;
//    Object val;
//    JDataContainer dcont;
//    
//    Font font; 
//    Color color;
//    HeaderCell hc = null;
//    Dimension dim;
//    AlignmentX alignX;
//    int w, h, colSpan, rowSpan, nrowSpan;
//    
//    // the table's header rows span (i.e. number of its header rows)
//    int headerRowsSpan = 1;
//    int nheaderRowsSpan, maxNHeaderRowsSpan = 0; // that of a nested table
//    
//    Row<Cell> currRow;
//    int rowInd = 0;
//    Object o;
//    
//    // the nested tables (if any)
//    List<Table> nestedTables = new ArrayList<>();
//    List<Integer> nestedCols = new ArrayList<>(); // to record the nested column indices
//    
//    // the spans of the columns of this table 
//    Integer[] nestedColSpans = new Integer[colCount];
//    Arrays.fill(nestedColSpans, null);
//    
//    // record the empty cells for update with column spans later
//    Map<String,TextCell> emptyCells = new HashMap<>();
//
//    // record whether or not a nested cols has got its header constructed
//    Boolean[] nestedHeaders = new Boolean[colCount];
//    Arrays.fill(nestedHeaders, null);
//    
//    List<JComponent> labels = panel.getLabelComponents();
//
//    // create table rows from objects in buffer
//    int colIndx;
//
//    Table nested;
//    PropertySet printfCfg = null; Class printfType; PropertySet printfRef; 
//    String[] printfRefAttribNames; Class printfRefCls; Collection<DomainConstraint> printfRefAttribs;
//    JDataContainer printfRefCont;
//    
//    // create a table with data rows
//    while (buffer.hasNext()) {
//      o = buffer.next();
//      
//      // create a data row (update the header if needed)
//      colIndx = 0;
//      rowSpan = 1;
//      nestedTables.clear();
//      nestedCols.clear();
//      hc = null;
//      
//      // only create headers once for each table
//      if (rowInd > 0 && withHeaders) withHeaders = false;
//      
//      currRow = table.beginRow();
//      for (JComponent c : labels) {
//        label = (JLabel) c;
//        comp = (JComponent)label.getLabelFor();
//        fieldName = panel.getAttributeName(comp);
//        if (printCfg != null)
//          printfCfg = printCfg.getExtension(fieldName);
//        
//        // skip null or invisible components
//        if (comp == null || !comp.isVisible())
//          continue;
//
//        // if this is the first data row then create the headers
//        if (rowInd == 0 && withHeaders) {
//          font = label.getFont();
//          color = label.getForeground();
//          alignX = GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
//          
//          dim = label.getSize();
//          w = (int) dim.getWidth();
//          h = (int) dim.getHeight();
//          colSpan = 1;  // to be updated below
//          hc = createHeaderCell(table, 
//              getLabelText(label),
//              w, h, font, color, null, alignX, colSpan, 1
//              );
//        }
//        
//     // create a table cell based on a combination of print configuration setting 
//        // and the configuration setting of comp
//        if (printfCfg != null) {
//          printfType = printfCfg.getPropertyValue("type", Class.class);
//          printfRef = printfCfg.getExtension("ref");
//
//          printfRefCls = printfRef.getPropertyValue("clazz", Class.class);
//          
//          // the referenced attributes (if any)
//          //TODO: Property: set to null if array-typed value is empty
//          printfRefAttribNames = printfRef.getPropertyValue("attributes", String[].class);
//          
//          // retrieve the domain attributes involved or all if no specific names were specified 
//          printfRefAttribs = schema.getAttributeConstraints(printfRefCls, printfRefAttribNames);
//          
//          // retrieve a data container of the referenced class to use (this is either the 
//          // root of the module's GUI (if available) or any container being used by 
//          // any other modules
//          printfRefCont = exportCtl.getDataContainerWithPreference(printfRefCls);
//          
//          if (printfType != null && JDataContainer.class.isAssignableFrom(printfType)) {
//            // a container type
//            // create nested table using the specified type and attributes
//            nestedCols.add(colIndx);
//
//            nested = createDocModel(schema, printfRefCont, null, // buffer
//                printfRefAttribs, 
//                exportCtl.getContainerPrintCfg(printfRefCont), pgSize, withHeaders);
//            
//            nestedTables.add(nested);
//            
//            TableCell tc = new TableCell(nested);
//            table.addTableCell(tc);
//            
//            // update the col span of this column (if not done so) 
//            if (nestedColSpans[colIndx] == null) {
//              int nestedSpan = nested.getColumnsSpan();
//              nestedColSpans[colIndx]= nestedSpan;
//              
//              table.setColumnSpan(colIndx, nestedSpan);
//              
//              //debug
//              //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
//            }
//            
//            // update the row span of the current row
//            nrowSpan = nested.getRowsSpan();
//            if (rowSpan < nrowSpan) 
//              rowSpan = nrowSpan;
//            
//            // determine the max header rows span among all the nested tables
//            //if (rowInd == 0 && withHeaders) {
//            if (withHeaders && nestedHeaders[colIndx] == null) {
//              nheaderRowsSpan = nested.getHeaderRowsSpan();
//              if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
//            }
//            
//            if (nestedHeaders[colIndx] == null) 
//              nestedHeaders[colIndx] = Boolean.TRUE;            
//          } else {
//            // a single data field (bounded if a ref attribute is specified)
//            createEmptyCell(schema, table, printfRefCont, printfRefAttribs.iterator().next());
//            table.setColumnSpan(colIndx, 1);
//          }
//        } else {
//          if (comp instanceof JDataField) { // data field
//            // add a Cell from the object value 
//            df = (JDataField) comp;
//            createCell(schema, table, df, o);
//            table.setColumnSpan(colIndx, 1);
//          } else { // data container
//            // add a TableCell from the nested data container
//            // starts a new row for the nested container
//            nestedCols.add(colIndx);
//            
//            dcont = AppGUI.toDataContainer(comp);
//            if (dcont.isVisible()
//                //&&!dcont.getController().isCurrentObjectNull()
//                ) {
//              // get the object buffer for dcont as determined by o
//              Collection bufferObjs = null;
//              Object linkObj = schema.getAttributeValue(o, dcont.getController().getLinkAttributeOfParent().name());
//              if (linkObj != null) {
//                if (linkObj instanceof Collection) {
//                  bufferObjs = (Collection) linkObj;
//                } else {
//                  bufferObjs = new ArrayList();
//                  bufferObjs.add(linkObj);
//                }
//              }
//              
//              // create a table regardless of whether its buffer is empty
//              // an empty table has a complete header (if not created) and an empty row 
//              if (bufferObjs != null && !bufferObjs.isEmpty()) {
//                // create  nested table for the container (create with headers only if not done so)
//                nested = createDocModel(schema, dcont, bufferObjs.iterator(), 
//                    exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders // to create headers once with the parent (_withHeaders)
//                    );
//              } else {
//                // empty buffer -> create a table with full header and an empty row
//                nested = createDocModel(schema, dcont, null, 
//                    exportCtl.getContainerPrintCfg(dcont), pgSize, withHeaders//_withHeaders
//                    );
//              }
//              
//              nestedTables.add(nested);
//              
//              TableCell tc = new TableCell(nested);
//              table.addTableCell(tc);
//              
//              // update the col span of this column (if not done so) 
//              if (nestedColSpans[colIndx] == null) {
//                int nestedSpan = nested.getColumnsSpan();
//                nestedColSpans[colIndx]= nestedSpan;
//                
//                table.setColumnSpan(colIndx, nestedSpan);
//                
//                //debug
//                //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
//              }
//              
//              // update the row span of the current row
//              nrowSpan = nested.getRowsSpan();
//              if (rowSpan < nrowSpan) 
//                rowSpan = nrowSpan;
//              
//              // determine the max header rows span among all the nested tables
//              //if (rowInd == 0 && withHeaders) {
//              if (withHeaders && nestedHeaders[colIndx] == null) {
//                nheaderRowsSpan = nested.getHeaderRowsSpan();
//                if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
//              }
//              
//              if (nestedHeaders[colIndx] == null) 
//                nestedHeaders[colIndx] = Boolean.TRUE;
//            } else {
//              // container not visible -> empty cell
//              TextCell tc = TextCell.EmptyCell.clone();
//              table.addTextCell(tc);
//              // record this so that we can update with a suitable column span later  
//              emptyCells.put(colIndx+"_"+rowInd, tc);
//            }
//          }
//        }
//        
//        colIndx++;
//      } // end data row loop
//      
//      // update the row span of this row and of all the non-nested cells
//      currRow.setProperty(RowSpan, rowSpan);
//      Cell c;
//      if (!nestedCols.isEmpty()) {
//        for (int i = 0; i < currRow.size(); i++) {
//          if (!nestedCols.contains(i)) {
//            c = currRow.get(i);
//            c.setProperty(RowSpan, rowSpan);
//          }
//        }
//      }
//      
//      if (currRow.isEmpty())
//        throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
//
//      table.endRow();
//
//      //if RowSpan is higher than some of the nested tables, fill these tables with empty rows up to the RowSpan
//      // TODO: can we use column span here?
//      if (!nestedTables.isEmpty()) {
//        for (Table t : nestedTables) {
//          nrowSpan = t.getRowsSpan();
//          if (nrowSpan < rowSpan) {
//            t.addEmptyRows(rowSpan - nrowSpan);
//          }
//        }
//      }
//      
//      // update rowInd based on rowspan
//      rowInd = rowInd+1;
//    } // end object buffer loop
//
//    // update header col and row spans
//    Integer cspan;
//    //if (withHeaders) {
//    if (table.hasHeader()) {
//      Row<HeaderCell> headerRow = table.getHeaderRow();
//      headerRowsSpan += maxNHeaderRowsSpan;
//      table.setHeaderRowsSpan(headerRowsSpan); 
//
//      for (int i = 0; i < headerRow.size(); i++) {
//        hc = headerRow.get(i);
//        cspan = nestedColSpans[i];
//        if (
//            cspan == null // not a nested column, update row span
//            ) {      
//          hc.setProperty(RowSpan, headerRowsSpan);
//        } else {  // nested column, update col span
//          hc.setProperty(ColSpan, cspan);
//        }
//      }
//    }
//    
//    // update all empty cells with suitable column span
//    if (!emptyCells.isEmpty()) {
//      String pos;
//      int colAt;
//      TextCell tc;
//      for (Entry<String,TextCell> e : emptyCells.entrySet()) {
//        pos = e.getKey();
//        colAt = Integer.parseInt(pos.split("_")[0]);
//        cspan = nestedColSpans[colAt];
//        if (cspan == null)  // not a nested column
//          cspan = 1;
//        tc = e.getValue();
//        tc.setProperty(ColSpan, cspan);
//        
//        // debug
//        //System.out.printf("Empty cell@col%d: %s (dcont: %s)%n  -> colspan: %d%n", colAt, tc, panel, cspan);
//      }
//    }
//    
//    //debug
//    //System.out.printf("%s%n", table);
//    
//    return table;
//  }
//  
//  /**
//   * @effects 
//   *  create and return a <tt>Table</tt> whose headers are defined from <tt>dtable.headers</tt>
//   *  and contains <b>all</b> data rows in <tt>dtable.buffer</tt>. The cell styles of each row 
//   *  are defined from the corresponding <tt>dtable.cellRenderers</tt>.
//   *  
//   *  <p>This table typically does not contain any nested tables.
//   */
//  private Table<Row<Cell>> createDocModelFromEmptyTableContainer(
//      DomainSchema schema, 
//      JDataTable dtable,
//      PropertySet printCfg, 
//      Dimension pgSize, boolean withHeaders) throws IOException {
//
//    /**
//     * adds the data model of this table to the document
//     */
//    // v2.5.4: ignore columns that are not visible
//    int colCount = dtable.getColumnCount();
//    int visibleColCount = dtable.getColumnCount(true); 
//    // a table with columns = dtable.columnCount
//    boolean border = false;
//    
//    Table<Row<Cell>> table = new Table<>(visibleColCount, TableType.NormalTable, printCfg);
//    table.setProperty(PageWidth, (int) pgSize.getWidth());
//
//    table.setBorder(border);
//    
//    // data: 
//    // v2.5.4: ignore columns that are not visible
//    Font headFont;
//    //Component renderer;
//    Color headColor;
//    int headW, headH;
//    Dimension headDim;
//    AlignmentX headAlignX;
//    JDataField dfeditor;
//    
//    // header rows span is 1 b/c there are no nested containers
//    int headerRowsSpan = 1;
//    
//    Row<Cell> currRow;
//    
//    int rowInd = 0, visColInd = 0;
//    
//    // create an empty table
//    currRow = table.beginRow();
//    for (int colInd = 0; colInd < colCount; colInd++) {
//      // if not visible then skip
//      if (!dtable.isColumnVisible(colInd)) {
//        continue;
//      }
//      
//      // create table header (if this is the first row)
//      if (withHeaders) { 
//        headFont = dtable.getHeaderFont(colInd);
//        headColor = dtable.getHeaderForeground(colInd);
//        headAlignX = dtable.getHeaderAlignmentX(colInd);
//        headDim = dtable.getHeaderPreferredSize(colInd);
//        headW = (int) headDim.getWidth();
//        headH = (int) headDim.getHeight();
//        createHeaderCell(table, 
//            dtable.getColumnName(colInd),
//            headW, headH, headFont, headColor, null, headAlignX, 1, 1
//            );
//      }
//      
//      dfeditor = dtable.getTableCellEditor(colInd);
//      createEmptyCell(schema, table, dfeditor);
//      table.setColumnSpan(visColInd, 1);
//      visColInd++;
//    }
//  
//    // row span of this row is 1 (since there are no nested rows)
//    currRow.setProperty(RowSpan, 1);
//    
//    if (currRow.isEmpty())
//      throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
//
//    table.endRow();
//    
//    // update the header rows span
//    table.setHeaderRowsSpan(headerRowsSpan);
//    
//    return table;
//  }
//  
//  /**
//   * @effects 
//   *  create and return a <tt>Table</tt> whose headers are defined from <tt>dtable.headers</tt>
//   *  and contains <b>all</b> data rows in <tt>dtable.buffer</tt>. The cell styles of each row 
//   *  are defined from the corresponding <tt>dtable.cellRenderers</tt>.
//   *  
//   *  <p>This table typically does not contain any nested tables.
//   */
//  private Table<Row<Cell>> createDocModelFromNonEmptyTableContainer(
//      DomainSchema schema, 
//      JDataTable dtable,
//      Iterator buffer, 
//      PropertySet printCfg, 
//      Dimension pgSize, boolean withHeaders) throws IOException {
//
//    /**
//     * adds the data model of this table to the document
//     */
//    // v2.5.4: ignore columns that are not visible
//    int colCount = dtable.getColumnCount();
//    int visibleColCount = dtable.getColumnCount(true); 
//    // a table with columns = dtable.columnCount
//    boolean border = false;
//    
//    Table<Row<Cell>> table = new Table<>(visibleColCount, TableType.NormalTable, printCfg);
//    table.setProperty(PageWidth, (int) pgSize.getWidth());
//
//    table.setBorder(border);
//    
//    // data: 
//    // v2.5.4: ignore columns that are not visible
//    Font headFont;
//    //Component renderer;
//    Color headColor;
//    int headW, headH;
//    Dimension headDim;
//    AlignmentX headAlignX;
//    JDataField dfeditor;
//    
//    // header rows span is 1 b/c there are no nested containers
//    int headerRowsSpan = 1;
//    
//    Row<Cell> currRow;
//    
//    int rowInd = 0, visColInd = 0;
//    Object o;
//    
//    while (buffer.hasNext()) {
//      o = buffer.next();
//      
//      currRow = table.beginRow();
//      for (int colInd = 0; colInd < colCount; colInd++) {
//        // if not visible then skip
//        if (!dtable.isColumnVisible(colInd)) {
//          continue;
//        }
//        
//        // create table header (if this is the first row)
//        if (withHeaders && rowInd==0) { 
//          headFont = dtable.getHeaderFont(colInd);
//          headColor = dtable.getHeaderForeground(colInd);
//          headAlignX = dtable.getHeaderAlignmentX(colInd);
//          headDim = dtable.getHeaderPreferredSize(colInd);
//          headW = (int) headDim.getWidth();
//          headH = (int) headDim.getHeight();
//          createHeaderCell(table, 
//              dtable.getColumnName(colInd),
//              headW, headH, headFont, headColor, null, headAlignX, 1, 1
//              );
//        }
//        
//        dfeditor = dtable.getTableCellEditor(colInd);
//        createCell(schema, table, dfeditor, o);
//        table.setColumnSpan(visColInd, 1);
//        visColInd++;
//      }
//    
//      // row span of this row is 1 (since there are no nested rows)
//      currRow.setProperty(RowSpan, 1);
//      
//      if (currRow.isEmpty())
//        throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
//
//      table.endRow();
//      rowInd++;
//    } // end row loop
//    
//    // update the header rows span
//    table.setHeaderRowsSpan(headerRowsSpan);
//    
//    return table;
//  }
//  
  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
   */  
  private Table<Row<Cell>> createDocModelFromPanelContainer(
      DODMBasic schema, 
      DefaultPanel panel,
      Iterator buffer, 
      Dimension pgSize, 
      boolean withHeaders) throws IOException {
  
    int colCount = panel.getVisibleComponents().size();
    
    // table with 2 columns: one for label, one for component
    Table<Row<Cell>> table = new Table(colCount, TableType.NormalTable, null);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    // disable the border of non-top-level tables
    if (panel.getParent() == null || panel.isNested()) {
      table.setBorder(false);
    }

    JLabel label;
    Component comp;
    JDataField df;
    Object val;
    JDataContainer dcont;
    
    Font font; 
    Color color;
    HeaderCell hc = null;
    Dimension dim;
    AlignmentX alignX;
    int w, h, colSpan, rowSpan, nrowSpan;
    
    // the table's header rows span (i.e. number of its header rows)
    int headerRowsSpan = 1;
    int nheaderRowsSpan, maxNHeaderRowsSpan = 0; // that of a nested table
    
    Row<Cell> currRow;
    int rowInd = 0;
    Object o;
    
    // the nested tables (if any)
    List<Table> nestedTables = new ArrayList<>();
    List<Integer> nestedCols = new ArrayList<>(); // to record the nested column indices
    
    // the spans of the columns of this table 
    Integer[] nestedColSpans = new Integer[colCount];
    Arrays.fill(nestedColSpans, null);
    
    // record the empty cells for update with column spans later
    Map<String,TextCell> emptyCells = new HashMap<>();

    // record whether or not a nested cols has got its header constructed
    Boolean[] nestedHeaders = new Boolean[colCount];
    Arrays.fill(nestedHeaders, null);
    
    List<JComponent> labels = panel.getLabelComponents();

    // create table rows from objects in buffer
    int colIndx;

    Table nested;
    //boolean _withHeaders;
    if (buffer == null) {
      // create an empty table
      colIndx = 0;

      currRow = table.beginRow();
      
      for (JComponent c : labels) {
        label = (JLabel) c;
        comp = label.getLabelFor();

        // skip null or invisible components
        if (comp == null || !comp.isVisible())
          continue;

        // create the headers
        if (withHeaders) {
          font = label.getFont();
          color = label.getForeground();
          alignX = DEF_HEADER_ALIGN; //GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
          
          dim = label.getSize();
          w = (int) dim.getWidth();
          h = (int) dim.getHeight();
          colSpan = 1;  // to be updated below
          hc = createHeaderCell(table, 
              getLabelText(label),
              w, h, font, color, null, alignX, colSpan, 1
              );
        }
        
        if (comp instanceof JDataField) {
          // create an empty cell
          createEmptyCell(schema, table, (JDataField)comp);
          table.setColumnSpan(colIndx, 1);
        } else { // data container
          // add a TableCell from the nested data container
          nestedCols.add(colIndx);
          
          dcont = View.toDataContainer(comp);
          
          //_withHeaders = (nestedHeaders[colIndx] == null);
          if (dcont.isVisible()
              ) {
             // create a table with full header and an empty row
              nested = createDocModel(schema, dcont, null, pgSize, withHeaders //_withHeaders
                  );
            //nestedTables.add(nested);
            
            TableCell tc = new TableCell(nested);
            table.addTableCell(tc);
            
            // update the col span of this column (if not done so) 
            if (nestedColSpans[colIndx] == null) {
              int nestedSpan = nested.getColumnsSpan();
              nestedColSpans[colIndx]= nestedSpan;
              
              table.setColumnSpan(colIndx, nestedSpan);
              //debug
              //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
            }
            
//            // update the row span of the current row
//            nrowSpan = nested.getRowsSpan();
//            if (rowSpan < nrowSpan) rowSpan = nrowSpan;
            
            // determine the max header rows span among all the nested tables
            if (withHeaders && nestedHeaders[colIndx] == null) {
              nheaderRowsSpan = nested.getHeaderRowsSpan();
              if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
            }
            
            if (nestedHeaders[colIndx] == null) 
              nestedHeaders[colIndx] = Boolean.TRUE;
          }
        }
        
        colIndx++;
      }
      
      if (currRow.isEmpty())
        throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");
      
      currRow.setProperty(RowSpan, 1);
      
      table.endRow();
    } else {
      // create a table with data rows
      while (buffer.hasNext()) {
        o = buffer.next();
        
        // create a data row (update the header if needed)
        colIndx = 0;
        rowSpan = 1;
        nestedTables.clear();
        nestedCols.clear();
        hc = null;
        
        // only create headers once for each table
        if (rowInd > 0 && withHeaders) withHeaders = false;
        
        currRow = table.beginRow();
        for (JComponent c : labels) {
          label = (JLabel) c;
          comp = label.getLabelFor();

          // skip null or invisible components
          if (comp == null || !comp.isVisible())
            continue;

          // if this is the first data row then create the headers
          if (rowInd == 0 && withHeaders) {
            font = label.getFont();
            color = label.getForeground();
            alignX = DEF_HEADER_ALIGN;//GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
            
            dim = label.getSize();
            w = (int) dim.getWidth();
            h = (int) dim.getHeight();
            colSpan = 1;  // to be updated below
            hc = createHeaderCell(table, 
                getLabelText(label),
                w, h, font, color, null, alignX, colSpan, 1
                );
          }
          
          if (comp instanceof JDataField) { // data field
            // add a Cell from the object value 
            df = (JDataField) comp;
            createCell(schema, table, df, o);
            table.setColumnSpan(colIndx, 1);
          } else { // data container
            // add a TableCell from the nested data container
            // starts a new row for the nested container
            nestedCols.add(colIndx);
            
            dcont = View.toDataContainer(comp);
            if (dcont.isVisible()
                //&&!dcont.getController().isCurrentObjectNull()
                ) {
              // get the object buffer for dcont as determined by o
              Collection bufferObjs = null;
              Object linkObj = schema.getDsm().getAttributeValue(o, dcont.getController().getLinkAttributeOfParent().name());
              if (linkObj != null) {
                if (linkObj instanceof Collection) {
                  bufferObjs = (Collection) linkObj;
                } else {
                  bufferObjs = new ArrayList();
                  bufferObjs.add(linkObj);
                }
              }
              
              // TODO: create a table regardless of whether its buffer is empty
              // an empty table has a complete header (if not created) and an empty row 
             // _withHeaders = (nestedHeaders[colIndx] == null);//(rowInd == 0) ? true : false;
              
              if (bufferObjs != null && !bufferObjs.isEmpty()) {
                // create  nested table for the container (create with headers only if not done so)
                nested = createDocModel(schema, dcont, bufferObjs.iterator(), pgSize, withHeaders // to create headers once with the parent (_withHeaders)
                    );
              } else {
                // empty buffer -> create a table with full header and an empty row
                nested = createDocModel(schema, dcont, null, pgSize, withHeaders//_withHeaders
                    );
              }
              
              nestedTables.add(nested);
              
              TableCell tc = new TableCell(nested);
              table.addTableCell(tc);
              
              // update the col span of this column (if not done so) 
              if (nestedColSpans[colIndx] == null) {
                int nestedSpan = nested.getColumnsSpan();
                nestedColSpans[colIndx]= nestedSpan;
                
                table.setColumnSpan(colIndx, nestedSpan);
                
                //debug
                //System.out.printf("nestedColSpans[%d]: %d (dcont: %s)%n", colIndx, nestedColSpans[colIndx], panel);
              }
              
              // update the row span of the current row
              nrowSpan = nested.getRowsSpan();
              if (rowSpan < nrowSpan) 
                rowSpan = nrowSpan;
              
              // determine the max header rows span among all the nested tables
              //if (rowInd == 0 && withHeaders) {
              if (withHeaders && nestedHeaders[colIndx] == null) {
                nheaderRowsSpan = nested.getHeaderRowsSpan();
                if (maxNHeaderRowsSpan < nheaderRowsSpan) maxNHeaderRowsSpan = nheaderRowsSpan;
              }
              
              if (nestedHeaders[colIndx] == null) 
                nestedHeaders[colIndx] = Boolean.TRUE;
            } else {
              // container not visible -> empty cell
              TextCell tc = TextCell.EmptyCell.clone();
              table.addTextCell(tc);
              // record this so that we can update with a suitable column span later  
              emptyCells.put(colIndx+"_"+rowInd, tc);
            }
          }
          
          colIndx++;
        } // end data row loop
        
        // update the row span of this row and of all the non-nested cells
        currRow.setProperty(RowSpan, rowSpan);
        Cell c;
        if (!nestedCols.isEmpty()) {
          for (int i = 0; i < currRow.size(); i++) {
            if (!nestedCols.contains(i)) {
              c = currRow.get(i);
              c.setProperty(RowSpan, rowSpan);
            }
          }
        }
        
        if (currRow.isEmpty())
          throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");

        table.endRow();

        //if RowSpan is higher than some of the nested tables, fill these tables with empty rows up to the RowSpan
        // TODO: can we use column span here?
        if (!nestedTables.isEmpty()) {
          for (Table t : nestedTables) {
            nrowSpan = t.getRowsSpan();
            if (nrowSpan < rowSpan) {
              t.addEmptyRows(rowSpan - nrowSpan);
            }
          }
        }
        
        // update rowInd based on rowspan
        rowInd = rowInd+1;
      } // end object buffer loop
    } // end non-empty data table

    // update header col and row spans
    Integer cspan;
    //if (withHeaders) {
    if (table.hasHeader()) {
      Row<HeaderCell> headerRow = table.getHeaderRow();
      headerRowsSpan += maxNHeaderRowsSpan;
      table.setHeaderRowsSpan(headerRowsSpan); 

      for (int i = 0; i < headerRow.size(); i++) {
        hc = headerRow.get(i);
        cspan = nestedColSpans[i];
        if (
            cspan == null // not a nested column, update row span
            ) {      
          hc.setProperty(RowSpan, headerRowsSpan);
        } else {  // nested column, update col span
          hc.setProperty(ColSpan, cspan);
        }
      }
    }
    
    // update all empty cells with suitable column span
    if (!emptyCells.isEmpty()) {
      String pos;
      int colAt;
      TextCell tc;
      for (Entry<String,TextCell> e : emptyCells.entrySet()) {
        pos = e.getKey();
        colAt = Integer.parseInt(pos.split("_")[0]);
        cspan = nestedColSpans[colAt];
        if (cspan == null)  // not a nested column
          cspan = 1;
        tc = e.getValue();
        tc.setProperty(ColSpan, cspan);
        
        // debug
        //System.out.printf("Empty cell@col%d: %s (dcont: %s)%n  -> colspan: %d%n", colAt, tc, panel, cspan);
      }
    }
    
    //debug
    //System.out.printf("%s%n", table);
    
    return table;
  }

  /**
   * @effects 
   *  create and return a <tt>Table</tt> whose headers are defined from <tt>dtable.headers</tt>
   *  and contains <b>all</b> data rows in <tt>dtable.buffer</tt>. The cell styles of each row 
   *  are defined from the corresponding <tt>dtable.cellRenderers</tt>.
   *  
   *  <p>This table typically does not contain any nested tables.
   */
  private Table<Row<Cell>> createDocModelFromTableContainer(
      DODMBasic schema, 
      JDataTable dtable,
      Iterator buffer, 
      Dimension pgSize, boolean withHeaders) throws IOException {

    /**
     * adds the data model of this table to the document
     */
    // v2.5.4: ignore columns that are not visible
    int colCount = dtable.getColumnCount();
    int visibleColCount = dtable.getColumnCount(true); 
    // a table with columns = dtable.columnCount
    boolean border = false;
    
    Table<Row<Cell>> table = new Table<>(visibleColCount, TableType.NormalTable, null);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    table.setBorder(border);
    
    // data: 
    // v2.5.4: ignore columns that are not visible
    Font headFont;
    //Component renderer;
    Color headColor;
    int headW, headH;
    Dimension headDim;
    AlignmentX headAlignX;
    JDataField dfeditor;
    
    // header rows span is 1 b/c there are no nested containers
    int headerRowsSpan = 1;
    
    Row<Cell> currRow;
    
    int rowInd = 0, visColInd = 0;
    Object o;
    
    if (buffer == null) {
      // create an empty table
      currRow = table.beginRow();
      for (int colInd = 0; colInd < colCount; colInd++) {
        // if not visible then skip
        if (!dtable.isColumnVisible(colInd)) {
          continue;
        }
        
        // create table header (if this is the first row)
        if (withHeaders) { 
          headFont = dtable.getHeaderFont(colInd);
          headColor = dtable.getHeaderForeground(colInd);
          headAlignX = DEF_HEADER_ALIGN; //dtable.getHeaderAlignmentX(colInd);
          headDim = dtable.getHeaderPreferredSize(colInd);
          headW = (int) headDim.getWidth();
          headH = (int) headDim.getHeight();
          createHeaderCell(table, 
              dtable.getColumnName(colInd),
              headW, headH, headFont, headColor, null, headAlignX, 1, 1
              );
        }
        
        dfeditor = dtable.getTableCellEditor(colInd);
        createEmptyCell(schema, table, dfeditor);
        table.setColumnSpan(visColInd, 1);
        visColInd++;
      }
    
      // row span of this row is 1 (since there are no nested rows)
      currRow.setProperty(RowSpan, 1);
      
      if (currRow.isEmpty())
        throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");

      table.endRow();
    } else {
      while (buffer.hasNext()) {
        o = buffer.next();
        
        currRow = table.beginRow();
        for (int colInd = 0; colInd < colCount; colInd++) {
          // if not visible then skip
          if (!dtable.isColumnVisible(colInd)) {
            continue;
          }
          
          // create table header (if this is the first row)
          if (withHeaders && rowInd==0) { 
            headFont = dtable.getHeaderFont(colInd);
            headColor = dtable.getHeaderForeground(colInd);
            headAlignX = DEF_HEADER_ALIGN; //dtable.getHeaderAlignmentX(colInd);
            headDim = dtable.getHeaderPreferredSize(colInd);
            headW = (int) headDim.getWidth();
            headH = (int) headDim.getHeight();
            createHeaderCell(table, 
                dtable.getColumnName(colInd),
                headW, headH, headFont, headColor, null, headAlignX, 1, 1
                );
          }
          
          dfeditor = dtable.getTableCellEditor(colInd);
          createCell(schema, table, dfeditor, o);
          table.setColumnSpan(visColInd, 1);
          visColInd++;
        }
      
        // row span of this row is 1 (since there are no nested rows)
        currRow.setProperty(RowSpan, 1);
        
        if (currRow.isEmpty())
          throw new ApplicationRuntimeException(null, DocumentBuilder.class.getSimpleName() + ".createTableFromPanelContainer: empty row");

        table.endRow();
        rowInd++;
      } // end row loop
    }
    
    // update the header rows span
    table.setHeaderRowsSpan(headerRowsSpan);
    
    return table;
  }
  
//  private void createTableHeader(Table table, DefaultPanel panel) {
//    
//    Font font; 
//    Color color;
//    
//    List<JComponent> labelComps = panel.getLabelComponents();
//    
//    int colCount = labelComps.size();
//    // ignore headers of columns that are not visible
//    HeaderCell hc;
//    Dimension dim;
//    AlignmentX alignX;
//    int w, h;
//    JLabel label;
//    
//    for (int i = 0; i < colCount; i++) {
//      // if not visible then skip
//      label = (JLabel) labelComps.get(i);
//      if (!label.isVisible()) {
//        continue;
//      }
//    
//      font = label.getFont();
//      color = label.getForeground();
//      alignX = GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
//      
//      dim = label.getSize();
//      w = (int) dim.getWidth();
//      h = (int) dim.getHeight();
//      
//      createHeaderCell(table, 
//          label.getText(),
//          w, h, font, color, null, alignX, 1
//          );
//    } 
//  }
//  
//  private void createTableHeader(Table table, JDataTable dtable) {
//    
//    Font font; 
//    Color color;
//    
//    int colCount = dtable.getColumnCount();
//    // ignore headers of columns that are not visible
//    HeaderCell hc;
//    Dimension dim;
//    AlignmentX alignX;
//    int w, h;
//    for (int i = 0; i < colCount; i++) {
//      // if not visible then skip
//      if (!dtable.isColumnVisible(i)) {
//        continue;
//      }
//    
//      font = dtable.getHeaderFont(i);
//      color = dtable.getHeaderForeground(i);
//      alignX = dtable.getHeaderAlignmentX(i);
//      dim = dtable.getHeaderPreferredSize(i);
//      w = (int) dim.getWidth();
//      h = (int) dim.getHeight();
//      createHeaderCell(table, 
//          dtable.getColumnName(i),
//          w, h, font, color, null, alignX, 1
//          );
//    } 
//  }

//  /**
//   * @effects 
//   *  return the value of the attribute of the domain object <tt>o</tt>, whose value is 
//   *  defined by <tt>df</tt>
//   */
//  private Object getAttributeValue(DomainSchema schema, Object o,
//      JDataField df) {
//    DomainConstraint dc = df.getDomainConstraint();
//    
//    Object val = schema.getAttributeValue(o, dc.name());
//    if (df instanceof JBindableField) {
//      // possibly bounded, if so get the bound value
//      DomainConstraint bc = ((JBindableField)df).getBoundConstraint();
//      if (bc != null) {
//        val = schema.getAttributeValue(val, bc.name());
//      }
//    }
//    
//    return val;
//  }

  protected Table<Row<Cell>> createDocModelSimple(DODMBasic schema, JDataContainer dataContainer, 
      Dimension pgSize, boolean withHeaders) throws IOException {
    Class containerType = dataContainer.getClass();

    Table<Row<Cell>> table = null;
    if (DefaultPanel.class.isAssignableFrom(containerType)) {
      DefaultPanel panel = (DefaultPanel) dataContainer;
      // no buffer needed, use the current object
      table =
          createSingleRowTableFromPanelContainer(schema, panel, pgSize);
    } else if (JDataTable.class.isAssignableFrom(containerType)) {
      JDataTable dtable = (JDataTable) dataContainer;
      Iterator buffer = dataContainer.getController().getObjectBuffer();
      table = createDocModelFromTableContainer(schema, dtable, buffer, pgSize, withHeaders);
    }

    return table;
  }
  
//  /**
//   * @effects 
//   *  create and return a <tt>Table</tt> whose headers are defined from <tt>panel.labels</tt>
//   *  and contains <b>all</b> the data rows that are mapped to the domain objects in <tt>panel.buffer</tt>.
//   *  The styles of the data cells of each row are defined based on <tt>panel.dataFields</tt>.
//   */
//  private Table<Row<Cell>> createTableFromPanelContainer(
//      DomainSchema schema, 
//      DefaultPanel panel,
//      Region printCfg, 
//      Dimension pgSize, 
//      boolean withHeaders) throws IOException {
//    // the object buffer
//    Iterator buffer = panel.getController().getObjectBuffer();
//    
//    return createTableFromPanelContainer(schema, panel, buffer, printCfg, pgSize, withHeaders);
//  }
  
  /**
   * @effects 
   *  create and return a 2-column <tt>Table</tt> that does not have any headers. 
   *  The first column are defined from <tt>panel.labels</tt> and the second column 
   *  is mapped to <b>single</b> current domain object of <tt>panel</tt>. The cells of 
   *  this column are defined from <tt>panel.dataFields</tt>.
   *  
   *  <p>This table contains a nested table for each child data container of <tt>panel</tt>.
   */
  private Table<Row<Cell>> createSingleRowTableFromPanelContainer(DODMBasic schema, 
      DefaultPanel panel,
      Dimension pgSize) throws IOException {

    // table with 2 columns: one for label, one for component
    Table<Row<Cell>> table = new Table(2, TableType.SideTable, null);
    table.setProperty(PageWidth, (int) pgSize.getWidth());

    //boolean tableBorder = true;
    
    // disable the border of non-top-level tables
    if (panel.getParent() == null || panel.isNested()) {
      table.setBorder(false);
    }

    // create header
    //createTableHeader(table, panel);
    
    DAttr dc;
    List<JComponent> labels = panel.getLabelComponents();

    JLabel label;
    Component comp;
    JDataField df;
    Object val;
    JDataContainer dcont;
    
    Object[] propValPairs = {
        ColSpan, 2
    };
    
    for (JComponent c : labels) {
      label = (JLabel) c;
      comp = label.getLabelFor();

      // skip null or invisible components
      if (comp == null || !comp.isVisible())
        continue;

      if (comp instanceof JDataField) { // data field
        df = (JDataField) comp;
        create2ColRowFromDataField(table, label, df);
      } else { // data container
        // the label
        // starts a new row
        table.beginRow();
        createTextCellFromLabel(table, label, propValPairs);
        
        // starts a new row for the nested container
        // the container's PDF table
        dcont = View.toDataContainer(comp);
        if (dcont.isVisible() &&  
            !dcont.getController().isCurrentObjectNull()) {
          // update the document with this container's view
          Table nested = createDocModelSimple(schema, dcont, pgSize, true);
          TableCell tc = new TableCell(nested);
          table.addTableCell(tc);
        } else {
          // empty cell
          TextCell tc = new TextCell("");
          table.addTextCell(tc);
        }
        
        table.endRow();
      }
    } // end for
    
    return table;
  }
  
  /**
   * @requires 
   *  table.currentRow is not null
   * @effects 
   *  creates and return a empty <tt>TextCell</tt> in the current row of <tt>table</tt> whose view settings 
   *  are defined based on <tt>df</tt>
   */
  public void createEmptyCell(DODMBasic schema, Table table, JDataField df) {
    Font font = df.getTextFont();
    Color foreground = df.getForegroundColor();
    Region viewCfg = df.getDataFieldConfiguration();
    
    // the object attribute for val 
    int w, h;
    Dimension dim = df.getGUIComponent().getPreferredSize();
    w = (int) dim.getWidth();
    h = (int) dim.getHeight();
    
    int colSpan = 1;
    Object[] propValPairs = {
        ColSpan, colSpan
    };
    createTextCell(table, TextCell.EmptyString, w, h, font, foreground, viewCfg, propValPairs);
  }
  
  /**
   * @effects 
   * @requires 
   *  table.currentRow is not null
   * @effects 
   *  creates and return a empty <tt>TextCell</tt> in the current row of <tt>table</tt> whose view settings 
   *  are defined based on the data field <tt>df</tt> of <tt>dataContainer</tt> 
   *  s.t. <tt>df.domainConstraint=attrib</tt>
   */
  private void createEmptyCell(DODMBasic schema, Table<Row<Cell>> table,
      JDataContainer dataContainer, DAttr attrib) {
    JDataField df = (JDataField) dataContainer.getComponent(attrib);
    createEmptyCell(schema, table, df);
  }
  
  /**
   * @requires 
   *  table.currentRow is not null
   * @effects
   *  add to <tt>table.currentRow</tt> an empty <tt>TextCell</tt>  
   */
  public void createEmptyCell(Table<Row<Cell>> table, Object...propValPairs) {
    TextCell c = TextCell.EmptyCell.clone();
    
    c.setProperty(propValPairs);
    
    table.addTextCell(c);
  }
  
  public void createCell(DODMBasic schema, Table<Row<Cell>> table,
      JDataField df, Object...propValPairs) {
    Object val = null;
    Font font = df.getTextFont();
    Color foreground = df.getForegroundColor();
    Region viewCfg = df.getDataFieldConfiguration();
    
    //TODO: use class name as well 
    String fieldName = df.getDomainConstraint().name();
    String imgName = fieldName;
    
    Dimension dim = df.getGUIComponent().getPreferredSize();
    int w = (int) dim.getWidth();
    int h = (int) dim.getHeight();
    // v2.7.2: support special-typed data fields (e.g. image, font, etc.)
    if (df instanceof JChooserDataField) {
      // get the actual object to display
      if (df instanceof JImageChooserField) {
        ImageIcon img = (ImageIcon) ((JImageChooserField) df).getValue();
        if (img != null)
          createImageCell(table, img, imgName, 
              ImageType.JPEG  // TODO: use field configuration option for this
              );
        else
          createTextCell(table, TextCell.EmptyString, w, h, font, foreground, viewCfg, propValPairs);
      } 
      // add renderers for other special types here
      else {
        val = df.getValue();
        createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
      }
    } else if (df.isSupportValueFormatting()) {
      // formatted field
      val = df.getValue();
      if (val != null)  {  
        val = df.getFormattedValue(val);
      }
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } else if (df instanceof JBindableField) { 
      // get the bounded value
      val = ((JBindableField) df).getBoundValue();
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } 
    // other cases: use the value
    else {
      val = df.getValue();
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    }    
  }
  
  /**
   * This method works similar to {@link #createCell(DODMBasic, Table, JDataField, Object)}
   * except that it takes a domain attribute as input and uses the style settings 
   * from the print configuration of that attribute, instead of from a data field.  
   * 
   * @effects 
   *  add a suitable <tt>Cell</tt> to <tt>table</tt> whose view configuration is defined by <tt>printfCfg</tt>
   *  and whose value is that of <tt>o.attrib</tt>
   */
  private void createCell(DODMBasic schema, 
      Table<Row<Cell>> table,
      JDataContainer dataContainer,
      DAttr attrib, 
      PropertySet printfCfg,
      Object o) {
    //TODO: use printfCfg of the attribute that is defined in the data container's print configuration
    // for now use the data field component setting
    JDataField df = (JDataField) dataContainer.getComponent(attrib);
    createCell(schema, table, df, o);
  }

  /**
   * @effects 
   *  add a suitable <tt>Cell</tt> to <tt>table</tt> whose view configuration is defined by <tt>df</tt>
   *  and whose value is defined by <tt>o</tt>
   */
  private void createCell(DODMBasic schema, Table table, JDataField df, Object o) {
    Font font = df.getTextFont();
    Color foreground = df.getForegroundColor();
    Region viewCfg = df.getDataFieldConfiguration();
    
    // the object attribute for val 
    final String attrib = df.getDomainConstraint().name();
    final int colSpan = 1;
    Object[] propValPairs = {
        ColSpan, colSpan
    };
    
    Object val = schema.getDsm().getAttributeValue(o, attrib);
    
    int w, h;
    Dimension dim = df.getGUIComponent().getPreferredSize();
    w = (int) dim.getWidth();
    h = (int) dim.getHeight();
    
    Oid id = schema.getDom().lookUpObjectId(o.getClass(), o);
    String idValString = id.toValueString(); 
    String fieldName = df.getDomainConstraint().name();
    String imgName = idValString+"_"+fieldName;
    
    // v2.7.2: support special-typed data fields (e.g. image, font, etc.)
    if (df instanceof JChooserDataField) {
      // get the actual object to display
      if (df instanceof JImageChooserField) {
        ImageIcon img = (ImageIcon) val;//((JImageChooserField) df).getValue();
        if (img != null)
          createImageCell(table, img, imgName, 
              ImageType.JPEG  // TODO: use field configuration option for this
              );
        else
          createTextCell(table, "", w, h, font, foreground, viewCfg, propValPairs);
      } 
      // add renderers for other special types here
      else {
        //val = schema.getDsm().getAttributeValue(o, attrib); //df.getValue();
        createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
      }
    } else if (df.isSupportValueFormatting()) {
      // formatted field
      //val = schema.getDsm().getAttributeValue(o, attrib); //df.getValue();
      if (val != null)  {  
        val = df.getFormattedValue(val);
      }
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } else if (df instanceof JBindableField) { 
      // get the bounded value
      //val = schema.getDsm().getAttributeValue(o, attrib); 
      if (val != null) {
        DAttr bConstraint = ((JBindableField)df).getBoundConstraint();
        if (bConstraint != null) {
          val = schema.getDsm().getAttributeValue(val, bConstraint.name()); 
        }
      }
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } 
    // other cases: use the value
    else {
      //val = schema.getDsm().getAttributeValue(o, attrib); //df.getValue();
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    }
  }
  
  /**
   * @effects 
   *  add <tt>(label,df)</tt> as a row to <tt>table</tt>
   * @version 2.7.2
   */
  private void create2ColRowFromDataField(Table table, 
      JLabel label, JDataField df) {
    
    final int colSpan = 1;
    Object[] propValPairs = {
        ColSpan, colSpan
    };
    
    table.beginRow();
    
    // (1) label
    //table.addCell(getPdfLabel(label));
    createTextCellFromLabel(table, label, propValPairs);
    
    Object val = null;
    Font font = df.getTextFont();
    Color foreground = df.getForegroundColor();
    Region viewCfg = df.getDataFieldConfiguration();
    
    int w, h;
    Dimension dim = df.getGUIComponent().getPreferredSize();
    w = (int) dim.getWidth();
    h = (int) dim.getHeight();
    
    String clsName = "";  // TODO: support class name
    String fieldName = df.getDomainConstraint().name();
    String imgName = clsName +"_" +fieldName;
    
    // v2.7.2: support special-typed data fields (e.g. image, font, etc.)
    if (df instanceof JChooserDataField) {
      // get the actual object to display
      if (df instanceof JImageChooserField) {
        ImageIcon img = (ImageIcon) ((JImageChooserField) df).getValue();
        if (img != null)
          //addImageCell(pdfWriter, table, img);
          createImageCell(table, img, imgName, 
              ImageType.JPEG  // TODO: use field configuration option for this
              );
        else
          //addTextCell(table, "", font, foreground);
          createTextCell(table, TextCell.EmptyString, w, h, font, foreground, viewCfg, propValPairs);
      } 
      // add renderers for other special types here
      else {
        val = df.getValue();
        //addTextCell(table, val, font, foreground);
        createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
      }
    } else if (df.isSupportValueFormatting()) {
      // formatted field
      val = df.getValue();
      if (val != null)  {  
        val = df.getFormattedValue(val);
      }
      //addTextCell(table, val, font, foreground);
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } else if (df instanceof JBindableField) { 
      // get the bounded value
      val = ((JBindableField) df).getBoundValue();
      //addTextCell(table, val, font, foreground);
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    } 
    // other cases: use the value
    else {
      val = df.getValue();
      //addTextCell(table, val, font, foreground);
      createTextCell(table, val, w, h, font, foreground, viewCfg, propValPairs);
    }
    
  //  if (val != null) {
  //    table.addCell(getPdfText(val.toString(), df.getFont(),
  //        df.getForeground()));
  //  } else {
  //    table.addCell("");
  //  }  
    
    table.endRow();
  }
  
  private void createImageCell(Table table,
      ImageIcon img, String imgName, ImageType imgType) {
    ImageCell c = new ImageCell(img, imgName, imgType);
    table.addImageCell(c);
  }
  
  
  /**
   * @effects 
   *  if printfCfg is not null and its styles settings are specified 
   *    create a <tt>HeaderCell</tt> using its style settings
   *  else
   *    create a <tt>HeaderCell</tt> using <tt>label</tt> style settings
   */
  private HeaderCell createHeaderCell(Table<Row<Cell>> table, 
      PropertySet printfCfg,
      JLabel label,
      int colSpan, int rowSpan) {
    //TODO: use printfCfg settings if available
    Font font = label.getFont();
    Color color = label.getForeground();
    
    AlignmentX alignX;
    /*
    AlignmentX alignX = (printfCfg != null) ? 
        printfCfg.getPropertyValue("alignX", AlignmentX.class): null;
        
    if (alignX == null)
        GUIToolkit.fromSwingAlignmentX(label.getHorizontalAlignment());
    */
    // use default alignment for header
    alignX = DEF_HEADER_ALIGN;
    
    Dimension dim = label.getSize();
    int w = (int) dim.getWidth();
    int h = (int) dim.getHeight();
    colSpan = 1;  // to be updated later
    HeaderCell hc = createHeaderCell(table, 
        getLabelText(label),
        w, h, font, color, null, alignX, colSpan, rowSpan
        );  
    
    return hc;
   }
  
  private HeaderCell createHeaderCell(Table table,
      String string, 
      int width, int height,
      Font font, Color color, 
      Region viewCfg,
      AlignmentX alignX, 
      int rowSpan,
      int colSpan) {
    HeaderCell c = new HeaderCell(string);
    boolean wrapping = true;
    
    String alignH = (alignX == null) ? 
        ((viewCfg != null) ? viewCfg.getAlignX().getName().toLowerCase() : DEF_CELL_ALIGN) :
        //alignX.getName().toLowerCase()
          alignX.getHtmlName()
          ;
    
    // TODO: add an option here whether or not to use the specified Color
    // for now use the default Color (Black)
    color = DEF_CELL_COLOR;
    
    c.setProperty(
        PreferredWidth, width,
        PreferredHeight, height,
        AlignX, alignH,
        TextFont, font, 
        TextColor, color,
        RowSpan, rowSpan, 
        ColSpan, colSpan,
        WrapText, wrapping
        );
    
    table.addHeaderCell(c);
    
    return c;
  }
  
  private void createTextCell(Table table,
      String string, 
      int width, int height,
      Font font, Color color, 
      Region viewCfg,
      AlignmentX alignX, 
      //int colSpan
      Object...propValPairs
      ) {
    TextCell c = new TextCell(string);
    
    String alignH = (alignX == null) ? 
        ((viewCfg != null) ? viewCfg.getAlignX().getName().toLowerCase() : DEF_CELL_ALIGN) :
        //alignX.getName().toLowerCase()
        alignX.getHtmlName()
          ;
    
    // TODO: add an option here whether or not to use the specified Color
    // for now use the default Color (Black)
    color = DEF_CELL_COLOR;

    c.setProperty(
        PreferredWidth, width,
        PreferredHeight, height,
        AlignX, alignH,
        TextFont, font, 
        TextColor, color
        //ColSpan, colSpan,
        //WrapText, wrapping
        );
    
    // other properties
    if (propValPairs != null) {
      c.setProperty(propValPairs);
    }
    
    if (!c.getProperty().containsKey(WrapText)) {
      // default wrapping
      boolean wrapping = true;
      c.setProperty(WrapText, wrapping);
    }
    
    table.addTextCell(c);
  }
  
//  private void createTextCell(Table table,
//      String string, 
//      int width, int height,
//      Font font, Color color, 
//      Region viewCfg,
//      int colSpan) {
//    createTextCell(table, string, width, height, font, color, viewCfg, null, colSpan);
//  }

//  private void createTextCell(Table table,
//      String string,
//      int width, int height,
//      Font font, Color color, 
//      Region viewCfg
//      ) {
//    int colSpan = 1;
//    //createTextCell(table, string, width, height, font, color, viewCfg, colSpan);
//    createTextCell(table, string, width, height, font, color, viewCfg, null, colSpan);
//  }

  private void createTextCell(Table table,
      Object stringable, 
      int width, int height, 
      Font font, Color foreground, 
      Region viewCfg, 
      Object...propValPairs
      ) {
//    Object[] propValPairs = {
//        ColSpan, colSpan
//    };
    
    if (stringable != null) {
      //createTextCell(table, stringable.toString(), width, height, font, foreground, viewCfg, colSpan);
      createTextCell(table, stringable.toString(), width, height, font, foreground, viewCfg, null, propValPairs);
    } else {
      //table.addCell("");
      //createTextCell(table, "", width, height, font, foreground, viewCfg, colSpan);
      createTextCell(table, TextCell.EmptyString, width, height, font, foreground, viewCfg, null, propValPairs);
    }
  }
  
  /**
   * @requires 
   *  table.currentRow is not null
   * @effects 
   *  create in the current row of <tt>table</tt> a <tt>TextCell</tt> whose value and view style (font, color) are defined 
   *  based on <tt>label</tt> and whose column span is <tt>colSpan</tt>. 
   */
  public void createTextCellFromLabel(Table table, JLabel label, Object...propValPairs) {
    String text = null;
    Color c = null;
    Font f = null;
    text = getLabelText(label);
    
    c = label.getForeground();
    f = label.getFont();
    
    Dimension dim = label.getPreferredSize();
    int w = (int) dim.getWidth();
    int h = (int) dim.getHeight();
    int alignXNo  = label.getHorizontalAlignment();
    AlignmentX alignX;
    switch(alignXNo) {
      case JLabel.LEFT:
        alignX = AlignmentX.Left; break;
      case JLabel.RIGHT:
        alignX = AlignmentX.Right; break;
      case JLabel.CENTER:
        alignX = AlignmentX.Center; break;
      default:
        alignX = AlignmentX.Left;
    }
    
    createTextCell(table, text, w, h, f, c, null, alignX, propValPairs);//colSpan);
  }

  /**
   * @requires 
   *  table.currentRow is not null
   * @effects 
   *  create in the current row of <tt>table</tt> a <tt>TextCell</tt> whose value and view style (font, color) are defined 
   *  based on <tt>label</tt> and whose column span is <tt>colSpan</tt>. 
   */
  private void createTextCellFromLabel(Table<Row<Cell>> table,
      Graphics g, 
      Label label, 
      Style style, 
      int width,
      AlignmentX alignX,
      int colSpan) {
    String text = getLabelText(label);
    
    Color c = GUIToolkit.getColorValue(style.getFgColor());
    Font f = GUIToolkit.getFontValue(style.getFont());
    
    int height = g.getFontMetrics(f).getHeight();  // to be determined 
    
    Object[] propValPairs = {
        ColSpan, colSpan
    };
    
    createTextCell(table, text, width, height, f, c, null, alignX, propValPairs);
  }
  
  /**
   * @requires 
   *  label != null
   * @effects 
   *  return the raw text of the label
   */
  private String getLabelText(JLabel label) {
    String text;
    if (label instanceof JHtmlLabel) {
      text = ((JHtmlLabel) label).getTextRaw();
    } else {
      text = label.getText();
    }  
    
    return text;
  }
  
  /**
   * @requires 
   *  label != null
   * @effects 
   *  return the raw text of the label
   */
  private String getLabelText(Label label) {
    String text = label.getValue();
    
    if (text != null) {
      StringBuffer raw = new StringBuffer(text);
      
      // remove all tags (if any)
      JHtmlLabel.removeHtmlTags(raw);
      
      text = raw.toString();
    }
    
    return text;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName();
  }
}
