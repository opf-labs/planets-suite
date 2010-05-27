package eu.planets_project.services.migrate.jtidy.impl;

import java.io.File;

/**
 * Utility class for the JTidy tests.
 * 
 * @author Peter Melms (peter.melms@uni-koeln.de)
 * 
 */
public final class JTidyUnitHelper {
	
    /** Local host address of the JBoss instance. */
    static final String LOCALHOST = "http://localhost:8080";
    /** Test server address of the JBoss instance. */
    static final String PLANETARIUM = "http://planetarium.hki.uni-koeln.de:8080";

    

    static final String JTIDY_LOCAL_TEST_OUT = "JTIDY_LOCAL_TEST_OUT";
    
    static final String JTIDY_SERVER_TEST_OUT = "JTIDY_SERVER_TEST_OUT";
    
    static final String JTIDY_STANDALONE_TEST_OUT = "JTIDY_STANDALONE_TEST_OUT";
           
    static final File HTML_OLD_FILE = new File("PA/jtidy/test/resources/sample_files/html_old.html");
    
    static final File HTML20_FILE = new File("PA/jtidy/test/resources/sample_files/html20.html");
    
    static final File HTML32_FILE = new File("PA/jtidy/test/resources/sample_files/html32.html");
    
    static final File HTML40_FILE = new File("PA/jtidy/test/resources/sample_files/html40.html");
    
    static final File HTML401_FILE = new File("PA/jtidy/test/resources/sample_files/html401.html");
    
    static final File WORD_HTML_FILE = new File("PA/jtidy/test/resources/sample_files/word_html.html");
    
    static final File CONFIG_FILE = new File("PA/jtidy/test/resources/sample_files/config_file.txt");
    
    
    

    /**
     * We enforce non-instantiability with a private constructor.
     */
    private JTidyUnitHelper() {}
}

