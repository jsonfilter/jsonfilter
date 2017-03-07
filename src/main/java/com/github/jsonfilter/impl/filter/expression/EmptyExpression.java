package com.github.jsonfilter.impl.filter.expression;

public class EmptyExpression extends AbstractExpression {

	public EmptyExpression(String filterKey) {
		this.filterKey = filterKey;
	}

	public boolean eval(Object o) {
		if(o==null) {
			return false;
		}
		return expressions[0].eval(o);
	}

}
