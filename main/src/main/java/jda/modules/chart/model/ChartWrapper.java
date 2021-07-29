package jda.modules.chart.model;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JComponent;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.general.PieDataset;

import jda.modules.common.exceptions.NotPossibleException;
import jda.modules.dcsl.syntax.DAttr;
import jda.modules.dcsl.syntax.DClass;
import jda.modules.dcsl.syntax.DAttr.Type;
import jda.mosa.controller.ControllerBasic;
import jda.mosa.view.assets.GUIToolkit;
import jda.mosa.view.assets.tables.JDataTable;

/**
 * @overview 
 *  A wrapper model class for <tt>JFreeChart</tt> 
 * @author dmle
 */
@DClass(serialisable=false)
public class ChartWrapper {
  
  // pseudo-id
  @DAttr(name="oid",type=Type.Long,id=true,auto=true)
  private long oid;

  @DAttr(name="chartTitle",type=Type.String,length=100)
  private String chartTitle;
  
  @DAttr(name="chartType",type=Type.Domain,optional=false)
  private ChartType chartType;

  /**
   *  applies only to line & bar chart:
   *  true if the category names are column names, false if category names are taken from
   *  a row (of the first column of the chart data area) 
   */
  @DAttr(name="categoryByColumn",type=Type.Boolean,optional=false)
  private boolean categoryByColumn;
  
  @DAttr(name="dataCtl",type=Type.Other)
  private ControllerBasic.DataController dataCtl;
  
  // derived
  @DAttr(name="chart",type=Type.Other,mutable=false,auto=true)
  private ChartPanel chart;

  
  private static final Color[] STANDARD_COLOURS = GUIToolkit.getStandardColors();
  
  public static enum ChartType {
    Line,
    Bar,
    Pie;
    
    @DAttr(name="name",type=Type.String,id=true,mutable=false)
    public String getName() {
      return name();
    }
  };
  
