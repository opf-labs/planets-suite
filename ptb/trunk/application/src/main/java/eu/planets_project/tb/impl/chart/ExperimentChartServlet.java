/**
 * 
 */
package eu.planets_project.tb.impl.chart;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.LogAxis;
import org.jfree.chart.labels.StandardCategoryToolTipGenerator;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.StandardBarPainter;
import org.jfree.chart.util.LogFormat;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.date.MonthConstants;
import org.jfree.ui.RectangleInsets;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

import eu.planets_project.tb.api.data.util.DigitalObjectRefBean;
import eu.planets_project.tb.api.model.Experiment;
import eu.planets_project.tb.api.persistency.ExperimentPersistencyRemote;
import eu.planets_project.tb.gui.backing.exp.ResultsForDigitalObjectBean;
import eu.planets_project.tb.impl.data.util.DataHandlerImpl;
import eu.planets_project.tb.impl.model.eval.mockup.TecRegMockup;
import eu.planets_project.tb.impl.model.exec.BatchExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionRecordImpl;
import eu.planets_project.tb.impl.model.exec.ExecutionStageRecordImpl;
import eu.planets_project.tb.impl.persistency.ExperimentPersistencyImpl;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentChartServlet extends HttpServlet {

    /**
     * @author anj
     */
    public class RunComparator implements Comparator<ExecutionRecordImpl> {

        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(ExecutionRecordImpl arg0, ExecutionRecordImpl arg1) {
            if( arg0 != null && arg1 != null && arg0.getStartDate() != null ) {
                return arg0.getStartDate().compareTo(arg1.getStartDate());
            }
            return 0;
        }

    }

    /** */
    private static Log log = LogFactory.getLog(ExperimentChartServlet.class);

    /**
     * 
     */
    private static final long serialVersionUID = -417368403030414811L;

    /**
     * Default constructor.
     */
    public ExperimentChartServlet() {
        // nothing required
    }

    /**
     * Process a GET request.
     *
     * @param request  the request.
     * @param response  the response.
     *
     * @throws ServletException if there is a servlet related problem.
     * @throws IOException if there is an I/O problem.
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

        OutputStream out = response.getOutputStream();
        try {
            String type = request.getParameter("type");
            String format = request.getParameter("format");
            String eid = request.getParameter("eid");
            
            JFreeChart chart = null;
            if ( "pie".equalsIgnoreCase(type) ) {
                chart = createPieChart();
            }
            else if ( "bar".equalsIgnoreCase(type) ) {
                chart = createBarChart();
            }
            else if ( "time".equalsIgnoreCase(type) ) {
                chart = createTimeSeriesChart();
            }
            else if ( "exp".equalsIgnoreCase(type) ) {
                chart = createXYChart(eid);
            }
            else if ( "wall".equalsIgnoreCase(type) ) {
                chart = createWallclockChart(eid);
            } else {
                chart = null;
            }
            // Render
            if (chart != null) {
                if( "svg".equalsIgnoreCase(format) ) {
                    response.setContentType("image/svg+xml");
                    writeChartAsSVG(out, chart, 600, 500);
                } else {
                    response.setContentType("image/png");
                    // force aliasing of the rendered content..
                    chart.getRenderingHints().put( RenderingHints.KEY_ANTIALIASING,
                            RenderingHints.VALUE_ANTIALIAS_ON );
                    ChartUtilities.writeChartAsPNG(out, chart, 600, 500);
                }
            }
        }
        catch (Exception e) {
            System.err.println(e.toString());
            e.printStackTrace();
        }
        finally {
            out.close();
        }

    }

    /**
     * Creates a sample pie chart.
     *
     * @return a pie chart.
     */
    private JFreeChart createPieChart() {

        // create a dataset...
        DefaultPieDataset data = new DefaultPieDataset();
        data.setValue("One", new Double(43.2));
        data.setValue("Two", new Double(10.0));
        data.setValue("Three", new Double(27.5));
        data.setValue("Four", new Double(17.5));
        data.setValue("Five", new Double(11.0));
        data.setValue("Six", new Double(19.4));

        JFreeChart chart = ChartFactory.createPieChart(
            "Pie Chart", data, true, true, false
        );
        return chart;

    }

    /**
     * Creates a sample bar chart.
     *
     * @return a bar chart.
     */
    private JFreeChart createBarChart() {

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        dataset.addValue(10.0, "S1", "C1");
        dataset.addValue(4.0, "S1", "C2");
        dataset.addValue(15.0, "S1", "C3");
        dataset.addValue(14.0, "S1", "C4");
        dataset.addValue(-5.0, "S2", "C1");
        dataset.addValue(-7.0, "S2", "C2");
        dataset.addValue(14.0, "S2", "C3");
        dataset.addValue(-3.0, "S2", "C4");
        dataset.addValue(6.0, "S3", "C1");
        dataset.addValue(17.0, "S3", "C2");
        dataset.addValue(-12.0, "S3", "C3");
        dataset.addValue(7.0, "S3", "C4");
        dataset.addValue(7.0, "S4", "C1");
        dataset.addValue(15.0, "S4", "C2");
        dataset.addValue(11.0, "S4", "C3");
        dataset.addValue(0.0, "S4", "C4");
        dataset.addValue(-8.0, "S5", "C1");
        dataset.addValue(-6.0, "S5", "C2");
        dataset.addValue(10.0, "S5", "C3");
        dataset.addValue(-9.0, "S5", "C4");
        dataset.addValue(9.0, "S6", "C1");
        dataset.addValue(8.0, "S6", "C2");
        dataset.addValue(null, "S6", "C3");
        dataset.addValue(6.0, "S6", "C4");
        dataset.addValue(-10.0, "S7", "C1");
        dataset.addValue(9.0, "S7", "C2");
        dataset.addValue(7.0, "S7", "C3");
        dataset.addValue(7.0, "S7", "C4");
        dataset.addValue(11.0, "S8", "C1");
        dataset.addValue(13.0, "S8", "C2");
        dataset.addValue(9.0, "S8", "C3");
        dataset.addValue(9.0, "S8", "C4");
        dataset.addValue(-3.0, "S9", "C1");
        dataset.addValue(7.0, "S9", "C2");
        dataset.addValue(11.0, "S9", "C3");
        dataset.addValue(-10.0, "S9", "C4");

        JFreeChart chart = ChartFactory.createBarChart3D(
            "Bar Chart",
            "Category",
            "Value",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );
        return chart;

    }

    /**
     * Creates a sample time series chart.
     *
     * @return a time series chart.
     */
    private JFreeChart createTimeSeriesChart() {

        // here we just populate a series with random data...
        TimeSeries series = new TimeSeries("Random Data");
        Day current = new Day(1, MonthConstants.JANUARY, 2001);
        for (int i = 0; i < 100; i++) {
            series.add(current, Math.random() * 100);
            current = (Day) current.next();
        }
        
        XYDataset data = new TimeSeriesCollection(series);

        JFreeChart chart = ChartFactory.createTimeSeriesChart(
            "Time Series Chart", "Date", "Rate",
            data, true, true, false
        );
        
        return chart;

    }
    
    /**
     * Creates a sample dataset.
     *
     * @return A sample dataset.
     */
    private static XYDataset createDataset() {
        XYSeries series = new XYSeries("Random Data");
        series.add(1.0, 500.2);
        series.add(5.0, 694.1);
        series.add(4.0, 100.0);
        series.add(12.5, 734.4);
        series.add(17.3, 453.2);
        series.add(21.2, 500.2);
        series.add(21.9, null);
        series.add(25.6, 734.4);
        series.add(30.0, 453.2);
        return new XYSeriesCollection(series);
    }
    
    public JFreeChart createXYChart(String expId) {
        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        long eid = Long.parseLong(expId);
        log.info("Building experiment chart for eid = "+eid);
        Experiment exp = edao.findExperiment(eid);

        final String expName = exp.getExperimentSetup().getBasicProperties().getExperimentName();
        final XYSeries series = new XYSeries(expName);

        for( BatchExecutionRecordImpl batch : exp.getExperimentExecutable().getBatchExecutionRecords() ) {
            int i = 1;
            for( ExecutionRecordImpl exr : batch.getRuns() ) {
                //log.info("Found Record... "+exr+" stages: "+exr.getStages());
                if( exr != null && exr.getStages() != null ) {
                    // Look up the object, so we can get the name.
                    DigitalObjectRefBean dh = new DataHandlerImpl().get(exr.getDigitalObjectReferenceCopy());
                    String dobName = "Object "+i;
                    if( dh != null ) dobName = dh.getName();

                    for( ExecutionStageRecordImpl exsr : exr.getStages() ) {
                        Double time = exsr.getDoubleMeasurement( TecRegMockup.PROP_SERVICE_TIME );
                        Double size = exsr.getDoubleMeasurement(TecRegMockup.PROP_DO_SIZE);
                        // Look for timing:
                        if( time != null && size != null && size.doubleValue() > 0.0 && time.doubleValue() > 0.0 ) {
                            series.add(size, time);
                            /*
                            if( exsr.isMarkedAsSuccessful() ) {
                                dataset.addValue( time, "Succeded", dobName);
                            } else {
                                dataset.addValue( time, "Failed", dobName);
                            }
                            */
                        }
                    }
                }
                // Increment, for the next run.
                i++;
            }
        }
        // Create the chart.
        JFreeChart chart = ChartFactory.createScatterPlot(
                null, "Size [bytes]", "Time [s]", new XYSeriesCollection(series), PlotOrientation.VERTICAL, true, true, false
        );
        
        XYPlot plot = (XYPlot) chart.getPlot();
        LogAxis xAxis = new LogAxis("Size [bytes]");
        // Set the base appropriately:
        xAxis.setBase(1024.0);
//        xAxis.setTickUnit( new NumberTickUnit(128.0) );
//        xAxis.getTickUnit().getMinorTickCount();
        // FIXME This should really be a KB/MB/etc number formatter...
        xAxis.setNumberFormatOverride(new LogFormat(1024.0, "1024", true));
//        LogAxis yAxis = new LogAxis("Y");
//        yAxis.setNumberFormatOverride(new LogFormat(10.0, "10", true));
        plot.setDomainAxis(xAxis);
//        plot.setRangeAxis(yAxis);
        
        // Add some tool-tips
        plot.getRenderer().setBaseToolTipGenerator( new StandardXYToolTipGenerator() );
        
        return chart;
        
    }
    
    public JFreeChart createWallclockChart(String expId) {
        ExperimentPersistencyRemote edao = ExperimentPersistencyImpl.getInstance();
        long eid = Long.parseLong(expId);
        log.info("Building experiment chart for eid = "+eid);
        Experiment exp = edao.findExperiment(eid);

        final DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        final String expName = exp.getExperimentSetup().getBasicProperties().getExperimentName();
        
        boolean hasSuccesses = false;
        boolean hasFails = false;

        for( BatchExecutionRecordImpl batch : exp.getExperimentExecutable().getBatchExecutionRecords() ) {
            int i = 1;
            List<ExecutionRecordImpl> runs = new ArrayList<ExecutionRecordImpl>(batch.getRuns());
            Collections.sort(runs, new RunComparator() );
            for( ExecutionRecordImpl exr : runs ) {
                //log.info("Found Record... "+exr+" stages: "+exr.getStages());
                if( exr != null && exr.getStages() != null ) {
                    // Look up the object, so we can get the name.
                    DigitalObjectRefBean dh = new DataHandlerImpl().get(exr.getDigitalObjectReferenceCopy());
                    String dobName = "Object "+i;
                    if( dh != null ) dobName = dh.getName();
                             
                    ResultsForDigitalObjectBean res = new ResultsForDigitalObjectBean(exr.getDigitalObjectReferenceCopy());
                    Double time = null;
                    boolean success = false;
                    // First, attempt to pull from stage records:
                    // FIXME: Note that this record is really at the wrong level.
                    /*
                    if( exr.getStages().size() == 1 ) {
                        for( ExecutionStageRecordImpl exsr : exr.getStages() ) {
                            Double stageTime = exsr.getDoubleMeasurement( TecRegMockup.PROP_SERVICE_TIME );
                            if( stageTime != null ) {
                                time = stageTime;
                                success = exsr.isMarkedAsSuccessful();
                            }
                        }
                    }
                    */
                    // Pick up from record duration:
                    if( time == null && res.getExecutionDuration()!=null){
                    	//convert from milli seconds to seconds
                    	time = (double)res.getExecutionDuration()/1000.0;
                    	success = res.getHasExecutionSucceededOK();
                    }
                    log.info("Found DOB: {"+exr.getDigitalObjectReferenceCopy()+"} {"+dobName+"} w/ time "+time);
                    log.info("Timing: "+res.getExecutionRecord().getStartDate()+" "+res.getExecutionRecord().getEndDate());
                    if( time != null ) {
                        if( success ) {
                            dataset.addValue( time, "Succeeded", dobName);
                            hasSuccesses = true;
                        } else {
                            dataset.addValue( time, "Failed", dobName);
                            hasFails = true;
                        }
                    }
                }
                // Increment, for the next run.
                i++;
            }
        }
        int si = dataset.getRowIndex("Succeeded");
        int ri = dataset.getRowIndex("Failed");

        // Create the chart.
        JFreeChart chart = ChartFactory.createStackedBarChart(
                null, "Digital Object", "Time [s]", dataset, PlotOrientation.VERTICAL, true, true, false
        );

        // set the background color for the chart...
        chart.setBackgroundPaint(Color.white);

        // get a reference to the plot for further customisation...
        final CategoryPlot plot = chart.getCategoryPlot();
        plot.setBackgroundPaint(Color.lightGray);
        plot.setDomainGridlinePaint(Color.gray);
        plot.setRangeGridlinePaint(Color.gray);

        // set the range axis to display integers only...
        //final NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
        //rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());

        // disable bar outlines...
        final BarRenderer renderer = (BarRenderer) plot.getRenderer();
        renderer.setDrawBarOutline(true);
        renderer.setShadowVisible(false);
        renderer.setBarPainter(new StandardBarPainter());

        // set up gradient paints for series...
        final GradientPaint gp0 = new GradientPaint(
                0.0f, 0.0f, Color.green, 
                0.0f, 0.0f, new Color(0.0f, 0.9f, 0.0f)
        );
        final GradientPaint gp1 = new GradientPaint(
                0.0f, 0.0f, Color.red, 
                0.0f, 0.0f, new Color(0.9f, 0.0f, 0.0f)
        );
        if( hasSuccesses ) renderer.setSeriesPaint(si, gp0);
        if( hasFails ) renderer.setSeriesPaint(ri, gp1);
        
        // Set the tooltips...
        //renderer.setBaseItemURLGenerator(new StandardCategoryURLGenerator("xy_chart.jsp","series","section"));
        renderer.setBaseToolTipGenerator(new MeasurementToolTipGenerator());

        final CategoryAxis domainAxis = plot.getDomainAxis();
        domainAxis.setCategoryLabelPositions(
                CategoryLabelPositions.createUpRotationLabelPositions(Math.PI / 4.0)
        );
        

        // More settings
        chart.getRenderingHints().put( RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON );
        // Remove the border, as the SVG renderer has problems with the text overflowing.
        chart.getLegend().setBorder(0.0, 0.0, 0.0, 0.0);
        // Remove the padding between the axes and the plot:
        chart.getPlot().setInsets(new RectangleInsets(0.0, 0.0, 0.0, 0.0));
        // Set a gradient fill, fading towards the top:
        final GradientPaint gpb0 = new GradientPaint(
                0.0f, 0.0f, new Color(245, 245, 245), 
                0.0f, 0.0f, Color.white
            );
        chart.getPlot().setBackgroundPaint( gpb0 );


        return chart;
    }
    
    static class MeasurementToolTipGenerator extends StandardCategoryToolTipGenerator {

        /**
         * 
         */
        private static final long serialVersionUID = 5318030018610824973L;

        /* (non-Javadoc)
         * @see org.jfree.chart.labels.StandardCategoryToolTipGenerator#generateToolTip(org.jfree.data.category.CategoryDataset, int, int)
         */
        @Override
        public String generateToolTip(CategoryDataset dataset, int row,
                int column) {
            String toolTip = super.generateToolTip(dataset, row, column);
            toolTip = super.generateColumnLabel(dataset, column)+" - "+super.generateRowLabel(dataset, row)+" in " + dataset.getValue(row, column) + "s";
            return toolTip;
        }
        
    }
    
    /**
     * A custom renderer that returns a different color for each item in a
     * single series.
     */
    static class CustomRenderer extends BarRenderer {

        /** The colors. */
        private Paint[] colors;

        /**
         * Creates a new renderer.
         *
         * @param colors  the colors.
         */
        public CustomRenderer(Paint[] colors) {
            this.colors = colors;
        }

        /**
         * Returns the paint for an item.  Overrides the default behaviour
         * inherited from AbstractSeriesRenderer.
         *
         * @param row  the series.
         * @param column  the category.
         *
         * @return The item color.
         */
        public Paint getItemPaint(int row, int column) {
            return this.colors[column % this.colors.length];
        }
    }

    public static void writeChartAsSVG( OutputStream outstream, JFreeChart chart, int width, int height ) 
            throws UnsupportedEncodingException, SVGGraphics2DIOException {
        
        Writer out = new OutputStreamWriter( outstream, "UTF-8" );
        
        // THE FOLLOWING CODE BASED ON THE EXAMPLE IN THE BATIK DOCUMENTATION...
        // Get a DOMImplementation
        DOMImplementation domImpl
            = GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document
        Document document = domImpl.createDocument(null, "svg", null);

        // Create an instance of the SVG Generator
        SVGGraphics2D svgGenerator = new SVGGraphics2D(document);

        // set the precision to avoid a null pointer exception in Batik 1.5
        svgGenerator.getGeneratorContext().setPrecision(3);

        // Other opts:
        svgGenerator.getGeneratorContext().setEmbeddedFontsOn(true);
//        svgGenerator.setFont( new Font("SansSerif", Font.PLAIN, 8) );

        // Ask the chart to render into the SVG Graphics2D implementation
        chart.draw(svgGenerator, new Rectangle2D.Double(0, 0, width, height), null);

        // Finally, stream out SVG to a file using UTF-8 character to
        // byte encoding
        boolean useCSS = true;

        svgGenerator.stream(out, useCSS);
        
    }
}
