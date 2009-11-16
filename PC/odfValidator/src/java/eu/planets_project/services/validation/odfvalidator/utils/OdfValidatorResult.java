package eu.planets_project.services.validation.odfvalidator.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author melmsp
 *
 */
public class OdfValidatorResult {
	
	private String contentErrors = null;
	private boolean contentValid = false;
	private String stylesErrors = null;
	private boolean stylesValid = false;
	private String metaErrors = null;
	private boolean metaValid = false;
	private String settingsErrors = null;
	private boolean settingsValid = false;
	private String manifestErrors = null;
	private boolean manifestValid;
	
	private static String CONTENT_XML = "content.xml";
	private static String STYLES_XML = "styles.xml";
	private static String META_XML = "meta.xml";
	private static String SETTINGS_XML = "settings.xml";
	private static String MANIFEST_XML = "manifest.xml";
	
	private boolean usedStrictValidation = false;
	
	private String odfVersion = "unknown";
	
	public String getOdfVersion() {
		return odfVersion;
	}

	public void setOdfVersion(String odfVersion) {
		this.odfVersion = odfVersion;
	}

	public boolean usedStrictValidation() {
		return usedStrictValidation;
	}

	public void setUsedStrictValidation(boolean usedStrictValidation) {
		this.usedStrictValidation = usedStrictValidation;
	}

	public void setValid(String odfPartName, boolean valid) {
		if(odfPartName.equalsIgnoreCase(CONTENT_XML)) {
			setContentValid(valid);
		}
		if(odfPartName.equalsIgnoreCase(STYLES_XML)) {
			setStylesValid(valid);
		}
		if(odfPartName.equalsIgnoreCase(SETTINGS_XML)) {
			setSettingsValid(valid);
		}
		if(odfPartName.equalsIgnoreCase(META_XML)) {
			setMetaValid(valid);
		}
		if(odfPartName.equalsIgnoreCase(MANIFEST_XML)) {
			setManifestValid(valid);
		}
	}
	
	public void setError(String odfPartName, String error) {
		if(odfPartName.equalsIgnoreCase(CONTENT_XML)) {
			setContentErrors(error);
		}
		if(odfPartName.equalsIgnoreCase(STYLES_XML)) {
			setStylesErrors(error);
		}
		if(odfPartName.equalsIgnoreCase(SETTINGS_XML)) {
			setSettingsErrors(error);
		}
		if(odfPartName.equalsIgnoreCase(META_XML)) {
			setMetaErrors(error);
		}
		if(odfPartName.equalsIgnoreCase(MANIFEST_XML)) {
			setManifestErrors(error);
		}
	}

	public boolean isOdfFile() {
		if(odfVersion.equalsIgnoreCase("unknown")) {
			return false;
		}
		else {
			return true;
		}
	}
	
	public boolean documentIsValid() {
		if(isContentValid() 
				&& isMetaValid() 
				&& isStylesValid() 
				&& isSettingsValid() 
				&& isManifestValid()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean isValid(String odfPartName) {
		if(odfPartName.equalsIgnoreCase(CONTENT_XML)) {
			return isContentValid();
		}
		if(odfPartName.equalsIgnoreCase(STYLES_XML)) {
			return isStylesValid();
		}
		if(odfPartName.equalsIgnoreCase(SETTINGS_XML)) {
			return isSettingsValid();
		}
		if(odfPartName.equalsIgnoreCase(META_XML)) {
			return isMetaValid();
		}
		if(odfPartName.equalsIgnoreCase(MANIFEST_XML)) {
			return isManifestValid();
		}
		return false;
	}
	
	/* ------------------------------------------------------------------------
	 * Get the error messages for different ODF components, 
	 * (content.xml, meta.xml, styles.xml, settings.xml, manifest.xml)
	 * ------------------------------------------------------------------------ 
	 */
	
	public String getContentErrors() {
		return contentErrors;
	}

	public String getStylesErrors() {
		return stylesErrors;
	}

	public String getMetaErrors() {
		return metaErrors;
	}

	public String getSettingsErrors() {
		return settingsErrors;
	}

	public String getManifestErrors() {
		return manifestErrors;
	}

	@Override
	public String toString() {
		return "OdfValidatorResult ["
				+ (odfVersion != null ? "odfVersion=" + odfVersion + ", " : "")
				+ "usedStrictValidation()=" + usedStrictValidation() 
				+ ", documentIsValid()=" + documentIsValid()
				+ ", isOdfFile()=" + isOdfFile() 
				+ ", isContentValid()=" + isContentValid() 
				+ ", isManifestValid()=" + isManifestValid()
				+ ", isMetaValid()=" + isMetaValid() 
				+ ", isSettingsValid()=" + isSettingsValid()
				+ ", isStylesValid()=" + isStylesValid()
				+ (getContentErrors() != null ? "getContentErrors()="
						+ getContentErrors() + ", " : "")
				+ (getManifestErrors() != null ? "getManifestErrors()="
						+ getManifestErrors() + ", " : "")
				+ (getMetaErrors() != null ? "getMetaErrors()="
						+ getMetaErrors() + ", " : "")
				+ (getSettingsErrors() != null ? "getSettingsErrors()="
						+ getSettingsErrors() + ", " : "")
				+ (getStylesErrors() != null ? "getStylesErrors()="
						+ getStylesErrors() + ", " : "" + "]");
				
	}

	private boolean isContentValid() {
		return contentValid;
	}

	private boolean isStylesValid() {
		return stylesValid;
	}

	private boolean isMetaValid() {
		return metaValid;
	}

	private boolean isSettingsValid() {
		return settingsValid;
	}

	private boolean isManifestValid() {
		return manifestValid;
	}
	
	
	/* ------------------------------------------------------------------------
	 * Get the error messages for different ODF components, 
	 * (content.xml, meta.xml, styles.xml, settings.xml, manifest.xml)
	 * ------------------------------------------------------------------------ 
	 */
	
	private void setContentValid(boolean contentValid) {
		this.contentValid = contentValid;
	}

	private void setStylesValid(boolean stylesValid) {
		this.stylesValid = stylesValid;
	}

	private void setMetaValid(boolean metaValid) {
		this.metaValid = metaValid;
	}

	private void setSettingsValid(boolean settingsValid) {
		this.settingsValid = settingsValid;
	}

	private void setManifestValid(boolean manifestValid) {
		this.manifestValid = manifestValid;
	}

	private void setContentErrors(String contentErrors) {
		this.contentErrors = contentErrors;
	}

	private void setStylesErrors(String stylesErrors) {
		this.stylesErrors = stylesErrors;
	}

	private void setMetaErrors(String metaErrors) {
		this.metaErrors = metaErrors;
	}

	private void setSettingsErrors(String settingsErrors) {
		this.settingsErrors = settingsErrors;
	}

	private void setManifestErrors(String manifestErrors) {
		this.manifestErrors = manifestErrors;
	}
}
