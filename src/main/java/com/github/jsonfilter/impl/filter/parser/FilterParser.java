package com.github.jsonfilter.impl.filter.parser;

import java.util.Collection;
import java.util.Map;

import com.github.jsonfilter.JsonFilterException;
import com.github.jsonfilter.impl.filter.expression.AndExpression;
import com.github.jsonfilter.impl.filter.expression.EmptyExpression;
import com.github.jsonfilter.impl.filter.expression.FilterExpression;
import com.github.jsonfilter.impl.filter.expression.NotExpression;
import com.github.jsonfilter.impl.filter.expression.ObjectExpression;
import com.github.jsonfilter.impl.filter.expression.OrExpression;
import com.github.jsonfilter.impl.filter.expression.ValueExpression;
import com.github.jsonfilter.impl.filter.json.JacksonJsonImpl;
import com.github.jsonfilter.impl.filter.json.Json;

public class FilterParser {
	private static Json json = new JacksonJsonImpl();

	public FilterExpression parse(String jsonFilter) {
		return parse(jsonFilter, null);
	}
	
	public FilterExpression parse(String jsonFilter, Map<String, ?> parameters) {
		try {
			Map<String, ?> filterMap = json.toMap(jsonFilter.getBytes());
			String filterFirstKey = (String) filterMap.keySet().toArray()[0];

			FilterExpression exp = new EmptyExpression(filterFirstKey);
			parseMap(filterMap, parameters, exp);
			return exp;
		} catch (Throwable e) {
			throw new JsonFilterException("Filter parsing error.", e);
		}
	}

	private void parseMap(Map<String, ?> filterMap, Map<String, ?> parameters, FilterExpression exp) {
		try {
			for (Map.Entry<String, ?> filterMapEntry : filterMap.entrySet()) {
				parseKey(filterMapEntry, parameters, exp);
			}
		} catch (Throwable e) {
			throw new JsonFilterException("Filter parsing error.", e);
		}
	}

	/**
	 * Replaces leaf key values FilterKeyValue object.
	 * 
	 * @param filterMapEntry
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private void parseKey(final Map.Entry<String, ?> filterMapEntry, Map<String, ?> parameters, FilterExpression exp) {

		String filterKey = filterMapEntry.getKey();
	
		Object filterValue = filterMapEntry.getValue();

		/** e.g. {a:"v"} */
		if (filterValue instanceof String) {
			parseProperty(filterKey, toArrays(filterValue, parameters), Operator.$eq, exp, String.class);
			return;
		}

		/** e.g {a:{$gt:"10"}} , {a:{$in:[1,2,3]}} */
		if ((filterValue instanceof Map) && (containsOperator((Map<String, ?>) filterValue))) {
			Map<String, ?> valueMap = (Map<String, ?>) filterValue;
			String s = (String) valueMap.keySet().toArray()[0];
			Operator operator = Operator.operatorOf(s);
			
			parseProperty(filterKey, toArrays(valueMap.get(s), parameters), operator, exp, Collection.class);
			return;
		}

		/** e.g. {$and:[{a:"v1"}, {b:"v2}]} */
		if (filterValue instanceof Collection) {
			if (!Operator.isJoin(filterKey)) {
				throw new JsonFilterException(" $and or $or or $not expected. with collection of expressions: " + filterValue+" but was "+filterKey);
			}
			parseCollection(filterKey, (Collection<Map<String, ?>>) filterValue, parameters, exp);
			return;
		}

		/** e.g. {a:{b:"v1", c:"v2"}} */
		if (filterValue instanceof Map) {
			parseObject(filterKey, (Map<String, ?>) filterValue, parameters, exp);
			return;
		}

		throw new JsonFilterException("Unknow filter parsing error.");
	}

	private void parseProperty(String filterKey, Object[] filterValues, Operator operator, FilterExpression exp, Class<?> filterKeyType) {
		FilterExpression nextExp = new ValueExpression(filterKey, filterValues, operator);
		exp.addExpression(nextExp);
	}
	
	private void parseCollection(String filterKey, Collection<Map<String, ?>> filterValue, Map<String,?> parameters, FilterExpression exp) {
		FilterExpression nextExp;
		Operator operator = Operator.operatorOf(filterKey);
		if (operator == Operator.$and) {
			nextExp = new AndExpression(filterKey);
		} else if (operator == Operator.$or) {
			nextExp = new OrExpression(filterKey);
		} else if (operator == Operator.$not) {
			nextExp = new NotExpression(filterKey);
		} else {
			throw new JsonFilterException(" Join operator not supported: " + operator);
		}

		for (Map<String, ?> filterMap : filterValue) {
			parseMap(filterMap, parameters, nextExp);
		}
		exp.addExpression(nextExp);
	}
	
	private void parseObject(String filterKey, Map<String, ?> filterValue, Map<String, ?> parameters, FilterExpression exp) {
		FilterExpression nextExp;
		/** for properties which returns Map objects */
		nextExp = new ObjectExpression(filterKey);

		parseMap(filterValue, parameters, nextExp);
		exp.addExpression(nextExp);
	}		

	private boolean containsOperator(final Map<String, ?> filterMap) {
		for (String key : filterMap.keySet()) {
			if (Operator.isComparator(key)) {
				return true;
			}
		}
		return false;
	}

	private Object[] toArrays(Object object, Map<String, ?> parameters) {
		if (object instanceof Collection) {
			return ((Collection<?>) object).toArray();
		} else {
			String s = (String) object;
			if (s.startsWith("?")) {
				
				if(!parameters.containsKey(s.substring(1))) {
					throw new JsonFilterException("Filter parameter [" + s + "] value not given");
				}
				
				Object o = parameters.get(s.substring(1));
				if (o instanceof Collection) {
					return ((Collection<?>) o).toArray();
				} else if (o instanceof Object[]){
					return (Object[]) o;
				} else {
					return new Object[] { o };
				}
			} else {
				throw new JsonFilterException(
						"Filter parameter ["
								+ s
								+ "] should be in '?n' or '?s' where n is integer and s is string, and values should be supplied as Map or variable argument.");
			}
		}

	}

}
