package jda.modules.exportdoc.controller.html;

import static jda.modules.exportdoc.util.table.Table.Prop.ColSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.Height;
import static jda.modules.exportdoc.util.table.Table.Prop.RowSpan;
import static jda.modules.exportdoc.util.table.Table.Prop.Visible;
import static jda.modules.exportdoc.util.table.Table.Prop.Width;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.DataSourceException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dodm.DODMBasic;
import jda.modules.dodm.dom.DOMBasic;
import jda.modules.exportdoc.controller.DocumentExportController;
import jda.modules.exportdoc.htmlpage.model.HtmlCell;
import jda.modules.exportdoc.htmlpage.model.HtmlHeader;
import jda.modules.exportdoc.htmlpage.model.HtmlHeaderRow;
import jda.modules.exportdoc.htmlpage.model.HtmlPage;
import jda.modules.exportdoc.htmlpage.model.HtmlRow;
import jda.modules.exportdoc.model.DataDocument;
import jda.modules.exportdoc.util.table.Cell;
import jda.modules.exportdoc.util.table.HeaderCell;
import jda.modules.exportdoc.util.table.ImageCell;
import jda.modules.exportdoc.util.table.Row;
import jda.modules.exportdoc.util.table.Table;
import jda.modules.exportdoc.util.table.TableCell;
import jda.modules.exportdoc.util.table.TextCell;
import jda.modules.exportdoc.util.table.Table.TableType;
import jda.modules.mccl.conceptmodel.Configuration;
import jda.mosa.view.assets.JDataContainer;
import jda.util.properties.PropertySet;

/**
 * @overview
 *  A sub-type of {@link BasicHtmlDocumentBuilder} which provides more complex HTML support with nested headers and data rows.
 *  
 * @author dmle
 */
public class DefaultHtmlDocumentBuilder extends BasicHtmlDocumentBuilder {

  private static final boolean debug = Toolkit.getDebug(DefaultHtmlDocumentBuilder.class);
  
  public DefaultHtmlDocumentBuilder(DocumentExportController exportCtl, Configuration appConfig) throws NotPossibleException {
    super(exportCtl, appConfig, "DefaultHtmlTemplate.html");
  }
  
