package com.github.jsonfilter.impl.filter.expression;

/**
 * 
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 */
public interface FilterExpression {

	String getFilterKey();
	
	void addExpression(FilterExpression expression);

	boolean eval(Object object);
}
