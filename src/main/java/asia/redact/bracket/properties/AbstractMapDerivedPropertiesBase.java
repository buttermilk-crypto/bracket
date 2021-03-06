/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Implemented here in the base are the map-like facade methods available to the Properties class. 
 * 
 * @author Dave
 *
 */
public abstract class AbstractMapDerivedPropertiesBase extends PropertiesBaseImpl implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	//the data 
	protected AbstractMap<String,ValueModel> map;

	public AbstractMapDerivedPropertiesBase() {
		super();
	}
	
	public Entry atIndex(int index) {
		if(index>this.size())throw new RuntimeException("Out of bounds: "+index);
		Iterator<String> iter = map.keySet().iterator();
		int count = 0;
		while(iter.hasNext()) {
			if(index < count) {
				iter.next();
				count++; 
				continue;
			}
		}
		
		String key = iter.next();
		return new Entry(key,map.get(key));
	}

	public int size() {
		lock.lock();
		try{
			return map.size();
		}finally{
			lock.unlock();
		}
	}

	public boolean isEmpty() {
		lock.lock();
		try{
			return map.isEmpty();
		}finally{
			lock.unlock();
		}
	}

	public boolean containsKey(String key) {
		lock.lock();
		try{
			return map.containsKey(key);
		}finally{
			lock.unlock();
		}
	}

	public boolean containsValue(String value) {
		lock.lock();
		try{
			return map.containsValue(value);
		}finally{
			lock.unlock();
		}
	}

	public Object remove(String key) {
		lock.lock();
		try{
			return map.remove(key);
		}finally{
			lock.unlock();
		}
	}

	public void clear() {
		lock.lock();
		try{
			map.clear();
		}finally{
			lock.unlock();
		}
	}

	public Set<String> keySet() {
		lock.lock();
		try{
			return map.keySet();
		}finally{
			lock.unlock();
		}
	}

	public Collection<ValueModel> values() {
		lock.lock();
		try{
			return map.values();
		}finally{
			lock.unlock();
		}
	}
	
	public String toString(){
		lock.lock();
		try{
			return map.toString();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((map == null) ? 0 : map.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AbstractMapDerivedPropertiesBase other = (AbstractMapDerivedPropertiesBase) obj;
		if (map == null) {
			if (other.map != null)
				return false;
		} else if (!map.equals(other.map))
			return false;
		return true;
	}
	
}
