/*
 *
 * Copyright 2016 EUROPEAN COMMISSION
 *
 * Licensed under the EUPL, Version 1.1 or – as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 *
 * You may not use this work except in compliance with the Licence.
 *
 * You may obtain a copy of the Licence at:
 *
 * https://joinup.ec.europa.eu/community/eupl/og_page/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 *
 */

package it.maggioli.eldasoft.dgue.msdgueserver.core.domain;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * Container class for storing the values of requirement groups in a dynamic way, preventing a possible proliferation
 * of classes and fields. This class is mostly useful for modeling the unbounded requirement groups.
 *
 * @author Fabio Gomiero <fabio.gomiero@akera.it>
 * @version 1.0.0
 * @since Mar 09, 2020
 */
public class DynamicRequirementGroup implements Map<String, Object> {
	
	/**
	 * Value for INDICATOR in subgroups
	 */
	@Getter @Setter
    private Boolean subIndicatorAnswer;

	private List<DynamicRequirementGroup> unboundedGroups;
	
	private List<DynamicRequirementGroup> unboundedGroups2;
	
	private final Map<String, Object> values;

	public DynamicRequirementGroup() {
		this(new HashMap<String, Object>(5));
	}

	DynamicRequirementGroup(Map<String, Object> values) {
		this.values = values;
	}

	@Override
	public int size() {
		return values.size();
	}

	@Override
	public boolean isEmpty() {
		return values.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return values.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return values.containsValue(value);
	}

	@Override
	public Object get(Object key) {
		if("subIndicatorAnswer".equals(key)) {
			return subIndicatorAnswer;
		}
		return values.get(key);
	}

	@Override
	public Object put(String key, Object value) {
		if("subIndicatorAnswer".equals(key)) {
			subIndicatorAnswer = (Boolean) value;
			return value;
		}
		return values.put(key, value);
	}

	@Override
	public Object remove(Object key) {
		return values.remove(key);
	}

	@Override
	public void putAll(Map<? extends String, ?> m) {
		values.putAll(m);
	}

	@Override
	public void clear() {
		values.clear();
	}

	@Override
	public Set<String> keySet() {
		return values.keySet();
	}

	@Override
	public Collection<Object> values() {
		return values.values();
	}

	@Override
	public Set<Entry<String, Object>> entrySet() {
		return values.entrySet();
	}

	public List<DynamicRequirementGroup> getUnboundedGroups() {
		return unboundedGroups;
	}

	public void setUnboundedGroups(List<DynamicRequirementGroup> unboundedGroups) {
		this.unboundedGroups = unboundedGroups;
	}

	public List<DynamicRequirementGroup> getUnboundedGroups2() {
		return unboundedGroups2;
	}

	public void setUnboundedGroups2(List<DynamicRequirementGroup> unboundedGroups2) {
		this.unboundedGroups2 = unboundedGroups2;
	}

}
