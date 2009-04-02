/**
 *
 */
package eu.planets_project.services.validate;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import eu.planets_project.services.datatypes.ServiceReport;
import eu.planets_project.services.datatypes.Property;

import java.net.URI;
import java.util.List;
import java.util.ArrayList;

/**
 * @author <a href="mailto:Andrew.Jackson@bl.uk">Andy Jackson</a>,
 * <a href="mailto:abr@statsbiblioteket.dk">Asger Blekinge-Rasmussen</a>,
 *
 */
@XmlRootElement
@XmlAccessorType(value = XmlAccessType.FIELD)
public class ValidateResult {




    /**
     * Validity is a two-step process. First, the file is regarded, to see if it
     * can be parsed as the given format. If it can be thus parsed, ofThisFormat
     * is set to true. If not, false. False means that the file is not really
     * of the specified format.
     *<br/>
     * False correspond to the JHove term of INVALID, which were also used
     * in Planets earlier on.
     * <br>
     * Default true;
     */
    private boolean ofThisFormat;

    /**
     * Only relevant if ofThisFormat is true. When the file have been parsed
     * as of a specific format, errors in regards to that format will be found.
     * <br>
     * A pdf file could be parsed as pdf, but if it lacks certain nessesary
     * datastructures it is not a valid pdf file, but still a pdf file. In that
     * case ofThisFormat would be set to true, but validInRegardsToThisFormat
     * to false.
     * <br/>
     * True corresponds the the JHove term of VALID, which were also used in
     * Planets earlier. False corresponds to WELL_FORMED.
     * <br>
     * Default true;
     * @see #ofThisFormat
     */
    private boolean validInRegardToThisFormat;

    /**
     * The format that the file was validated against.
     */
    private URI thisFormat;
    /**
     *
     */
    private ServiceReport report;


    /** Also allow properties to be returned, to permit extensible behaviour. */
    public List<Property> properties;


    /**
     * The list of errors collected during the validation. Errors should be
     * of the form <pre>
     *  line x: Error Description
     * </pre>
     * If no line number can be found, or the error is in regards to the
     * entire file, use -1.
     * <br>
     * These errors are tool specific, and no further attempts have been made to
     * standardize them.
     * <br>
     * Errors are problems with the file, that cause either ofThisFormat or
     * validInRegardsToThisFormat to be false.
     * @see #ofThisFormat
     * @see #validInRegardToThisFormat
     * @see #warnings
     */
    public List<String> errors;

    /**
     * The list of warning collected during the validation. Warning should be
     * of the form <pre>
     *  line x: warning Description
     * </pre>
     * If no line number can be found, or the warning is in regards to the
     * entire file, use -1.
     * <br>
     * These warnings are tool specific, and no further attempts have been made to
     * standardize them.
     * <br>
     * Warnings are problems with the file, that are not serious enough to
     * make validInRegardToThisFormat or ofThisFormat false.
     */
    public List<String> warnings;


    /**
     * No-args constructor required by JAXB
     */
    protected ValidateResult() {}


    /**
     * Constructor method.
     * @param thisFormat
     *           The format to validate against
     * @param report
     *           The service report
     *
     * @see #thisFormat
     */
    public ValidateResult(
            URI thisFormat,
            ServiceReport report,
            List<Property> properties) {
        errors = new ArrayList<String>();
        warnings = new ArrayList<String>();
        ofThisFormat = true;
        validInRegardToThisFormat = true;
        this.thisFormat = thisFormat;
        this.report = report;
        this.properties = properties;
    }


    /**
     * Adds a warning for a specified line number in the file
     * @param linenumber The offending line
     * @param warning The warning description
     * @see #warnings
     */
    public void addWarning(int linenumber, String warning){
        warnings.add("line "+linenumber+": "+warning);
    }

    /**
     * Adds a warning for line number -1, ie. for the entire file
     * @param warning the description of the warning
     * @see #warnings
     */
    public void addWarning(String warning){
        warnings.add("line -1: "+warning);
    }

    /**
     * Adds a error for a specified line number in the file. Remember
     * to also mark the file as not ofThisFormat or not
     * validInRegardToThisFormat
     * @param linenumber the offending line
     * @param error The error description
     * @see #errors
     * @see #ofThisFormat
     * @see #validInRegardToThisFormat
     */
    public void addError(int linenumber, String error){
        errors.add("line "+linenumber+": "+error);
    }


    /**
     * Adds a error for line number -1, ie. for the entire file
     * @param error the description of the error
     * @see #addError(int, String)
     */
    public void addError(String error){
        errors.add("line -1: "+error);
    }

    /**
     *
     * @param ofThisFormat specifies whether or not the parser could
     * parse the file
     * @see #ofThisFormat
     */
    public void setOfThisFormat(boolean ofThisFormat) {
        this.ofThisFormat = ofThisFormat;
        if (!ofThisFormat){
            validInRegardToThisFormat = false;
        }
    }

    /**
     *
     * @param validInRegardToThisFormat Specifies whether or not
     * there were any errors in regards to the format
     * @see #validInRegardToThisFormat
     */
    public void setValidInRegardToThisFormat(boolean validInRegardToThisFormat) {
        this.validInRegardToThisFormat = validInRegardToThisFormat;
    }

    /**
     *
     * @return The collected errors in the file
     * @see #errors
     */
    public List<String> getErrors() {
        return errors;
    }

    /**
     *
     * @return The collected warnings
     * @see #warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * @return the report
     */
    public ServiceReport getReport() {
        return report;
    }

    /**
     * @see #ofThisFormat
     * @return whether or not the file was of this format
     */
    public boolean isOfThisFormat() {
        return ofThisFormat;
    }

    /**
     * @see #validInRegardToThisFormat
     * @return whether or not the file was valid in regards to this format
     */
    public boolean isValidInRegardToThisFormat() {
        return validInRegardToThisFormat;
    }

    /**
     * @see #thisFormat
     * @return This format
     */
    public URI getThisFormat() {
        return thisFormat;
    }

    public List<Property> getProperties() {
        return properties;
    }
}
