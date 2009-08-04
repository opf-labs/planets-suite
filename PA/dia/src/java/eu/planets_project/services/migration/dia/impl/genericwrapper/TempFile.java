package eu.planets_project.services.migration.dia.impl.genericwrapper;

import java.io.File;

/**
 * TODO abr forgot to document this class
 */
public class TempFile {

    private String codename;

    private String requestedName = "";

    private File file = null;

    public TempFile(String codename) {
        this.codename = codename;
    }

    public void setRequestedName(String requestedName) {
        this.requestedName = requestedName;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getCodename() {
        return codename;
    }

    public String getRequestedName() {
        return requestedName;
    }

    public File getFile() {
        return file;
    }


}
