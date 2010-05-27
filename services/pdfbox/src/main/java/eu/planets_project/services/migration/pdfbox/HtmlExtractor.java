package eu.planets_project.services.migration.pdfbox;

import java.io.IOException;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFText2HTML;

/**
 * Converts PDF files to HTML files using PDFBox.
 *
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 */
public class HtmlExtractor extends AbstractExtractor {

    /**
     * Extract text using a PDF2HTML extraction.
     * @param document Takes a PDF document.
     * @return The document as HTML text,
     * or as the PDFBox documentation says simple HTML.
     *
     * @throws IOException can be thrown by
     * InputStream used in AbstractExtractor.
     * @throws CryptographyException can be thrown by document.
     * @throws InvalidPasswordException can be thrown by document.
     */
    public final String getText(final PDDocument document)
        throws IOException, CryptographyException, InvalidPasswordException {
        final String docPassword = "";
        if (document.isEncrypted()) {
            document.decrypt(docPassword);
        }

        final PDFText2HTML stripper = new PDFText2HTML();
        stripper.shouldSeparateByBeads();
        stripper.shouldSortByPosition();


        return stripper.getText(document);
    }
}
