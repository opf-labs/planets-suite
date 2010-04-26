package eu.planets_project.ifr.core.services.migration.genericwrapper1;

import java.io.File;

/**
 * TODO abr forgot to document this class
 */
public class TempFile {

    private String codename;

    private String requestedName = "";

    private File file = null;

    /**
     * @param codename
     */
    public TempFile(String codename) {
        this.codename = codename;
    }

    /**
     * @param requestedName
     */
    public void setRequestedName(String requestedName) {
        this.requestedName = requestedName;
    }

    /**
     * @param file
     */
    public void setFile(File file) {
        this.file = file;
    }

    /**
     * @return the code name of the file
     */
    public String getCodename() {
        return this.codename;
    }

    /**
     * @return the requested name of the temp file
     */
    public String getRequestedName() {
        return this.requestedName;
    }

    /**
     * @return the java.io.File 
     */
    public File getFile() {
        return this.file;
    }


}
