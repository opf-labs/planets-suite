package eu.planets_project.services.migration.pdfbox;

import java.io.IOException;

import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.util.PDFTextStripper;

/**
 * Convert PDF files to text files using PDFBox.
 *
 * @author <a href="mailto:cjen@kb.dk">Claus Jensen</a>
 */
public class TextExtractor extends AbstractExtractor {

    /**
     * Extract text using a PDF2TEXT extraction.
     * @param document Takes a PDF document.
     * @return The document as text UTF-16 Unicode (Java String).
     *
     * @throws IOException can be thrown by
     * InputStream used in AbstractExtractor.
     * @throws CryptographyException can be thrown by document.
     * @throws InvalidPasswordException can be thrown by document.
     */
    public final String getText(final PDDocument document)
        throws CryptographyException, IOException, InvalidPasswordException {
        final String docPassword = "";
        if (document.isEncrypted()) {
            document.decrypt(docPassword);
        }

        final PDFTextStripper stripper = new PDFTextStripper();
        stripper.shouldSeparateByBeads();
        stripper.shouldSortByPosition();

        return stripper.getText(document);
        }
}
