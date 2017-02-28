package com.github.khankamranali.jsonfilter.impl.filter.expression;

/**
 * 
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 * @param <T>
 */
public interface FilterExpression {

	String getFilterKey();
	
	void addExpression(FilterExpression expression);

	boolean eval(Object object);
}
