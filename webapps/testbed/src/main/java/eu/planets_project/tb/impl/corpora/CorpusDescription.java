/*******************************************************************************
 * Copyright (c) 2007, 2010 The Planets Project Partners.
 *
 * All rights reserved. This program and the accompanying 
 * materials are made available under the terms of the 
 * Apache License, Version 2.0 which accompanies 
 * this distribution, and is available at 
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *******************************************************************************/
/**
 * 
 */
package eu.planets_project.tb.impl.corpora;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * This class wraps up the information we need to record about a corpus.
 * 
 * It can also be used to build/parse an XML document that describes a corpus, via JAXB.
 * 
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>
 *
 */
@XmlRootElement(name = "CorpusDescription", namespace = "http://www.planets-project.eu/testbed")
@XmlAccessorType(value = XmlAccessType.FIELD)
public class CorpusDescription {
    
    String name;
    
    String description;
    
    String version;
    
    String date;
    
    String author;
    
    String licence;
    
    URL homepage;
    
    URL logo;
    
    String root;
    
    /* ----------------------------------------------------- */

    /** For JAXB */
    protected CorpusDescription() {}
    
    /* ----------------------------------------------------- */

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return the version
     */
    public String getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * @return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return the author
     */
    public String getAuthor() {
        return author;
    }

    /**
     * @param author the author to set
     */
    public void setAuthor(String author) {
        this.author = author;
    }

    /**
     * @return the licence
     */
    public String getLicence() {
        return licence;
    }

    /**
     * @param licence the licence to set
     */
    public void setLicence(String licence) {
        this.licence = licence;
    }

    /**
     * @return the homepage
     */
    public URL getHomepage() {
        return homepage;
    }

    /**
     * @param homepage the homepage to set
     */
    public void setHomepage(URL homepage) {
        this.homepage = homepage;
    }

    /**
     * @return the logo
     */
    public URL getLogo() {
        return logo;
    }

    /**
     * @param logo the logo to set
     */
    public void setLogo(URL logo) {
        this.logo = logo;
    }
    
    /**
     * @return the root
     */
    public String getRoot() {
        return root;
    }

    /**
     * @param root the root to set
     */
    public void setRoot(String root) {
        this.root = root;
    }

    /* ----------------------------------------------------- */
    
    /**
     * @param xml The XML representation of a service description (as created
     *        from calling toXml)
     * @return A digital object instance created from the given XML
     */
    public static CorpusDescription of(final String xml) {
        try {
            /* Unmarshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(CorpusDescription.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object object = unmarshaller.unmarshal(new StringReader(xml));
            CorpusDescription unmarshalled = (CorpusDescription) object;
            return unmarshalled;
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @return An XML representation of this service description (can be used to
     *         instantiate an object using the static factory method)
     */
    public String toXml() {
        return toXml(false);
    }

    /**
     * @return A formatted (pretty-printed) XML representation of this service description
     */
    public String toXmlFormatted() {
        return toXml(true);
    }

    private String toXml(boolean formatted) {
        try {
            /* Marshall with JAXB: */
            JAXBContext context = JAXBContext
                    .newInstance(CorpusDescription.class);
            Marshaller marshaller = context.createMarshaller();
            StringWriter writer = new StringWriter();
            marshaller.setProperty("jaxb.formatted.output", formatted);
            marshaller.marshal(this, writer);
            return writer.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return null;
    }

    /* ----------------------------------------------------- */
    
    /**
     * A simple example of a Corpus description file
     */
    public static void main( String[] args ) throws IOException {
        // Create an example description:
        CorpusDescription cd = new CorpusDescription();
        cd.setAuthor("author");
        cd.setDate("date");
        cd.setVersion("version");
        cd.setDescription("description");
        cd.setName("name");
        cd.setHomepage(new URL("http://homepage"));
        cd.setLicence("licence");
        cd.setLogo(new URL("http://homepage/logo"));

        // Output to a file:
        FileOutputStream fos = new FileOutputStream("corpora/example-corpus-description.xml");
        OutputStreamWriter out = new OutputStreamWriter(fos, "UTF-8");
        Writer writer = new BufferedWriter(out);
        writer.write(cd.toXmlFormatted());
        writer.close();
        
        // Now attempt to read one in:
        //FileInputStream fis =  new FileInputStream("corpora/png-suite/corpus.xml");
        //InputStreamReader in = new InputStreamReader(fis, "UTF-8");
        
        cd = CorpusDescription.of(readFileAsString("corpora/png-suite/corpus.xml"));
        System.out.println("Loaded description for corpus: "+cd.getName());

    }
    
    /**
     * @param filePath
     *            the name of the file to open. Not sure if it can accept URLs
     *            or just filenames. Path handling could be better, and buffer
     *            sizes are hardcoded
     */
    private static String readFileAsString(String filePath)
            throws java.io.IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            fileData.append(buf, 0, numRead);
        }
        reader.close();
        return fileData.toString();
    }

}
