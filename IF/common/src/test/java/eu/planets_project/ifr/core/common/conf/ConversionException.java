package eu.planets_project.ifr.core.common.conf;

public class ConversionException extends RuntimeException {
	private static final long serialVersionUID = 4168024286827885968L;

	public ConversionException() {
	}

	public ConversionException(String message) {
		super(message);
	}

	public ConversionException(Throwable cause) {
		super(cause);
	}

	public ConversionException(String message, Throwable cause) {
		super(message, cause);
	}

}
