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
