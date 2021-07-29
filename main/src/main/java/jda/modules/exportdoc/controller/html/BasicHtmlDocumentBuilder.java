package jda.modules.exportdoc.controller.html;

import static jda.modules.exportdoc.util.table.Table.Prop.AlignX;
import static jda.modules.exportdoc.util.table.Table.Prop.TextColor;
import static jda.modules.exportdoc.util.table.Table.Prop.TextFont;
import static jda.modules.exportdoc.util.table.Table.Prop.Width;
import static jda.modules.exportdoc.util.table.Table.Prop.WrapText;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.ImageIcon;

import jda.modules.common.CommonConstants;
import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.common.io.ToolkitIO;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.DODMToolkit;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.dodm.dsm.DSMBasic;
import jda.modules.exportdoc.controller.DocumentBuilder;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.page.model.Page;
import jda.modules.exportdoc.util.table.Cell;
import jda.modules.exportdoc.util.table.HeaderCell;
import jda.modules.exportdoc.util.table.ImageCell;
import jda.modules.exportdoc.util.table.Row;
import jda.modules.exportdoc.util.table.Table;
import jda.modules.exportdoc.util.table.TableCell;
import jda.modules.exportdoc.util.table.TextCell;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.JDataContainer;
import jda.mosa.view.assets.GUIToolkit.ImageType;
import jda.mosa.view.assets.datafields.JBindableField;
import jda.mosa.view.assets.datafields.JDataField;
import jda.util.properties.PropertySet;

/**
 * @overview 
 *  A sub-type of {@link DocumentBuilder} that generates a basic HTML document. A basic document 
 *  uses a simple HTML table to represent the data table.
 *  
 * @author dmle
 */
public class BasicHtmlDocumentBuilder extends DocumentBuilder {
  private static final boolean debug = Toolkit.getDebug(BasicHtmlDocumentBuilder.class);

  protected static final char NL = '\n';

  private String htmlTemplateFile;
  protected HtmlPage htmlPageTempl;
  
  public BasicHtmlDocumentBuilder(DocumentExportController exportCtl, Configuration appConfig) throws NotPossibleException {
    this(exportCtl, appConfig, "BasicHtmlTemplate.html");
  }
  
  public BasicHtmlDocumentBuilder(DocumentExportController exportCtl, Configuration appConfig, 
      String htmlTemplateFile) throws NotPossibleException {
    super(exportCtl, appConfig);
    
    // Read the HTML template and store it in a Document object
    //htmlPageTempl = new HtmlDoc(htmlTemplateFile);
    this.htmlTemplateFile = htmlTemplateFile;
  }
  
  @Override
  protected String getFileExtension() {
    return ".html";
  }
  
  @Override
  public void init(DataDocument doc, PropertySet printCfg) 
      throws NotPossibleException, NotFoundException {
    super.init(doc, printCfg);
    
    //htmlPageTempl.init();
    // v2.7.4: use html template file from config (if specified)
    if (printCfg != null) {
      String configTmpFile = printCfg.getPropertyValue("docTemplate", String.class);
      if (configTmpFile != null && !configTmpFile.equals(CommonConstants.NullString)) {
        htmlTemplateFile = configTmpFile;
      }
    }
    
    if (htmlTemplateFile ==null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_DOCUMENT_TEMPLATE);
    }
    
    /*v2.7.4: requires all template files to be in the export/templates directory 
    htmlPageTempl = HtmlPage.createTemplatePage(htmlTemplateFile);
    */
    String tmpFolder = getTemplateFolderPath();
    String filePath = tmpFolder + File.separator + htmlTemplateFile;
    File tempFile = new File(filePath);
    if (!tempFile.exists()) {
      throw new NotFoundException(NotFoundException.Code.FILE_NOT_FOUND, new Object[] {filePath});
    }

