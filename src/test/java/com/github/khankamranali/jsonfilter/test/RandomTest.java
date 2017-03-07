package com.github.khankamranali.jsonfilter.test;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.github.jsonfilter.JsonFilter;

import edu.emory.mathcs.backport.java.util.Arrays;

public class RandomTest {
	static JsonFilter filter;
	
	@BeforeClass
	public static void createData() throws JsonParseException, JsonMappingException, IOException {
		List<byte[]> jsons = new ArrayList<byte[]>();
		jsons.add("{'name':'dog','born':'2014-02-28 22:00:00.0','color':['grey'],'legs':4, 'pet':true, 'children':[{'name':'dog2'},{'name':'dog3'}]}}".getBytes());
		jsons.add("{'name':'cat1','born':'2014-02-28 22:00:00.0','color':['black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat3'}]}".getBytes());
		jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen3'}]}".getBytes());
		jsons.add("{'name':'hen1','born':'2014-02-28 22:00:00.0','color':['red'],'legs':2, 'pet':true, 'children':[{'name':'hen4'}]}".getBytes());
		jsons.add("{'name':'cat2','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':true, 'children':[{'name':'cat2'},{'name':'cat5'}]}".getBytes());
		jsons.add("{'name':'loin','born':'2014-02-28 22:00:00.0','color':['white','black'],'legs':4, 'pet':false, 'children':[{'name':'loin2'},{'name':'loin3'}]}}".getBytes());

		filter = new JsonFilter(jsons);
	}
	
	@Test
	public void testSimple() {
		assertEquals(5,filter.filter("{'pet':'?1'}", (Boolean)true).out().size());
	}
	
	@Test
	public void testIn() {
		assertEquals(2,filter.filter("{'color':{'$in':'?1'}}", "white").out().size());
		assertEquals(1,filter.filter("{'color':{'$in':'?1'}}", new String[]{"grey"},"").out().size());
		assertEquals(3,filter.filter("{'color':{'$in':'?1'}}", new String[]{"black"},"").out().size());
		assertEquals(4,filter.filter("{'color':{'$in':'?1'}}", new String[]{"black","grey"},"").out().size());
	}
	
	@Test
	public void testMap() {
		assertEquals(4,filter.filter("{'children':{'name':{'$eq':'?1'}}}", "cat2").map("children").count());
		assertEquals(2,filter.filter("{'children':{'name':{'$eq':'?1'}}}", "loin2").map("children").count());
		assertEquals(2,filter.filter("{'children':{'name':{'$eq':'?1'}}}", "dog2").map("children").count());
		assertEquals(0,filter.filter("{'children':{'name':{'$eq':'?1'}}}", "hen2").map("children").count());
		assertEquals(0,filter.filter("{'children':{'name':'?1'}}", "hen2").map("children").count());
	}
	
	@Test
	public void testMapWithoutFilter() {
		assertEquals(10,filter.map("children").count());
	}
	
	@Test
	public void testMapAtomicValue() {
		//List<?> list = filter.filter("{'children':{'name':'?1'}}", "cat2").map("children").out();
		List<?> names = Arrays.asList(new String[]{"cat2", "cat3", "cat2", "cat5"}); 
		assertEquals(true,filter.filter("{'children':{'name':'?1'}}", "cat2").map("children").map("name").out().containsAll(names));
		assertEquals(new Integer(8),filter.filter("{'children':{'name':'?1'}}", "cat2").map("legs").<Integer>sum());
		assertEquals(0,filter.filter("{'children':{'name':'?1'}}", "hen2").map("children").count());
	}
	
	@Test
	public void testAnd() {
		assertEquals(1,filter.filter("{'$and':[{'name':'?1'},{'children':{'name':'?2'}}]}", "hen1", "hen3").map("children").count());
		assertEquals(0,filter.filter("{'$and':[{'name':'?1'},{'children':{'name':'?2'}}]}", "hen1", "hen2").map("children").count());
		assertEquals(1,filter.filter("{'$and':[{'name':'?1'},{'children':{'name':'?2'}}]}", "hen1", "hen4").map("children").count());
	}
	
}
