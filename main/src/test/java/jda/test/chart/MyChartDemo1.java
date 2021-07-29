package jda.test.chart;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GradientPaint;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.StandardChartTheme;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;

public class MyChartDemo1 {
  
  private static class MyChartPanel extends JPanel implements ActionListener {
    static {
      // set a theme using the new shadow generator feature available in
      // 1.0.14 - for backwards compatibility it is not enabled by default
      ChartFactory.setChartTheme(new StandardChartTheme("JFree/Shadow",
              true));
    }

    private JScrollPane scrollable;
    
    public MyChartPanel() {
      super();
      
      setLayout(new BorderLayout());
      
      //  a hide button
      JButton b = new JButton("Hide");
      b.addActionListener(this);
      
      // create North panel
      JPanel north = new JPanel();
      north.add(b);
      add(north, BorderLayout.NORTH);
      
      // create a chart panel object
      ChartPanel chartPane = createChartPanel();
      add(chartPane, BorderLayout.CENTER);
      
      // put chart panel in a scroll bar
      scrollable = new JScrollPane(this);
    }
    
    public JScrollPane getScrollable() {
      return scrollable;
    }
    
    public void actionPerformed(ActionEvent e) {
      String cmd = e.getActionCommand();
      
      if (cmd.equals("Hide")) {
        // hide this
        this.setVisible(false);
        //scrollable.setVisible(false);
      } else if (cmd.equals("Show chart")) {
        // show this
        //if (!scrollable.isVisible()) {
        if (!this.isVisible()) {
          this.setVisible(true);
          //scrollable.setVisible(true);
        }
      }
    }
    
    /** Chart-related methods **/
    private ChartPanel createChartPanel() {
      CategoryDataset dataset = createDataset();
      JFreeChart chart = createLineChart(dataset); // createBarChart(dataset);
      ChartPanel chartPanel = new ChartPanel(chart);
      chartPanel.setFillZoomRectangle(true);
      chartPanel.setMouseWheelEnabled(true);
      chartPanel.setPreferredSize(new Dimension(500, 270));
      
      return chartPanel;
    }
    
    /**
     * Returns a sample dataset.
     *
     * @return The dataset.
     */
    private static CategoryDataset createDataset() {

        // row keys...
        String series1 = "First";
        String series2 = "Second";
        String series3 = "Third";

        // column keys...
        String category1 = "Category 1";
        String category2 = "Category 2";
        String category3 = "Category 3";
        String category4 = "Category 4";
        String category5 = "Category 5";

        // create the dataset...
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        dataset.addValue(1.0, series1, category1);
        dataset.addValue(4.0, series1, category2);
        dataset.addValue(3.0, series1, category3);
        dataset.addValue(5.0, series1, category4);
        dataset.addValue(5.0, series1, category5);

        dataset.addValue(5.0, series2, category1);
        dataset.addValue(7.0, series2, category2);
        dataset.addValue(6.0, series2, category3);
        dataset.addValue(8.0, series2, category4);
        dataset.addValue(4.0, series2, category5);

        dataset.addValue(4.0, series3, category1);
        dataset.addValue(3.0, series3, category2);
        dataset.addValue(2.0, series3, category3);
        dataset.addValue(3.0, series3, category4);
        dataset.addValue(6.0, series3, category5);

        return dataset;

    }

