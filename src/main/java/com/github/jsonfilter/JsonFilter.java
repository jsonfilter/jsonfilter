package com.github.jsonfilter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.github.jsonfilter.impl.filter.expression.FilterExpression;
import com.github.jsonfilter.impl.filter.json.JacksonJsonImpl;
import com.github.jsonfilter.impl.filter.json.Json;
import com.github.jsonfilter.impl.filter.parser.FilterParser;
import com.github.jsonfilter.impl.mr.m.MapParser;
import com.github.jsonfilter.impl.mr.m.Mapper;
import com.github.jsonfilter.impl.mr.r.Reducer;

/**
 * <p>
 * Main class to filter/query collections of Json data. Each Json data is converted into java Map object 
 * then filter is executed on each Map. So This class instance should be cached to execute queries multiple times with different data set.
 * Date values should be given in "yyyy-MM-dd hh:mm:ss.0" format in json data.
 * Following are different ways to execute filter.
 * </p>
 * 
 * <blockquote><pre>{@code
 * List<byte[]> jsons = new ArrayList<byte[]>();
 * jsons.add("{'name':'dog','born':'2014-02-28 22:00:00.0','color':['grey'],'legs':4, 'pet':true, 'children':[{'name':'dog2'},{'name':'dog3'}]}}".getBytes());
 * jsons.add("{'name':'cat1','born':'2014-02-28 22:00:00.0','color':['black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat3'}]}".getBytes());
 * jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen3'}]}".getBytes());
 * jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen4'}]}".getBytes());
 * jsons.add("{'name':'cat2','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat5'}]}".getBytes());
 * jsons.add("{'name':'loin','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':false, 'children':[{'name':'loin2'},{'name':'loin3'}]}}".getBytes());
 *
 * filter = new JsonFilter(jsons);
 * filter.filter("{'children':{'name':{'$eq':'?1'}}}", "cat2").map("children"));
 * filter.filter("{'children':{'name':'?1'}}", "hen2").map("children")
 * </pre></blockquote>
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 */
public class JsonFilter {
	
	private FilterParser filterParser;
	/** Supported collection types */
	private Iterable<?> iterable;
	
	Json jsonObjectMapper = new JacksonJsonImpl();
	
	/**
	 * Creates new instance.
	 * Example:
	 * <blockquote><pre>{@code
	 * List<byte[]> jsons = new ArrayList<byte[]>();
	 * jsons.add("{'name':'dog','born':'2014-02-28 22:00:00.0','color':['grey'],'legs':4, 'pet':true, 'children':[{'name':'dog2'},{'name':'dog3'}]}}".getBytes());
	 * jsons.add("{'name':'cat1','born':'2014-02-28 22:00:00.0','color':['black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat3'}]}".getBytes());
	 * jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen3'}]}".getBytes());
	 * jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen4'}]}".getBytes());
	 * jsons.add("{'name':'cat2','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat5'}]}".getBytes());
	 * jsons.add("{'name':'loin','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':false, 'children':[{'name':'loin2'},{'name':'loin3'}]}}".getBytes());
	 * filter = new JsonFilter(jsons);
	 * </pre></blockquote>
	 * @param jsons List of json data bytes.
	 */
	public JsonFilter(List<byte[]> jsons) {
		List<Map> list = new ArrayList<Map>();
		for (byte[] json : jsons) {
			list.add(jsonObjectMapper.toMap(json));
		}
		iterable = list;
		init();
	}
	
	/**
	 * Creates new instance.
	 * @param json Array of jsons e.g. [{"name":"dog", "color":"white"}, {"name":"cat", "color":"black"}] 
	 */
	public JsonFilter(byte[] json) {
		if(new String(json).startsWith("[")) {
			/* collection */
			iterable = jsonObjectMapper.toList(json);
		} else {
			/* object */
			List<Map> list = new ArrayList<Map>();
			list.add(jsonObjectMapper.toMap(json));
			iterable = list;
		}
		init();
	}
	
	/**
	 * 
	 * @param collection
	 *            collection to be filtered.
	 */
	private JsonFilter(Iterable<?> iterable) {
		this.iterable = iterable;
		init();
	}
	

	/**
	 * For making clone.
	 * 
	 * @param iterable
	 * @param bean
	 */
	private JsonFilter(Iterable<?> iterable, FilterParser filterParser) {
		this.iterable = iterable;
		this.filterParser = filterParser;
	}

	private void init() {
		this.filterParser = new FilterParser();
	}

	private List<?> execute(Iterator<?> itr, FilterExpression filterExp) {
		List<Object> result = new ArrayList<Object>();

		while (itr.hasNext()) {
			Object o = itr.next();
			if (filterExp.eval(o)) {
				result.add(o);
			}
		}
		return result;
	}

