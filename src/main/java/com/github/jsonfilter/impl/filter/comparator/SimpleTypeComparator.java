package com.github.jsonfilter.impl.filter.comparator;

import com.github.jsonfilter.JsonFilterException;
import com.github.jsonfilter.impl.filter.parser.Operator;

public class SimpleTypeComparator {
	/**
	 * Compares the object value with the filter value.
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public boolean compare(Comparable objectValue, Comparable[] filterValues, Operator operator) {
		if (operator == Operator.$eq) {
			return (compare(objectValue, filterValues[0]) == 0);

		} else if (operator == Operator.$ne) {
			return (compare(objectValue, filterValues[0]) != 0);
		} else if (operator == Operator.$gt) {
			return (compare(objectValue, filterValues[0]) > 0);
		} else if (operator == Operator.$ge) {
			return (compare(objectValue, filterValues[0]) >= 0);
		} else if (operator == Operator.$lt) {
			return (compare(objectValue, filterValues[0]) < 0);
		} else if (operator == Operator.$le) {
			return (compare(objectValue, filterValues[0]) <= 0);
		} else if (operator == Operator.$sw) {
			if (objectValue == null) {
				return false;
			}

			if (objectValue instanceof String) {
				String fv = (String) filterValues[0];
				String ov = (String) objectValue;
				return ov.startsWith(fv);
			} else {
				throw new JsonFilterException("$sw operator is applicable for String data type. Parameter value is : " + filterValues[0]);
			}
		} else if (operator == Operator.$ew) {
			if (objectValue == null) {
				return false;
			}
			if (objectValue instanceof String) {
				String fv = (String) filterValues[0];
				String ov = (String) objectValue;
				return ov.endsWith(fv);
			} else {
				throw new JsonFilterException("$ew operator is applicable for String data type. Parameter value is : " + filterValues[0]);
			}
		} else if (operator == Operator.$cts) {
			if (objectValue == null) {
				return false;
			}
			if (objectValue instanceof String) {
				String fv = (String) filterValues[0];
				String ov = (String) objectValue;
				return ov.contains(fv);
			} else {
				throw new JsonFilterException("$cts operator is applicable for String data type. Parameter value is : " + filterValues[0]);
			}
		} else if (operator == Operator.$in) {
			for (Comparable filterValue : filterValues) {
				if (compare(objectValue, filterValue) == 0) {
					return true;
				}
			}
			return false;
		} else if (operator == Operator.$nin) {
			for (Comparable filterValue : filterValues) {
				if (compare(objectValue, filterValue) == 0) {
					return false;
				}
			}
			return true;
		} else {
			throw new JsonFilterException("Operator : " + operator + " not supported.");
		}
	}

	public <T extends Comparable<? super T>> int compare(T v1, T v2) {
		return v1 == null && v2 == null ? 0 : v1 == null ? -1 : v2 == null ? +1 : v1.compareTo(v2);
	}
}
