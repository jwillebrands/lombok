/*
 * Copyright (C) 2020 The Project Lombok Authors.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package lombok.core.configuration.resolution;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import lombok.core.configuration.ConfigurationFile;

public class ResolutionSpecificationParser {
	public ConfigurationResolutionStrategy parseSpecification(String specification) {
		specification = specification == null ? "" : specification.trim();
		if (specification.trim().isEmpty()) {
			return null;
		}
		String[] configValue = specification.split(":", 2);
		if (configValue.length < 2) {
			return null;
		}
		String type = configValue[0];
		if ("file".equalsIgnoreCase(type)) {
			return new StaticFileResolutionStrategy(configValue[1], new ConfigurationFileFactory() {
				@Override public ConfigurationFile configrationFileFromLocation(String location) {
					return ConfigurationFile.forFile(new File(location));
				}
			});
		} else if ("bubbling".equalsIgnoreCase(type)) {
			return new FileSystemBubblingResolutionStrategy();
		} else if ("classpath".equalsIgnoreCase(type)) {
			return new StaticFileResolutionStrategy(configValue[1], new ConfigurationFileFactory() {
				@Override public ConfigurationFile configrationFileFromLocation(String location) {
					return ConfigurationFile.forClasspathResource(location);
				}
			});
		}
		return null;
	}
	
	public ConfigurationResolutionStrategy parseResolutionSpecification(String... specifications) {
		return parseResolutionSpecification(Arrays.asList(specifications));
	}

	public ConfigurationResolutionStrategy parseResolutionSpecification(Collection<String> specifications) {
		List<ConfigurationResolutionStrategy> strategies = new ArrayList<ConfigurationResolutionStrategy>();
		for (String resolutionSpec : specifications) {
			ConfigurationResolutionStrategy strategy = parseSpecification(resolutionSpec);
			if (strategy != null) {
				strategies.add(strategy);
			}
		}
		return new CompositeResolutionStrategy(strategies);
	}
}