    /**
     * Creates a sample chart.
     *
     * @param dataset  the dataset.
     *
     * @return The chart.
     */
    private static JFreeChart createLineChart(CategoryDataset dataset) {

        // create the chart...
      JFreeChart chart = ChartFactory.createLineChart(
          "Line Chart Demo 1",       // chart title
          "Category",               // domain axis label
          "Value",                  // range axis label
          dataset,                  // data
          PlotOrientation.VERTICAL, // orientation
          true,                     // include legend
          true,                     // tooltips?
          false                     // URLs?
      );
        // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        CategoryPlot plot = (CategoryPlot) chart.getPlot();
        plot.setBackgroundPaint(new Color(255,255,180));
        
        // ******************************************************************
        //  More than 150 demo applications are included with the JFreeChart
        //  Developer Guide...for more information, see:
        //
        //  >   http://www.object-refinery.com/jfreechart/guide.html
        //
        // ******************************************************************

        // set the range axis to display integers only...
        NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        LineAndShapeRenderer renderer = (LineAndShapeRenderer) plot.getRenderer();

        // set up gradient paints for series...
        GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
                0.0f, 0.0f, new Color(0, 0, 64));
        GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
                0.0f, 0.0f, new Color(0, 64, 0));
        GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
                0.0f, 0.0f, new Color(64, 0, 0));
        renderer.setSeriesPaint(0, gp0);
        renderer.setSeriesPaint(1, gp1);
        renderer.setSeriesPaint(2, gp2);

        // set up the series line
        Stroke stroke = new BasicStroke(3);
        renderer.setSeriesStroke(0, stroke);
        renderer.setSeriesStroke(1, stroke);
        renderer.setSeriesStroke(2, stroke);

        //Shape shape = null;
        //renderer.setSeriesShape(0,shape);
        renderer.setSeriesShapesVisible(0,  true);
        //renderer.setSeriesShapesFilled(0,  true);
        //renderer.setSeriesOutlineStroke(0, new BasicStroke(1));
        
        // set up the domain axis
        CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(
                        Math.PI / 6.0));
        // OPTIONAL CUSTOMISATION COMPLETED.

        return chart;

    }    

    private static JFreeChart createBarChart(CategoryDataset dataset) {

      // create the chart...
      JFreeChart chart = ChartFactory.createBarChart(
          "Bar Chart Demo 1",       // chart title
          "Category",               // domain axis label
          "Value",                  // range axis label
          dataset,                  // data
          PlotOrientation.VERTICAL, // orientation
          true,                     // include legend
          true,                     // tooltips?
          false                     // URLs?
      );

      // NOW DO SOME OPTIONAL CUSTOMISATION OF THE CHART...

      // set the background color for the chart...
      chart.setBackgroundPaint(Color.white);

      // get a reference to the plot for further customisation...
      CategoryPlot plot = (CategoryPlot) chart.getPlot();

      // ******************************************************************
      //  More than 150 demo applications are included with the JFreeChart
      //  Developer Guide...for more information, see:
      //
      //  >   http://www.object-refinery.com/jfreechart/guide.html
      //
      // ******************************************************************

      // set the range axis to display integers only...
      NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
      rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

      // disable bar outlines...
      BarRenderer renderer = (BarRenderer) plot.getRenderer();
      renderer.setDrawBarOutline(false);

      // set up gradient paints for series...
      GradientPaint gp0 = new GradientPaint(0.0f, 0.0f, Color.blue,
              0.0f, 0.0f, new Color(0, 0, 64));
      GradientPaint gp1 = new GradientPaint(0.0f, 0.0f, Color.green,
              0.0f, 0.0f, new Color(0, 64, 0));
      GradientPaint gp2 = new GradientPaint(0.0f, 0.0f, Color.red,
              0.0f, 0.0f, new Color(64, 0, 0));
      renderer.setSeriesPaint(0, gp0);
      renderer.setSeriesPaint(1, gp1);
      renderer.setSeriesPaint(2, gp2);

      CategoryAxis domainAxis = plot.getDomainAxis();
      domainAxis.setCategoryLabelPositions(
              CategoryLabelPositions.createUpRotationLabelPositions(
                      Math.PI / 6.0));
      // OPTIONAL CUSTOMISATION COMPLETED.

      return chart;

  }    

  } // end MyChartPanel
  
  private static class MyAppPanel extends JPanel {
    public MyAppPanel(Dimension wz) {
      super();
      
      /*
       *  create this panel with:
       *    border layout
       *     + north: a title 
       *     + centre: a sample data panel
       *     + south: a sample data button panel with a "Show chart" button  
       *     + west: a chart panel
       */
      setLayout(new BorderLayout());
      
      // create a title label to put in the north
      JLabel title = createLabel("Enter details", wz.width-20,20);
      add(title, BorderLayout.NORTH);
      
      // create a detailed panel to put in the center
      Dimension detailedSize = new Dimension(wz.width-20,wz.height-100);
      JPanel panel = createDetailedPanel(detailedSize.width, detailedSize.height);
      // fix the size of detailed panel and put it in a scroll bar
      //panel.setMinimumSize(new Dimension(50,50));
      JScrollPane detailedPanelScroll = new JScrollPane(panel);
      //detailedPanelScroll.setMinimumSize(new Dimension(50,50));      
      add(detailedPanelScroll, BorderLayout.CENTER);
      
      /* create a nested panel with:
       *  border layout
       *  + north: a sub-panel containing a button to hide the panel
       *  + centre: the chart panel
       */
      MyChartPanel chartPanel = new MyChartPanel();
      add(
          //chartPanel.getScrollable()
          chartPanel
          , 
          BorderLayout.EAST);
      
      // create a button panel to put in the south
      JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
      // a demo data button
      buttonPanel.add(new JButton("OK"));
      // show chart button: event handled by chart panel (above) 
      JButton chart = new JButton("Show chart");
      chart.addActionListener(chartPanel);
      buttonPanel.add(chart);
      add(buttonPanel, BorderLayout.SOUTH);
    }
  } // end MyAppPanel

  /**
   * Create the GUI and show it. For thread safety, this method should be
   * invoked from the event-dispatching thread.
   */
  private static void createAndShowGUI() {
    // Create and set up the window.
    final JFrame w = new SimpleWindow(MyChartDemo1.class.getName(), 800, 600);
    w.setLayout(new BorderLayout());
    
    final Dimension wz = w.getSize();
    
    JPanel appPanel = new MyAppPanel(wz);
    
    w.add(appPanel);
    
    // Display the window.
    w.setVisible(true);
  }

  public static JLabel createLabel(String text, int width, int height) {
    JLabel label = new JLabel(text);
    label.setOpaque(true);
    label.setBackground(Color.YELLOW);
    label.setForeground(Color.BLUE);
    label.setPreferredSize(new Dimension(width, height));  
    return label;
  }

  public static void createDetailedPanel(JPanel panel, int width, int length) {
    panel.setPreferredSize(new Dimension(width, length));
    panel.setBorder(BorderFactory.createEtchedBorder());
    String[] labels = {"name:", "address:"};
    char[] mnemonics = {'n', 'a'}; 
    final int labelWidth=100;
    final int labelHeight=20;
    
    // labels and texts
    JLabel label; 
    JTextField tf; 
    int index = 0;
    for (String lbl : labels) {
      // the text field
      tf = new JTextField(15);
      
      // label
      label = new JLabel(lbl);    
      label.setLabelFor(tf);
      label.setPreferredSize(new Dimension(labelWidth,labelHeight));
      label.setDisplayedMnemonic(mnemonics[index]);

      // add to window
      panel.add(label);
      panel.add(tf);
      index++;
    }
  }
  
  public static JPanel createDetailedPanel(int width, int length) {
    JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
    createDetailedPanel(panel, width, length);
    
    return panel;
  }
  
  public static void main(String[] args) {
    // Schedule a job for the event-dispatching thread:
    // creating and showing this application's GUI.
    javax.swing.SwingUtilities.invokeLater(new Runnable() {
      public void run() {
        createAndShowGUI();
      }
    });
  }
}
