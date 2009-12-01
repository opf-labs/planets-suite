package eu.planets_project.ifr.core.common.conf;

public class ConfigurationException extends RuntimeException {
	private static final long serialVersionUID = -6240551757411773584L;

	public ConfigurationException() {
		super();
	}

	public ConfigurationException(String message) {
		super(message);
	}

	public ConfigurationException(Throwable cause) {
		super(cause);
	}

	public ConfigurationException(String message, Throwable cause) {
		super(message, cause);
	}

}
