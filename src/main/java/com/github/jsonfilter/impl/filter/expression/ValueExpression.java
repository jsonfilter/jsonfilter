package com.github.jsonfilter.impl.filter.expression;

import java.util.Collection;

import com.github.jsonfilter.impl.filter.comparator.SimpleTypeComparator;
import com.github.jsonfilter.impl.filter.parser.Operator;

public class ValueExpression extends AbstractExpression implements FilterExpression {

	final private SimpleTypeComparator comparator = new SimpleTypeComparator();
	private Operator operator;
	/** for $in and $nin array of values otherwise single value. */
	private Comparable<?>[] filterValues;

	public ValueExpression(String filterKey, Object[] filterValues, Operator operator) {
		this.filterKey = filterKey;
		this.operator = operator;
		setFilterValues(filterValues);
	}

	private void setFilterValues(Object[] filterValues) {
		this.filterValues = new Comparable<?>[filterValues.length];
		int i = 0;
		for (Object object : filterValues) {
			this.filterValues[i] = (Comparable<?>) object;
			++i;
		}
	}

	public boolean eval(Object object) {
		Object o = getValue(object);
		if(o==null) {
			return false;
		}
		
		if(Collection.class.isAssignableFrom(o.getClass())) {
			return evalCollection((Collection<?>) o);
		} else {
			return evalAtomic(o);
		}
		
	}
	
	private boolean evalAtomic(Object o) {
		return comparator.compare((Comparable) o, this.filterValues, this.operator);
	}
	
	/**
	 * if any value in collection is matched with filter value then returns true 
	 * @param c
	 * @return
	 */
	private boolean evalCollection(Collection<?> c) {
		for(Object v: c) {
			if(evalAtomic(v)) {
				return true;
			}
		}
		return false;
	}

}
