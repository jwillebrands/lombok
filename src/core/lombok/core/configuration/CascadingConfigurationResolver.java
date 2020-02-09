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
package lombok.core.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class CascadingConfigurationResolver implements ConfigurationResolver {
	private final Iterable<ConfigurationSource> configurationSources;

	public CascadingConfigurationResolver(Iterable<ConfigurationSource> configurationSources) {
		this.configurationSources = configurationSources;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T resolve(ConfigurationKey<T> key) {
		Iterator<ConfigurationSource> iterator = configurationSources.iterator();
		boolean isList = key.getType().isList();
		List<List<ConfigurationSource.ListModification>> listModificationsList = null;
		ConfigurationSource.Result result = null;

		while ((result == null || !result.isAuthoritative()) && iterator.hasNext()) {
			ConfigurationSource configurationSource = iterator.next();
			result = configurationSource.resolve(key);
			if (result != null && isList) {
				if (listModificationsList == null)
					listModificationsList = new ArrayList<List<ConfigurationSource.ListModification>>();
				listModificationsList.add((List<ConfigurationSource.ListModification>) result.getValue());
			}
		}
		if (isList) {
			return computeListValues(listModificationsList);
		}
		return result != null ? (T) result.getValue() : null;
	}

	@SuppressWarnings("unchecked")
	private <T> T computeListValues(List<List<ConfigurationSource.ListModification>> modifications) {
		if (modifications == null) return (T) Collections.emptyList();

		List<Object> listValues = new ArrayList<Object>();
		Collections.reverse(modifications);
		for (List<ConfigurationSource.ListModification> listModifications : modifications) {
			if (listModifications != null) for (ConfigurationSource.ListModification modification : listModifications) {
				listValues.remove(modification.getValue());
				if (modification.isAdded()) listValues.add(modification.getValue());
			}
		}
		return (T) listValues;
	}
}
