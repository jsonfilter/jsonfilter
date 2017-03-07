package com.github.jsonfilter.impl.mr.m;

import java.util.Arrays;

/**
 * Builds Mapper objects from String e.g. "skus.price".
 * 
 * @author Kamran Ali Khan (khankamranali@gmail.com)
 * 
 */
public class MapParser {

	public static Mapper parse(String property) {
		String[] properties = property.split("\\.");
		return parse(properties);
	}

	private static Mapper parse(String[] properties) {
		Mapper mapper;
		if (properties.length > 1) {
			// recursion
			mapper = createMapper(parse(Arrays.copyOfRange(properties, 1, properties.length)), properties[0]);
		} else {
			mapper = createMapper(null, properties[0]);
		}
		return mapper;
	}

	private static Mapper createMapper(Mapper mapper, String property) {
		return new MapperImpl(mapper, property);
	}

}
