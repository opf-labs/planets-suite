package eu.planets_project.services.datatypes;

import static org.junit.Assert.assertEquals;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests for Content objects. Reads the same data using Content objects both by value and by
 * reference, checking for equality of the results.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class ContentTests {
    private static final String LOCATION = "IF/common/src/test/resources/sample_content.txt";

    private File file;
    private URL url;
    private InputStream stream;
    private byte[] byteArray;
    private String content;

    @Before
    public void init() throws IOException {
        file = new File(LOCATION);
        url = file.toURI().toURL();
        stream = url.openStream();
        byteArray = IOUtils.toByteArray(new FileInputStream(file));
        content = FileUtils.readFileToString(file);
    }

    @Test
    public void byReferenceToFile() {
        test(Content.byReference(file));
    }
    
    @Test
    public void byReferenceToInputStream() {
        test(Content.byReference(stream));
    }
    
    @Test
    public void byReferenceToUrl() {
        test(Content.byReference(url));
    }

    @Test
    public void byValueOfFile() {
        test(Content.byValue(file));
    }
    
    @Test
    public void byValueOfInputStream() {
        test(Content.byValue(stream));
    }
    
    @Test
    public void byValueOfByteArray() {
        test(Content.byValue(byteArray));
    }

    private void test(DigitalObjectContent object) {
        Assert.assertEquals("Original content and wrapped content should be equal", content,
                read(object.getInputStream()));
    }

    @Test
    public void equals() {
        DigitalObjectContent c1 = Content.byReference(url);
        DigitalObjectContent c2 = Content.byReference(url);
        assertEquals("Equal object don't equal;", c1, c2);
        assertEquals("Equal objects have different string representations;", c1.toString(), c2
                .toString());

    }

    @Test
    public void hashcode() {
        Set<DigitalObjectContent> set = new HashSet<DigitalObjectContent>(Arrays.asList(Content
                .byReference(url), Content.byReference(url), Content.byReference(url)));
        assertEquals("Set contains duplicates;", 1, set.size());
    }

    /**
     * @param source The source to read from
     * @return Returns the content of the source
     */
    private String read(final InputStream source) {
        StringBuilder builder = new StringBuilder();
        Scanner s = new Scanner(source);
        while (s.hasNextLine()) {
            builder.append(s.nextLine()).append("\n");
        }
        return builder.toString().trim();
    }

}
