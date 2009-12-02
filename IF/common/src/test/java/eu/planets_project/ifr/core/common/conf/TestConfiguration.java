package eu.planets_project.ifr.core.common.conf;

import java.net.URI;
import java.util.Iterator;
import java.util.Properties;

public class TestConfiguration implements Configuration {
	private Properties props;
	public TestConfiguration(Properties props) {
		this.props = props;
	}

	public int getInteger(String key) {
		throw new RuntimeException("Unimplemented");
	}

	public int getInteger(String key, int defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

	public Iterator getKeys() {
		return props.keySet().iterator();
	}

	public String getString(String key) {
		return props.getProperty(key);
	}

	public String getString(String key, String defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

	public URI getURI(String key) {
		throw new RuntimeException("Unimplemented");
	}

	public URI getURI(String key, URI defaultValue) {
		throw new RuntimeException("Unimplemented");
	}

}
