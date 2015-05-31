/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;

import asia.redact.bracket.util.AccessorMethodSetter;
import asia.redact.bracket.util.EnvResolver;

/**
 * Backed with a list
 * 
 * @author Dave
 *
 */
public class ArrayListPropertiesImpl extends PropertiesBaseImpl implements Properties, Serializable {
	
	// data
	protected ArrayList<Entry> list;
	private static final long serialVersionUID = 1L;
	
	public ArrayListPropertiesImpl(ArrayList<Entry> list) {
		super();
		this.list = list;
	}

	public ArrayListPropertiesImpl() {
		super();
		list= new ArrayList<Entry>();
	}

	/**
	 * for compatibility with PropertiesImpl code
	 * 
	 */
	@Override
	public Map<String, ValueModel> getPropertyMap() {
		Map<String,ValueModel> map = new LinkedHashMap<String,ValueModel>();
		for(Entry e: list){
			map.put(e.getKey(), e.getModel());
		}
		return map;
	}

	@Override
	public Map<String, String> getFlattenedMap() {
		Map<String,String> map = new LinkedHashMap<String,String>();
		for(Entry e: list){
			map.put(e.getKey(), e.getModel().getValue());
		}
		return map;
	}
	
	/**
	 * Given a key, locate our Entry in the array. This is thread-safe
	 * @param key
	 * @return
	 */
	public Entry find(String key) {
		lock.lock();
		try {
		for(Entry e: list){
			if(e.getKey().equals(key)) return e;
		}
		}finally{
			lock.unlock();
		}
		
		throw new RuntimeException("no such key: "+key);
	}
	
