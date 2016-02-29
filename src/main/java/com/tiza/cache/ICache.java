package com.tiza.cache;

public interface ICache {

	public void put(Object key, Object value);
	
	public Boolean containsKey(Object key);
	
	public Object get(Object key);
	
	public void remove(Object key);
	
	public void clear();
}
