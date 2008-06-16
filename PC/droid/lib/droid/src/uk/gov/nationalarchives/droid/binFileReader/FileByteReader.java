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
 * $Id: FileByteReader.java,v 1.8 2006/03/13 15:15:28 linb Exp $
 *
 * $Log: FileByteReader.java,v $
 * Revision 1.8  2006/03/13 15:15:28  linb
 * Changed copyright holder from Crown Copyright to The National Archives.
 * Added reference to licence.txt
 * Changed dates to 2005-2006
 *
 * Revision 1.7  2006/02/09 15:34:10  linb
 * Updates to javadoc and code following the code review
 *
 * Revision 1.5  2006/02/09 15:31:23  linb
 * Updates to javadoc and code following the code review
 *
 * Revision 1.5  2006/02/09 13:17:42  linb
 * Changed StreamByteReader to InputStreamByteReader
 * Refactored common code from UrlByteReader and InputStreamByteReader into new class StreamByteReader, from which they both inherit
 * Updated javadoc
 *
 * Revision 1.4  2006/02/09 12:14:16  linb
 * Changed some javadoc to allow it to be created cleanly
 *
 * Revision 1.3  2006/02/08 08:56:35  linb
 * - Added header comments
 *
 */

package uk.gov.nationalarchives.droid.binFileReader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

import uk.gov.nationalarchives.droid.AnalysisController;
import uk.gov.nationalarchives.droid.IdentificationFile;

/**
 * The <code>FileByteReader</code> class is a <code>ByteReader</code> that
 * reads its data from a file.
 * <p/>
 * <p>This class can have two files associated with it: The file represented by it
 * (its <code>IdentificationFile</code>) and a (possibly different) backing file.
 * The purpose of this separation is so that this object can represent a URL that
 * has been downloaded or an InputStream that has been saved to disk.
 *
 * @author linb
 */
public class FileByteReader extends AbstractByteReader {

    /**
     * Creates a new instance of FileByteReader
     * <p/>
     * <p>This constructor can set the <code>IdentificationFile</code> to
     * a different file than the actual file used. For example, if <code>theIDFile</code>
     * is a URL or stream, and is too big to be buffered in memory, it could be written
     * to a temporary file.  This file would then be used as a backing file to store
     * the data.
     *
     * @param theIDFile the file represented by this object
     * @param readFile  <code>true</code> if the file is to be read
     * @param filePath  the backing file (containing the data)
     */
    FileByteReader(IdentificationFile theIDFile, boolean readFile, String filePath) {
        super(theIDFile);
        this.file = new File(filePath);
        if (readFile) {
            this.readFile();
        }

    }

    /**
     * Creates a new instance of FileByteReader
     * <p/>
     * <p>This constructor uses the same file to contain the data as is specified by
     * <code>theIDFile</code>.
     *
     * @param theIDFile the source file from which the bytes will be read.
     * @param readFile  <code>true</code> if the file is to be read
     */
    FileByteReader(IdentificationFile theIDFile, boolean readFile) {
        this(theIDFile, readFile, theIDFile.getFilePath());
    }

    private int randomFileBufferSize = AnalysisController.FILE_BUFFER_SIZE;
    private boolean isRandomAccess = false;


    protected byte[] fileBytes;
    private long myNumBytes;
    private long fileMarker;

    private RandomAccessFile myRandomAccessFile;
    private long myRAFoffset = 0L;

    private static final int MIN_RAF_BUFFER_SIZE = 1000000;
    private static final int RAF_BUFFER_REDUCTION_FACTOR = 2;
    private File file;


    public boolean isRandomAccess() {
        return isRandomAccess;
    }


    public int getRandomFileBufferSize() {
        return randomFileBufferSize;
    }


    public RandomAccessFile getMyRandomAccessFile() {
        return myRandomAccessFile;
    }

