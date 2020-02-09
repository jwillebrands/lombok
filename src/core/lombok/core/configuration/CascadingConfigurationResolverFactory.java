package lombok.core.configuration;

import java.net.URI;
import java.util.Iterator;

import lombok.core.configuration.resolution.ConfigurationResolutionSpecification;
import lombok.core.configuration.resolution.ConfigurationSourceIteratorFactory;

public class CascadingConfigurationResolverFactory implements ConfigurationResolverFactory {
	private final ConfigurationSourceIteratorFactory iteratorFactory;
	private final FileSystemSourceCache fileSystemSourceCache;
	private final ConfigurationFileToSource fileToSource;

	public CascadingConfigurationResolverFactory(ConfigurationResolutionSpecification specification, FileSystemSourceCache fileSystemSourceCache, ConfigurationParser parser) {
		this.iteratorFactory = specification.getIteratorFactory();
		this.fileSystemSourceCache = fileSystemSourceCache;
		this.fileToSource = fileSystemSourceCache.fileToSource(parser);
	}

	public CascadingConfigurationResolverFactory(ConfigurationResolutionSpecification specification, FileSystemSourceCache fileSystemSourceCache) {
		this(specification, fileSystemSourceCache, new ConfigurationParser(ConfigurationProblemReporter.CONSOLE));
	}

	@Override public ConfigurationResolver createResolver(final URI sourceLocation) {
		return new CascadingConfigurationResolver(new Iterable<ConfigurationSource>() {
			@Override public Iterator<ConfigurationSource> iterator() {
				return iteratorFactory.iterateConfigurationSources(fileSystemSourceCache.forUri(sourceLocation), fileToSource);
			}
		});
	}
}
