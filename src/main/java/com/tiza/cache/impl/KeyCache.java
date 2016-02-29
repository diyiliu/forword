package com.tiza.cache.impl;

import com.tiza.cache.ICache;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


public class KeyCache implements ICache {

	private Map<Object, Object> data = new ConcurrentHashMap<Object, Object>();

	@Override
	public void put(Object key, Object value) {

		data.put(key, value);
	}

	@Override
	public Boolean containsKey(Object key) {

		return data.containsKey(key);
	}

	@Override
	public Object get(Object key) {

		return data.get(key);
	}

	@Override
	public void remove(Object key) {

		data.remove(key);
	}

	@Override
	public void clear() {

		data.clear();
	}
}
