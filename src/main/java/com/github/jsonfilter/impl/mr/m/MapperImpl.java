package com.github.jsonfilter.impl.mr.m;

import java.util.Collection;
import java.util.Map;

import com.github.jsonfilter.JsonFilterException;

public class MapperImpl implements Mapper {
	private Mapper mapper;
	private String property;
	protected MapperImpl(Mapper mapper, String property) {
		this.mapper = mapper;
		this.property = property;
	}
	
	
	protected <O, T> void mapOne(O o, Collection<T> list) {
		if (mapper != null) {
			mapper.map(o, list);
		} else {
			list.add((T) o);
		}
	}
	
	@Override
	public <O, T> void map(O object, Collection<T> list) {
		if(!(object instanceof Map)) {
			throw new JsonFilterException("map operation is only supported on json object type values. This is value type is "+object.getClass().getName()+"");
		}
		Map<?, ?> map = (Map<?, ?>)object;
		Object o = map.get(property);
		if (o == null) {
			throw new JsonFilterException("property "+property+"not found.");
		}
		
		if (Collection.class.isAssignableFrom(o.getClass())) {
			mapCollection(o, list);
		} else {
			mapObject(o, list);
		}

	}

	public <O, T> void mapCollection(O object, Collection<T> list) {
		if (object == null) {
			return;
		}

		Collection<O> c = (Collection<O>) object;
		for (O o : c) {
			mapOne(o, list);
		}
	}

	public <O, T> void mapObject(O object, Collection<T> list) {
		if (object == null) {
			return;
		}
		mapOne(object, list);
	}

}
