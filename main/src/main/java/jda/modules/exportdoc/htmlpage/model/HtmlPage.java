package jda.modules.exportdoc.htmlpage.model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jda.modules.common.Toolkit;
import jda.modules.common.exceptions.NotFoundException;
import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.exportdoc.page.model.Page;

@DClass(serialisable=false)
public class HtmlPage extends Page {

  private static Map<String, StringBuffer> templates;
  private static StringBuffer templateContent;
  private static int pageCounter = 0;
  
  private StringBuffer tableTemplate;

  private List<HtmlHeaderRow> headerRows;

  private List<HtmlRow> dataRows;

  private List<HtmlPage> extensions;
  private StringBuffer headerStyles;
  private StringBuffer cellStyles;
  private StringBuffer bodyContent;
  private String dataTableAlignX;
  private String dataTableWidth;
  
  //private StringBuffer content;
  
  public static final String NL = "\n";
  
  private static final boolean debug = Toolkit.getDebug(HtmlPage.class);

  /**
   * @effects 
   *  return a copy of this that contains a reference to {@link #templates} and to {@link #templateContent} and
   *  whose {@link #content} is set to a copy of {@link #templateContent}.
   */
  public static HtmlPage createPage(HtmlPage pageTemplate) {
    HtmlPage clone = new HtmlPage();
    
    pageCounter++;
    
    // initialise content to a copy of the templateContent
    clone.content = new StringBuffer(pageTemplate.templateContent);
    File tempFile = pageTemplate.getOutputFile();
    /*v3.1: ONLY use pageCounter for output file if debug = true
     * to reduce number of pages produced in production

    clone.setOutputFile(new File(tempFile.getPath()+pageCounter));
     */
    
    File outputFile; 
    
    if (debug)
      outputFile = new File(tempFile.getPath()+pageCounter);
    else
      outputFile = tempFile;
    
    clone.setOutputFile(outputFile);
    
    return clone;
  }

  /**
   * @effects 
   *  create and return a new <tt>HtmlPage</tt> from the template content provided in <tt>contentStream</tt> 
   */
  public static HtmlPage createTemplatePage(InputStream contentStream) {
    return new HtmlPage(contentStream);
  }


  /**
   * Use this method <b>once</b> to initialise the Html template page that is used as the basis to create all other pages.
   * 
   * Then to create a new Html page from this template use {@link #createPage()}. 
   * 
   * @effects 
   *  create a new template page whose {@link #templateContent} is contained in <tt>htmlFile</tt>
   *  and whose {@link #content} is a copy of {@link #templateContent}
   */
  public static HtmlPage createTemplatePage(String htmlFile) {
    return new HtmlPage(htmlFile);
  }
  
  /**
   * @effects 
   *  initialises this with content of template file in <tt>contentStream</tt> 
   */
  private HtmlPage(InputStream contentStream) 
      throws NotFoundException, NotPossibleException {
    super();
    
    templateContent = new StringBuffer();
    
    createPage(contentStream);
  }
  
  /**
   * @requires 
   *  <tt>htmlFile</tt> is stored in the same directory as this.class
   * @effects 
   *  initialises this with content of template file <tt>htmlFile</tt> stored in 
   *  the same directory as this.class
   */
  private HtmlPage(String htmlFile) 
      throws NotFoundException, NotPossibleException {
    super();
    
    templateContent = new StringBuffer();
    
    // initialise this with content of the specified file
    InputStream ins = HtmlPage.class.getResourceAsStream(htmlFile);
    
    createPage(ins);
  }

  private void createPage(InputStream ins) throws NotPossibleException {
    BufferedReader reader = null;
    try {
      reader = new BufferedReader(new InputStreamReader(ins, Charset.forName("utf-8")));
      
      String s;
      while ((s = reader.readLine()) != null) {
        append(templateContent, s, NL);
        //append(content, s, NL);
      }
      
      // initialise element templates
      init();
      
      // initialise content
      content = new StringBuffer(templateContent);
      
    } catch (Exception e) {
      throw new NotPossibleException(NotPossibleException.Code.FAIL_TO_CREATE_OBJECT, 
          e, new Object[] {HtmlPage.class.getSimpleName(), ""}  
          );
    } finally {
      if (reader != null)
        try {reader.close(); } catch (Exception e) {}
    }    
  }

  /**
   * This constructor must only be used for {@link #clone()}
   */
  private HtmlPage() {
    // empty
    //super();
  }
  
