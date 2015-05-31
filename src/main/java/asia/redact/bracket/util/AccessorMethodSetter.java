/*
 *  This file is part of Bracket Properties
 *  Copyright 2013 David R. Smith
 *
 */
package asia.redact.bracket.util;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

/**
 * Use the java Reflection API to set values on a given instance of a class
 * 
 * @author Dave
 *
 */
public final class AccessorMethodSetter {
	
	public final Logger log = Logger.getLogger(AccessorMethodSetter.class);
	private final Pattern setterPattern = Pattern.compile("set[A-Z][a-zA-Z0-9]+");
	
	final Class<?> clazz;
	final Object instance;
	final String identifier;
	final String value;
	
	private String listDelimiter;
	
	// outcome
	private Exception exception;
	private boolean success;
	
	public AccessorMethodSetter(Class<?> clazz, Object instance, String identifier) {
		super();
		this.clazz = clazz;
		this.instance = instance;
		this.identifier = identifier;
		value = null; // not used in this mode, we must call set(Object)
		listDelimiter = " "; // space by default
	}
	
	public AccessorMethodSetter(Class<?> clazz, Object instance, String identifier, String value) {
		super();
		this.clazz = clazz;
		this.instance = instance;
		this.identifier = identifier;
		this.value = value;
		listDelimiter = " "; // space by default
	}
	
	public boolean success() {
		return success;
	}

	public Exception getException() {
		return exception;
	}

	private String setify() {
		
		Matcher match = setterPattern.matcher(identifier);
		if(match.matches()){
			return identifier;
		}else{
			StringBuffer buf = new StringBuffer("set");
			char start = identifier.charAt(0);
			buf.append(Character.toUpperCase(start));
			if(identifier.length()>1) buf.append(identifier.substring(1));
			return buf.toString();
		}
	}

