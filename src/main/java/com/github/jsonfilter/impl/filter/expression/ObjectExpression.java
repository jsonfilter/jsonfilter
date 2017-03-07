package com.github.jsonfilter.impl.filter.expression;

import java.util.Collection;
import java.util.Map;

import com.github.jsonfilter.JsonFilterException;

public class ObjectExpression extends AbstractExpression {
	
	public ObjectExpression(String filterKey) {
		this.filterKey = filterKey;
	}

	/**
	 * If any of the collection element matches the filter it is true.
	 */
	@Override
	public boolean eval(Object object) {
		Object o = getValue(object);
		if(o==null) {
			return false;
		}
		
		Class<?> valueType = o.getClass();
		if(Map.class.isAssignableFrom(valueType)) {
			return evalMap((Map<?, ?>) o);
		} else if(Collection.class.isAssignableFrom(valueType)) {
			return evalCollection((Collection<?>) o);
		} else {
			throw new JsonFilterException("Value type is unknown: "+valueType.getName());
		}

	}
	
	private boolean evalMap(Map<?,?> m) {
		return and(m);
	}
	private boolean evalCollection(Collection<?> c) {
		for (Object value : c) {
			if (and(value) == true) {
				return true;
			}
		}
		return false;
	}
	
	private boolean and(Object object) {
		for (FilterExpression exp : expressions) {
			if (exp.eval(object) == false) {
				return false;
			}
		}
		return true;
	}
}
