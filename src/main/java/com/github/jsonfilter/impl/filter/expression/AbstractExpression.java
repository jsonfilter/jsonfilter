package com.github.jsonfilter.impl.filter.expression;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A Filter can have expressions and an expression can have filters.
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 */
public abstract class AbstractExpression implements FilterExpression {

	protected String filterKey;
	final private List<FilterExpression> expressionsList = new ArrayList<FilterExpression>(5);
	/**
	 * array is used for performance reason, during the test with 1 million
	 * records it is found that array iteration is roughly 2 times faster than
	 * List.
	 */
	FilterExpression[] expressions = null;
	
	@Override
	public String getFilterKey() {
		return filterKey;
	}
	
	public void addExpression(FilterExpression expression) {
		expressionsList.add(expression);
		expressions = expressionsList.toArray(new FilterExpression[expressionsList.size()]);
	}
	
	FilterExpression[] getExpressions() {
		return expressions;
	}
	
	Object getValue(Object object) {
		Map map = (Map) object;
		Object o = map.get(filterKey);
		return o;
	}
}
