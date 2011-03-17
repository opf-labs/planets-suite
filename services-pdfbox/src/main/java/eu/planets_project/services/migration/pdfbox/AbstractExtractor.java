package eu.planets_project.services.migration.pdfbox;

import java.io.IOException;
import java.io.InputStream;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;

/**
 * Get PDF document from InputStrem and load it using PDFBox.
 *
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 */
public abstract class AbstractExtractor {

    /**
     * Extract text using PDFBox.
     * @param in A input stream containing a PDF document.
     * @return The converted document.
     *
     * @throws IOException can be thrown by
     * InputStream used in AbstractExtractor.
     * @throws CryptographyException can be thrown by document.
     * @throws InvalidPasswordException can be thrown by document.
     */
    public final String getText(final InputStream in)
        throws IOException, CryptographyException, InvalidPasswordException {
        if (in == null) {
            throw new NullPointerException("The content of "
                + "the digital object in null");
        }
        final PDDocument doc = PDDocument.load(in);
        String res = "";
        try {
            res = this.getText(doc);
        } finally {
            doc.close();
        }
        return res;
    }
    /**
     * Abstract method which extracts text using a PDF2HTML extraction.
     * @param document Takes a PDF document.
     * @return A string containing the migrated document.
     * @throws IOException can be thrown by
     * InputStream used in AbstractExtractor.
     * @throws CryptographyException can be thrown by document.
     * @throws InvalidPasswordException can be thrown by document.
     */
    public abstract String getText(PDDocument document)
        throws CryptographyException, IOException, InvalidPasswordException;
}
