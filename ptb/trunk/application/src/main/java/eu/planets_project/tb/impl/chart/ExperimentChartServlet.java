/**
 * 
 */
package eu.planets_project.tb.impl.chart;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYDataset;
import org.jfree.date.MonthConstants;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
public class ExperimentChartServlet extends HttpServlet {

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
            
            JFreeChart chart = null;
            if ( "pie".equalsIgnoreCase(type) ) {
                chart = createPieChart();
            }
            else if ( "bar".equalsIgnoreCase(type) ) {
                chart = createBarChart();
            }
            else if ( "time".equalsIgnoreCase(type) ) {
                chart = createTimeSeriesChart();
            } else {
                chart = null;
            }
            
            if (chart != null) {
                
                // Remove the border, as the SVG renderer has problems with the text overflowing.
                chart.getLegend().setBorder(0.0, 0.0, 0.0, 0.0);
                // Set a gradient fill, fading towards the top:
                final GradientPaint gp0 = new GradientPaint(
                        0.0f, 0.0f, new Color(216, 216, 216), 
                        0.0f, 0.0f, Color.white
                    );
                chart.getPlot().setBackgroundPaint( gp0 );
                //chart.getPlot().setDomainGridlinePaint(Color.white);
                //chart.getPlot().setRangeGridlinePaint(Color.white);
                
                if( "svg".equalsIgnoreCase(format) ) {
                    response.setContentType("image/svg+xml");
                    writeChartAsSVG(out, chart, 400, 300);
                } else {
                    response.setContentType("image/png");
                    ChartUtilities.writeChartAsPNG(out, chart, 400, 300);
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