  @Override
  public void buildContent(DODMBasic dodm, JDataContainer dataContainer, PropertySet printCfg, DataDocument doc) 
      throws NotPossibleException {
    /************* GENERATE A MULTI-PAGE HTML DOCUMENT ***/
    Dimension pgSize = doc.getPageSize(); //PAGE_SIZE;
    
    try {
      
      // title table model (if any)
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

      /*
      // update height
      int docWidth = 0, docHeight = 0;
      docWidth = titleTable.getAbsoluteWidth(pgSize.getWidth());
      docHeight = titleTable.getHeight(); 
      
      int contentWidth = contentTable.getAbsoluteWidth(pgSize.getWidth());
      if (contentWidth > docWidth)
        docWidth = contentWidth;
      
      docHeight += contentTable.getHeight();
      if (docHeight > pgSize.getHeight()) {
      */

      //System.out.printf("%s.buildMultiPageContent%n", this);
      //buildMultiPageContent(dodm, doc, titleTable, contentTable);
      // FIXME: only supports multi-page export for non-nested tables
      // multi-page printing support for nested-tables is possible for much more complex!!! 
      if (!contentTable.hasMultipleNestedRows()) {
        // non nested -> multi-page
        System.out.printf("%s.buildMultiPageContent%n", this);
        buildMultiPageContent(dodm, doc, titleTable, contentTable);
      } else {
        System.out.printf("%s.buildSinglePageContent%n", this);
        // single-page
        buildSinglePageContent(dodm, doc, titleTable, contentTable);
      }
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_DOCUMENT,
          e, "Không thể tạo nội dung văn bản {0} cho {1}", doc.getName(), dataContainer.toString());
    }
  }

  /**
   * @modifies doc
   * 
   * @effects 
   *  generate a Html document containt multiple pages using {@link HtmlPage} as the template and containing the data in <tt>titleTable</tt>
   *  and <tt>contentTable</tt>.
   *  
   *  <br>Add these pages to <tt>doc</tt> and to the object pool of <tt>dodm</tt>
   */
  protected void buildMultiPageContent(DODMBasic dodm, DataDocument<HtmlPage> doc,
      Table<Row<Cell>> titleTable, Table<Row<Cell>> contentTable) throws DataSourceException {

    // keep a record of the title table so that it can be used on all the pages 
    doc.setTitleTable(titleTable);
    
    // generate the Html table 
    writeMultiPageComplexTable(dodm, doc, contentTable, htmlPageTempl);
  }

  /**
   * @modifies doc
   * @effects 
   *  create a multi-page Html data table from the content of <tt>contentTable</tt> and add the pages
   *  to <tt>doc</tt>
   * @pseudocode  Pagination scheme:
   * --------------------------------
   *  pages[1] = titleTable + contentTable.headers + contentTable.dataPage[1]
   *  init contentH = pageH - 2 * pageMargins 
   *  init dataPageH = 0 (data page height)
   *  if repeateHeaders=false
   *    dataPageH = contentH
   *    pages[2...n] = getRowsPages(null, contentTable.rows, dataPageH)
   *  else
   *    dataPageH = contentH - contentTable.headerHeight
   *    pages[2...n] = getRowsPages(contentTable.header, contentTable.rows, dataPageH)
   *  
   *  <<Data page division scheme>>
   *  getRowsPages(header, rows, dataPageH) : Page[] 
   *  -------------------------------------------------------------------
   *  init dataH = 0
   *  init Page[] pages
   *  init p = HtmlDocTempl.clone
   *  for each row r in rows (incl. those of nested tables, if any)
   *    dataH = dataH + r.height
   *      
   *    if dataH > dataPageH
   *      // new page
   *      pages[r.index] = p
   *      p = HtmlDocTempl.clone
   *    
   *    write r into p
   *    
   *  return pages 
   */
  private void writeMultiPageComplexTable(DODMBasic dodm, DataDocument<HtmlPage> doc,
      Table<Row<Cell>> table, HtmlPage htmlPageTempl) throws DataSourceException {
    //final int tableId = table.getId();
    
    RowCounter rcounter = new RowCounter(0);
    
    // first page 
    HtmlPage firstPage = createPageObject(dodm, doc, htmlPageTempl, table, rcounter.val);
    
    // 1. create the header rows for table in the first row and the headers of the nested tables (if any)
    // in the sub-sequent rows. The header rows of the nested tables are only available from the cells 
    // in table.firstRow.
    // 2. Add header rows to doc.firstPage
    writeHeaders(firstPage, table);

    // write data rows (creating new pages if needed)
    writeDataRows(dodm, doc, htmlPageTempl, firstPage, table, rcounter.val, rcounter);
    
    // finalise the last page
    HtmlPage lastPage = doc.getLastPage();
    
    // write extension tables (if any) into the last page
    Iterator<Table<Row<Cell>>> exts = table.getExtensions();
    if (exts != null) {
      Table<Row<Cell>> ext;
      HtmlPage extensionDoc;
      
      while (exts.hasNext()) {
        ext = exts.next();
        extensionDoc = createTabularPageObject(htmlPageTempl, ext, -1);//HtmlPage.createPage(htmlPageTempl);
        writeSinglePageComplexTable(extensionDoc, ext, htmlPageTempl);
        lastPage.addExtension(extensionDoc);
      }
    }

    // IMPORTANT: finalise all pages (must do this after everything else)
    Collection<HtmlPage> pages = doc.getPages();
    for (HtmlPage page : pages) {
      finalisePageContent(doc, page);
    }
  }
  
  /**
   * @overview 
   *  Represents an absolute row counter
   * @author dmle
   */
  private static class RowCounter {
    public RowCounter(int i) {
      this.val = i;
    }

    private int val;
  }
  
  /**
   * @effects 
   *  write the data rows of <tt>table</tt> into <tt>currPage</tt> starting at 
   *  <tt>startRowIndex</tt>, creating new pages if needed
   */
  private void writeDataRows(DODMBasic dodm, DataDocument<HtmlPage> doc, HtmlPage htmlPageTempl, 
      HtmlPage currPage, Table<Row<Cell>> table, 
      final int startRowIndex,    // start row index for this table
      final RowCounter rowCounter // row counter for doc (i.e. total number of rows added so far)
      ) throws DataSourceException {
    int tableId = table.getId();
    int normHeight;
    boolean withCellStyles;
    boolean firstRowOfANewPage; 
    
    HtmlRow myRow;
    int rowIndx = startRowIndex;
    HtmlPage lastPage;
    
    for (Row<Cell> r : table) {
      firstRowOfANewPage = false;
      // check pagination for this row
      if (rowIndx >= currPage.getDataRowsCount()) {
        // a new data row
        if (doc.isFull(currPage)) {  //page is full
          // if exists a page that covers rowIndx use it; else create new
          currPage = doc.getPageAtRow(rowIndx);
          if (currPage == null) {
            currPage = createPageObject(dodm, doc, htmlPageTempl, table, rowIndx);
            firstRowOfANewPage = true;
          }
        } else {
          // add this row to page coverage 
          currPage.addRowCoverage(rowIndx);
        }
        
        // increment row counter
        rowCounter.val++;
        
        myRow = new HtmlRow(tableId);
        currPage.addDataRow(myRow);
      } else {
        // an existing data row -> update it 
        myRow = currPage.getDataRow(rowIndx);
      }
      
      // update row's height to the normalised height of r as follows:
      //  if r is not nested uses row.height; else uses the height of the first nested row of a nested table of r
      normHeight = r.getNormalisedHeight();
      if (myRow.getHeight() < normHeight) myRow.setHeight(normHeight);
      
      // generate cell styles once, for the first row of every page or of every new nested table
      withCellStyles = firstRowOfANewPage || (rowIndx == startRowIndex); 
      
      writeMultiPageRow(dodm, doc, currPage, 
          tableId, r, myRow, rowCounter, rowIndx, withCellStyles);
      
      // continue from last page if different (this is necessary if pagination was used in writeMultiPageRow 
      // with multiple pages)
      lastPage = doc.getLastPage();
      if (currPage != lastPage) {
        // a different page (i.e. pagination was used in writing myRow above) 
        currPage = lastPage;
        // starts next row from the next available position in this page
        rowIndx = currPage.getDataRowsCount();
      } else {
        // same page (no pagination was used)
        rowIndx = rowCounter.val;
      }
    } // end row loop
  }

  /**
   * @modifies
   *  dataRows, headerRows, headerStyles, cellStyles
   *  
   * @effects 
   *  write an Html row from the data cells of <tt>r</tt> into the 
   *  row <tt>rowIndx</tt>(th) of <tt>dataRows</tt>; 
   *  update <tt>cellStyles</tt> accordingly.
   *  
   *  <p>If <tt>r</tt> is nested then recursively write the nested rows
   *  as well.
   */
  private void writeMultiPageRow(
      DODMBasic dodm, 
      DataDocument<HtmlPage> doc,
      HtmlPage currPage, 
      int tableId, 
      Row<Cell> r, HtmlRow myRow, 
      final RowCounter rowCounter,
      final int rowIndx,
      boolean withCellStyles  // whether or not to create the cell styles
      ) throws DataSourceException {
    
    //
    String ci;
    int cindex = 0;

    /*
     * if r is nested (i.e rowspan > 1) 
     *  delay the writing of the non-nested cells in case the row data spans multiple pages
     *  (when this occurs, we need to add these cells to all the pages and update their rowspans)
     * else
     *  write r into currPage 
     */
    if (!r.isNested()) {
      // not nested row: write cells directly  
      for (Cell c : r) {
        ci = tableId+"_"+cindex;
        writeCell(currPage, myRow, c, ci, withCellStyles, false);
        cindex++;
      } // end cell loop
    } else {
      // nested row
      List<Cell> nonNestedCells = new ArrayList<>();
      for (Cell c : r) {
        if (!(c instanceof TableCell)) {
          ci = tableId+"_"+cindex;
          // not nested
          nonNestedCells.add(c);
          writeCell(currPage, myRow, c, ci, withCellStyles, false);
        } else {
          // nested table cell: write each row of this table, starting at the same position as the current rowIndx
          Table<Row<Cell>> nt = (Table<Row<Cell>>) ((TableCell)c).getVal();
          writeDataRows(dodm, doc, htmlPageTempl, currPage, nt, rowIndx, rowCounter);
        }
        
        cindex++;
      } // end cell loop
      
//      Collection<HtmlDoc> pages = doc.getPageCoverage(tableId, rowIndx);
      // TODO: if there were non-nested cells in r and and pagination was needed to write r then 
      // update the pages created by pagination to include the non-nested cells with suitable rowspans
//      if (!nonNestedCells.isEmpty() && pages.size() > 1) {
//        // update the pages to include the non-nested cells with suitable rowspans
//        for (HtmlDoc page: pages) {
//          
//        }
//      }
    } // end if
  }
  
  private void writeCell(HtmlPage currPage, HtmlRow myRow, Cell c, String columnNo, 
      final boolean withCellStyles, boolean writeDelay) {

    StringBuffer St;

    StringBuffer Ct = currPage.getTemplate("DataCell", true);
    if (writeDelay) {
      // write delay: only add the template to the cell, write the cell content later
    } else {
      // not write delay
      setHtmlVars(Ct, 
          "rowspan", c.getProperty().getIntegerValue(RowSpan, 1)+"",
          "colspan", c.getProperty().getIntegerValue(ColSpan, 1)+"",
          "rowheight", c.getProperty().getIntegerValue(Height, 0)+"");
      
      String colVal;
      if (c instanceof ImageCell) {
        colVal = writeImageCell((ImageCell) c);
      } else {
        //TODO: support other types of cell
        colVal = ((TextCell)c).getVal();
      }
      
      setHtmlVars(Ct, "Column.value", colVal);
      setHtmlVars(Ct, "Column.no",columnNo);
      
      if (withCellStyles) {  // create cell style
        St = currPage.getTemplate("CellStyle",true);
        setHtmlVars(St,
            "Column.width", c.getProperty().get(Width,0)+"");
        setHtmlVarsFromCell(St, c);
        setHtmlVars(St, "Column.no", columnNo);
        currPage.addCellStyle(St);
      }
    }
    
    HtmlCell htmlCell = new HtmlCell(c, columnNo, withCellStyles, Ct);
    
    myRow.addCell(htmlCell);
  }

  /**
   * @param rowIndx 
   * @effects 
   *  create in <tt>dodm</tt> a <tt>page</tt> from <tt>htmlPageTemplate</tt> and add it to <tt>doc</tt> (i.e. <tt>doc.pages = doc.pages + {page}</tt>;
   *  return page
   */
  private HtmlPage createPageObject(DODMBasic dodm, DataDocument<HtmlPage> doc, HtmlPage htmlPageTempl, 
      Table<Row<Cell>> table, int rowIndx) throws DataSourceException {
    HtmlPage page = createTabularPageObject(htmlPageTempl, table, rowIndx);
    
    // copy headers and header styles from the first page (if it has been created)
    // TODO: add option here as to whether or not to use headers
    HtmlPage firstPage = doc.getFirstPage();
    if (firstPage != null) {
      page.setHeaderRows(firstPage.getHeaderRows());
      page.setHeaderStyles(firstPage.getHeaderStyles());
    }
    
    // add page to DODM
    DOMBasic dom = dodm.getDom();
    dom.addObject(page);
    
    // add page to doc
    doc.addPage(page);
    
    return page;
  }

  /**
   * @effects 
   *  finalise <tt>page</tt>'s HTML content  
   */
  private void finalisePageContent(DataDocument<HtmlPage> doc, HtmlPage page) {
    Table<Row<Cell>> titleTable = doc.getTitleTable();
    StringBuffer titleHtml = writeTitleTable(page, titleTable);
    
    StringBuffer tableHtml = writeDataTableContent(page);

    page.setTitleTable(titleHtml);
    page.setDataTable(tableHtml);
    
    page.finalise();
  }
  
  @Override
  protected void save(DataDocument doc) {
    // save all pages
    Iterator<HtmlPage> it = doc.getPages().iterator(); 
    HtmlPage page;
    while (it.hasNext()) {
      page = it.next();
      savePage(page);
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
  @Override
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
    
    // set page settings to that of the table.
    // Note: these settings apply only to the outer-most data table
    page.setDataTableAlignX(contentTable.getAlignXAsString());
    page.setDataTableWidth(contentTable.getWidth());
    
    //write title table
    StringBuffer titleHtml = writeTitleTable(page, titleTable);
    page.setTitleTable(titleHtml);
      
    // generate the Html table 
    TableType tableType = contentTable.getType();
    StringBuffer tableHtml;
    
    if (tableType == TableType.SideTable) {
      tableHtml = writeSinglePageSideTable(page, contentTable);
    } else {
      tableHtml = writeSinglePageComplexTable(page, contentTable, htmlPageTempl);
          // writeSinglePageNormalTable(doc, contentTable, headerStyles, cellStyles);
    }
    
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
   * @modifies <tt>doc</tt>
   * @effects
   *  produce an Html table in <tt>doc</tt> for <tt>table</tt> that may contain nested tables.
   *  
   *  <br>These nested tables are called <b>extensions</b>.
   */
  protected StringBuffer writeSinglePageComplexTable(HtmlPage doc, Table<Row<Cell>> table, HtmlPage htmlPageTempl) {
    
    final int tableId = table.getId();
    
    // 1. create the header rows for table in the first row and the headers of the nested tables (if any)
    // in the sub-sequent rows. The header rows of the nested tables are only available from the cells 
    // in table.firstRow.
    // 2. Add header rows to doc
    writeHeaders(doc, table);
    
    // write data rows and update the headers
    int rowIndx = 0, rowSpan;
    boolean withCellStyles;
    for (Row<Cell> r : table) {
      withCellStyles = (rowIndx == 0); // generate cell styles once, for the first row
      writeRow(doc, table, tableId, r, rowIndx, withCellStyles); 
      rowSpan = r.getProperty().getIntegerValue(RowSpan, 1);
      rowIndx = rowIndx+rowSpan;
    }
    
    
    // use different templates for whether or not table has a border
    if (table.getBorder()) {
      doc.setDataTableTemplate(doc.getTemplate("DataTable", true));
    } else {
      doc.setDataTableTemplate(doc.getTemplate("DataTableNoBorder", true));
    }
    
    // write extension tables (if any) at the end
    Iterator<Table<Row<Cell>>> exts = table.getExtensions();
    if (exts != null) {
      Table<Row<Cell>> ext;
      HtmlPage extensionDoc;
      
      while (exts.hasNext()) {
        ext = exts.next();
        extensionDoc = createTabularPageObject(htmlPageTempl, ext, -1); //HtmlPage.createPage(htmlPageTempl);
        writeSinglePageComplexTable(extensionDoc, ext, htmlPageTempl);
        doc.addExtension(extensionDoc);
      }
    } 
    
    StringBuffer Tt = writeDataTableContent(doc);

    return Tt;
  }
  
  /**
   * @requires 
   *  dataRows != null /\ headerRows != null
   *  
   * @effects 
   *  generate an Html table for the content of doc.dataTable using {@link #tableTemplate} as the template 
   *  and using {@link #tableHeaders} and {@link #tableRows} to fill the table body.
   *  
   *  <br>sets doc.bodyContent = Html table that is generated
   */
  public StringBuffer writeDataTableContent(HtmlPage page) {
    final String tableWidth = page.getDataTableWidth(); //tableModel.getWidth();
    String tableAlignX = page.getDataTableAlignX();
    // get the template out and replaces its vars by actual Html values 
    StringBuffer tableContent = page.getTableTemplate();
    
    setHtmlVars(tableContent, 
        "Table.align", tableAlignX,
        "Table.width",tableWidth+""); 
    
    // debug
//    System.out.printf("writeComplexTable(%s): table Html (with width) = %n %s%n", 
//        table.getRefId(), Tt.toString());
    
    // write data table headers into doc
    List<HtmlHeaderRow> headerRows = page.getHeaderRows();
    List<HtmlRow> dataRows = page.getDataRows();
    List<HtmlPage> exts = page.getExtensions();
    
    StringBuffer htmlHeaderRows = new StringBuffer();
    StringBuffer Ht;
    //StringBuffer rh;
    if (headerRows != null) {
      for (HtmlHeaderRow hr : headerRows) {
        //rh = new StringBuffer();
        //for (HtmlHeader hhtml : hr) append(rh, hhtml.getBuffer());
        Ht = page.getTemplate("HeaderRow", true);
        setHtmlVars(Ht, "Cells", hr.toString()); //rh.toString());
        append(htmlHeaderRows, Ht); 
      }
    }
    
    setHtmlVars(tableContent, "HeaderRows", htmlHeaderRows.toString());
    
    // write data rows into doc
    StringBuffer htmlDataRows = new StringBuffer();
    StringBuffer Rt;
    for (HtmlRow r : dataRows) {
      Rt = page.getTemplate("DataRow", true);
      setHtmlVars(Rt, "Cells", r.toString());
      append(htmlDataRows, Rt);
    }
     
    setHtmlVars(tableContent, "DataRows", htmlDataRows.toString());
    
    // write extension tables (if any) at the end
    StringBuffer extStyles;
    if (exts != null) {
      StringBuffer extensions = new StringBuffer();
      for (HtmlPage ext: exts) {
        // use only the body of an extension, which contains the Html tables
        append(extensions, ext.getBodyContent());
        
        // add doc styles
        extStyles = ext.getHeaderStyles();
        if (extStyles != null) page.addHeaderStyle(extStyles);
        extStyles = ext.getCellStyles();
        if (extStyles != null) page.addCellStyle(extStyles);
      }
      setHtmlVars(tableContent, "Extensions", extensions.toString());
    } else {
      setHtmlVars(tableContent, "Extensions", "");
    }
    
    page.setBodyContent(tableContent);
    
    return tableContent;
  }
  
  /**
   * @modifies
   *  dataRows, headerRows, headerStyles, cellStyles
   *  
   * @effects 
   *  write an Html row from the data cells of <tt>r</tt> into the 
   *  row <tt>rowIndx</tt>(th) of <tt>dataRows</tt>; 
   *  update <tt>cellStyles</tt> accordingly.
   *  
   *  <p>If <tt>r</tt> is nested then recursively write the nested rows
   *  as well.
   */
  private void writeRow(HtmlPage doc, 
      Table<Row<Cell>> table, 
      int tableId, 
      Row<Cell> r, final int rowIndx,
      boolean withCellStyles  // whether or not to create the cell styles
      ) {
    //
    StringBuffer Ct, St;
    HtmlRow myRow;
    if (rowIndx >= doc.getDataRowsCount()){ 
      myRow = new HtmlRow(tableId);
      doc.addDataRow(myRow);
    } else {
      myRow = doc.getDataRow(rowIndx); 
    }

    int normHeight = r.getNormalisedHeight();
    if (myRow.getHeight() < normHeight) myRow.setHeight(normHeight);

    String ci;
    int cindex = 0, nrowSpan;
    String colVal; 

    for (Cell c : r) {
      
      Ct = doc.getTemplate("DataCell", true);
      if (!(c instanceof TableCell)) {
        // normal cell
        ci = tableId+"_"+cindex; 
        /*
        setHtmlVars(Ct, 
            "rowspan", c.getProperty().getIntegerValue(RowSpan, 1)+"",
            "colspan", c.getProperty().getIntegerValue(ColSpan, 1)+"",
            "rowheight", c.getProperty().getIntegerValue(Height, 0)+"");
        
        if (c instanceof ImageCell) {
          colVal = writeImageCell((ImageCell) c);
        } else {
          //TODO: support other types of cell
          colVal = ((TextCell)c).getVal();
        }
        
        setHtmlVars(Ct, "Column.value", colVal);
        setHtmlVars(Ct, "Column.no",ci);
        
        if (withCellStyles) {  // create cell style
          St = doc.getTemplate("CellStyle",true);
          setHtmlVars(St,
              "Column.width", c.getProperty().get(Width,0)+"");
          setHtmlVarsFromCell(St, c);
          setHtmlVars(St, "Column.no", ci);
          //append(cellStyles, St);
          doc.addCellStyle(St);
        }
        
        //append(myRow, Ct);
        myRow.addCell(Ct);
        */
        writeCell(doc, myRow, c, ci, withCellStyles, false);
        
      } else {
        // nested table cell: write each row of this table, starting at the same position as the current rowIndx
        int rnidx = rowIndx;
        Table<Row<Cell>> nt = (Table<Row<Cell>>) ((TableCell)c).getVal();
        for (Row<Cell> rn : nt) {
          boolean _withCellStyles = (rnidx == rowIndx); // create cell styles once for the first row
          writeRow(doc, nt, nt.getId(), rn, rnidx, 
              //(parentHeaderVisible) ? hrowIndex+1 : hrowIndex,  // hrowIndex+1,
              //dataRows, headerRows, //headerStyles, cellStyles, 
              _withCellStyles);
          nrowSpan = rn.getProperty().getIntegerValue(RowSpan, 1);
          rnidx = rnidx + nrowSpan;//rnidx + 1;    
        }
      }
      
      cindex++;
    }
  }
  
  /**
   * @modifies
   *  headerStyles, headerRows
   * @effects 
   *  if <tt>table.header</tt> is not null
   *    write the Html content of <tt>table.header</tt> as the <tt>hrowIndex</tt>(th) 
   *    element of <tt>headerRows</tt>, 
   *    update headerStyles accordingly 
   *    
   *  if there are headers of nested tables of <tt>table</tt>
   *    write them in the sub-sequent header rows.
   *    
   *  <p>Note: the headers of a nested table are defined in the first row of that table  
   */
  private void writeHeaders(HtmlPage doc, Table<Row<Cell>> table) {
    // write headers of table
    int hrowIndex = 0;
    writeHeaders(doc, table, null, hrowIndex);
    
    // if there are nested tables, write their headers too
    writeSubHeaders(doc, table, hrowIndex);
  }
  
  /**
   * @effects 
   *  if there are sub-headers of <tt>table</tt> (i.e. headers of the nested tables)
   *    write them as sub-headers in <tt>doc</tt>
   *  else
   *    do nothing
   */
  private void writeSubHeaders(HtmlPage doc, Table<Row<Cell>> table, int hrowIndex) {
    if (table.isEmpty()) {
      // no content, no sub-headers: return immediately
      return;
    }
    
    HeaderCell parentHeader; 
    final Row<HeaderCell> headerRow = table.getHeaderRow();
    boolean parentHeaderVisible;

    // access first row
    Row<Cell> r = table.get(0);
    int cindex = 0;
    Table<Row<Cell>> nt;
    for (Cell c : r) {
      if (c instanceof TableCell) {
        nt = (Table<Row<Cell>>) c.getVal();
        // nested table: write its header
        if (headerRow != null) {
          parentHeader = headerRow.get(cindex); // the header cell of the parent table (to overwrite if needed)
          parentHeaderVisible = parentHeader.getProperty().getBooleanValue(Visible, true);
        } else {
          parentHeader = null;
          parentHeaderVisible = true;
        }
        
        writeHeaders(doc, nt, parentHeader, hrowIndex+1);
        
        // RECURSIVE: now write sub-headers of nt (if any)
        writeSubHeaders(doc, nt, (parentHeaderVisible) ? hrowIndex+1 : hrowIndex);
      }
      cindex++;
    }    
  }

  /**
   * @modifies
   *  headerStyles, headerRows
   * @effects 
   *  if <tt>table.header</tt> is not null
   *    write the Html content of <tt>table.header</tt> as the <tt>hrowIndex</tt>(th) 
   *    element of <tt>headerRows</tt>, 
   *    update headerStyles accordingly 
   *    
   *  <p>Note: the headers of a nested table are defined in the first row of that table  
   */
  private void writeHeaders(HtmlPage doc, Table<Row<Cell>> table, HeaderCell parentHeader, int hrowIndex) {
    Row<HeaderCell> hr = table.getHeaderRow();
    if (hr == null)
      return;
    
    HtmlHeaderRow myHeader;
    // if parentHeader.visible = false then create an empty list to overwrite
    // parentHeader; else create a list to update into headerRows
    final boolean parentHeaderVisible = (parentHeader != null) ? 
          parentHeader.getProperty().getBooleanValue(Visible, true) : true;
    
    Integer parentHToOverwrite = null; HtmlHeaderRow parentRow = null;
    HtmlHeader htmlHeader;
    if (!parentHeaderVisible) {
      myHeader = new HtmlHeaderRow();
      
      // get the parent header html (from the previous header row) out so that we can overwrite
      parentRow = doc.getHeaderRow(hrowIndex-1); //headerRows.get(hrowIndex-1);
      for (int i = 0; i < parentRow.size(); i++) {
        htmlHeader = parentRow.get(i);
        if (htmlHeader.getHc() == parentHeader) {
          // found
          parentHToOverwrite = i; break;
        }
      }
    } else {
      if (hrowIndex >= doc.getHeaderRowsCount()) {// > headerRows.size()-1) {
        myHeader = new HtmlHeaderRow();
        //headerRows.add(myHeader);
        doc.addHeader(myHeader);
      } else {
        myHeader = doc.getHeaderRow(hrowIndex); //headerRows.get(hrowIndex);
      }
    }
    
    // update header row height
    if (myHeader.getHeight() < hr.getHeight()) myHeader.setHeight(hr.getHeight());
    
    final int tableId = table.getId();
    
    StringBuffer Ct, St;
    String ci;
    int index = 0, rowSpan;
    String headerStr;
    final int numHeaders = hr.size();
    
    for (HeaderCell hc : hr) {
      Ct = doc.getTemplate("HeaderCell", true);
      St = doc.getTemplate("HeaderStyle", true);
    
      ci = tableId+"_"+index;
      
      rowSpan = hc.getProperty().getIntegerValue(RowSpan, 1);
      
      if (parentHToOverwrite != null && doc.getHeaderRowsCount() > 1) { //headerRows.size() > 1) {
        // to overwrite parent header: 
        rowSpan++;  // increase rowspan by one (TODO: needs to determine exactly how many rows this should span)

        // if there are only one header then use the parent label
        // TODO: needs an option here as to whether to use the child or parent label
        headerStr = (numHeaders == 1) ? parentHeader.getVal() : hc.getVal();
      } else {
        // use child header
        headerStr = hc.getVal();
      }
      
      setHtmlVars(Ct, 
          "rowspan", rowSpan+"",
          "colspan", hc.getProperty().getIntegerValue(ColSpan, 1)+"",
          "Header.no", ci,
          "Header.value", headerStr//hc.getVal()
          );
      
      setHtmlVarsFromCell(St, hc);
      setHtmlVars(St,
          "Header.no", ci,
          "Header.width", 
          hc.getProperty().get(Width,0)+"");
    
      //append(headerStyles, St);
      doc.addHeaderStyle(St);
      
      htmlHeader = new HtmlHeader(Ct, hc);
      //append(myHeader, Ct);
      myHeader.add(htmlHeader);
          
      index++;
    }
    
    // now if parentHeader was to overwrite then overwrite it
    if (parentHToOverwrite != null) {
      // replace parentHhtml by myHeader
      if (parentHToOverwrite == parentRow.size()-1) { // last position
        parentRow.remove(parentHToOverwrite.intValue());
        parentRow.append(myHeader);
      } else {  // other positions
        parentRow.remove(parentHToOverwrite.intValue());
        parentRow.append(parentHToOverwrite,myHeader);  
      }
    }
  }
  
  protected StringBuffer writeSinglePageSideTable(HtmlPage doc, Table<Row<Cell>> table 
      //StringBuffer headerStyles, StringBuffer cellStyles
      ) {
    /*
     * A side table is a table that displays its headers on the first column
     * and the data rows on the sub-sequent columns. It can be thought of 
     * being transformed from a normal table by a 90-deg rotation.
     * 
     * For this implementation, only the top-level table can be the side-table.
     * All the nested tables (if any) of this table are treated as normal tables.
     */

    // TODO: implement this, for now use the basic side-table design
    StringBuffer tableHtml = super.writeSinglePageTable(doc, table);//, headerStyles, cellStyles);
    
    //doc.setVar("DataTable", tableHtml.toString());
    
    return tableHtml;
  }
  
  protected StringBuffer writeSinglePageNormalTable(HtmlPage doc, Table<Row<Cell>> table,
      StringBuffer headerStyles, StringBuffer cellStyles) {
    /*
     * A normal table is a table that contains all headers at the top row(s)
     * and all the data rows below them. 
     * The headers of a nested table are nested inside the header cell of its outer table.
     * Similarly, the data rows of a nested table are nested inside the data row of
     * its outer table.
     * 
     * let headerStyles = {}
     * let cellStyles = {}
     * let headerRows = {}
     * 
     * // write headers
     * int hrindx = 0
     * writeHeaders(Table table, hrindx, headerStyles, headerRows)  
     *  
     * // write data rows and update the headers
     * let dataRows = []
     * let rindx = 0
     * for each row r in table.rows
     *   let Rv = writeRow(r, rindx, hrindx, dataRows, headerRows, headerStyles, cellStyles)
     *   rindx = rindx+1
     * 
     * // write styles into doc
     * set doc{HeaderStyle} = headerStyles
     * set doc{CellStyle} = cellStyles
     * 
     * let Tt = data table template
     * set Tt{Table.width} = table.width 
     * 
     * // write data table headers into doc
     * let htmlHeaderRows = StringBuffer[]
     * for each rh in headerRows
     *  let Ht = header row template
     *  set Ht{Cells} = rh
     *  add Ht to htmlHeaderRows 
     * 
     * set Tt{HeaderRows} = htmlHeaderRows
     * 
     * // write data rows into doc
     * let htmlDataRows = StringBuffer[]
     * for each r in dataRows
     *  let Rt = data row template
     *  set Rt{Cells} = r
     *  add Rt to htmlDataRows
     *  
     * set Tt{DataRows} = htmlDataRows
     *  
     * writeHeaders(Table table, int hrindx, {} headerStyles, {} headerRows):
     *   let hr = table.header
     *   let tableId = table.id
     *   
     *   for each header cell hc of hr
     *     let Ct = header cell template
     *     let St = header style template
     *  
     *     let ci = tableId+"_"+hc.index
     *     //TODO: calculate rowspan and colspan for each cell while finalising Table 
     *     set Ct({rowspan}, {colspan} = hc.(*)
     *     set Ct{Header.value} = hc.value
     *     set Ct{Header.no} = ci
     *     set St({Header.width,{Text.align},{Text.size},{Text.family},{Text.color}) = 
     *         hc.(width,text-align,font-size,font-family,color)
     *  
     *     add St to headerStyles
     *     add Ct to headerRows[hrindx]
     *   end for 
     * 
     * writeRow(Row r, int rindx, int hrindx, [] dataRows, Set headerRows, Set headerStyles, Set cellStyles): 
     *  for each cell c in r
     *    let Ct = data cell template
     *    if c is not TableCell
     *      let ci = tableId+"_"+c.index
     *      set Ct({rowspan}, {colspan}) = c.(*)
     *      set Ct{Column.value} = c.value
     *      set Ct{Column.no} = ci
     *      if rindx = 0  // first row: create cell style
     *        let St = data cell style
     *        set St({Column.width},{Text.align},{Text.size},{Text.family},{Text.color}) = 
     *          c.(width,text-align,font-size,font-family,color)
     *        set St{Column.no} = ci
     *        add St to cellStyles
     *      
     *      add Ct to dataRows[rindx]
     *    else
     *      // nested
     *      // TODO: table.finalise():
     *      //    (1) to read the parent's object buffer while constructing the table model
     *      //    (2) fill c with empty rows up to max number of rows among all the nested TableCells on the same row as c
     *      let rnidx = 0
     *      for each row rn in c.rows
     *        if rnidx = 0
     *          // write next header row
     *          writeHeader(c, hrindx+1, headerStyles, headerRows)
     *          
     *        writeRow(rn, rnidx,hdrindx+1, dataRows, headerRows, headerStyles)
     *        rnidx = rnidx + 1
     */
    List<StringBuffer> headerRows = new ArrayList<>(),
        dataRows = new ArrayList<>();
    int hrowIndex = 0;
    final String tableWidth = table.getWidth();
    final int tableId = table.getId();
    
    // write headers
    writeStandardHeader(doc, table, hrowIndex, headerStyles, headerRows);
    
    // write data rows and update the headers
    int rowIndx = 0, rowSpan;
    boolean withCellStyles;
    for (Row<Cell> r : table) {
      withCellStyles = (rowIndx == 0); // generate cell styles once, for the first row
      writeStandardRow(doc, tableId, r, rowIndx, hrowIndex, dataRows, headerRows, headerStyles, cellStyles, withCellStyles);
      rowSpan = r.getProperty().getIntegerValue(RowSpan, 1);
      rowIndx = rowIndx+rowSpan;
    }
    
    StringBuffer Tt = doc.getTemplate("DataTable", true);
    setHtmlVars(Tt, "Table.width",tableWidth+""); 
    
    // write data table headers into doc
    StringBuffer htmlHeaderRows = new StringBuffer();
    StringBuffer Ht;
    for (StringBuffer rh : headerRows) {
     Ht = doc.getTemplate("HeaderRow", true);
     setHtmlVars(Ht, "Cells", rh.toString());
     append(htmlHeaderRows, Ht); 
    }
    
    setHtmlVars(Tt, "HeaderRows", htmlHeaderRows.toString());
    
    // write data rows into doc
    StringBuffer htmlDataRows = new StringBuffer();
    StringBuffer Rt;
    for (StringBuffer r : dataRows) {
     Rt = doc.getTemplate("DataRow", true);
     setHtmlVars(Rt, "Cells", r.toString());
     append(htmlDataRows, Rt);
    }
     
    setHtmlVars(Tt, "DataRows", htmlDataRows.toString());
    
    return Tt;
  }

  /**
   * @modifies
   *  dataRows, headerRows, headerStyles, cellStyles
   *  
   * @effects 
   *  write an Html row from the data cells of <tt>r</tt> into the 
   *  row <tt>rowIndx</tt>(th) of <tt>dataRows</tt>; 
   *  update <tt>cellStyles</tt> accordingly.
   *  
   *  <p>If <tt>r</tt> is nested then recursively write the nested rows
   *  as well.
   */
  @Deprecated
  private void writeRow(HtmlPage doc, 
      Table<Row<Cell>> table, 
      int tableId, 
      Row<Cell> r, final int rowIndx, int hrowIndex,
      List<StringBuffer> dataRows, List<List<HtmlHeader>> headerRows,
      //StringBuffer headerStyles, StringBuffer cellStyles,
      boolean withCellStyles  // whether or not to create the cell styles
      ) {
    //
    StringBuffer Ct, St;
    StringBuffer myRow;
    if (rowIndx > dataRows.size()-1) {
      myRow = new StringBuffer();
      dataRows.add(myRow);
    } else {
      myRow = dataRows.get(rowIndx);
    }
    
    String ci;
    int cindex = 0, nrowSpan;
    HeaderCell headerCell; 
    final Row<HeaderCell> headerRow = table.getHeaderRow();
    boolean parentHeaderVisible;
    String colVal; 

    for (Cell c : r) {
      Ct = doc.getTemplate("DataCell", true);
      if (!(c instanceof TableCell)) {
        // normal cell
        ci = tableId+"_"+cindex; 
        setHtmlVars(Ct, 
            "rowspan", c.getProperty().getIntegerValue(RowSpan, 1)+"",
            "colspan", c.getProperty().getIntegerValue(ColSpan, 1)+"",
            "rowheight", c.getProperty().getIntegerValue(Height, 0)+"");
        
        if (c instanceof ImageCell) {
          /*v2.7.3: support ImageCell
          colVal = "";
          */
          colVal = writeImageCell((ImageCell) c);
        } else {
          //TODO: support other types of cell
          colVal = ((TextCell)c).getVal();
        }
        
        setHtmlVars(Ct, "Column.value", colVal);
        setHtmlVars(Ct, "Column.no",ci);
        
        if (withCellStyles) {  // create cell style
          St = doc.getTemplate("CellStyle",true);
          setHtmlVars(St,
              "Column.width", c.getProperty().get(Width,0)+"");
          setHtmlVarsFromCell(St, c);
          setHtmlVars(St, "Column.no", ci);
          //append(cellStyles, St);
          doc.addCellStyle(St);
        }
        
        append(myRow, Ct);
      } else {
        // nested table cell: write each row of this table, starting at the same position as the current rowIndx
        if (headerRow != null) {
          headerCell = headerRow.get(cindex); // the header cell of the parent table (to overwrite if needed)
          parentHeaderVisible = headerCell.getProperty().getBooleanValue(Visible, true);
        } else {
          headerCell = null;
          parentHeaderVisible = true;
        }
        
        int rnidx = rowIndx;
        Table<Row<Cell>> nt = (Table<Row<Cell>>) ((TableCell)c).getVal();
        for (Row<Cell> rn : nt) {
          if (rnidx == rowIndx) {
            // write next header row
            writeHeader(doc, nt, hrowIndex+1, headerCell, //headerStyles, 
                headerRows);
          }
          boolean _withCellStyles = (rnidx == rowIndx); // create cell styles once for the first row
          writeRow(doc, nt, nt.getId(), rn, rnidx, 
              (parentHeaderVisible) ? hrowIndex+1 : hrowIndex,  // hrowIndex+1,
              dataRows, headerRows, //headerStyles, cellStyles, 
              _withCellStyles);
          nrowSpan = rn.getProperty().getIntegerValue(RowSpan, 1);
          rnidx = rnidx + nrowSpan;//rnidx + 1;    
        }
      }
      
      cindex++;
    }
  }
  
  /**
   * @modifies
   *  headerStyles, headerRows
   * @effects 
   *  if <tt>table.header</tt> is not null
   *    write the Html content of <tt>table.header</tt> as the <tt>hrowIndex</tt>(th) 
   *    element of <tt>headerRows</tt>, 
   *    update headerStyles accordingly 
   */
  @Deprecated
  private void writeHeader(HtmlPage doc, Table<Row<Cell>> table, 
      final int hrowIndex,
      HeaderCell parentHeader, 
      //StringBuffer headerStyles, 
      List<List<HtmlHeader>> headerRows) {
    Row<HeaderCell> hr = table.getHeaderRow();
    if (hr == null)
      return;
    
    List<HtmlHeader> myHeader;
    // if parentHeader.visible = false then create an empty list to overwrite
    // parentHeader; else create a list to update into headerRows
    final boolean parentHeaderVisible = (parentHeader != null) ? 
          parentHeader.getProperty().getBooleanValue(Visible, true) : true;
    
    Integer parentHToOverwrite = null; List<HtmlHeader> parentRow = null;
    HtmlHeader hhtml;
    if (!parentHeaderVisible) {
      myHeader = new ArrayList<>();
      
      // get the parent header html (from the previous header row) out so that we can overwrite
      parentRow = headerRows.get(hrowIndex-1);
      for (int i = 0; i < parentRow.size(); i++) {
        hhtml = parentRow.get(i);
        if (hhtml.getHc() == parentHeader) {
          // found
          parentHToOverwrite = i; break;
        }
      }
    } else {
      if (hrowIndex > headerRows.size()-1) {
        myHeader = new ArrayList<>();
        headerRows.add(myHeader);
      } else {
        myHeader = headerRows.get(hrowIndex);
      }
    }
    
    final int tableId = table.getId();
    
    StringBuffer Ct, St;
    String ci;
    int index = 0, rowSpan;
    String headerStr;
    final int numHeaders = hr.size();
    
    for (HeaderCell hc : hr) {
      Ct = doc.getTemplate("HeaderCell", true);
      St = doc.getTemplate("HeaderStyle", true);
    
      ci = tableId+"_"+index;
      
      rowSpan = hc.getProperty().getIntegerValue(RowSpan, 1);
      
      if (parentHToOverwrite != null && headerRows.size() > 1) {
        // to overwrite parent header: 
        rowSpan++;  // increase rowspan by one (TODO: needs to determine exactly how many rows this should span)

        // if there are only one header then use the parent label
        // TODO: needs an option here as to whether to use the child or parent label
        headerStr = (numHeaders == 1) ? parentHeader.getVal() : hc.getVal();
      } else {
        // use child header
        headerStr = hc.getVal();
      }
      
      setHtmlVars(Ct, 
          "rowspan", rowSpan+"",
          "colspan", hc.getProperty().getIntegerValue(ColSpan, 1)+"",
          "Header.no", ci,
          "Header.value", headerStr//hc.getVal()
          );
      
      setHtmlVarsFromCell(St, hc);
      setHtmlVars(St,
          "Header.no", ci,
          "Header.width", 
          hc.getProperty().get(Width,0)+"");
    
      //append(headerStyles, St);
      doc.addHeaderStyle(St);
      
      hhtml = new HtmlHeader(Ct, hc);
      //append(myHeader, Ct);
      myHeader.add(hhtml);
          
      index++;
    }
    
    // now if parentHeader was to overwrite then overwrite it
    if (parentHToOverwrite != null) {
      // replace parentHhtml by myHeader
      if (parentHToOverwrite == parentRow.size()-1) { // last position
        parentRow.remove(parentHToOverwrite.intValue());
        parentRow.addAll(myHeader);
      } else {  // other positions
        parentRow.remove(parentHToOverwrite.intValue());
        parentRow.addAll(parentHToOverwrite,myHeader);  
      }
    }
  }
  
  /**
   * @modifies
   *  dataRows, headerRows, headerStyles, cellStyles
   *  
   * @effects 
   *  write an Html row from the data cells of <tt>r</tt> into the 
   *  row <tt>rowIndx</tt>(th) of <tt>dataRows</tt>; 
   *  update <tt>cellStyles</tt> accordingly.
   *  
   *  <p>If <tt>r</tt> is nested then recursively write the nested rows
   *  as well.
   */
  private void writeStandardRow(HtmlPage doc, int tableId, Row<Cell> r, final int rowIndx, int hrowIndex,
      List<StringBuffer> dataRows, List<StringBuffer> headerRows,
      StringBuffer headerStyles, StringBuffer cellStyles,
      boolean withCellStyles  // whether or not to create the cell styles
      ) {
    //
    StringBuffer Ct, St;
    StringBuffer myRow;
    if (rowIndx > dataRows.size()-1) {
      myRow = new StringBuffer();
      dataRows.add(myRow);
    } else {
      myRow = dataRows.get(rowIndx);
    }
    
    String ci;
    int cindex = 0, nrowSpan;
    String colVal;
    
    for (Cell c : r) {
      Ct = doc.getTemplate("DataCell", true);
      if (!(c instanceof TableCell)) {
        // normal cell
        ci = tableId+"_"+cindex; 
        setHtmlVars(Ct, 
            "rowspan", c.getProperty().getIntegerValue(RowSpan, 1)+"",
            "colspan", c.getProperty().getIntegerValue(ColSpan, 1)+"",
            "rowheight", c.getProperty().getIntegerValue(Height, 0)+"");
        
        /*v2.7.3: support ImageCell
        setHtmlVars(Ct, "Column.value", ((TextCell)c).getVal());
        */
        if (c instanceof ImageCell) {
          colVal = writeImageCell((ImageCell) c);
        } else {
          //TODO: support other types of cell
          colVal = ((TextCell)c).getVal();
        }
        
        setHtmlVars(Ct, "Column.value", colVal);
        setHtmlVars(Ct, "Column.no",ci);

        if (withCellStyles) {  // create cell style
          St = doc.getTemplate("CellStyle",true);
          setHtmlVars(St,
              "Column.width", c.getProperty().get(Width,0)+"");
          setHtmlVarsFromCell(St, c);
          setHtmlVars(St, "Column.no", ci);
          append(cellStyles, St);
        }
        
        append(myRow, Ct);
      } else {
        // nested table cell: write each row of this table, starting at the same position as the current rowIndx
        int rnidx = rowIndx;
        Table<Row<Cell>> nt = (Table<Row<Cell>>) ((TableCell)c).getVal();
        for (Row<Cell> rn : nt) {
          if (rnidx == rowIndx) {
            // write next header row
            writeStandardHeader(doc, nt, hrowIndex+1, headerStyles, headerRows);
          }
          boolean _withCellStyles = (rnidx == rowIndx); // create cell styles once for the first row
          writeStandardRow(doc, nt.getId(), rn, rnidx, hrowIndex+1, dataRows, headerRows, headerStyles, cellStyles, _withCellStyles);
          nrowSpan = rn.getProperty().getIntegerValue(RowSpan, 1);
          rnidx = rnidx + nrowSpan;//rnidx + 1;    
        }
      }
      
      cindex++;
    }
  }
  
  /**
   * @modifies
   *  headerStyles, headerRows
   * @effects 
   *  if <tt>table.header</tt> is not null
   *    write the Html content of <tt>table.header</tt> as the <tt>hrowIndex</tt>(th) 
   *    element of <tt>headerRows</tt>, 
   *    update headerStyles accordingly 
   */
  private void writeStandardHeader(HtmlPage doc, Table<Row<Cell>> table, 
      int hrowIndex,
      StringBuffer headerStyles, List<StringBuffer> headerRows) {
    Row<HeaderCell> hr = table.getHeaderRow();
    if (hr == null)
      return;
    
    StringBuffer myHeader;
    if (hrowIndex > headerRows.size()-1) {
      myHeader = new StringBuffer();
      headerRows.add(myHeader);
    } else {
      myHeader = headerRows.get(hrowIndex);
    }
    
    final int tableId = table.getId();
    
    StringBuffer Ct, St;
    String ci;
    int index = 0;
    for (HeaderCell hc : hr) {
      Ct = doc.getTemplate("HeaderCell", true);
      St = doc.getTemplate("HeaderStyle", true);
    
      ci = tableId+"_"+index;
      setHtmlVars(Ct, 
          "rowspan", hc.getProperty().getIntegerValue(RowSpan, 1)+"",
          "colspan", hc.getProperty().getIntegerValue(ColSpan, 1)+"",
          "Header.no", ci,
          "Header.value", hc.getVal()
          );
      
      setHtmlVarsFromCell(St, hc);
      setHtmlVars(St,
          "Header.no", ci,
          "Header.width", 
          hc.getProperty().get(Width,0)+"");
    
      append(headerStyles, St);
      append(myHeader, Ct);
          
      index++;
    }
  }
}
