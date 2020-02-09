package lombok.core.configuration.resolution;

import lombok.core.configuration.ConfigurationFile;

interface ConfigurationFileFactory {
	ConfigurationFile configrationFileFromLocation(String location);
}
