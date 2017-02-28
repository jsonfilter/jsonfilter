package com.github.khankamranali.jsonfilter.impl.filter.expression;

public class OrExpression extends AbstractExpression {

	public OrExpression(String filterKey) {
		this.filterKey = filterKey;
	}

	public boolean eval(Object object) {
		if(object==null) {
			return false;
		}
		
		for (FilterExpression exp : expressions) {
			if (exp.eval(object) == true) {
				return true;
			}
		}
		return false;
	}

}