    /**
     * Reads in the binary file specified.
     * <p/>
     * <p>If there are any problems reading in the file, it gets classified as unidentified,
     * with an explanatory warning message.
     */
    private void readFile() {

        //If file is not readable or is empty, then it gets classified
        //as unidentified (with an explanatory warning)

        if (!file.exists()) {
            this.setErrorIdent();
            this.setIdentificationWarning("File does not exist");
            return;
        }

        if (!file.canRead()) {
            this.setErrorIdent();
            this.setIdentificationWarning("File cannot be read");
            return;
        }

        if (file.isDirectory()) {
            this.setErrorIdent();
            this.setIdentificationWarning("This is a directory, not a file");
            return;
        }

        FileInputStream binStream;
        try {
            binStream = new FileInputStream(file);
        } catch (FileNotFoundException ex) {
            this.setErrorIdent();
            this.setIdentificationWarning("File disappeared or cannot be read");
            return;
        }

        try {

            int numBytes = binStream.available();

            if (numBytes > 0) {
                BufferedInputStream buffStream = new BufferedInputStream(binStream);

                fileBytes = new byte[numBytes];
                int len = buffStream.read(fileBytes, 0, numBytes);

                if (len != numBytes) {
                    //This means that all bytes were not successfully read
                    this.setErrorIdent();
                    this.setIdentificationWarning("Error reading file: " + Integer.toString(len) + " bytes read from file when " + Integer.toString(numBytes) + " were expected");
                } else if (buffStream.read() != -1) {
                    //This means that the end of the file was not reached
                    this.setErrorIdent();
                    this.setIdentificationWarning("Error reading file: Unable to read to the end");
                } else {
                    myNumBytes = (long) numBytes;
                }

                buffStream.close();
            } else {
                //If file is empty , status is error
                //this.setErrorIdent();
                myNumBytes = 0L;
                this.setIdentificationWarning("Zero-length file");

            }
            binStream.close();

            isRandomAccess = false;
        } catch (IOException e) {
            this.setErrorIdent();
            this.setIdentificationWarning("Error reading file: " + e.toString());
        } catch (OutOfMemoryError e) {
            try {
                myRandomAccessFile = new RandomAccessFile(file, "r");
                isRandomAccess = true;

                //record the file size
                myNumBytes = myRandomAccessFile.length();
                //try reading in a buffer
                myRandomAccessFile.seek(0L);
                boolean tryAgain = true;
                while (tryAgain) {
                    try {
                        fileBytes = new byte[(int) randomFileBufferSize];
                        myRandomAccessFile.read(fileBytes);
                        tryAgain = false;
                    } catch (OutOfMemoryError e4) {
                        randomFileBufferSize = randomFileBufferSize / RAF_BUFFER_REDUCTION_FACTOR;
                        if (randomFileBufferSize < MIN_RAF_BUFFER_SIZE) {
                            throw e4;
                        }

                    }
                }

                myRAFoffset = 0L;
            } catch (FileNotFoundException e2) {
                this.setErrorIdent();
                this.setIdentificationWarning("File disappeared or cannot be read");
            } catch (Exception e2) {
                try {
                    myRandomAccessFile.close();
                } catch (IOException e3) {

                }
                this.setErrorIdent();
                this.setIdentificationWarning("Error reading file: " + e2.toString());
            }

        }
    }

    /**
     * Position the file marker at a given byte position.
     * <p/>
     * <p>The file marker is used to record how far through the file
     * the byte sequence matching algorithm has got.
     *
     * @param markerPosition The byte number in the file at which to position the marker
     */
    public void setFileMarker(long markerPosition) {
        if ((markerPosition < -1L) || (markerPosition > this.getNumBytes())) {
            throw new IllegalArgumentException("  Unable to place a fileMarker at byte "
                    + Long.toString(markerPosition) + " in file " + this.myIDFile.getFilePath() + " (size = " + Long.toString(this.getNumBytes()) + " bytes)");
        } else {
            this.fileMarker = markerPosition;
        }
    }

    /**
     * Gets the current position of the file marker.
     *
     * @return the current position of the file marker
     */
    public long getFileMarker() {
        return this.fileMarker;
    }

    /**
     * Get a byte from file
     *
     * @param fileIndex position of required byte in the file
     * @return the byte at position <code>fileIndex</code> in the file
     */
    public byte getByte(long fileIndex) {

        byte theByte = 0;
        if (isRandomAccess) {
            //If the file is being read via random acces,
            //then read byte from buffer, otherwise read in a new buffer.
            long theArrayIndex = fileIndex - myRAFoffset;
            if (fileIndex >= myRAFoffset && theArrayIndex < randomFileBufferSize) {
                theByte = fileBytes[(int) (theArrayIndex)];
            } else {
                try {
                    //Create a new buffer:
                    /*
                    //When a new buffer is created, the requesting file position is
                    //taken to be the middle of the buffer.  This is so that it will
                    //perform equally well whether the file is being examined from
                    //start to end or from end to start
                    myRAFoffset = fileIndex - (myRAFbuffer/2);
                    if(myRAFoffset<0L) {
                        myRAFoffset = 0L;
                    }
                    System.out.println("    re-read file buffer");
                    myRandomAccessFile.seek(myRAFoffset);
                    myRandomAccessFile.read(fileBytes);
                    theByte = fileBytes[(int)(fileIndex-myRAFoffset)];
                     */
                    if (fileIndex < randomFileBufferSize) {
                        myRAFoffset = 0L;
                    } else if (fileIndex < myRAFoffset) {
                        myRAFoffset = fileIndex - randomFileBufferSize + 1;
                    } else {
                        myRAFoffset = fileIndex;
                    }
                    //System.out.println("    re-read file buffer from "+myRAFoffset+ " for "+myRAFbuffer+" bytes");
                    //System.out.println("    seek start");
                    myRandomAccessFile.seek(myRAFoffset);
                    //System.out.println("        read start");
                    myRandomAccessFile.read(fileBytes);
                    //System.out.println(fileIndex);

                    //System.out.println("            read end");
                    theByte = fileBytes[(int) (fileIndex - myRAFoffset)];

                } catch (Exception e) {
                }
            }
        } else {
            //If the file is not being read by random access, then the byte should be in the buffer array
            theByte = fileBytes[(int) fileIndex];
        }
        return theByte;
    }


    /**
     * Returns the number of bytes in the file
     */
    public long getNumBytes() {
        return myNumBytes;
    }

    public byte[] getbuffer() {
        return fileBytes;
    }
}