    InputStream fins = null;
    try {
      fins = new FileInputStream(tempFile);
      
      htmlPageTempl = HtmlPage.createTemplatePage(fins);
      
      htmlPageTempl.setOutputFile(new File(getDocFile()));
    } catch (FileNotFoundException e) {
      // should not happen
    } finally {
      // v3.2c: added this clause
      if (fins != null) {
        try {
          fins.close();
        } catch (IOException e) {
          // ignore: e.printStackTrace();
        }
      }
    }
    
  }
  
  /**
   * @effects 
   *  read content of the template file <tt>tempFile</tt> from the designated template directory
   *  and return it as <tt>StringBuffer</tt>.
   *  
   *  <p>Throws <tt>NotPossibleException</tt> if failed to read file or file is not found.
   * @version 2.7.4
   */
  protected StringBuffer readTemplate(String tempFile) throws NotPossibleException {
    BufferedReader reader = null;
    try {
      String filePath = getTemplateFolderPath() + File.separator + tempFile;
      FileInputStream ins = new FileInputStream(filePath);
      
      reader = new BufferedReader(new InputStreamReader(ins, Charset.forName("utf-8")));
      
      StringBuffer templateContent = new StringBuffer();
      
      String s;
      while ((s = reader.readLine()) != null) {
        templateContent.append(s).append(NL);
      }
      
      return templateContent;
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          e, new Object[] {HtmlPage.class.getSimpleName(), ""}  
          );
    } finally {
      if (reader != null)
        try {reader.close(); } catch (Exception e) {}
    }  
  }
  
  @Override
  public void buildContent(DODMBasic dodm, JDataContainer dataContainer, PropertySet printCfg, DataDocument doc) 
      throws NotPossibleException {
    /************* GENERATE A SINGLE-PAGE HTML DOCUMENT ***/
    
    /*
     * 1/ Read the HTML template and store it in a Document object
     * 2/ Write the title table model
     *      let titleS = Document.TitleStyle
     *      let title = dataContainer.title
     *      
     *      set   titleS{Text.align} = title.alignX
     *            titleS{Text.size} = title.font.size
     *            titleS{Text.family} = title.font.family
     *            titleS{Text.color} = title.color
     *      add titleS to Document.TitleStyles
     *      
     *      set Document{Title} = title.value
     * 
     * 3/ Write the content table
     *      let tableModel = content table model
     *      let table = Document.Table
     *      set table{Table.width} = tableModel.width
     *      
     *      for each header cell h of tableModel
     *        let headerS = Document.HeaderStyle
     *        let headerT = table.Header
     *        
     *        set headerS{Header.no} = h.index
     *            headerS{Header.width} = h.width
     *            headerS{Text.align} = h.alignX
     *            headerS{Text.size} = h.font.size
     *            headerS{Text.family} = h.font.family
     *            headerS{Text.color} = h.color
     *        add headerS to Document.HeaderStyles
     *            
     *        set headerT{Header.no} = h.index
     *            headerT{Header.value} = h.value
     *        add headerT to table.Headers
     *        
     *      for each row r of tableModel
     *        let rowT = table.Row
     *        
     *        for each cell c of r
     *          let cellS = Document.CellStyle
     *          let cellT = rowT.Cell
     *          
     *          if r.index = 0
     *            set cellS{Column.no} = c.index
     *              cellS{Column.width} = c.width
     *              cellS{Text.align} = c.alignX
     *              cellS{Text.size} = c.font.size
     *              cellS{Text.family} = c.font.family
     *              cellS{Text.color} = c.color
     *            add cellS to Document.CellStyles
     *            
     *          set cellT{Column.no} = c.index
     *              cellT{Column.value} = c.value
     *          add cellT to rowT.Cells
     *        
     *        add rowT to table.Rows
     *     save document to file
     */
    try {
      
      // title table model (if any)
      Dimension pgSize = doc.getPageSize(); //PAGE_SIZE;
      
      Table<Row<Cell>> titleTable = createTitleModel(dataContainer, pgSize);
      titleTable.finalise();
      
      // content table model
      boolean withHeaders = true;
      Iterator buffer = dataContainer.getController().getObjectBuffer();
      Collection<DAttr> attribs = dataContainer.getDomainAttributes(true);
      Table<Row<Cell>> contentTable = createDocModel(dodm, dataContainer, buffer, 
          attribs, printCfg, pgSize, withHeaders);
      
      // Use this if print configuration is not used: 
      //  Table<Row<Cell>> contentTable = createDocModel(schema, dataContainer, buffer, 
      //     printCfg, pgSize, withHeaders);
      //
      
      contentTable.finalise();
      
      buildSinglePageContent(dodm, doc, titleTable, contentTable);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_DOCUMENT,
          e, "Không thể tạo nội dung văn bản {0} cho {1}", doc.getName(), dataContainer.toString());
    }
  }
  
  /**
   * @modifies doc
   * 
   * @effects 
   *  generate a single Html page using {@link HtmlPage} as the template and containing the data in <tt>titleTable</tt>
   *  and <tt>contentTable</tt>.
   *  
   *  <br>Add this page to <tt>doc</tt> and to the object pool of <tt>dodm</tt>
   */
  protected void buildSinglePageContent(DODMBasic dodm, DataDocument doc,
      Table<Row<Cell>> titleTable, Table<Row<Cell>> contentTable) throws DataSourceException {
    Dimension pgSize = doc.getPageSize();

    int docWidth = titleTable.getAbsoluteWidth(pgSize.getWidth());
    int docHeight = titleTable.getHeight(); 
    
    // update height
    int contentWidth = contentTable.getAbsoluteWidth(pgSize.getWidth());
    if (contentWidth > docWidth)
      docWidth = contentWidth;
    
    docHeight += contentTable.getHeight();
    
    // create a page from the template
    HtmlPage page = createTabularPageObject(htmlPageTempl, contentTable, -1); //HtmlPage.createPage(htmlPageTempl);
    
    //write title table
    StringBuffer titleHtml = writeTitleTable(page, titleTable);
    page.setTitleTable(titleHtml);
      
    // generate the Html table 
    StringBuffer tableHtml = writeSinglePageTable(page, contentTable);
    
    StringBuffer headerStyles = page.getHeaderStyles();
    StringBuffer cellStyles = page.getCellStyles();

    if (headerStyles != null) {
      page.setVar("HeaderStyle", headerStyles.toString());
    } else {
      page.setVar("HeaderStyle", "");        
    }

    if (cellStyles != null) {
      page.setVar("CellStyle", cellStyles.toString());
    } else {
      page.setVar("CellStyle", "");        
    }

    //htmlPageTempl.setVar("DataTable", tableHtml.toString());
    page.setDataTable(tableHtml);
    
    //docSize.setSize(docWidth, docHeight);
    page.setSize(docWidth, docHeight);
    
    // add pages to DODM
    DOMBasic dom = dodm.getDom();
    dom.addObject(page);
    
    doc.addPage(page);    
  }

  /**
   * @param rowIndx 
   * @effects 
   *  create a <b>tabular</b> <tt>page</tt> from <tt>htmlPageTemplate</tt>
   *  return page
   */
  protected HtmlPage createTabularPageObject(HtmlPage htmlPageTempl, 
      Table<Row<Cell>> table, int rowIndx) {
    HtmlPage page = HtmlPage.createPage(htmlPageTempl);
    
    // use different templates for whether or not table has a border
    if (table.getBorder()) {
      page.setDataTableTemplate(page.getTemplate("DataTable", true));
    } else {
      page.setDataTableTemplate(page.getTemplate("DataTableNoBorder", true));
    }
    
    // set page settings to that of the table.
    // Note: these settings apply only to the outer-most data table
    page.setDataTableAlignX(table.getAlignXAsString());
    page.setDataTableWidth(table.getWidth());
    
    // set page coverage to contain rowIndx
    page.addRowCoverage(rowIndx);
    
    return page;
  }
  
  /**
   * @param rowIndx 
   * @effects 
   *  create a <b>simple</b> <tt>page</tt> from <tt>htmlPageTemplate</tt>
   *  return page
   */
  protected HtmlPage createSimplePageObject(HtmlPage htmlPageTempl) {
    HtmlPage page = HtmlPage.createPage(htmlPageTempl);
    
    return page;
  }
  
  @Override
  public void finalise(DataDocument doc) {
    super.finalise(doc);
    
    // save doc content
    save(doc);
  }
  
  protected void save(DataDocument doc) {
    // ASSUME: single page document
    savePage((HtmlPage)doc.getPages().iterator().next());
  }
  
  /**
   * @requires 
   *  doc != null /\ size(doc.pages) = 1 (i.e. single-page document)
   */
  public void savePage(Page htmlPage) throws NotPossibleException {
    // Save the results to docFile
    //String docFile = getDocFile();
    //File file = new File(docFile);
    
    File file = htmlPage.getOutputFile();
    
    if (!file.exists()) {
      try {
        file.createNewFile();
      } catch (IOException e) {
        throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_TO_FILE, 
            e, "Không thể ghi ra tệp {0}", file.getPath()
        );
      }
    }
    
    StringBuffer content = htmlPage.getContent();
    
    BufferedWriter writer = null;
    try {
      writer = new BufferedWriter(
                  new OutputStreamWriter(
                      new FileOutputStream(file, false), Charset.forName("utf-8")));
      
      String[] lines = content.toString().split(HtmlPage.NL);
      int numLines = lines.length;
      int i = 0;
      for (String line : lines) {
        writer.write(line);
      
        if (i < numLines-1)
          writer.write(HtmlPage.NL);
        
        i++;
      }
    } catch (IOException e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_WRITE_TO_FILE, 
          e, "Không thể ghi ra tệp {0}", file.getPath()
      );
      
    } finally {
      if (writer != null) try {writer.close(); } catch (Exception e) {}
    }      
  }
  
  @Override
  public void close() {
    // do nothing
  }
  
  /**
   * @modifies doc
   * @effects 
   *  (recursively) create and return an Html data table from the content of <tt>contentTable</tt>; 
   *  updating the header and cell styles in <tt>doc</tt>
   */
  protected StringBuffer writeSinglePageTable(HtmlPage doc, 
      Table<Row<Cell>> contentTable
//      StringBuffer headerStyles,
//      StringBuffer cellStyles
      ) {
    StringBuffer tableHtml = doc.getTemplate("DataTable", true);

    setHtmlVars(tableHtml, 
        "Table.width", contentTable.getWidth()+"");
    
    // write header (if specified)
    Row<HeaderCell> header = contentTable.getHeaderRow();
    final int tableId = contentTable.getId(); 
        
    if (header != null) {
      int indx = 0;
      String headerId;
      StringBuffer headerS, headerCellT, headerRowT, 
      headerCells = new StringBuffer();
      headerRowT = doc.getTemplate("HeaderRow", true);
      
      for (HeaderCell h : header) {
        headerCellT = doc.getTemplate("HeaderCell", true);
        headerId = tableId + "_" + indx;
        
        headerS = doc.getTemplate("HeaderStyle", true);
        setHtmlVars(headerS, 
            "Header.no",
            headerId,
            "Header.width",
            h.getProperty().get(Width,0)+""
          );
        setHtmlVarsFromCell(headerS, h);
        
        //append(headerStyles, headerS);
        doc.addHeaderStyle(headerS);
        
        setHtmlVars(headerCellT, 
              "Header.no",headerId,
              "rowspan", "1", //h.getProperty().getIntegerValue(RowSpan, 1)+"", 
              "colspan", "1", //h.getProperty().getIntegerValue(ColSpan, 1)+"", 
              "Header.value",h.getVal()
            );
        
        append(headerCells, headerCellT);
        
        indx++;
      }

      
      setHtmlVars(headerRowT, 
          "Cells", headerCells.toString());
      
      setHtmlVars(tableHtml, 
          "HeaderRows", headerRowT.toString());

    } else {
      // no header
      setHtmlVars(tableHtml, "HeaderRows", "");
    }
    
    // write data rows
    int numRows = contentTable.getNumberOfRows();
    int colNo;
    String colId;
    Row<Cell> r;
    TextCell c;
    StringBuffer dataRowT, cellS, cellT;
    StringBuffer dataCells;
    StringBuffer dataRows = new StringBuffer();
    String cellVal;
    for (int rowInd = 0; rowInd < numRows; rowInd++) {
      // next row
      r = contentTable.get(rowInd);
      dataRowT = doc.getTemplate("DataRow", true);

      colNo = 0;
      //append(dataRows, "<tr>", HtmlDoc.NL);
      dataCells = new StringBuffer();
      
      for (Cell co : r) {
        cellT = doc.getTemplate("DataCell", true);
        colId = tableId + "_" + colNo;

        if (co instanceof TableCell) {
          // recursive
          StringBuffer nestedTable = writeSinglePageTable(doc, 
              (Table<Row<Cell>>) ((TableCell)co).getVal()
//              headerStyles,
//              cellStyles
              );
          cellVal = nestedTable.toString();
        } else {
          /*v2.7.3: support ImageCell
          c = (TextCell) co;  
          cellVal = c.getVal();
          */
          if (co instanceof ImageCell) {
            cellVal = writeImageCell((ImageCell) co);
          } else {
            // TODO: support other cell types here
            c = (TextCell) co;  
            cellVal = c.getVal();
          }
        }
        
        
        if (header != null && rowInd == 0) {
          // first row of a table with header: generate cell styles used for all rows

          cellS = doc.getTemplate("CellStyle", true);
          setHtmlVars(cellS, 
              "Column.no", colId,
              "Column.width", co.getProperty().get(Width,0)+""              
              );
          setHtmlVarsFromCell(cellS, co);
          
          //append(cellStyles, cellS);
          doc.addCellStyle(cellS);
        } else if (header == null) {
          // table with a side header: generate cell styles for each cell
          colId = colId + "_" + rowInd; // add rowId to the column number
          cellS = doc.getTemplate("CellStyle", true);
          setHtmlVars(cellS, 
              "Column.no", colId,
              "Column.width", co.getProperty().get(Width,0)+""              
              );
          setHtmlVarsFromCell(cellS, co);
          
          //append(cellStyles, cellS);
          doc.addCellStyle(cellS);
        }
        
        // cell text
        setHtmlVars(cellT, 
            "Column.no", colId, 
            "rowspan", "1", //co.getProperty().getIntegerValue(RowSpan, 1)+"", 
            "colspan", "1", //co.getProperty().getIntegerValue(ColSpan, 1)+"",
            "height", "0", 
            "Column.value", cellVal);
        append(dataCells, cellT);
        
        colNo++;
      } // end for Cell

      setHtmlVars(dataRowT, 
          "Cells", dataCells.toString()
          );
      
      append(dataRows, dataRowT);
    } // end for Row
    
    setHtmlVars(tableHtml, "DataRows", dataRows.toString());

    return tableHtml;
  }

  /**
   * @modifies doc
   *  create Html table from <tt>titleTable</tt> and sets it to <tt>page</tt>;
   *  also update <tt>page.title</tt> and <tt>page.styles</tt> used for this table 
   */
  protected StringBuffer writeTitleTable(HtmlPage page, Table<Row<Cell>> titleTable) {
    
    /*v2.7.4: support more complete title 
    Row<Cell> titleRow = titleTable.get(0);
    
    // title table template
    StringBuffer tableHtml = page.getTemplate("TitleTable", true);
    StringBuffer titleS = page.getTemplate("TitleStyle", true);
    StringBuffer rowT = page.getTemplate("TitleRow", true);
    

    // TODO: supportcomplete title
    //TextCell titleCell = (TextCell)titleRow.get(0);
    Cell titleCell = titleRow.get(0);
    String titleTxt;
    if (titleCell instanceof ImageCell) {
      titleTxt = writeImageCell((ImageCell) titleCell);//support image cell "";
    } else {
      titleTxt = ((TextCell)titleCell).getVal();
    }
    
    // title styles
    setHtmlVarsFromCell(titleS, titleCell);
    page.setVar("TitleStyle", titleS.toString());
    
    // document title
    page.setVar("Title", titleTxt); //titleCell.getVal());

    // title text, images, etc.
    setHtmlVars(rowT, "Title", titleTxt); //titleCell.getVal());
    
    // title table html
    setHtmlVars(tableHtml,
        "Table.align", titleTable.getAlignXAsString()+"",
        "Table.width", titleTable.getWidth()+"",
        "TitleRow", rowT.toString()
        );

    return tableHtml;
    */
    Row<Cell> titleRow = titleTable.get(0);
    
    // title table template
    StringBuffer tableHtml = page.getTemplate("TitleTable", true);
    StringBuffer titleS = page.getTemplate("TitleStyle", true);
    StringBuffer rowT = null;
    
    String titleTxt;
    Cell firstCell = titleRow.get(0);
    String left = null;
    if (titleRow.size() == 1) {
      // simple title
      rowT = page.getTemplate("TitleRow", true);
      
      if (firstCell instanceof ImageCell) {
        left = writeImageCell((ImageCell) firstCell);//support image cell "";
        titleTxt = "";
      } else {
        left = ((TextCell)firstCell).getVal();
        titleTxt = left;
      }
      
      // title styles
      setHtmlVarsFromCell(titleS, firstCell);
      
      // title text, images, etc.
      setHtmlVars(rowT, "Title", left);     
    } else {
      // ASSUME: titleRow.size() == 2
      // TODO: support right component
      rowT = page.getTemplate("TitleRowWithLeft", true);      
      Cell secondCell = titleRow.get(1);
      String centre = null;

      if (firstCell instanceof ImageCell) {
        left = writeImageCell((ImageCell) firstCell);
      } else {
        left = ((TextCell)firstCell).getVal();
      }

      if (secondCell instanceof ImageCell) {
        centre = writeImageCell((ImageCell) secondCell);
        titleTxt = "";
      } else {
        centre = ((TextCell)secondCell).getVal();
        titleTxt = centre;
      }

      // title styles
      setHtmlVarsFromCell(titleS, secondCell);
      
      // title text, images, etc.
      setHtmlVars(rowT, "Left", left, "Title", centre);     
    }

    
    // update page
    page.setVar("TitleStyle", titleS.toString());
    page.setVar("Title", titleTxt); //titleCell.getVal());

    // title table html
    setHtmlVars(tableHtml,
        "Table.align", titleTable.getAlignXAsString()+"",
        "Table.width", titleTable.getWidth()+"",
        "TitleRow", rowT.toString()
        );

    return tableHtml;

  }
  
  /**
   * This method converts value in a similar fashion to {@link #createCell(DODMBasic, Table, JDataField, Object...)}
   * 
   * @effects 
   * if attribVal is null
   *  return null
   * else
   *  convert <tt>attribVal</tt> 
   *    (using the corresponding data field in <tt>dataContainer</tt> of <tt>attrib</tt>, if available) 
   *  to an HTML-friendly value and return it
   *  
   */
  protected Object toHtmlFriendlyVal(DODMBasic dodm, 
      JDataContainer dataContainer, // nullable 
      DAttr attrib, 
      Object attribVal) {
    Object val = attribVal;
    
    DSMBasic dsm = dodm.getDsm();
    
    // use data field to format value (if available)
    JDataField dataField = (dataContainer != null) ? 
        (JDataField) dataContainer.getComponent(attrib) : 
          null;
    
    //TODO: use class name as well 
    String fieldName = attrib.name();
    DAttr.Type type = attrib.type();
    DAttr.Format format = attrib.format();
    DAttr boundedAttrib;
    
    // v2.7.2: support special-typed data fields (e.g. image, font, etc.)
    if (type.isImage()) {
      // get the actual object to display
      if (attribVal != null) {
        ImageIcon img = (ImageIcon) attribVal;
        String imgName = fieldName;
        //TODO: get actual type here
        ImageType imgType = GUIToolkit.ImageType.PNG;
        val = writeImage(img, imgName,imgType);
      }
    } else if (format != DAttr.Format.Nil) {
      // formatted field
      if (attribVal != null) {
        if (dataField != null && dataField.isSupportValueFormatting()) {
          // has data field
          val = dataField.getFormattedValue(attribVal);
        } else {
          // no data field or no format supported: try to format
          val = DODMToolkit.formatDomainValue(dsm, type, format, attribVal);
        }
      }
    } else if (type.isDomainType()) { 
      // get the bounded value
      if (attribVal != null) {
        boundedAttrib = (dataField != null && dataField instanceof JBindableField) ? 
            ((JBindableField) dataField).getBoundConstraint() : null;
        if (boundedAttrib != null) 
          val = dsm.getAttributeValue(attribVal.getClass(), attribVal, boundedAttrib);
        else  // no bounded attribute specified, get id attribute value
          val = dsm.getIDAttributeValue(attribVal);
      }
    } 
    // other cases: use the value
    
    return val;
  }

  /**
   * @requires 
   *  imgCell != null
   *  
   * @effects 
   *  write <tt>imgCell.val</tt> to a <tt>file</tt> in a designated <tt>images</tt> sub-folder of the configured 
   *  document export folder, 
   *  generate and return an HTML <tt>img</tt> tag whose <tt>src = (path-to-file)</tt>.
   *  
   *  <p>Throws NotPossibleException if failed to create the file
   */
  protected String writeImageCell(ImageCell imgCell) throws NotPossibleException {
    // write imgCell to file 
    ImageIcon img = imgCell.getVal();
    String imgName = imgCell.getName();
    ImageType imgType = imgCell.getImageType();
    
    /*v2.7.4: moved to method
    String imagesFolderName = "images";
    String imgFolderPath = getExportFolderPath()+Configuration.FILE_SEPARATOR+imagesFolderName;
    String imageFileName = imgName + "." + imgType.getCommonExtension();
    String imgFile = imgFolderPath + Configuration.FILE_SEPARATOR + imageFileName;
    String imageTagFile = imagesFolderName + "/" + imageFileName;
    
    File imgFolder = new File(imgFolderPath);
    Toolkit.createFolderIfNotExists(imgFolder);
    
    GUIToolkit.writeImageFile(img, imgType, imgFile);
    
    // generate HTML img tag from the template
    StringBuffer imgTag = htmlPageTempl.getTemplate("ImageTag", true);
    setHtmlVars(imgTag, "Image.file", imageTagFile);
    
    return imgTag.toString();
    */
    
    return writeImage(img, imgName, imgType);
  }

  /**
   * @effects 
   *  write <tt>img</tt> to a file in "images" directory and return its tag name
   */
  protected String writeImage(ImageIcon img, String imgName, ImageType imgType) {
    String imagesFolderName = "images";
    String imgFolderPath = getExportFolderPath()+Configuration.FILE_SEPARATOR+imagesFolderName;
    String imageFileName = imgName + "." + imgType.getCommonExtension();
    String imgFile = imgFolderPath + Configuration.FILE_SEPARATOR + imageFileName;
    String imageTagFile = imagesFolderName + "/" + imageFileName;
    
    File imgFolder = new File(imgFolderPath);
    ToolkitIO.createFolderIfNotExists(imgFolder);
    
    GUIToolkit.writeImageFile(img, imgType, imgFile);
    
    // generate HTML img tag from the template
    StringBuffer imgTag = htmlPageTempl.getTemplate("ImageTag", true);
    setHtmlVars(imgTag, "Image.file", imageTagFile);
    
    return imgTag.toString();
  }

  protected void append(StringBuffer buffer, String...strings) {
    for (String s: strings) {
      buffer.append(s);
    }
  }
  
  public void append(StringBuffer buffer, StringBuffer sb) {
    buffer.append(sb);
  }
  
  /**
   * @requires 
   *  varValuepairs != null
   * @effects
   * <pre> 
   *  for each pair (var,val) in varValuePairs
   *    replaces all instances of var in htmlStr by val
   *    throws NotFoundException if var is not found
   * </pre>   
   */
  public void setHtmlVars(StringBuffer htmlStr, String...varValuePairs) throws NotFoundException {
    //v3.3: int start, end;
    String var, varName, val;
    
    for (int i = 0; i < varValuePairs.length; i++) {
      varName = varValuePairs[i];
      var = "{"+varName+"}";
      i++;
      val = varValuePairs[i];
      /*v3.3: fixed: this code replaces only the first occurrence, not ALL as required
      start = htmlStr.indexOf(var);
      if (start < 0)
        throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, 
            new Object[] {varName});
      
      end = start + var.length();
      htmlStr.replace(start, end, val);
      */
      replaceAllInBuffer(htmlStr, var, varName, val);
    }
  }

  /**
   * @effects 
   *  replaces all occurences of <tt>var</tt> in <tt>buffer</tt> (whose actual variable name is <tt>varName</tt>, if specified) 
   *  by <tt>val</tt>
   * @version 3.3
   */
  private void replaceAllInBuffer(StringBuffer buffer, String var, String varName, String val) throws NotPossibleException {
    int start, end;
    final int htmlStrLen = buffer.length(); // v3.3
    final int varLen = var.length();
    for (int pos = 0; pos < htmlStrLen ; pos++) { // replace all occurrences of var
      start = buffer.indexOf(var, pos);
      if (start < 0) {
        if (pos == 0) {
          // error: var is not found at all
          throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, 
            new Object[] {(varName != null) ? varName : var});
        } else {
          // no more vars
          break;
        }
      }
        
      end = start + varLen;
      buffer.replace(start, end, val);
      
      pos = start + val.length()-1;
    }    
  }

  /**
   * This is a more general version of {@link #setHtmlVars(StringBuffer, String...)} in that it does not assume 
   * any knowledge of <tt>key</tt>, which can be a variable or an embedded script fragment. 
   * 
   * @requires 
   *  key is found in <tt>content</tt>
   *  
   * @modifies content
   * @effects 
   *  replaces the <b>first</b> occurrence of the string <tt>key</tt> in <tt>content</tt> with <tt>value</tt>
   *  
   * @version 3.3
   */
  public void setHtmlContentKeyValue(StringBuffer content, String key, String value) {
    int start = content.indexOf(key);
    
    if (start >= 0) { 
      int end = start + key.length();
      
      content.replace(start, end, value);
    }
  }
  
  /**
   * @requires 
   *  varValuepairs != null
   * @effects
   * <pre> 
   *  for each pair (prop,propVal) where prop is in cell.properties and propVal is prop.value
   *    replaces all instances of prop in htmlStr by propVal
   *    throws NotFoundException if prop is not found
   * </pre>   
   */
  protected void setHtmlVarsFromCell(StringBuffer htmlStr, Cell cell) throws NotFoundException {
    Font font = (Font) cell.getProperty().getObjectValue(TextFont, Font.class);
    Color color = (Color) cell.getProperty().getObjectValue(TextColor, Color.class);
    
    String webColor = (color != null) ? toWebColor(color) : ""; 
    
    String[] varNames = {
        "Text.align",
        "Text.wrap",
        "Text.size", 
        "Text.family",
        "Text.color",
    };
    
    String align = cell.getProperty().getStringValue(AlignX, DEF_CELL_ALIGN);
    String fsz = (font != null) ? font.getSize()+"" : DEF_FONT_SIZE;
    String fn = (font != null) ? font.getFamily()+"" : DEF_FONT_FAMILY;
    boolean wrapText = cell.getProperty().getBooleanValue(WrapText, DEF_TEXT_WRAPPING);
    String wrapping = (wrapText) ? "" : "nowrap";
    //int h = cell.getProperty().getIntegerValue(Height, DEF_ROW_HEIGHT);
    //String height = (h == DEF_ROW_HEIGHT) ? "" : h+""; 
    
    String[] values = {
        align,
        wrapping,
        fsz,
        fn,
        webColor,
    };
    
    int i = 0;
    // v3.3: int start, end;
    String var; 
    String val; // v3.3
    for (String varName : varNames) {
      var = "{" + varName + "}";
      /* v3.3: fixed: this code only replaces one occurrence of var, not ALL as required
      start = htmlStr.indexOf(var);
      if (start < 0)
        throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, 
            "Không tìm thấy mẫu (template): ", varName);
      
      end = start + var.length();
      htmlStr.replace(start, end, values[i]);
      i++;
      */
      val = values[i];
      i++;
      replaceAllInBuffer(htmlStr, var, null, val);
    }
  }

  protected String toWebColor(Color color) {
    StringBuffer sb = new StringBuffer("#");
    
    String red = Integer.toHexString(color.getRed()); 
    String green = Integer.toHexString(color.getGreen());
    String blue = Integer.toHexString(color.getBlue());

    // add the leading 0s (if needed)
    if (red.length()==1) { 
      red = "0"+red;
    }

    if (green.length()==1) {
      green = "0"+green;
    }

    if (blue.length()==1) { 
      blue = "0"+blue;
    }
    
    sb.append(red).append(green).append(blue);
    
    return sb.toString();

  }

//  @Override
//  public InputStream getContentStream() {
//    if (htmlPageTempl != null) {
//      return htmlPageTempl.getContentStream();
//    } else {
//      return null;
//    }
//  }
//  
//  @Override
//  public String getContentAsString() {
//    if (htmlPageTempl != null)
//      return htmlPageTempl.getContent().toString();
//    else
//      return null;
//  }
  
//  @Override
//  public Dimension getContentSize() {
//    if (htmlPageTempl != null) {
//      return doc.getSize();
//    } else {
//      return null;
//    }
//  }
  
//  public Dimension getPreferredSize() {
//    return PAGE_SIZE;
//  }
}
