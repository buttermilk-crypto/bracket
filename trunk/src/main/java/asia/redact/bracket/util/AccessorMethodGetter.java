/*
 *  This file is part of Bracket Properties
 *  Copyright 2013 David R. Smith
 *
 */
package asia.redact.bracket.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AccessorMethodGetter {

	final Class<?> clazz;
	final Object instance;
	
	private final Pattern getterPattern = Pattern.compile("(get|is)[A-Z][a-zA-Z0-9]+");
	
	public AccessorMethodGetter(Class<?> clazz, Object instance) {
		this.clazz = clazz;
		this.instance = instance;
	}
	
	/**
	 * Return a list of all the "getter" accessors in clazz
	 * 
	 * Does not return "getClass"
	 * 
	 * @return
	 */
	public List<Method> listGetters() {
		List<Method> list = new ArrayList<Method>();
		for(Method m : clazz.getMethods()) {
			if(m.getName().equals("getClass")) continue; //skip this
			Matcher matcher = getterPattern.matcher(m.getName());
			if(matcher.matches()){
				Class<?>[] params = m.getParameterTypes();
				if(params != null && params.length == 0){
					list.add(m);
				}
			}
		}
		return list;
	}
	
	public CustomKeyWrapper invoke(Method m){
	    m.setAccessible(true);
	    try {
			Object obj = m.invoke(instance, new Object[0]);
			CustomKeyWrapper wrapper = new CustomKeyWrapper(keyify(m.getName()),obj);
			if(obj instanceof List){
				
			}
			return wrapper;
		} catch (Exception x) {
			throw new RuntimeException(x);
		}
	}
	
	private String keyify(String getter){
		StringBuffer buf = new StringBuffer();
		if(getter.startsWith("get")){
			buf.append(getter.substring(3).toLowerCase());
		}else if(getter.startsWith("is")) {
			buf.append(getter.substring(2).toLowerCase());
		}
		return buf.toString();
	}

}