	public void set(Object obj) {
		
		log.debug("set: "+obj);
		
		// basic sanity checks
			if(identifier == null || identifier.length() == 0) {
				exception = new RuntimeException("identifier cannot be null or zero length");
				success = false;
				return;
			}
			
			if(clazz == null || instance == null) {
				exception = new RuntimeException("class or instance cannot be null.");
				success = false;
				return;
			}
			
			String preparedName = setify();
			
			log.debug("prepared method name = "+preparedName);
			
			Method [] allMethods = clazz.getMethods();
			for (Method m : allMethods) {
				if (!m.getName().equals(preparedName)) {
				    continue;
				}
				
				log.debug("Found method "+m);
				
				Class<?>[] pType  = m.getParameterTypes();
				if(pType.length==1){
					log.debug("********* setup for call to invoke() **********");
					log.debug("class for the setter = "+clazz.getName());
					log.debug("instance we are calling set on = "+instance);
					log.debug("Type param's expected class = "+pType[0].getName());
					log.debug("Object's actual class = "+obj.getClass().getName());
					if(pType[0].isAssignableFrom(obj.getClass())){
						try {
						m.invoke(instance,  obj);
						success = true;
						log.debug("******** Success ***********");
						}catch(Exception x){
							x.printStackTrace();
						}
					}
				}
			}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void set() throws InstantiationException, IllegalAccessException {
		
		// basic sanity checks
		if(identifier == null || identifier.length() == 0) {
			exception = new RuntimeException("identifier cannot be null or zero length");
			success = false;
			return;
		}
		
		if(value == null) {
			exception = new RuntimeException("value cannot be null");
			success = false;
			return;
		}
		
		if(clazz == null || instance == null) {
			exception = new RuntimeException("class or instance cannot be null.");
			success = false;
			return;
		}
		
		String preparedName = setify();
		
		Method setMethod = null;
		String parameterizedArgType = null;
		
		Method [] allMethods = clazz.getMethods();
		for (Method m : allMethods) {
			if (!m.getName().equals(preparedName)) {
			    continue;
			}
			
			Type [] paramTypes = m.getGenericParameterTypes();
			
			// skip any setters with multiple params
			if(paramTypes.length != 1) continue;
			
			// we know there is only one at this point, we need to know the type and deal with generics
			Type paramType = paramTypes[0];
			if(paramType instanceof ParameterizedType){
				ParameterizedType pt = (ParameterizedType) paramType;
				
				Type [] actual = pt.getActualTypeArguments();
				Class<?> c = (Class<?>)actual[0];
				parameterizedArgType = c.getName();
			}
			setMethod = m;
			break;
		}
		    
		

			Class<?>[] pType  = setMethod.getParameterTypes();
			
			// TODO I would like to use a String case statement here but it creates a java 7 dependency
			
			Object param = null;
			if(pType[0].getName().equals("java.lang.String")){
				param = value; // String, no conversion
			}else if(pType[0].getName().equals("int")){
				param = Integer.valueOf(value);
			}else if(pType[0].getName().equals("boolean")){
				param = Boolean.valueOf(value);
			}else if(pType[0].getName().equals("double")){
				param = Double.valueOf(value);
			}else if(pType[0].getName().equals("long")){
				param = Long.valueOf(value);
			}else if(pType[0].getName().equals("float")){
				param = Float.valueOf(value);
			}else if(pType[0].getName().equals("char")){
				param = Character.valueOf(value.charAt(0));
			}else if(pType[0].getName().equals("[C")){
				param = value.toCharArray();
			} else if(pType[0].getName().equals("java.util.Date")){
				Date date = new Date();
				date.setTime(Long.valueOf(value));
				param = date;
			}else if(pType[0].getName().equals("java.util.List")){
				
				// our raw values
				ArrayList<String> rawList = new ArrayList<String>();
				String [] split = value.split(listDelimiter);
				for(String s: split){
					rawList.add(s);
				}
				
				ArrayList list = new ArrayList();
				if(parameterizedArgType.equals("java.lang.String")) {
					list.addAll(rawList);
				}else if(parameterizedArgType.equals("java.lang.Integer")) {
					for(String s: rawList){
						list.add(Integer.parseInt(s));
					}
				}else if(parameterizedArgType.equals("int")) {
					for(String s: rawList){
						list.add(Integer.parseInt(s));
					}
				}else if(parameterizedArgType.equals("java.lang.Double")) {
					for(String s: rawList){
						list.add(Double.parseDouble(s));
					}
				}else if(parameterizedArgType.equals("double")) {
					for(String s: rawList){
						list.add(Double.parseDouble(s));
					}
				}else if(parameterizedArgType.equals("java.lang.Float")) {
					for(String s: rawList){
						list.add(Float.parseFloat(s));
					}
				}else if(parameterizedArgType.equals("float")) {
					for(String s: rawList){
						list.add(Float.parseFloat(s));
					}
				}else if(parameterizedArgType.equals("java.lang.Long")) {
					for(String s: rawList){
						list.add(Long.parseLong(s));
					}
				}else if(parameterizedArgType.equals("long")) {
					for(String s: rawList){
						list.add(Long.parseLong(s));
					}
				}else if(parameterizedArgType.equals("java.lang.Boolean")) {
					for(String s: rawList){
						list.add(Boolean.valueOf(s));
					}
				}else if(parameterizedArgType.equals("boolean")) {
					for(String s: rawList){
						list.add(Boolean.valueOf(s));
					}
				}else if(parameterizedArgType.equals("java.util.Date")) {
					for(String s: rawList){
						Date date = new Date();
						date.setTime(Long.valueOf(value));
						list.add(date);
					}
				}else{
					// catch all here
					// try Class.forName()
					if(parameterizedArgType.startsWith("java")) {
						throw new RuntimeException("Not Yet Supported As List Type: "+parameterizedArgType);
					}
					if(parameterizedArgType.startsWith("sun")) {
						throw new RuntimeException("Not Yet Supported As List Type: "+parameterizedArgType);
					}
					
				}
					
				param = list;
			}
				
			try{
			    setMethod.invoke(instance, param);
				success = true;
			} catch (Exception e) {
				e.printStackTrace();
				exception = e;
				success = false;
			}
	}

	public String getListDelimiter() {
		return listDelimiter;
	}

	public void setListDelimiter(String delimiter) {
		this.listDelimiter = delimiter;
	}
	

}
