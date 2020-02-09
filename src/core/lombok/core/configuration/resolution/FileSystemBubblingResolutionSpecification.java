package lombok.core.configuration.resolution;

import java.util.Iterator;

import lombok.core.configuration.BubblingConfigurationSourceIterator;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;

class FileSystemBubblingResolutionSpecification extends ConfigurationResolutionSpecification {
	@Override public ConfigurationSourceIteratorFactory getIteratorFactory() {
		return new ConfigurationSourceIteratorFactory() {
			@Override public Iterator<ConfigurationSource> iterateConfigurationSources(ConfigurationFile forUri, ConfigurationFileToSource fileToSource) {
				return new BubblingConfigurationSourceIterator(forUri, fileToSource);
			}
		};
	}
}