	/**
	 * Update an entry by index. Thread-safe.
	 * 
	 * @param index
	 * @param entry
	 */
	public void updateAt(int index, Entry entry){
		lock.lock();
		try {
			list.remove(index);
			list.add(index, entry);
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Update a position based on the key found in the entry. Thread-safe. If the key is not found, just add it by
	 * calling list.add();
	 * 
	 * @param entry
	 */
	public void update(Entry entry) {
		lock.lock();
		try {
			boolean found = false;
			int i = 0;
			for(Entry e: list){
				if(entry.key.equals(e.key)) {
					found=true;
					break;
				}
				i++;
			}
			if(found){
				list.remove(i);
				list.add(i,entry);
			}else{
				list.add(entry);
			}
			
		}finally{
			lock.unlock();
		}
	}
	
	public void delete(String key) {
		lock.lock();
		try {
			int i = 0;
			for(Entry e: list){
				if(key.equals(e.key)) break;
				i++;
			}
			if(i>0)list.remove(i);
		}finally{
			lock.unlock();
		}
	}
	

	@Override
	public String get(String key) {
		return find(key).getModel().getValue();
	}

	/**
	 * Special method which resolves environment variables in the value. The variables take the form ${varname} like in ant.
	 * 
	 * Variables in keys are not allowed
	 * 
	 */
	@Override
	public String resolve(String key) {
		
		String template = get(key);
		Matcher matcher = antStyleVarPattern.matcher(template);
		StringBuffer sb = new StringBuffer();
	    while(matcher.find()){
    		String val = matcher.group(1);
    		String repl = EnvResolver.INSTANCE.get(val);
    		matcher.appendReplacement(sb,Matcher.quoteReplacement(repl));
    		
	    }
	    matcher.appendTail(sb);
		
		return sb.toString();
	}

	@Override
	public int intValue(String key) {
		return Integer.parseInt(get(key));
	}

	/**
	 * Works for "true/false", "enabled/disabled", "yes/no"
	 */
	public boolean booleanValue(String key) {
		String val = get(key);
		
		if(Boolean.parseBoolean(val)) return true;
		else{
			// special tests
			if(val == null || val.equals("")) return false;
			if(val.toLowerCase().trim().equals("enabled")) return true;
			if(val.toLowerCase().trim().equals("ok")) return true;
			if(val.toLowerCase().trim().equals("yes")) return true;
		}
		
		return false;
	}

	@Override
	public long longValue(String key) {
		return Long.parseLong(get(key));
	}

	/**
	 * The value is assumed to be a long integer
	 * 
	 */
	public Date dateValue(String key) {
		lock.lock();
		try {
			String val = get(key);
			if(val.trim().length() != 13)
				throw new RuntimeException("Value does not look like a long that could be used as a date");
			return new java.util.Date(longValue(key));
		}finally{
			lock.unlock();
		}
	}

	/**
	 * I should really deprecate this - you should really use long values as dates in a serialization. See above.
	 */
	public Date dateValue(String key, String format) throws ParseException {
		lock.lock();
		try {
			SimpleDateFormat f = new SimpleDateFormat(format);
			return f.parse(get(key));
		}finally{
			lock.unlock();
		}
	}

	/**
	 * Where the value is a string you wish to tokenize, space delimiter
	 * 
	 */
	public List<String> listValue(String key){
		List<String> l = new ArrayList<String>();
		String val = get(key);
		String [] items = val.split(" ");
		for(String s : items){
			l.add(s);
		}
		return l;
	}
	
	/**
	 * Where the value is a string you wish to tokenize, any delimiter
	 * 
	 */
	public List<String> listValue(String key, String delimiter){
		List<String> l = new ArrayList<String>();
		String val = get(key);
		String [] items = val.split(delimiter);
		for(String s : items){
			l.add(s);
		}
		return l;
	}
	
	/**
	 * Complement of the above
	 * 
	 * @param key
	 * @param ch
	 * @param l
	 */
	public void putList(String key, char ch, List<String> l) {
		StringBuffer buf = new StringBuffer();
		for(String val : l){
			buf.append(val);
			buf.append(ch);
		}
		buf.deleteCharAt(buf.length()-1);
		put(key,buf.toString());
	}

	public BitSet bitsetValue(String key) {
		String value = get(key).trim();
		BitSet set = new BitSet(value.length());
		for(int i=0;i<value.length();i++){
			set.set(i,value.charAt(i)=='1' ? true : false);
		}
		return set;
	}

	public BigInteger bigValue(String key) {
		return new BigInteger(get(key).trim());
	}

	public BigDecimal bigDecimalValue(String key) {
		return new BigDecimal(get(key).trim());
	}

	public Object beanValue(Class<?> clazz, String keyBase) {
		  
		  try {  
			Object obj = clazz.newInstance();
			// collect the keys which match
			for(Entry e : list){
				String k = e.key;
				if(k.startsWith(keyBase)){
					// verify key is actually of the correct form, with a dot and a terminal identifier
					String remainder = k.substring(keyBase.length(),k.length());
					Matcher matcher = dotIdentifierPattern.matcher(remainder);
					if(matcher.matches()){
						String identifier = matcher.group(1);
						String value = get(k);
						AccessorMethodSetter setter = new AccessorMethodSetter(clazz, obj, identifier, value);
						setter.set();
						if(setter.success()){
						
						}else{
							
						}
					
					}else{
						continue;
					}
				}
			}
			
			return obj;
				
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}


	public Node getTree(){
		return getTree(new GroupParams());
	}
	
	public Node getTree(GroupParams params) {
		lock.lock();
		try {
			TreeBuilder builder = new TreeBuilder(this,params.rootNodeName);
			for(Entry e : list){
				String key = e.key;
				if(key.startsWith(params.getPartialKey())){
					ValueModel value = find(key).getModel();
					builder.createNode(key, value, params.getSeparator());
				}
			}
			
			return builder.tree();
			
		}finally{
			lock.unlock();
		}
	}
	
	public Properties getGroup(GroupParams params){
		PropertiesImpl impl = new PropertiesImpl();
		for(String key : this.getPropertyMap().keySet()){
			if(key.startsWith(params.getPartialKey())){
				ValueModel value = find(key).getModel();
				impl.getPropertyMap().put(key, value);
			}
		}
		return impl;
	}

	public List<String> getComments(String key) {
		lock.lock();
		try {
			if(!containsKey(key)) return null;
			return getPropertyMap().get(key).getComments();
		}finally{
			lock.unlock();
		}
	}	

	public char getSeparator(String key) {
		lock.lock();
		try {
			if(!containsKey(key)) return '\0';
			return find(key).getModel().getSeparator();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Useful for adding e.g., ImmutableValueObjects
	 * 
	 */
	public void put(Entry entry) {
		update(entry);
	}

	public void put(String key, String ... values){
		lock.lock();
		try {
			if(!containsKey(key)){
				update(new Entry(key, new BasicValueModel(values)));
			}else{
				ValueModel val = find(key).getModel();
				val.getValues().clear();
				for(String s:values){
					val.getValues().add(s);
				}
			}
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void put(String key, int val) {
		put(key,String.valueOf(val));
	}

	@Override
	public void put(String key, float val) {
		put(key,String.valueOf(val));
	}

	@Override
	public void put(String key, double val) {
		put(key,String.valueOf(val));
	}

	@Override
	public void put(String key, boolean val) {
		put(key,String.valueOf(val));
	}

	@Override
	public void put(String key, char val) {
		put(key,String.valueOf(val));
	}

	@Override
	public void put(String key, BigInteger bi) {
		put(key,bi.toString());
	}

	@Override
	public void put(String key, BigDecimal bd) {
		put(key,bd.toString());
	}

	public void put(String key, Comment comment, String ... values){
		lock.lock();
		try {
			if(!containsKey(key)){
				update(new Entry(key, new BasicValueModel(comment,values)));
			}else{
				ValueModel val = find(key).getModel();
			    if(val instanceof BasicValueModel){
			    	((BasicValueModel)val).addComment(comment.comment);
			    }
				val.getValues().clear();
				for(String s:values){
					val.getValues().add(s);
				}
			}
		}finally{
			lock.unlock();
		}
	}

	public void put(String key, char separator, Comment comment, String ... values){
		lock.lock();
		try {
			if(!containsKey(key)){
				update(new Entry(key, new BasicValueModel(comment,separator,values)));
			}else{
				ValueModel val = find(key).getModel();
			    if(val instanceof BasicValueModel){
			    	((BasicValueModel)val).addComment(comment.comment);
			    	((BasicValueModel)val).setSeparator(separator);
			    }
				val.getValues().clear();
				for(String s:values){
					val.getValues().add(s);
				}
			}
		}finally{
			lock.unlock();
		}
	}

	@Override
	public int size() {
		lock.lock();
		try {
		   return list.size();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void clear() {
		lock.lock();
		try {
		   list.clear();
		}finally{
			lock.unlock();
		}
	}

	@Override
	public boolean containsKey(String key) {
		lock.lock();
		try {
		for(Entry e: list){
			if(e.getKey().equals(key)) return true;
		}
		}finally{
			lock.unlock();
		}
		return false;
	}

	@Override
	public boolean hasValue(String key) {
		lock.lock();
		try {
		for(Entry e: list){
			if(e.getKey().equals(key)){
				if(!e.getModel().getValue().equals("")) return true;
			}
		}
		}finally{
			lock.unlock();
		}
		return false;
	}
	
	

	/**
	 * This is a combine function, existing keys which do not collide
	 * with the incoming are kept, keys that collide are overwritten 
	 * with the new values
	 * 
	 * @param props
	 */
	public Properties merge(Properties props) {
		merge(props,false,false);
		return this;
	}
	
	public Properties merge(Properties props, boolean mergeComments) {
		merge(props,mergeComments,false);
		return this;
	}
	
	/**
	 * merge and deobfuscate not yet implemented
	 */
	public Properties merge(Properties props, boolean mergeComments, boolean deobfuscate) {
		lock.lock();
		try {
			
			if(props instanceof ArrayListPropertiesImpl){
				ArrayListPropertiesImpl impl = (ArrayListPropertiesImpl)props;
					for(Entry e: impl.list) {
						this.update(e);
					}
			}else{
					Iterator<String> iter = props.getPropertyMap().keySet().iterator();
					while(iter.hasNext()){
						String key = iter.next();
						this.update(new Entry(key,props.getPropertyMap().get(key)));
					}
			}
			
		}finally{
			lock.unlock();
		}
		
		return this;
	}

	public void obfuscate(String key){
		String val = this.get(key);
		if(key != null && !key.equals("")){
			String obfuscated = Obfuscate.FACTORY.encrypt(val);
			this.put(key, obfuscated);
		}
	}
	
	public void deobfuscate(String key){
		String val = this.get(key);
		if(key != null && !key.equals("")){
			String deobfuscated = Obfuscate.FACTORY.decrypt(val);
			this.put(key, deobfuscated);
		}
	}
	
	public char[] deobfuscateToChar(String key){
		String val = this.get(key);
		return Obfuscate.FACTORY.decryptToChar(val,StandardCharsets.UTF_8);
	}

	/**
	 * Caution
	 * 
	 */
	@Override
	public void mergeIntoSystemProperties() {
		Map<String,ValueModel> map = getPropertyMap();
		for(String key : map.keySet()){
			System.setProperty(key, map.get(key).getValue());
		}
	}

	/**
	 * Given a base like key, collect keys like key.0, key.1, key.2 etc into a list. The keys need not 
	 * be in order in the file
	 */
	public List<String> getList(String keyBase) {
		List<String> l = new ArrayList<String>();
		Map<Integer,String> numberedMap = new TreeMap<Integer,String>();
		
		// collect the keys which match
		for(Entry e : list){
			String key = e.getKey();
			if(key.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot and a terminal integer
				String remainder = key.substring(keyBase.length(),key.length());
				Matcher matcher = dotIntegerPattern.matcher(remainder);
				if(matcher.matches()){
					Integer keyInt = Integer.parseInt(matcher.group(1));
					numberedMap.put(keyInt,find(key).getModel().getValue());
				}else{
					continue;
				}
			}
		}
		
		if(numberedMap.size() == 0) return l;
		//collect the values in order of the numbers
		Set<Integer> numberKeySet = numberedMap.keySet();
		for(Integer i: numberKeySet){
			l.add(numberedMap.get(i));
		}
		return l;
	}
	
	public void put(String key, char separator, String ... values){
		lock.lock();
		try {
			if(!containsKey(key)){
				update(new Entry(key, new BasicValueModel(separator,values)));
			}else{
				ValueModel val = find(key).getModel();
			    if(val instanceof BasicValueModel){
			    	((BasicValueModel)val).setSeparator(separator);
			    }
				val.getValues().clear();
				for(String s:values){
					val.getValues().add(s);
				}
			}
		}finally{
			lock.unlock();
		}
	}

	@Override
	public void putList(List<String> list, String rootKey) {
		int count = 0;
		for(String val : list){
			StringBuffer buf = new StringBuffer(rootKey);
			buf.append(".");
			buf.append(count);
			put(buf.toString(),val);
			count++;
		}
	}

	public void putList(List<String> list, Comment comment, String rootKey) {
		int count = 0;
		for(String val : list){
			StringBuffer buf = new StringBuffer(rootKey);
			buf.append(".");
			buf.append(count);
			if(count == 0) put(buf.toString(),comment,val);
			else put(buf.toString(),val);
			count++;
		}
	}
	
	public void putList(List<String> list, char sep, Comment comment, String rootKey) {
		int count = 0;
		for(String val : list){
			StringBuffer buf = new StringBuffer(rootKey);
			buf.append(".");
			buf.append(count);
			if(count == 0) put(buf.toString(),sep,comment,val);
			else put(buf.toString(),sep,val);
			count++;
		}
	}
	

	public List<String> getListKeys(String keyBase) {
		
		List<String> l = new ArrayList<String>();
		
		// collect the keys which match
		for(Entry e : list){
			String k = e.key;
			if(k.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot and a terminal integer
				String remainder = k.substring(keyBase.length(),k.length());
				Matcher matcher = dotIntegerPattern.matcher(remainder);
				if(matcher.matches()){
					l.add(k);
				}else{
					continue;
				}
			}
		}
		//TODO use a comparator
		Collections.sort(l);
		return l;
	}

	public List<String> getMapKeys(String keyBase) {
		
		List<String> l = new ArrayList<String>();
		
		for(Entry e : list){
			String k = e.key;
			if(k.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot, integer, dot, and k or v
				String remainder = k.substring(keyBase.length(),k.length());
				Matcher matcher = dotKeyValuePattern.matcher(remainder);
				if(matcher.matches()){
					l.add(k);
				}else{
					continue;
				}
			}
		}
		Collections.sort(l);
		return l;
	}
	public boolean hasKeyLike(String partial){
		for(String key : this.getPropertyMap().keySet()){
			if(key.startsWith(partial)){
				return true;
			}
		}
		return false;
	}

	public java.util.Properties convertToLegacyProperties() {
		lock.lock();
		try {
		java.util.Properties legacy = new java.util.Properties();
		for(Entry e: list){
			String key = e.key;
			String value = get(key);
			legacy.put(key, value);
		}
		return legacy;
		}finally{
			lock.unlock();
		}
	}

	public void put(String key, List<String> values){
		lock.lock();
		try {
			putList(key,' ',values);
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Return null if out of bounds
	 * @param index
	 * @return
	 */
	public Entry atIndex(int index){
		try {
			return list.get(index);
		}catch(Exception x){
			return null;
		}
	}

}
