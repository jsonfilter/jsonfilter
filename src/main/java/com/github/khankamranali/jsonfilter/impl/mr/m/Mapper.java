package com.github.khankamranali.jsonfilter.impl.mr.m;

import java.util.Collection;

public interface Mapper {
	<O, T> void map(O object, Collection<T> list);
}