  /**
   * @effects 
   *  generates all the HTML content element templates that are used to generate the content elements of an 
   *  HTML document. 
   */
  @Override
  public void init() {
    templates = new HashMap<>();
    
    StringBuffer buffer = new StringBuffer();
    
    //v2.7.3: supports img tag
    append(buffer, "<img src=\"{Image.file}\"/>");
    templates.put("ImageTag", buffer);
    
    buffer = new StringBuffer();
    append(buffer,
      "h1#tcaption {", NL,
      "  text-align: {Text.align};", NL,
      "  white-space: {Text.wrap};", NL, 
      "  font-size: {Text.size};", NL,
      "  font-family: {Text.family};", NL,
      "  color: {Text.color};", NL,
      "}" , NL
    );
    
    templates.put("TitleStyle", buffer);
    
    buffer = new StringBuffer();
    append(buffer, 
        "th#hd{Header.no} { ", NL,
        "  width: {Header.width};", NL,
        "  text-align: {Text.align};", NL,
        "  white-space: {Text.wrap};", NL,
        "  font-size: {Text.size};", NL,
        "  font-family: {Text.family};", NL,
        "  color: {Text.color}", NL,
        "}", NL        
    );

    templates.put("HeaderStyle", buffer);
    
    buffer = new StringBuffer();
    append(buffer,
        "td#cl{Column.no} {", NL,
        "  width: {Column.width};", NL,
        "  text-align: {Text.align};", NL,
        "  white-space: {Text.wrap};", NL,        
        "  font-size: {Text.size};", NL,
        "  font-family: {Text.family};", NL,
        "  color: {Text.color}", NL,
        "}", NL     
    );
    templates.put("CellStyle", buffer);

    buffer = new StringBuffer();
    
    append(buffer, 
        "<tr><td>", NL,
        "<h1 id=\"tcaption\">{Title}</h1>", NL,
        "</td></tr>", NL
        );
    templates.put("TitleRow", buffer);

    /*v2.7.4: support title row with icon on the left*/
    buffer = new StringBuffer();
    append(buffer, 
        "<tr><td>{Left}</td>", NL,
        "<td><h1 id=\"tcaption\">{Title}</h1></td>", NL,
        "</tr>", NL
        );
    templates.put("TitleRowWithLeft", buffer);

    buffer = new StringBuffer();
    append(buffer, 
        "<div align={Table.align}>", NL, 
        "  <table width=\"{Table.width}\" id=\"title\">", NL,
        "    {TitleRow}", NL,
        "  </table>", NL, 
        "</div>", NL
        );
    templates.put("TitleTable", buffer);
    
    buffer = new StringBuffer();
    append(buffer, 
        "<th id=\"hd{Header.no}\" colspan=\"{colspan}\" rowspan=\"{rowspan}\">{Header.value}</th>");
    templates.put("HeaderCell",    
        buffer);

    buffer = new StringBuffer();
    append(buffer, 
        "<tr>",NL, 
        " {Cells}", NL,          
        "</tr>", NL);
    templates.put("HeaderRow",  
        buffer); 

    buffer = new StringBuffer();
    append(buffer, 
        "<td id=\"cl{Column.no}\" colspan=\"{colspan}\" rowspan=\"{rowspan}\" height=\"{rowheight}\">{Column.value}</td>");
    templates.put("DataCell", buffer);
    
    buffer = new StringBuffer();
    append(buffer, 
        "<tr>",NL, 
        " {Cells}", NL, 
        "</tr>", NL);
    templates.put("DataRow", buffer);
    
    //
    buffer = new StringBuffer();
    append(buffer,
        "<div align={Table.align}>", NL, 
        "  <table width=\"{Table.width}\" id=\"data\">", NL,
        "    {HeaderRows}", NL, 
        "    {DataRows}", NL, 
        "  </table>", NL, 
        "</div>", NL,
        "{Extensions}", NL
        );
    
    templates.put("DataTable", buffer);
    
    //
    buffer = new StringBuffer();
    append(buffer,
        "<div align={Table.align}>", NL, 
        "  <table width=\"{Table.width}\" id=\"dataNbr\">", NL,
        "    {HeaderRows}", NL, 
        "    {DataRows}", NL, 
        "  </table>", NL, 
        "</div>", NL,
        "{Extensions}", NL
        );
    
    templates.put("DataTableNoBorder", buffer);
  }
  
  protected void append(StringBuffer buffer, String...strings) {
    for (String s: strings) {
      buffer.append(s);
    }
  }

  public StringBuffer getTemplate(String name, boolean clone) throws NotFoundException {
    StringBuffer temp = templates.get(name);
    if (temp == null)
      throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, 
          "Không tìm thấy mẫu (template): {0}", name);
    
