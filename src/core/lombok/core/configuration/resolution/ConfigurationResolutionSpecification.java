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
import java.util.Arrays;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationValueType;

public abstract class ConfigurationResolutionSpecification implements ConfigurationValueType {
	public abstract ConfigurationSourceIteratorFactory getIteratorFactory();

	public static ConfigurationResolutionSpecification valueOf(String value) {
		value = value == null ? "" : value.trim();
		if (value.trim().isEmpty()) {
			return null;
		}
		String[] configValue = value.split(":", 2);
		if (configValue.length < 2) {
			return null;
		}
		String type = configValue[0];
		if ("file".equalsIgnoreCase(type)) {
			return new StaticFileResolutionSpecification(configValue[1], new ConfigurationFileFactory() {
				@Override public ConfigurationFile configrationFileFromLocation(String location) {
					return ConfigurationFile.forFile(new File(location));
				}
			});
		} else if ("bubbling".equalsIgnoreCase(type)) {
			return new FileSystemBubblingResolutionSpecification();
		} else if ("classpath".equalsIgnoreCase(type)) {
			return new StaticFileResolutionSpecification(configValue[1], new ConfigurationFileFactory() {
				@Override public ConfigurationFile configrationFileFromLocation(String location) {
					return ConfigurationFile.forClasspathResource(location);
				}
			});
		}
		return null;
	}

	public static String description() {
		return "Strategy for resolving applicable configuration files.";
	}

	public static String exampleValue() {
		return "bubbling:file";
	}

	public ConfigurationResolutionSpecification andThen(ConfigurationResolutionSpecification next) {
		return compositeResolutionSpecification(this, next);
	}

	public static ConfigurationResolutionSpecification compositeResolutionSpecification(ConfigurationResolutionSpecification... specifications) {
		return new CompositeResolutionSpecification(Arrays.asList(specifications));
	}

	public static ConfigurationResolutionSpecification bubbleFileSystem() {
		return new FileSystemBubblingResolutionSpecification();
	}
}