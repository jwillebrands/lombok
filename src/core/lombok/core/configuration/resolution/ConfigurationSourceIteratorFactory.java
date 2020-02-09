package lombok.core.configuration.resolution;

import java.util.Iterator;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;

public interface ConfigurationSourceIteratorFactory {
	Iterator<ConfigurationSource> iterateConfigurationSources(ConfigurationFile forUri, ConfigurationFileToSource fileToSource);
}
