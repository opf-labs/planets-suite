/*
 * © The National Archives 2005-2006.  All rights reserved.
 * See Licence.txt for full licence details.
 *
 * Developed by:
 * Tessella Support Services plc
 * 3 Vineyard Chambers
 * Abingdon, OX14 3PX
 * United Kingdom
 * http://www.tessella.com
 *
 * Tessella/NPD/4826
 * PRONOM 5a
 *
 * $Id: InputStreamByteReader.java,v 1.3 2006/03/13 15:15:28 linb Exp $
 *
 * $Log: InputStreamByteReader.java,v $
 * Revision 1.3  2006/03/13 15:15:28  linb
 * Changed copyright holder from Crown Copyright to The National Archives.
 * Added reference to licence.txt
 * Changed dates to 2005-2006
 *
 * Revision 1.2  2006/02/09 15:31:23  linb
 * Updates to javadoc and code following the code review
 *
 * Revision 1.1  2006/02/09 13:17:42  linb
 * Changed StreamByteReader to InputStreamByteReader
 * Refactored common code from UrlByteReader and InputStreamByteReader into new class StreamByteReader, from which they both inherit
 * Updated javadoc
 *
 */

package uk.gov.nationalarchives.droid.binFileReader;

import java.io.IOException;

import uk.gov.nationalarchives.droid.IdentificationFile;

/**
 * The <code>InputStreamByteReader</code> class is a <code>ByteReader</code> that
 * reads its data from the <code>System.in</code> input stream.
 *
 * @author linb
 */
public class InputStreamByteReader extends StreamByteReader {

    /**
     * Creates a new instance of UrlByteReader
     */
    private InputStreamByteReader(IdentificationFile theIDFile, boolean readFile) {
        super(theIDFile);
        if (readFile) {
            this.readInputStream();
        }
    }

    /**
     * Static constructor for class.  Trys to read stream into a buffer. If it doesn't fit,
     * save it to a file, and return a FileByteReader with that file.
     */
    static ByteReader newInputStreamByteReader(IdentificationFile theIDFile, boolean readFile) {
        InputStreamByteReader byteReader = new InputStreamByteReader(theIDFile, readFile);
        if (byteReader.tempFile == null) {
            return byteReader;
        } else {
            return new FileByteReader(theIDFile, readFile, byteReader.tempFile.getPath());
        }
    }

    /**
     * Read data into buffer or temporary file from the <code>System.in</code> input stream.
     */
    private void readInputStream() {

        try {
            readStream(System.in);
        } catch (IOException ex) {
            this.setErrorIdent();
            this.setIdentificationWarning("Input stream could not be read");

        }
    }

    /**
     * Checks if the path represents the input stream
     *
     * @param path the path to check
     * @return <code>true</code> if <code>path</code> is equal to "-", <code>false</code> otherwise
     */
    public static boolean isInputStream(String path) {

        if ("-".equals(path)) {
            return true;
        } else {
            return false;
        }
    }


}