	/**
	 * Executes filter with map of parameter values. filter parameter values are
	 * given in key value form where key is string given in filter in "?string"
	 * format and value is a object of same class as of json value. $in and
	 * $nin values are given as List of objects.
	 * 
	 * @param filter
	 *            Json filter string e.g. "{'id':'?id'}"
	 * @param parameters
	 *            Filter parameters values are given in key value form where key
	 *            is string given in filter in "?string" format and value is
	 *            object of bean property class. $in and $nin values are given
	 *            as List of objects.
	 * 
	 * @return filtered values.
	 */
	public JsonFilter filter(String filter, Map<String, ?> parameters) {
		FilterExpression filterExp = filterParser.parse(filter, parameters);
		return new JsonFilter(execute(iterable.iterator(), filterExp), filterParser);
	}

	/**
	 * Executes filter with array of parameter values. Parameters are given as
	 * "?1", "?2" etc in the filter, starting from "?1" to "?n" where n is
	 * integer. Parameter values are are picked from corresponding argument
	 * position in the variable arguments.
	 * 
	 * @param filter
	 *            Json filter string e.g. "{'id':'?1'}"
	 * @param parameters
	 *            Filter parameters value objects are given in the same order as
	 *            given in filter in the form of "?1", "?2" ..."?n". The object
	 *            should be of json vaue type class. $in and $nin values are
	 *            given as List of objects.
	 * 
	 * @return filtered values.
	 */
	public JsonFilter filter(String filter, Object... parameters) {
		return filter(filter, getParameterMap(parameters));
	}

	/**
	 * Returns first element of the filtered values.
	 * 
	 * @return First element of the filtered values.
	 */
	public <T> T getFirst() {
		return (T) iterable.iterator().next();
	}

	/**
	 * Selects given property from the collection and creates new JFilter
	 * object.
	 * 
	 * @param property
	 *            property/method name of the bean, dot notation should be used
	 *            for inner properties e.g. "sku.price".
	 * @return JFilter
	 */
	@SuppressWarnings("unchecked")
	public JsonFilter map(String property) {
		List<Map> list = new ArrayList<Map>();
		Mapper mapper = MapParser.parse(property);
		for (Object o : iterable) {
			mapper.map(o, list);
		}
		return new JsonFilter(list);
	}
	
	/**
	 * Returns maximum value.
	 * 
	 * @return Maximum value.
	 * @throws JsonFilterException
	 *             if bean class does not implement Comparable interface.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T max() {
		Class<?> klass = iterable.iterator().next().getClass();
		if (!Comparable.class.isAssignableFrom(klass)) {
			throw new JsonFilterException("Reduce function on type: " + klass + " is not supported.");
		}

		return (T) Reducer.max((Iterable<Comparable>) iterable);
	}

	/**
	 * Returns minimum value.
	 * 
	 * @return Minimum value.
	 * @throws JsonFilterException
	 *             if bean class does not implement Comparable interface.
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public <T> T min() {
		Class<?> klass = iterable.iterator().next().getClass();
		if (!Comparable.class.isAssignableFrom(klass)) {
			throw new JsonFilterException("Reduce function on type: " + klass + " is not supported.");
		}

		return (T) Reducer.min((Iterable<Comparable>) iterable);
	}

	/**
	 * Returns sum of values.
	 * 
	 * @return Sum of values.
	 * @throws JsonFilterException
	 *             if bean class does not extends Number class.
	 */
	@SuppressWarnings("unchecked")
	public <T> T sum() {
		Class<?> klass = iterable.iterator().next().getClass();
		if (!Number.class.isAssignableFrom(iterable.iterator().next().getClass())) {
			throw new JsonFilterException("Reduce function on type: " + klass + " is not supported.");
		}
		return (T) Reducer.sum((Iterable<Number>) iterable);
	}

	/**
	 * Returns count
	 * 
	 * @return count.
	 * @throws JsonFilterException
	 *             if bean class does not implement Comparable interface.
	 */

	public int count() {
		return Reducer.count(iterable);
	}


	/**
	 * Collection of json data by the filter, map result. You need to call this method after filter or map method to collect the result.
	 * 
	 * @return Collection of json data
	 * @throws JsonFilterException
	 *             if bean class does not implement Comparable interface.
	 */

	public List<?> out() {
		List out = new ArrayList();
		for (Object o : iterable) {
			out.add(o);
		}
		return out;
	}


	private Map<String, Object> getParameterMap(Object... parameters) {
		Map<String, Object> map = new HashMap<String, Object>();

		Integer i = 1;
		for (Object parameter : parameters) {
			map.put(i.toString(), parameter);
			++i;
		}
		return map;
	}

}