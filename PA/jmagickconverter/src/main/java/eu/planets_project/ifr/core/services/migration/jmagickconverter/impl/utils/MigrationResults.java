package eu.planets_project.ifr.core.services.migration.jmagickconverter.impl.utils;

/**
 * The purpose of this class is to store the results of a migration process
 * carried out by the GeneralImageConverter. It contains: 1) byte[] holding the
 * migrated image, 2) an optional message 3) the success flag. 
 *
 *  @author : Peter Melms
 *  Email  : peter.melms@uni-koeln.de
 *  Created : 27.05.2008
 *
 */
public class MigrationResults {
    // The result of the migration as byte[]
    private byte[] resultArray = null;
    // an optional message
    private String message = null;
    // a flag indicating success or failure.
    private boolean success = false;

    /**
     * @return Returns the contained byte[]
     */
    public byte[] getByteArray() {
	return this.resultArray;
    }

    /**
     * @param toSet gets the migrated image as byte[] toSet and stores it in
     *                this MigrationResults object.
     */
    public void setByteArray(byte[] toSet) {
	this.resultArray = toSet;
    }

    /**
     * @return the (optional) message as String, informing on success or
     *         warnings which occurred during the migration process.
     */
    public String getMessage() {
	return this.message;
    }

    /**
     * @param messageToSet sets the message String
     */
    public void setMessage(String messageToSet) {
	this.message = messageToSet;
    }

    /**
     * @return Returns a boolean indicating Success or failure of the migration
     *         process.
     */
    public boolean migrationWasSuccessful() {
	return this.success;
    }

    /**
     * @param wasSuccessful gets a boolean indicating success or not.
     */
    public void setMigrationSuccess(boolean wasSuccessful) {
	this.success = wasSuccessful;
    }
}
