package eu.planets_project.services.shotgun;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.io.FileUtils;

/**
 * Java re-implementation of the C++ shotgun file modification tool by Manfred
 * Thaller (http://www.hki.uni-koeln.de/material/shotGun).
 * @author Fabian Steeg (fabian.steeg@uni-koeln.de)
 */
public final class FileShotgun {

    /**
     * Modifies the given file by picking seqCount random sequences, each of
     * seqLength length and applies the given action to it.
     * @param file The file to modify
     * @param seqCount The number of sequences to modify
     * @param seqLength The length of each sequence to modify
     * @param action The modification action
     * @return The modified file
     */
    public File shoot(File file, int seqCount, int seqLength, Action action) {
        List<Integer> startOffsets = randomStartOffsets(file, seqCount,
                seqLength);
        for (Integer startOffset : startOffsets) {
            try {
                file = action.modify(file, startOffset, seqLength);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return file;
    }

    private static final Random RANDOM = new Random();

    /**
     * Possible shotgun modification actions applied to the bytes.
     */
    public static enum Action {
        CORRUPT {
            /**
             * {@inheritDoc}
             * @throws IOException 
             * @see eu.planets_project.services.shotgun.FileShotgun.Action#modify(java.io.File,
             *      long, int)
             */
            File modify(File file, long offset, int length) throws IOException {
                RandomAccessFile raf = null;
                try {
                    raf = new RandomAccessFile(file, "rwd");
                    raf.seek(offset);
                    byte[] nullBytes = new byte[length];
                    raf.write(nullBytes);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    raf.close();
                }
                return file;
            }
        },
        DROP {
            /**
             * {@inheritDoc}
             * @throws IOException 
             * @see eu.planets_project.services.shotgun.FileShotgun.Action#modify(java.io.File,
             *      long, int)
             */
            File modify(File file, long offset, int length) throws IOException {
                byte[] input = FileUtils.readFileToByteArray(file);
                byte[] result = new byte[input.length - length];
                for (int i = 0, j = 0; i < input.length && j < result.length; i++, j++) {
                    if (i < offset || i > offset + length) {
                        result[j] = result[i];
                    }
                }
                File resultFile = File.createTempFile("shotgun", null);
                FileUtils.writeByteArrayToFile(resultFile, result);
                return resultFile;
            }
        };
        /**
         * @param file The file to modify
         * @param offset The offset to start modifying
         * @param length The number of bytes to modify starting from the given
         *        offset
         * @return The modified file
         */
        abstract File modify(File file, long offset, int length) throws IOException;
    }

    /** Shotgun configuration keys. */
    public static enum Key {
        SEQ_COUNT, SEQ_LENGTH, ACTION
    }

    private List<Integer> randomStartOffsets(File file, int seqCount,
            int seqLength) {
        List<Integer> result = new ArrayList<Integer>();
        for (int i = 0; i < seqCount; i++) {
            result.add(randomStartOffset(file, seqLength));
        }
        return result;
    }

    private Integer randomStartOffset(File file, int seqLength) {
        /* TODO we should make sure here that we have no overlap */
        return RANDOM.nextInt((int) file.length() - seqLength);
    }

}