    if (clone) {
      // clone 
      return new StringBuffer(temp.toString());
    } else {
      return temp;
    }
  }

  public void addHeaderStyle(StringBuffer style) {
    if (this.headerStyles == null) headerStyles = new StringBuffer();
    headerStyles.append(style);
  }
  
  public void addCellStyle(StringBuffer style) {
    if (cellStyles == null) cellStyles = new StringBuffer();
    cellStyles.append(style);
  }
  
  /**
   * @effects 
   *  sets TitleTable in {@link #content} = titleTable 
   */
  public void setTitleTable(StringBuffer titleTable) {
    setVar("TitleTable", titleTable.toString());
  }
  
  /**
   * @effects 
   *  sets DataTable in {@link #content} = dataTable 
   */
  public void setDataTable(StringBuffer dataTable) {
    setVar("DataTable", dataTable.toString());
  }
  
  public StringBuffer getHeaderStyles() {
    return headerStyles;
  }

  public StringBuffer getCellStyles() {
    return cellStyles;
  }

  public void setHeaderRows(List<HtmlHeaderRow> headerRows) {
    this.headerRows = headerRows;
  }


  public void setHeaderStyles(StringBuffer headerStyles) {
    this.headerStyles = headerStyles;
  }

  public StringBuffer getBodyContent() {
    return bodyContent;
  }

  public void setBodyContent(StringBuffer bodyContent) {
    this.bodyContent = bodyContent;
  }

  /**
   * @effects 
   *  replaces all occurrences of variable whose name is <tt>name</tt> in {@link #content} with <tt>val</tt>;
   *  
   *  throws NotFoundException if no such variable is found
   */
  public void setVar(String name, String val) throws NotFoundException {
    String varName = "{"+name+"}";
    
    int pos;
    int count = 0;
    do {
      pos = content.indexOf(varName);
      if (pos < 0 && count == 0) 
        throw new NotFoundException(NotFoundException.Code.TEMPLATE_NOT_FOUND, 
            "Không tìm thấy mẫu (template): {0}", name);
      
      if (pos >-1) content.replace(pos, pos+varName.length(), val);
      count++;
    } while (pos > -1);
  }

  public boolean isVarDefined(String varName) {
    return content.indexOf("{"+varName+"}") < 0;
  }

  public void setDataTableTemplate(StringBuffer template) {
    this.tableTemplate = template;
  }

  public void addDataRow(HtmlRow row) {
    if (dataRows == null) dataRows = new ArrayList<>();
    
    // debug
    //System.out.printf("%s.addDataRow: %n  num rows: (%d)%n  agg. data height: %d%n", this, getDataRowsCount(), getAggregatedDataHeight());
    
    dataRows.add(row);
  }
  
  public StringBuffer getTableTemplate() {
    return tableTemplate;
  }

  public List<HtmlHeaderRow> getHeaderRows() {
    return headerRows;
  }

  public List<HtmlRow> getDataRows() {
    return dataRows;
  }

  /**
   * @effects 
   *  if this has no data rows
   *    return true
   *  else
   *    return false
   */
  public boolean isEmpty() {
    return dataRows == null || dataRows.isEmpty();
  }
  
  
  public List<HtmlPage> getExtensions() {
    return extensions;
  }

  public void addHeader(HtmlHeaderRow headerRow) {
    if (headerRows == null) headerRows = new ArrayList<>();
    
    headerRows.add(headerRow);
  }
  
  public void addExtension(HtmlPage extensionDoc) {
    if (extensions == null) extensions = new ArrayList<>();
    extensions.add(extensionDoc);
  }

  /**
   * @effects 
   *  if this has data rows
   *    return their count
   *  else
   *    return 0
   */
  public int getDataRowsCount() {
    return (dataRows != null) ? dataRows.size() : 0;
  }

  /**
   * @effects 
   *  if <tt>rowIndx</tt> is a valid data row index in this
   *    return <tt>DataRow</tt> at <tt>rowIndx</tt>
   *  else
   *    return null
   */
  public HtmlRow getDataRow(int rowIndx) {
    if (rowIndx < 0 || rowIndx >= getDataRowsCount()) {
      return null;
    } else {
      return dataRows.get(rowIndx);
    }
  }


  /**
   * @effects 
   *  if <tt>index</tt> is valid header row index in this 
   *    return the header row at <tt>index</tt>
   *  else
   *    return null
   */
  public HtmlHeaderRow getHeaderRow(int index) {
    if (index < 0 || index >= getHeaderRowsCount()) {
      return null;
    } else {
      return headerRows.get(index);
    }
  }

  /**
   * @effects 
   *  if this has header rows
   *    return their count
   *  else
   *    return 0
   */
  public int getHeaderRowsCount() {
    return (headerRows != null) ? headerRows.size() : 0;
  }

  @Override
  public int getAggregatedDataHeight() {
    int sumH = 0;
    if (dataRows != null) {
      for (HtmlRow r : dataRows) {
        sumH += r.getHeight();
      }
      
      // include headers if used
      if (headerRows != null) {
        for (HtmlHeaderRow h : headerRows) {
          sumH += h.getHeight();
        }
      }
    }
    
    return sumH;
  }



  public void setDataTableAlignX(String dataTableAlignX) {
    this.dataTableAlignX = dataTableAlignX;
  }


  public void setDataTableWidth(String width) {
    this.dataTableWidth = width;
  }


  public String getDataTableWidth() {
    return dataTableWidth;
  }


  public String getDataTableAlignX() {
    return dataTableAlignX;
  }
  
  @Override
  public void finalise() {
    StringBuffer headerStyles = getHeaderStyles();
    StringBuffer cellStyles = getCellStyles();

    if (headerStyles != null) {
      setVar("HeaderStyle", headerStyles.toString());
    } else {
      setVar("HeaderStyle", "");        
    }

    if (cellStyles != null) {
      setVar("CellStyle", cellStyles.toString());
    } else {
      setVar("CellStyle", "");        
    }
  }


//  @Override
//  public void close() {
//    super.close();
//    templates = null;
//  }
}
