package com.github.khankamranali.jsonfilter.impl.filter.expression;

import com.github.khankamranali.jsonfilter.JsonFilterException;

public class NotExpression extends AbstractExpression {

	public NotExpression(String filterKey) {
		this.filterKey = filterKey;
	}

	public boolean eval(Object object) {
		if(object==null) {
			return false;
		}
		if (expressions.length != 1) {
			new JsonFilterException("$not operator must have only one argument e.g. {'$not':[{'color':'?1'}]}.");
		}
		
		return !expressions[0].eval(object);
	}

}
