/**
 *
 */
package eu.planets_project.services.validate;

import eu.planets_project.services.datatypes.Property;
import eu.planets_project.services.datatypes.ServiceReport;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

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
     * The list of errors collected during the validation.
     * Errors are expressed via the contained class Message. A message consist of
     * two strings, adress and description. For this interface, the adress is
     * meant to be a line number.
     * <br>
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
     * @see eu.planets_project.services.validate.ValidateResult.Message
     */
    public List<Message> errors;

    /**
     * The list of warning collected during the validation.  A message consist of
     * two strings, adress and description. For this interface, the adress is
     * meant to be a line number.
     * </pre>
     * If no line number can be found, or the warning is in regards to the
     * entire file, use -1.
     * <br>
     * These warnings are tool specific, and no further attempts have been made to
     * standardize them.
     * <br>
     * Warnings are problems with the file, that are not serious enough to
     * make validInRegardToThisFormat or ofThisFormat false.
     * @see eu.planets_project.services.validate.ValidateResult.Message
     */
    public List<Message> warnings;


    /**
     * No-args constructor required by JAXB
     */
    protected ValidateResult() {}

    private ValidateResult(Builder builder) {
        errors = builder.errors;
        warnings = builder.warnings;
        ofThisFormat = builder.ofThisFormat;
        validInRegardToThisFormat = builder.validInRegardToThisFormat;
        thisFormat = builder.thisFormat;
        report = builder.report;
        properties = builder.properties;
    }



    /**
     *
     * @return The collected errors in the file
     * @see #errors
     */
    public List<Message> getErrors() {
        return errors;
    }

    /**
     *
     * @return The collected warnings
     * @see #warnings
     */
    public List<Message> getWarnings() {
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

    /**
     * Messages about errors and warnings collected during the validation. A
     * message consist of two Strings, adress and description.
     *
     * 
     */
    public static final class Message {

        /**
         * The adress that contained the data that this message is about.
         * In files, this is just the line-number. 
         */
        private String adress;

        /**
         * The message.
         */
        private String description;

        /** Constructor for JAXB */
        @SuppressWarnings("unused")
        private Message() {
        }

        /**
         * Constructor.
         * @param adress The adress of what provoked the message
         * @param description The message
         */
        public Message(String adress, String description) {
            this.adress = adress;
            this.description = description;
        }

        /**
         * Construtor.
         * @param description The message
         */
        public Message(String description) {
            this.adress = "";
            this.description = description;
        }


        public String getAdress() {
            return adress;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "Message{" +
                    "adress='" + adress + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }


    public static final class Builder {

        private boolean ofThisFormat;

        private boolean validInRegardToThisFormat;

        private URI thisFormat;

        private ServiceReport report;



        public List<Property> properties;

        public List<Message> errors;

        public List<Message> warnings;



        public Builder(final ValidateResult validateResult) {
            initialize(validateResult);
        }

        public Builder(URI thisFormat, ServiceReport report) {
            init();
            this.thisFormat = thisFormat;
            this.report = report;
        }

        public ValidateResult build(){
            return new ValidateResult(this);
        }

        private void init(){
            ofThisFormat = true;
            validInRegardToThisFormat = true;
            properties = new ArrayList<Property>();
            errors = new ArrayList<Message>();
            warnings = new ArrayList<Message>();
        }

        private void initialize(final ValidateResult validateResult){
            if (validateResult == null){
                init();
                return;
            }
            else {
                ofThisFormat = validateResult.ofThisFormat;
                validInRegardToThisFormat = validateResult.validInRegardToThisFormat;
                thisFormat = validateResult.thisFormat;
                report = validateResult.report;
                properties = validateResult.properties;
                errors = validateResult.errors;
                warnings = validateResult.warnings;
            }
        }

        public Builder ofThisFormat(boolean ofThisFormat){
            this.ofThisFormat = ofThisFormat;
            return this;
        }

        public Builder validInRegardToThisFormat(boolean validInRegardToThisFormat){
            this.validInRegardToThisFormat = validInRegardToThisFormat;
            return this;
        }

        public Builder thisFormat(URI thisFormat){
            this.thisFormat = thisFormat;
            return this;
        }

        public Builder report(ServiceReport report){
            this.report = report;
            return this;
        }

        public Builder properties(List<Property> properties){
            this.properties = properties;
            return this;
        }

        /**
         * Adds a warning for a specified line number in the file
         * @param linenumber The offending line
         * @param warning The warning description
         * @see #warnings
         */
        public Builder addWarning(int linenumber, String warning){
            warnings.add(new Message("line "+linenumber,warning));
            return this;
        }

        /**
         * Adds a warning for line number -1, ie. for the entire file
         * @param warning the description of the warning
         * @see #warnings
         */
        public Builder addWarning(String warning){
            warnings.add(new Message("line -1",warning));
            return this;
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
        public Builder addError(int linenumber, String error){
            errors.add(new Message("line "+linenumber, error));
            return this;
        }


        /**
         * Adds a error for line number -1, ie. for the entire file
         * @param error the description of the error
         * @see #addError(int, String)
         */
        public Builder addError(String error){
            errors.add(new Message("line -1",error));
            return this;
        }

    }
}
