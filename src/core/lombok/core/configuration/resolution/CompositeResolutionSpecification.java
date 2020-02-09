package lombok.core.configuration.resolution;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;

class CompositeResolutionSpecification extends ConfigurationResolutionSpecification {
	private final List<ConfigurationResolutionSpecification> specifications;

	public CompositeResolutionSpecification(List<ConfigurationResolutionSpecification> specifications) {
		this.specifications = specifications;
	}

	@Override public ConfigurationSourceIteratorFactory getIteratorFactory() {
		return new ConfigurationSourceIteratorFactory() {
			@Override public Iterator<ConfigurationSource> iterateConfigurationSources(ConfigurationFile forUri, ConfigurationFileToSource fileToSource) {
				List<Iterator<ConfigurationSource>> nestedIterators = new ArrayList<Iterator<ConfigurationSource>>(specifications.size());
				for (ConfigurationResolutionSpecification specification : specifications) {
					nestedIterators.add(specification.getIteratorFactory().iterateConfigurationSources(forUri, fileToSource));
				}
				return ConfigurationSourceIterators.iterateInOrder(nestedIterators);
			}
		};
	}
}
