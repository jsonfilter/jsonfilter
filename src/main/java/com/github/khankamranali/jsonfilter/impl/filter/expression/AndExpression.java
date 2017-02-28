package com.github.khankamranali.jsonfilter.impl.filter.expression;

public class AndExpression extends AbstractExpression {

	public AndExpression(String filterKey) {
		this.filterKey = filterKey;
	}

	public boolean eval(Object object) {
		if(object==null) {
			return false;
		}
		
		for (FilterExpression exp : expressions) {
			if (exp.eval(object) == false) {
				return false;
			}
		}
		return true;
	}

}
