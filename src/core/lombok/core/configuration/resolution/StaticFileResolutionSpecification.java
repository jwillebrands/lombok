package lombok.core.configuration.resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;

class StaticFileResolutionSpecification extends ConfigurationResolutionSpecification {
	private final List<ConfigurationFile> configurationFiles;

	public StaticFileResolutionSpecification(String parameters, ConfigurationFileFactory fileFactory) {
		String[] params = parameters.split(",");
		List<ConfigurationFile> files = new ArrayList<ConfigurationFile>(params.length);
		for (String param : params) {
			String trimmedParam = param.trim();
			if (!trimmedParam.isEmpty()) {
				files.add(fileFactory.configrationFileFromLocation(trimmedParam));
			}
		}
		this.configurationFiles = Collections.unmodifiableList(files);
	}

	@Override public ConfigurationSourceIteratorFactory getIteratorFactory() {
		return new ConfigurationSourceIteratorFactory() {
			@Override public Iterator<ConfigurationSource> iterateConfigurationSources(ConfigurationFile forUri, ConfigurationFileToSource fileToSource) {
				return ConfigurationSourceIterators.forStaticFiles(configurationFiles, fileToSource);
			}
		};
	}
}
