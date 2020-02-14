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

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Queue;

import lombok.core.configuration.ConfigurationFile;
import lombok.core.configuration.ConfigurationFileToSource;
import lombok.core.configuration.ConfigurationSource;

public class ConfigurationSourceIterators {
	static Iterator<ConfigurationSource> forStaticFiles(Collection<ConfigurationFile> configurationFiles, ConfigurationFileToSource fileToSource) {
		return new StaticFileIterator(configurationFiles, fileToSource);
	}

	static Iterator<ConfigurationSource> iterateInOrder(Collection<Iterator<ConfigurationSource>> iterators) {
		return new CompositeIterator<ConfigurationSource>(iterators);
	}

	private static final class StaticFileIterator implements Iterator<ConfigurationSource> {
		private final List<ConfigurationFile> configurationFiles;
		private final ConfigurationFileToSource fileToSource;
		private int nextIndex = 0;

		public StaticFileIterator(Collection<ConfigurationFile> configurationFiles, ConfigurationFileToSource fileToSource) {
			this.configurationFiles = new ArrayList<ConfigurationFile>(configurationFiles);
			this.fileToSource = fileToSource;
		}

		@Override public boolean hasNext() {
			return nextIndex < configurationFiles.size();
		}

		@Override public ConfigurationSource next() {
			ConfigurationFile configurationFile = configurationFiles.get(nextIndex);
			nextIndex++;
			return fileToSource.parsed(configurationFile);
		}

		@Override public void remove() {
			throw new UnsupportedOperationException();
		}
	}

	private static final class CompositeIterator<T> implements Iterator<T> {
		private final Queue<Iterator<T>> iterators;

		public CompositeIterator(Collection<Iterator<T>> iterators) {
			this.iterators = new ArrayDeque<Iterator<T>>(iterators);
		}

		@Override public boolean hasNext() {
			while (!iterators.isEmpty() && !iterators.peek().hasNext()) {
				iterators.remove();
			}
			return !iterators.isEmpty();
		}

		@Override public T next() {
			Iterator<T> iterator = iterators.peek();
			if (iterators.isEmpty() || !iterator.hasNext()) {
				throw new NoSuchElementException();
			}
			return iterator.next();
		}

		@Override public void remove() {
			throw new UnsupportedOperationException();
		}
	}
}
