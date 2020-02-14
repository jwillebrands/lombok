/*
 * Copyright (C) 2014-2020 The Project Lombok Authors.
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

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import lombok.ConfigurationKeys;
import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;
import lombok.core.configuration.ConfigurationSource.Result;


public class BubblingConfigurationSourceIterator implements Iterator<ConfigurationSource> {
	private final ConfigurationFileToSource fileMapper;
	private ConfigurationFile currentLevel;

	private boolean stopBubbling = false;
	private Deque<ConfigurationFile> filesToProcess = new ArrayDeque<ConfigurationFile>();
	private Set<ConfigurationFile> visited = new HashSet<ConfigurationFile>();


	public BubblingConfigurationSourceIterator(ConfigurationFile start, ConfigurationFileToSource fileMapper) {
		this.fileMapper = fileMapper;
		this.enqueueFile(start, false);
	}

	public boolean hasNext() {
		return !filesToProcess.isEmpty();
	}

	public ConfigurationSource next() {
		if (!hasNext()) {
			throw new NoSuchElementException();
		}
		ConfigurationFile currentFile = filesToProcess.pop();
		ConfigurationSource source = fileMapper.parsed(currentFile);

		enqueueImports(source);

		Result stop = source.resolve(ConfigurationKeys.STOP_BUBBLING);
		stopBubbling = stopBubbling || (stop != null && Boolean.TRUE.equals(stop.getValue()));

		if (!stopBubbling && filesToProcess.isEmpty()) {
			enqueueFile(currentLevel.parent(), false);
		}

		return source;
	}

	@Override public void remove() {
		throw new UnsupportedOperationException();
	}

	private void enqueueFile(ConfigurationFile file, boolean isImport) {
		if (file != null && visited.add(file)) {
			filesToProcess.push(file);
			if (!isImport) {
				currentLevel = file;
			}
		}
	}

	private void enqueueImports(ConfigurationSource configurationSource) {
		for (ConfigurationFile importFile : configurationSource.imports()) {
			enqueueFile(importFile, true);
		}
	}
}
