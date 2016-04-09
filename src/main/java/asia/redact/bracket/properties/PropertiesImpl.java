/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
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
 * A better Properties class. This implementation class is thread-safe.
 * 
 * @author Dave
 *
 */

public class PropertiesImpl extends AbstractMapDerivedPropertiesBase implements Properties {
	
	private static final long serialVersionUID = 1L;
		
	public PropertiesImpl() {
		super();
		initMap();
	}
	
	protected void initMap() {
		map = new LinkedHashMap<String,ValueModel>();
	}
	
	/**
	 * <pre>
	 * Load from a legacy instance.
	 * Some slight information loss here is unavoidable. For example if a colon is used as the separator in the original,
	 * that information will be lost. If you need a more exact translation you can do that
	 * with the lexer and parser classes by reading in the data directly from the properties file instead
	 * of reading in with java.util.Properties and then converting.
	 * 
	 * </pre>
	 */
	public PropertiesImpl(java.util.Properties legacy) {
		this();
		lock.lock();
		try {
			Set<Object> set = legacy.keySet();
			for(Object key: set) {
				String val = legacy.getProperty(String.valueOf(key));
				//TODO ascii to native filtering goes here
				this.put(String.valueOf(key), val);
			}
		}finally{
			lock.unlock();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public PropertiesImpl(Map map){
		Iterator iter = map.keySet().iterator();
		while(iter.hasNext()) {
			Object key = iter.next();
			Object value = map.get(key);
			this.put(String.valueOf(key), String.valueOf(value));
		}
	}
	
	public PropertiesImpl(URL url){
		this();
		InputStream in = null;
		lock.lock();
		try {
			in = url.openStream();
			PropertiesLexer lexer = new PropertiesLexer(in);
			lexer.lex();
			List<PropertiesToken> list = lexer.getList();
			new PropertiesParser(list, this).parse();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(in != null)
				try {
					in.close();
				} catch (IOException e) {}
			lock.unlock();
		}
	}
	
	public PropertiesImpl(InputStream in){
		this();
		lock.lock();
		try {
			PropertiesLexer lexer = new PropertiesLexer(in);
			lexer.lex();
			List<PropertiesToken> list = lexer.getList();
			new PropertiesParser(list, this).parse();
		}finally{
			lock.unlock();
		}
	}
	
	public PropertiesImpl(InputStream in, Charset charset){
		this();
		lock.lock();
		try {
			PropertiesLexer lexer = new PropertiesLexer(in, charset);
			lexer.lex();
			List<PropertiesToken> list = lexer.getList();
			new PropertiesParser(list, this).parse();
		}finally{
			lock.unlock();
		}
	}
	
	public PropertiesImpl(Reader in){
		this();
		lock.lock();
		try {
			PropertiesLexer lexer = new PropertiesLexer(in);
			lexer.lex();
			List<PropertiesToken> list = lexer.getList();
			new PropertiesParser(list, this).parse();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Get the value of the property; concatenate multiple lines.
	 * @param key
	 * @return
	 * @throws RuntimeException if key is not present.
	 * 
	 */
	public String get(String key){
		lock.lock();
		try {
			ValueModel val = map.get(key);
			if(val==null) throw new RuntimeException("Missing value "+key+". Normally you would test for the existence of keys by using containsKey(key) prior to using get() if there is doubt");
			return val.getValue();
		}finally{
			lock.unlock();
		}
	}
	
	public String getKeyRef(String key){
		String v = get(key);
		if(v.startsWith(REF_TOKEN)){
			return getKeyRef(v.substring(2));
		}else return v;
	}
	
	public void putKeyRef(String newRefKey, String existingKey){
		if(!containsKey(existingKey)) {
			throw new RuntimeException("Should be existing key: "+existingKey);
		}
		put(newRefKey, REF_TOKEN+existingKey);
	}
	
	public void put(Entry entry){
		map.put(entry.key,entry.model);
	}
	
	/**
	 * Yes, multi-valued (multi-lined) properties are in the spec. If the key already exists, all
	 * the values are removed and replaced
	 * 
	 * @param key
	 * @param values
	 */
	public void put(String key, String ... values){
		lock.lock();
		try {
			if(!map.containsKey(key)){
				map.put(key, new BasicValueModel(values));
			}else{
				ValueModel val = map.get(key);
				val.getValues().clear();
				for(String s:values){
					val.getValues().add(s);
				}
			}
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
	
	public void put(String key, Comment comment, String ... values){
		lock.lock();
		try {
			if(!map.containsKey(key)){
				map.put(key, new BasicValueModel(comment,values));
			}else{
				ValueModel val = map.get(key);
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
	
	public void put(String key, char separator, String ... values){
		lock.lock();
		try {
			if(!map.containsKey(key)){
				map.put(key, new BasicValueModel(separator,values));
			}else{
				ValueModel val = map.get(key);
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
	
	public void put(String key, char separator, Comment comment, String ... values){
		lock.lock();
		try {
			if(!map.containsKey(key)){
				map.put(key, new BasicValueModel(comment,separator,values));
			}else{
				ValueModel val = map.get(key);
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
	
	// just syntactic sugar
	
	public void put(String key, int val){
		put(key,String.valueOf(val));
	}
	
	public void put(String key, BigInteger bi){
		put(key,String.valueOf(bi));
	}
	
	public void put(String key, BigDecimal bd){
		put(key,String.valueOf(bd));
	}
	
	public void put(String key, float val){
		put(key,String.valueOf(val));
	}
	
	public void put(String key, long val){
		put(key,String.valueOf(val));
	}
	
	public void put(String key, double val){
		put(key,String.valueOf(val));
	}
	
	public void put(String key, boolean val){
		put(key,String.valueOf(val));
	}
	
	public void put(String key, char val){
		put(key,String.valueOf(val));
	}
	
	

	/* (non-Javadoc)
	 * @see asia.redact.bracket.properties.HasMap#getMap()
	 */
	public Map<String, ValueModel> getPropertyMap() {
		return map;
	}
	
	public synchronized Map<String,String> getFlattenedMap() {
		lock.lock();
		try{
		LinkedHashMap<String,String> out = new LinkedHashMap<String,String>();
		Iterator<String> iter = map.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			String value = map.get(key).getValue();
			out.put(key, value);
		}
		return out;
		}finally{
			lock.unlock();
		}
	}

	/**
	 * 
	 */
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
			return getPropertyMap().get(key).getSeparator();
		}finally{
			lock.unlock();
		}
	}
	
	public List<String> getKeyGroup(String keyBase){
		lock.lock();
		try {
			List<String> list = new ArrayList<String>();
			for(String s: map.keySet()){
				if(s.indexOf(keyBase) == 0){
					list.add(s);
				}
			}
			return list;
		}finally{
			lock.unlock();
		}
	}
	
	@Override
	public int hashCode() {
		lock.lock();
		try {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((map == null) ? 0 : map.hashCode());
			return result;
		}finally{
			lock.unlock();
		}
	}

	@Override
	public boolean equals(Object obj) {
		lock.lock();
		try {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			PropertiesImpl other = (PropertiesImpl) obj;
			if (map == null) {
				if (other.map != null)
					return false;
			} else if (!map.equals(other.map))
				return false;
			return true;
		}finally{
			lock.unlock();
		}
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
	
	public Properties merge(Properties props, boolean mergeComments, boolean deobfuscate) {
		lock.lock();
		try {
			Set<String> set = props.getPropertyMap().keySet();
			for(String key: set) {
				if(mergeComments){
					List<String> comments = this.getComments(key);
					List<String> newComments = props.getComments(key);
					if(comments == null) comments = new ArrayList<String>();
					if(newComments != null){
						for(String s: newComments) comments.add(s);
					}
					BasicValueModel model = new BasicValueModel(comments,
							props.getPropertyMap().get(key).getValues());
					this.getPropertyMap().put(key, model);
				}else{
					this.getPropertyMap().put(key, props.getPropertyMap().get(key));
				}
			}
			
			if(deobfuscate){
				
				for(String key: set) {
					BasicValueModel obj = (BasicValueModel) this.getPropertyMap().get(key);
					try {
						String deobfuscated = Obfuscate.FACTORY.decrypt(obj.getValue());
						obj.clearValues();
						obj.addValue(deobfuscated);
					}catch(RuntimeException x){
						// TODO log error
					}
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
	 * Use "\\." as the separator
	 */
	public Node getTree(){
		return getTree(new GroupParams());
	}
	
	public Node getTree(GroupParams params) {
		lock.lock();
		try {
			TreeBuilder builder = new TreeBuilder(this,params.rootNodeName);
			Set<String> keys = map.keySet();
			for(String key : keys){
				if(key.startsWith(params.getPartialKey())){
					ValueModel value = map.get(key);
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
				ValueModel value = map.get(key);
				impl.getPropertyMap().put(key, value);
			}
		}
		return impl;
	}
	
	public Properties slice(String root){
		PropertiesImpl impl = new PropertiesImpl();
		for(String key : this.getPropertyMap().keySet()){
			if(key.startsWith(root)){
				ValueModel value = map.get(key);
				impl.getPropertyMap().put(key, value);
			}
		}
		return impl;
	}
	
	/**
	 * Return the keys which match the partial key submitted
	 * 
	 * @param partial
	 * @return
	 */
	public List<String> getMatchingKeys(String partial){
		ArrayList<String> list = new ArrayList<String>();
		for(String key : this.getPropertyMap().keySet()){
			if(key.startsWith(partial)){
				ValueModel value = map.get(key);
			    list.add(value.getValue());
			}
		}
		return list;
	}
	
	public boolean hasKeyLike(String partial){
		for(String key : this.getPropertyMap().keySet()){
			if(key.startsWith(partial)){
				return true;
			}
		}
		return false;
	}

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

	public long longValue(String key) {
		return Long.parseLong(get(key));
	}
	
	public float floatValue(String key) {
		return Float.parseFloat(get(key));
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

	public boolean hasValue(String key) {
		lock.lock();
		try {
			String val = get(key);
			return val != null && (!val.equals(""));
		}finally{
			lock.unlock();
		}
	}

	/**
	 * Caution
	 * 
	 */
	public void mergeIntoSystemProperties() {
		Map<String,ValueModel> map = getPropertyMap();
		for(String key : map.keySet()){
			System.setProperty(key, map.get(key).getValue());
		}
	}
	
	public List<String> getMapKeys(String keyBase) {
		
		List<String> list = new ArrayList<String>();
		
		Set<String> keys = map.keySet();
		// collect the keys which match
		for(String k : keys){
			if(k.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot, integer, dot, and k or v
				String remainder = k.substring(keyBase.length(),k.length());
				Matcher matcher = dotKeyValuePattern.matcher(remainder);
				if(matcher.matches()){
					list.add(k);
				}else{
					continue;
				}
			}
		}
		//TODO use a comparator
		Collections.sort(list);
		return list;
	}
	
	public List<String> getListKeys(String keyBase) {
		
		List<String> list = new ArrayList<String>();
		
		Set<String> keys = map.keySet();
		// collect the keys which match
		for(String k : keys){
			if(k.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot and a terminal integer
				String remainder = k.substring(keyBase.length(),k.length());
				Matcher matcher = dotIntegerPattern.matcher(remainder);
				if(matcher.matches()){
					list.add(k);
				}else{
					continue;
				}
			}
		}
		//TODO use a comparator
		Collections.sort(list);
		return list;
	}

	public List<String> getList(String keyBase) {
		List<String> list = new ArrayList<String>();
		Map<Integer,String> numberedMap = new TreeMap<Integer,String>();
		Set<String> keys = map.keySet();
		// collect the keys which match
		for(String k : keys){
			if(k.startsWith(keyBase)){
				// verify key is actually of the correct form, with a dot and a terminal integer
				String remainder = k.substring(keyBase.length(),k.length());
				Matcher matcher = dotIntegerPattern.matcher(remainder);
				if(matcher.matches()){
					Integer keyInt = Integer.parseInt(matcher.group(1));
					numberedMap.put(keyInt,map.get(k).getValue());
				}else{
					continue;
				}
			}
		}
		
		if(numberedMap.size() == 0) return list;
		//collect the values in order of the numbers
		Set<Integer> numberKeySet = numberedMap.keySet();
		for(Integer i: numberKeySet){
			list.add(numberedMap.get(i));
		}
		return list;
	}
	
	public List<String> listValue(String key){
		List<String> list = new ArrayList<String>();
		String val = get(key);
		String [] items = val.split(" ");
		for(String s : items){
			list.add(s);
		}
		return list;
	}
	
	public List<String> listValue(String key, String delimiter){
		List<String> list = new ArrayList<String>();
		String val = get(key);
		String [] items = val.split(delimiter);
		for(String s : items){
			list.add(s);
		}
		return list;
	}
	
	public void putList(String key, char ch, List<String> list) {
		StringBuffer buf = new StringBuffer();
		for(String val : list){
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
		Set<String> keys = map.keySet();
		// collect the keys which match
		for(String k : keys){
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
	
	public java.util.Properties convertToLegacyProperties() {
		java.util.Properties legacy = new java.util.Properties();
		Iterator<String> iter = getPropertyMap().keySet().iterator();
		while(iter.hasNext()) {
			String key = iter.next();
			String value = get(key);
			legacy.put(key, value);
		}
		return legacy;
	}
	

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
	
	public void delete(String key){
		map.remove(key);
	}
}
