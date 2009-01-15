package eu.planets_project.ifr.core.services.characterisation.metadata.impl;

/**
 * Enumeration of sample files, their mime types (as the tool detects them), the
 * adapter jars for each type, and a sample Pronom ID for testing purpose.
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public enum MetadataType {
    /** Some types work just fine and give a full result. */
    BMP("bmp/test1.bmp", "image/bmp", "bmp_adapter_1_0.jar", "fmt/114"),
    /***/
    GIF("gif/AA_Banner.gif", "image/gif", "gif_adapter_1_0.jar", "fmt/4"),
    /***/
    JPEG("jpeg/AA_Banner.jpg", "image/jpeg", "jpg_adapter_1_1.jar", "fmt/42"),
    /***/
    TIFF("tiff/AA_Banner.tif", "image/tiff", "tiff_adapter_1_0.jar", "fmt/7"),
    /***/
    PDF("pdf/AA_Banner-single.pdf", "application/pdf", "pdf_adapter_1_0.jar", "fmt/14"),
    /***/
    WAV("wav/comet.wav", "audio/wav", "wave_adapter_1_0.jar", "fmt/2"),
    /***/
    HTML("html/sample.html", "text/html", "html_adapter_1_0.jar", "fmt/97"),
    /** The OO adapter throws an exception but still works. */
    OPEN_OFFICE1("oo1/planets.sxw", "application/open-office-1.x", "openoffice_adapter_1_0.jar", "fmt/128"),
    /**
     * And some are characterized as unknown although they should be supported
     * (according to http://meta-extractor.sourceforge.net).
     */
    WORD_PERFECT("wordperfect/sample.wpd",
    /* Word perfect identification works only on windows: */
    System.getProperty("os.name").toLowerCase().contains("windows") ? "application/vnd.wordperfect"
            : "file/unknown", "wordperfect_adapter_1_0.jar", "x-fmt/44"),
    /***/
    WORD6("word6/planets.doc", "file/unknown", "msword_adapter_2_0.jar", "fmt/39"),
    /***/
    WORKS("works/sample.wps", "file/unknown", "msworks_adapter_1_0.jar", "x-fmt/120"),
    /***/
    EXCEL("excel/Travel.xls", "file/unknown", "excel_adapter_1_0.jar", "fmt/55"),
    /***/
    POWER_POINT("pp/planets.ppt", "file/unknown", "powerpoint_adapter_1_0.jar", "fmt/125"),
    /***/
    MP3("mp3/Arkansas.mp3", "file/unknown", "mp3_adapter_1_0.jar", "fmt/134"),
    /***/
    XML("xml/sample.xml", "file/unknown", "xml_adapter_1_0.jar", "fmt/101");
    /***/
    String mime, sample, adapter, samplePuid;

    /**
     * @param sample The file location
     * @param mime The type, as the tool detects it
     * @param adapter The adapter jar file name
     * @param samplePuid A sample Pronom ID for the type
     */
    private MetadataType(final String sample, final String mime,
            final String adapter, final String samplePuid) {
        this.sample = sample;
        this.mime = mime;
        this.adapter = adapter;
        this.samplePuid = samplePuid;
    }
}