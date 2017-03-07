package com.github.jsonfilter.impl.filter.json;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.jsonfilter.JsonFilterException;

public class JacksonJsonImpl implements Json {
	final private static ObjectMapper mapper = new ObjectMapper();
	static {
		SimpleModule simpleModule = new SimpleModule();
	    simpleModule.addDeserializer(Object.class, new CustomDateDeseralizer());
	    mapper.registerModule(simpleModule);
	    mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
	}
	
	public <T> byte[] toJson(T t) {
		try {
			return mapper.writeValueAsBytes(t);
		} catch (JsonGenerationException e) {
			throw new JsonFilterException(e);
		} catch (JsonMappingException e) {
			throw new JsonFilterException(e);
		} catch (IOException e) {
			throw new JsonFilterException(e);
		}
	}

	public Map<String, ?> toMap(byte[] json) {
		try {
			return mapper.readValue(json, Map.class);
		} catch (JsonParseException e) {
			throw new JsonFilterException(e);
		} catch (JsonMappingException e) {
			throw new JsonFilterException(e);
		} catch (IOException e) {
			throw new JsonFilterException(e);
		}
	}

	public List<Map<?,?>> toList(byte[] json) {
		try {
			return mapper.readValue(json, List.class);
		} catch (JsonParseException e) {
			throw new JsonFilterException(e);
		} catch (JsonMappingException e) {
			throw new JsonFilterException(e);
		} catch (IOException e) {
			throw new JsonFilterException(e);
		}
	}

}