  static {
    // set a theme using the new shadow generator feature available in
    // 1.0.14 - for backwards compatibility it is not enabled by default
    ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
            true));
  }

  
  public ChartWrapper(String chartTitle, ChartType chartTypeName, Boolean categoryByColumn) {
    this.chartTitle = chartTitle;
    this.chartType = chartTypeName;
    // default: category names are column names
    if (categoryByColumn == null)
      this.categoryByColumn = true;
    else
      this.categoryByColumn = categoryByColumn;
    
    oid = System.currentTimeMillis();
  }

  public long getOid() {
    return oid;
  }
  
  public String getChartTitle() {
    return chartTitle;
  }

  public void setChartTitle(String chartTitle) {
    this.chartTitle = chartTitle;
    
    // update the title
    JFreeChart chartObj = chart.getChart();
    if (chartObj != null)
      chartObj.setTitle(chartTitle);
  }

  public ChartType getChartType() {
    return chartType;
  }

  public void setChartType(ChartType chartType) {
    this.chartType = chartType;
    
    // re-construct the chart
    createChart(); 
  }

  public ControllerBasic.DataController getDataCtl() {
    return dataCtl;
  }

  /**
   * @effects 
   *  set this.dataCtl = dataCtl
   *  create a chart object from the data in the data container of the specified data controller <tt>dataCtl</tt>.
   *  put the chart object in a chart panel
   *  <br>Throws NotPossibleException if could not create chart object from the data.
   */
  public void setDataCtl(ControllerBasic.DataController dataCtl) throws NotPossibleException {
    this.dataCtl = dataCtl;
    createChart();
  }
  
  public boolean getCategoryByColumn() {
    return categoryByColumn;
  }

  public void setCategoryByColumn(boolean categoryByColumn) {
    this.categoryByColumn = categoryByColumn;
    
    // re-construct the chart
    createChart(); 
  }

  public JComponent getChart() {
    return chart;
  }

  /**
   * @effects 
   *  create a chart object from the data contained in the data container of <tt>dataCtl</tt>
   *  and a <tt>ChartPanel</tt> that contains the chart object.
   *  
   *  <br>If <tt>ChartPanel</tt> already exists then update the chart object onto it.
   */
  private void createChart() {
    Dataset dset = null;
    // create a chart object from the data contained in the container
    JFreeChart chartObj = null;
    if (chartType == ChartType.Line) {
      dset = createCategoryDataSet();
      chartObj = createChart(dset);
      setUpLineChart(chartObj);
    } else if (chartType == ChartType.Bar) {
      dset = createCategoryDataSet();
      chartObj = createChart(dset);
      setUpBarChart(chartObj);      
    } else if (chartType == ChartType.Pie){
      // add other data set ?
      dset = (PieDataset) createPieDataSet();
      chartObj = createChart(dset);
      setUpPieChart(chartObj);
    }
    
    if (chart == null) {
      // create chart panel that contains chart object
      chart = new ChartPanel(chartObj);
      chart.setFillZoomRectangle(true);
      chart.setMouseWheelEnabled(true);
    } else {
      // update
      chart.setChart(chartObj);
    }
  }
  
  /**
   * @effects  
   *  if there are at least two table columns of the source data container selected 
   *    return their column indices in an array
   *  else
   *    return all column indices satisfy <tt>visibleOnly</tt>
   */
  private int[] getTableColumns(boolean visibleOnly) {
    JDataTable dataTable = (JDataTable) dataCtl.getDataContainer();
    
    int[] selectedCols = dataTable.getSelectedColumns();
    
    if (selectedCols.length >= 2) {
      // return selected columns
      return selectedCols;
    } else {
      // get all columns
      int colCount = dataTable.getColumnCount();
      List<Integer> cols = new ArrayList();
      for (int i = 0; i < colCount; i++) {
        if (visibleOnly && !dataTable.isColumnVisible(i))
          continue; // skip
        
        cols.add(i);
      }
      
      // convert to int[]
      int[] columns = new int[cols.size()]; 
      for (int j = 0; j < cols.size(); j++){
        columns[j] = cols.get(j);
      }
      
      return columns;
    }
  }
  
  /**
   * @effects
   *  return a <tt>PieDataset</tt>  
   */
  private PieDataset createPieDataSet() {
    /**
     *  data set is a two-column table:
     *    + col1: category names
     *    + col2: category values
     */
    boolean visibleColOnly = true;
    JDataTable dataTable = (JDataTable) dataCtl.getDataContainer();

    // use selected columns (if any)
    int[] tableCols = getTableColumns(visibleColOnly);

    // make sure that table has two columns
    if (tableCols.length < 2) {
      throw new NotPossibleException(NotPossibleException.Code.NOT_ENOUGH_CHART_DATA_COLUMNS, 
          "Bảng dữ liệu không có đủ 2 cột cho biểu đồ");
    }

    //TODO: use visible column indices
    int catCol = tableCols[0];
    int valCol = tableCols[1];
    
    // category names are in the first column
    String[] categories = dataTable.getValuesAtColumnAsString(catCol);
    if (categories == null) {
      throw new NotPossibleException(NotPossibleException.Code.NO_CHART_DATA, 
          "Không có dữ liệu cho biểu đồ {0}", chartTitle);
    }

    // values are in the second column
    Object[] vals = dataTable.getValuesAtColumn(valCol);
    
    //  create a new data set object of this type
    DefaultPieDataset dataset = new DefaultPieDataset();
    
    // add category values for each row
    Number n;
    Object val;
    String category;
    for (int row = 0; row < categories.length; row++) {
      category = categories[row];
      val = vals[row];
      // make sure that all values are numeric
      if (!(val instanceof Number)) {
        throw new NotPossibleException(
            NotPossibleException.Code.INVALID_CHART_DATA, 
            "Dữ liệu biểu đồ tại ô ({0},{1}) không hợp lệ: {2}", row, valCol, val);
      }
      n = (Number) val;
      dataset.setValue(category, n);      
    }
    
    return dataset;
  }
  
  /**
   * @effects 
   *  create a return a <tt>DefaultCategoryDataset</tt> object of this type
   */
  private DefaultCategoryDataset createCategoryDataSet() throws NotPossibleException {
    // categories are names of columns starting with the second
    boolean visibleColOnly = true;
    JDataTable dataTable = (JDataTable) dataCtl.getDataContainer();
    
    // use selected columns (if any)
    int[] tableCols = getTableColumns(visibleColOnly);
    // make sure that table has at least two columns
    if (tableCols.length < 2) {
      throw new NotPossibleException(NotPossibleException.Code.NOT_ENOUGH_CHART_DATA_COLUMNS, 
          "Bảng dữ liệu không có đủ ít nhất 2 cột cho biểu đồ");
    }

    //  create a new data set object of this type
    DefaultCategoryDataset dataset;

    if (categoryByColumn) {
      /***
       * Data set is a table:
       *  + first column: series name
       *  + columns from the second till end: category names
       *  + rows: data values for the categories 
       */
      int seriesCol = tableCols[0];
      // int catCol = tableCols[1];

      String[] categories = new String[tableCols.length - 1];
      
      // use the selected column headers
      for (int i = 1; i < tableCols.length; i++) {
        categories[i - 1] = dataTable.getTableHeaderAsString(tableCols[i]);
      }

      String[] series = dataTable.getValuesAtColumnAsString(seriesCol);
      if (series == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_CHART_DATA,
            "Không có dữ liệu cho biểu đồ {0}", chartTitle);
      }

      // create a new data set object of this type
      dataset = new DefaultCategoryDataset();

      // add category values for each row
      Number n;
      Object val;
      String serie, category;
      int headerIndex = 0;
      for (int row = 0; row < series.length; row++) {
        // for each serie
        headerIndex = 0;
        serie = series[row];
        for (int i = 1; i < tableCols.length; i++) {
          int col = tableCols[i];
          category = categories[headerIndex];
          // for each category column
          val = dataTable.getValueAt(row, col);
          // make sure that all values are numeric
          if (!(val instanceof Number)) {
            throw new NotPossibleException(
                NotPossibleException.Code.INVALID_CHART_DATA,
                "Dữ liệu biểu đồ tại ô ({0},{1}) không hợp lệ: {2}", row, col, val);
          }
          n = (Number) val;
          dataset.addValue(n, serie, category);
          headerIndex++;
        }
      }
    } else {
      /**
       * data table swaps category names into rows:
       *  first column: category names 
       *  second column till end: series values 
       */
      // series names are in the first column
      int catCol = tableCols[0];
      
      String[] categories = dataTable.getValuesAtColumnAsString(catCol); 
      if (categories == null) {
        throw new NotPossibleException(NotPossibleException.Code.NO_CHART_DATA, 
            "Không có dữ liệu cho biểu đồ {0}", chartTitle);
      }
      
      // series start from the second column
      String[] series = new String[tableCols.length - 1];
      
      // use the selected column headers
      for (int i = 1; i < tableCols.length; i++) {
        series[i - 1] = dataTable.getTableHeaderAsString(tableCols[i]);
      }
      
      // create a new data set object of this type
      dataset = new DefaultCategoryDataset();

      // add category values for each row
      Number n;
      Object val;
      String serie, category;
      int rowCount = dataTable.getRowCount();
      
      for (int i = 1; i < tableCols.length; i++) {
        int col = tableCols[i];
        // for each serie
        serie = series[i-1];
        for (int row = 0; row < rowCount; row++) {
          category = categories[row];
          // for each category
          val = dataTable.getValueAt(row, col);
          // make sure that all values are numeric
          if (!(val instanceof Number)) {
            throw new NotPossibleException(
                NotPossibleException.Code.INVALID_CHART_DATA,
                "Dữ liệu biểu đồ tại ô ({0},{1}) không hợp lệ: {2}", row, col, val);
          }
          n = (Number) val;
          dataset.addValue(n, serie, category);
        }
      }
    }

    return dataset;
  }

  /**
   * @effects 
   *  create and return a <tt>JFreeChart</tt> object from the given <tt>dataset</tt>
   */
  private JFreeChart createChart(Dataset dataset) {
    JFreeChart chart = null;
    
    boolean includeLegend = true;
    boolean toolTip = true;
    boolean urls = false;
    PlotOrientation orientation = PlotOrientation.VERTICAL;
    String domainAxis = "Category";
    String rangeAxis = "Value";
    if (chartType == ChartType.Line) {
      chart = ChartFactory.createLineChart(
          chartTitle,       // chart title
          domainAxis,               // domain axis label
          rangeAxis,                  // range axis label
          (DefaultCategoryDataset) dataset,                  // data
          orientation, // orientation
          includeLegend,                     // include legend
          toolTip,                     // tooltips?
          urls                     // URLs?
      );
    } else if (chartType == ChartType.Bar) {
      chart = ChartFactory.createBarChart(
          chartTitle,       // chart title
          domainAxis,               // domain axis label
          rangeAxis,                  // range axis label
          (DefaultCategoryDataset) dataset,                  // data
          orientation, // orientation
          includeLegend,                     // include legend
          toolTip,                     // tooltips?
          urls                     // URLs?
      );
    } else if (chartType == ChartType.Pie) {
      chart = ChartFactory.createPieChart(
          chartTitle,       // chart title
          (PieDataset) dataset,                  // data
          includeLegend,                     // include legend
          toolTip,                     // tooltips?
          urls                     // URLs?
      );
    }
    
    return chart;
  }
  
  private void setUpCommon(JFreeChart chart) {
    // set the background color for the chart...
    chart.setBackgroundPaint(Color.white);

    // get a reference to the plot for further customisation...
    Plot plot = chart.getPlot();

    plot.setBackgroundPaint(new Color(255,255,180));  // light-yellow 
  }
  
  private void setUpLineChart(JFreeChart chart) {
    // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

    setUpCommon(chart);

    // get a reference to the plot for further customisation...
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    //plot.setDomainGridlinePaint(Color.BLUE);

    // set the range axis to display integers only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

    // set up the series...
    int numSeries = plot.getLegendItems().getItemCount();
    Stroke stroke = new BasicStroke(3);
    
    Color[] color1s = STANDARD_COLOURS;
    int numColor1 = color1s.length;
    Color[] color2s = {
        new Color(0, 0, 64), 
        new Color(0, 64, 0), 
        new Color(64, 0, 0)};
    int numColor2 = color2s.length;
    
    Color color1;
    Color color2;
    GradientPaint gc;
    for (int i = 0; i < numSeries; i++) {
      // series stroke
      renderer.setSeriesStroke(i, stroke);
      
      // series color
      color1 = color1s[i%numColor1];
      color2 = color2s[i%numColor2];
      gc = new GradientPaint(0.0f, 0.0f, color1, 0.0f, 0.0f, color2);
      renderer.setSeriesPaint(i, gc);

      // make data item boxes visible
      renderer.setSeriesShapesVisible(i,  true);
    }
    
    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(CategoryLabelPositions
        .createUpRotationLabelPositions(Math.PI / 6.0));
    // OPTIONAL CUSTOMISATION COMPLETED.
  }

  private void setUpBarChart(JFreeChart chart) {
    // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

    setUpCommon(chart);
    
    // get a reference to the plot for further customisation...
    CategoryPlot plot = (CategoryPlot) chart.getPlot();
    //plot.setDomainGridlinePaint(Color.RED);

    // set the range axis to display integers only...
    NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
    rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

    // disable bar outlines...
    BarRenderer renderer = (BarRenderer) plot.getRenderer();
    renderer.setDrawBarOutline(false);
    renderer.setShadowVisible(false);
    
    // set up the series...
    int numSeries = plot.getLegendItems().getItemCount();
    Stroke stroke = new BasicStroke(3);
    
    Color[] color1s = STANDARD_COLOURS;
    int numColor1 = color1s.length;
    Color[] color2s = {
        new Color(0, 0, 64), 
        new Color(0, 64, 0), 
        new Color(64, 0, 0)};
    int numColor2 = color2s.length;
    
    Color color1;
    Color color2;
    GradientPaint gc;
    for (int i = 0; i < numSeries; i++) {
      // series stroke
      renderer.setSeriesStroke(i, stroke);
      
      // series color
      color1 = color1s[i%numColor1];
      color2 = color2s[i%numColor2];
      gc = new GradientPaint(0.0f, 0.0f, color1, 0.0f, 0.0f, color2);
      renderer.setSeriesPaint(i, gc);
    }

    CategoryAxis domainAxis = plot.getDomainAxis();
    domainAxis.setCategoryLabelPositions(
            CategoryLabelPositions.createUpRotationLabelPositions(
                    Math.PI / 6.0));
    // OPTIONAL CUSTOMISATION COMPLETED.
  }

  private void setUpPieChart(JFreeChart chart) {
    setUpCommon(chart);
    
    PiePlot plot = (PiePlot) chart.getPlot();
    
    plot.setSectionOutlinesVisible(false);
  }
  
  
  private String getDataTableLabel() {
    String label = dataCtl.getDataContainer().getLabel();
    if (label == null)
      label = dataCtl.getUser().getGUI().getTitle();
    
    return label;
  }

  @Override
  public String toString() {
    return "ChartWrapper (" + getDataTableLabel() + ")";
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((chartType == null) ? 0 : chartType.hashCode());
    result = prime * result + ((dataCtl == null) ? 0 : dataCtl.hashCode());
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
    ChartWrapper other = (ChartWrapper) obj;
    if (chartType != other.chartType)
      return false;
    if (dataCtl == null) {
      if (other.dataCtl != null)
        return false;
    } else if (!dataCtl.equals(other.dataCtl))
      return false;
    return true;
  }
  
  
}
