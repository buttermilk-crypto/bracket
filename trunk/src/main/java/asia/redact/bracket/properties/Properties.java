/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.util.BitSet;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import asia.redact.bracket.properties.alt.DotPropertiesParser;
import asia.redact.bracket.properties.line.LineScanner;
import asia.redact.bracket.properties.mgmt.Attributes;
import asia.redact.bracket.properties.mgmt.LoadList;
import asia.redact.bracket.properties.mgmt.PropertiesReference;

/**
 * <pre>
 * In the java.util package, Properties is not an Interface. Bracket Properties
 * has one, which allows for both a standard and a sorted implementation. The
 * standard implementation is backed by a LinkedHashMap, which keeps insertion
 * order intact. The sorted implementation can do other things with the order which
 * extend beyond insertion order. These are useful capabilities for non-trivial use 
 * of Properties files.
 * 
 * This interface is also home to the static Factory, which is the suggested way to 
 * instantiate a Bracket Properties object.
 * 
 * </pre>
 * @author Dave
 *
 */
public interface Properties extends Serializable {

	/**
	 * Can be used to get direct access to the Entry data structures
	 * @return
	 */
	public Map<String, ValueModel> getPropertyMap();
	
	/**
	 * Just return Strings as the values
	 * 
	 * @return
	 */
	public Map<String,String> getFlattenedMap();
	
	/**
	 * Get the value.
	 * @param key
	 * @return
	 */
	public String get(String key);
	
	/**
	 * <pre>
	 * System properties and environment variable resolution. 
	 * This uses the familiar "ant" style replacement expression ${name} 
	 * in the property value. 
	 * 
	 * For example: 
	 * 
	 * localpath=${PATH}
	 * user.home=${user.home}
	 * 
	 * </pre>
	 * @param key
	 * @return
	 */
	
	public String resolve(String key);
	
	/**
	 * Coerce the value to an integer. Obviously this works better if the value is actually an integer.
	 * @param key
	 * @return
	 */
	public int intValue(String key);
	
	/**
	 * Coerce to boolean. Works for "true/false", "enabled/disabled", "yes/no"
	 */
	public boolean booleanValue(String key);
	/**
	 * Coerce to a long value. Obviously this works better if the value is actually a long.
	 * @param key
	 * @return
	 */
	public long longValue(String key);
	
	/**
	 * Date value here is assumed to be a long
	 * @param key
	 * @return
	 */
	public java.util.Date dateValue(String key);
	
	/**
	 * Just syntactical sugar to use a SimpleDateFormat
	 * 
	 * @param key
	 * @param format
	 * @return
	 * @throws ParseException
	 */
	public java.util.Date dateValue(String key, String format) throws ParseException;
	
	/**
	 * Given a value of the form item1 item2 item3, return a List of those values
	 * 
	 * @param key
	 * @return
	 */
	public List<String> listValue(String key);
	
	/**
	 * Like the above but with a specified delimiter such as item1-item2-item3 where the delimiter is -
	 * 
	 * @param key
	 * @param delimiter
	 * @return
	 */
	public List<String> listValue(String key, String delimiter);
	
	/**
	 * companion of the above. Given a list flatten it to a set of items as a value
	 * 
	 * @param key
	 * @param list
	 */
	public void put(String key, List<String> list);
	
	/**
	 * Return the value as a BitSet. Obviously works better if the value is zeros and ones
	 * @param key
	 * @return
	 */
	public BitSet bitsetValue(String key);
	
	/**
	 * serialized BigInteger support
	 * 
	 * @param key
	 * @return
	 */
	public BigInteger bigValue(String key);
	
	/**
	 * Serialized bigDecimal support
	 * 
	 * @param key
	 * @return
	 */
	public BigDecimal bigDecimalValue(String key);
	
	/**
	 * Serialize an arbitrary object
	 * @param clazz
	 * @param keyBase
	 * @return
	 */
	public Object beanValue(Class<?> clazz, String keyBase);
	
	/**
	 * <pre>Get the properties as a tree of nodes. For example,
	 * 
	 * a.b.c=something
	 * a.b.c.d=something else
	 * 
	 * looks like
	 * 
	 * a
	 *   b
	 *     c - something
	 *       d - something else
	 *       
	 *  This method is identical in results to getTree(regex) where the regex
	 *  is "\\.". That is, the separator token in the key is a full stop
	 *  
	 *  Obviously this works better if your keys are delimited by dot characters
	 *  </pre>
	 *  
	 * @return
	 */
	public Node getTree();
	
	/**
	 * <pre>
	 * Get the properties as a tree of nodes with a selector
	 * 
	 * a.b.c=something
	 * a.b.c.d=something else
	 * a.b.c.e.f=item
	 * a.b.c.e=item2
	 * </pre>
	 * 
	 *
	 */
	public Node getTree(GroupParams params);
	
	/**
	 * Get the list of comments, return an empty list if none
	 * 
	 * @param key
	 * @return
	 */
	public List<String> getComments(String key);
	
	/**
	 * The char found in the parse, normally '='
	 * 
	 * @param key
	 * @return
	 */
	public char getSeparator(String key);
	
	public void put(Entry entry);
	
	/**
	 * Add the key and value or values. Useful with multi-line entries
	 * 
	 * @param key
	 * @param values
	 */
	public void put(String key, String ... values);
	
	public void put(String key, int val);
	public void put(String key, float val);
	public void put(String key, double val);
	public void put(String key, boolean val);
	public void put(String key, char val);
	public void put(String key, BigInteger bi);
	public void put(String key, BigDecimal bd);
	
	public void put(String key, Comment comment, String ... values);
	public void put(String key, char separator, Comment comment, String ... values);
	
	/**
	 * Number of entries in the underlying map
	 * 
	 * @return
	 */
	public int size();
	/**
	 * remove all entries from the underlying map
	 */
	public void clear();
	
	/**
	 * <pre>
	 * get(key) will throw a RuntimeException if the key does not
	 * exist. This method can be used to test for a key prior to
	 * calling get(). 
	 * 
	 * Returns true if the underlying map has this key
	 * 
	 * </pre>
	 * @param key
	 * @return
	 */
	public boolean containsKey(String key);
	
	/**
	 * Returns true if the key exists and has a non-empty value
	 * 
	 * @param key
	 * @return
	 */
	public boolean hasValue(String key);
	
	/**
	 * Overwrite existing keys with the new ones, keep those existing ones that don't collide
	 * This operation is non-destructive on the input
	 * does not concatenate comments
	 * 
	 * @param props
	 * @return the merged properties
	 */
	public Properties merge(Properties props);
	
	/**
	 * Overwrite existing keys with the new ones, keep those existing ones that don't collide
	 * This operation is non-destructive on the input
	 * 
	 * @param props
	 * @return the merged properties
	 */
	public Properties merge(Properties props, boolean mergeComments);
	public Properties merge(Properties props, boolean mergeComments, boolean deobfuscate);
	
	/**
	 * Obfuscate the value so it is more difficult to read. First call "put(key,value)" then "obfuscate(key)" 
	 * 
	 * @param key
	 */
	public void obfuscate(String key);
	
	/**
	 * Obverse of the above
	 * 
	 * @param key
	 */
	public void deobfuscate(String key);
	public char[] deobfuscateToChar(String key);
	
	/**
	 * Convenience method. Put all of our current properties into the System properties using System.setProperty() call on each one
	 * 
	 *  Not easily reversible, use with great care
	 *  
	 */
	public void mergeIntoSystemProperties();
	
	/**
	 * <pre>
	 * This method relies on the convention of using numbers at
	 * the end of a property key to represent a list member. The total 
	 * list is the set of all similar keys with key as the base
	 * and dot delimited integers at the end. Suppose the following 
	 * (from, e.g., the Tanukisoft wrapper.conf):
	 * 
	 * wrapper.java.classpath.1=../lib/wrapper.jar
	 * wrapper.java.classpath.2=../lib/myapp.jar
	 * wrapper.java.classpath.3=../lib/mysql.jar
	 * wrapper.java.classpath.4=../classes
	 * 
	 * Then calling getList("wrapper.java.classpath") would
	 * return all the values above, in numeric order, as a List.
	 * 
	 * If key does not exist but numbered properties exist, the
	 * key is synthesized. if no numbered properties exist, an
	 * empty list is returned. If the key does not exist and no numbered
	 * keys exist, the method returns an empty list
	 * 
	 * Numbers need not be sequential
	 *  
	 *  </pre>
	 * @param key
	 * @return
	 */
	public List<String> getList(String key);
	
	/**
	 * <pre>
	 * create property keys and values based on a root key and a list in the form:
	 * 
	 * rootKey.0= list.get(0);
	 * rootKey.1= list.get(1);
	 * 
	 * and so on. This method is a complement to using getList(rootKey).
	 * 
	 * </pre>
	 * @param list
	 * @param rootKey
	 */
	public void putList(List<String> list, String rootKey);
	public void putList(List<String> list, Comment comment, String rootKey);
	
	/**
	 * Return keys which match the pattern keyBase.0, keyBase.1, keyBase.2, etc.
	 * The list returned is in natural sorted order.
	 * 
	 * @param keyBase
	 * @return
	 */
	public List<String> getListKeys(String keyBase);
	
	/**
	 * Specialty method used with a map that has been serialized in the form keyBase dot integer dot k|v
	 * For example, 
	 * 
	 *  0.0.k=a key
	 *  0.0.v=a value
	 *  
	 */
	public List<String> getMapKeys(String keyBase);
	
	/**
	 * Like containsKey but matching on the partial
	 * 
	 * @param partial
	 * @return
	 */
	public boolean hasKeyLike(String partial);
	
	/**
	 * Get java.util.Properties from ours
	 * 
	 * @return
	 */
	public java.util.Properties convertToLegacyProperties() ;
	
	
	public void delete(String key) ;	
	
	/**
	 * <pre>
	 * Mode is the available combinations of lexer and parser
	 * 
	 * BasicToken - PropertiesLexer and PropertiesParser. 
	 * Input is a String and a list of tokens is created, then the list
	 * is parsed. Trivial. Possibly good for small properties files
	 * 
	 * Compatibility - PropertiesLexer and PropertiesParser.
	 * Same parser as above but an attempt is made to retain 
	 * java.util.Properties compatibility in the parse.
	 * 
	 * Line - LineScanner and PropertiesParser2.
	 * Uses the LineScanner which is essentially a BufferedReader, and there is no
	 * separate token list, parser works directly off lines. Should work best for 
	 * larger properties files. Probably the best option full stop. 
	 * 
	 *  Usage:
	 * 
	 *  Properties.Factory.Mode = Properties.Mode.StreamingToken;
	 *  Properties props = Properties.Factory.getInstance(reader);
	 *  
	 *  </pre>
	 * 
	 * @author Dave
	 *
	 */
	
	public enum Mode {
		BasicToken, Compatibility, Line;
	}
	
	/**
	 * <pre>
	 * The default mode is the trivial memory mode, which is BasicToken.
	 * 
	 * You can change the Mode of the factory globally by setting it to a different value like this:
	 * 
	 * Properties.mode = Mode.Line;
	 * 
	 * which changes the mode (and thus the parser/lexer) to Line.
	 * 
	 * </pre>
	 * 
	 * @author Dave
	 *
	 */
	public static final class Factory {
		
		public static Mode mode = Mode.BasicToken;
		
		public static Properties getInstance(){
			return new PropertiesImpl();
		}
		
		public synchronized static Properties getInstance(URL url){
			try {
				return Factory.getInstance(url.openStream());
			} catch (IOException e) {
				throw new RuntimeException("IOException caught",e);
			}
			
		}
		public synchronized static Properties getInstance(Reader reader){
			switch(mode){
				case BasicToken: return new PropertiesImpl(reader);
				case Compatibility: {
						PropertiesLexer lexer = new PropertiesLexer(reader);
						lexer.lex();
						List<PropertiesToken> list = lexer.getList();
						PropertiesImpl props = new PropertiesImpl();
						PropertiesParser p = new PropertiesParser(list, props);
						p.setTrimValues(true);
						p.parse();
						return props;
				}
				case Line:{
						LineScanner lexer = new LineScanner(reader);
						PropertiesImpl props = new PropertiesImpl();
						new PropertiesParser2(lexer,props).parse();
						return props;
				}
			default:
				break;
		    }
			return new PropertiesImpl(reader);
		}
		
		public synchronized static Properties getInstance(File file, Charset charset){
			FileInputStream stream=null;
			try {
				stream = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(stream,charset);
				return getInstance(reader);
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}finally{
				if(stream !=null)
					try {
						stream.close();
					} catch (IOException e) {}
			}
			
		}
		
		public synchronized static Properties getInstance(InputStream in){
			switch(mode){
				case BasicToken: return new PropertiesImpl(in);
				case Compatibility: {
				
						PropertiesLexer lexer = new PropertiesLexer(in);
						lexer.lex();
						List<PropertiesToken> list = lexer.getList();
						PropertiesImpl props = new PropertiesImpl();
						PropertiesParser p = new PropertiesParser(list, props);
						p.setTrimValues(true);
						p.parse();
						return props;
				}
				case Line:{
						LineScanner lexer = new LineScanner(new InputStreamReader(in));
						PropertiesImpl props = new PropertiesImpl();
						new PropertiesParser2(lexer,props).parse();
						return props;
				}
			default:
				break;
	        }
			return new PropertiesImpl(in);
		}
		
		public synchronized static Properties getInstance(InputStream in, Charset charset){
			switch(mode){
				case BasicToken: return new PropertiesImpl(in);
				case Compatibility: {
				
						PropertiesLexer lexer = new PropertiesLexer(new InputStreamReader(in,charset));
						lexer.lex();
						List<PropertiesToken> list = lexer.getList();
						PropertiesImpl props = new PropertiesImpl();
						PropertiesParser p = new PropertiesParser(list, props);
						p.setTrimValues(true);
						p.parse();
						return props;
				}
				case Line:{
						LineScanner lexer = new LineScanner(new InputStreamReader(in, charset));
						PropertiesImpl props = new PropertiesImpl();
						new PropertiesParser2(lexer,props).parse();
						return props;
					
				}
			default:
				break;
	        }
			return new PropertiesImpl(in, charset);
		}
		
		/**
		 * Load from a legacy Properties file object
		 * 
		 * @param legacy
		 * @return
		 */
		public static Properties getInstance(java.util.Properties legacy){
			return new PropertiesImpl(legacy);
		}
		
		@SuppressWarnings("rawtypes")
		public static Properties getInstance(java.util.Map map){
			return new PropertiesImpl(map);
		}
		
		/**<pre>
		 * Similar to the behavior of a ResourceBundle.
		 * 
		 * baseName is something like a.b.c.MyProperty which with Locale.AU will be 
		 * a search path like /a.b.c.MyProperty_en_AU.properties
		 * 
		 * </pre>
		 * 
		 * @param baseName
		 * @param locale
		 * @return
		 */
		public static Properties getInstance(String baseName, Locale locale) {
			LocaleStringBuilder builder = new LocaleStringBuilder(baseName,locale);
			List<String> list = builder.getSearchStrings();
			PropertiesImpl base = new PropertiesImpl();
			for(String s: list){
				InputStream in = null;
				try {
					in = Thread.currentThread().getClass().getResourceAsStream(s);
					if(in != null){
						base.merge(Properties.Factory.getInstance(in));
					}
				}finally{
					if(in != null)
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
				
			}
			return base;
		}
		
		
		/**
		 * The input file must have been generated by OutputAdapter.writeAsXML(Writer) or meet the same
		 * requirements as regards form. This is not yet documented but looking at the test case files you can
		 * figure it out. 
		 * 
		 * @throws RuntimeException if it fails due to I/O
		 * 
		 * @param file
		 * @return
		 */
		public synchronized static Properties getInstanceFromXML(File file, Charset charset) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				InputStreamReader reader = new InputStreamReader(in, charset);
				BufferedReader breader = new BufferedReader(reader);
				ParseXML parser = new ParseXML();
				parser.parse(breader);
				return parser.getProps();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * If Properties.Factory.mode == Mode.Compatibility, will expect ISO-8859-1. Otherwise,
		 * UTF-8 is used.
		 * 
		 * @param file
		 * @return
		 */
		public synchronized static Properties getInstanceFromXML(File file) {
			FileInputStream in = null;
			try {
				in = new FileInputStream(file);
				Charset charset = null;
				if(Properties.Factory.mode == Mode.Compatibility){
					charset = Charset.forName("ISO-8859-1");
				}else{
					charset = Charset.forName("UTF-8");
				}
				InputStreamReader reader = new InputStreamReader(in, charset);
				BufferedReader breader = new BufferedReader(reader);
				ParseXML parser = new ParseXML();
				parser.parse(breader);
				return parser.getProps();
			} catch (FileNotFoundException e) {
				throw new RuntimeException(e);
			}
		}
		
		/**
		 * Converts a set of properties into an Immutable one
		 * @param props
		 * @return
		 */
		public synchronized static Properties immutableInstance(Properties props){
			Entry[] list = new Entry[props.size()];
			Map<String,ValueModel> map = props.getPropertyMap();
			Iterator<String> iter = map.keySet().iterator();
			int i = 0;
			while(iter.hasNext()){
				String key = iter.next();
				ValueModel model = map.get(key);
				if(model instanceof ImmutableValueModel){
					list[i] = new Entry(key,model);
				}else{
					list[i] = 
							new Entry(key,
									new ImmutableValueModel(
											model.getComments(),
											model.getSeparator(), 
											model.getValues()
					));
				}
				i++;
			}
			
			return new ImmutablePropertiesImpl(list);
		}
		
		/**
		 * Convert Properties to the list-backed implementation
		 * 
		 * @param props
		 * @return
		 */
		public synchronized static Properties listBackedInstance(Properties props){
			ArrayListPropertiesImpl impl = new ArrayListPropertiesImpl();
			return impl.merge(props);
		}
		
		/**
		 * Convert properties to a sorted map backed instance
		 * @param props
		 * @return
		 */
		
		public synchronized static Properties sortedInstance(Properties props){
				SortedPropertiesImpl impl = new SortedPropertiesImpl();
				return impl.merge(props);
		}
		
		public synchronized static Properties sortedInstance(Properties props, Comparator<String> comp){
			SortedPropertiesImpl impl = new SortedPropertiesImpl(comp);
			return impl.merge(props);
		}
		
		public synchronized static Properties getDotInstance(Reader reader){
			LineScanner lexer = new LineScanner(reader);
			DotPropertiesParser p = new DotPropertiesParser(lexer);
			p.parse();
			Properties props = p.getProperties();
			return props;
		}
		
		public synchronized static Properties getDotInstance(InputStream in){
			LineScanner lexer = new LineScanner(new InputStreamReader(in));
			DotPropertiesParser p = new DotPropertiesParser(lexer);
			p.parse();
			Properties props = p.getProperties();
			return props;
		}
		
		public synchronized static Properties loadReferences(List<PropertiesReference> refs){
			Attributes attribs = new Attributes();
			attribs.warnOnNoPropertiesFileExtension = true;
			attribs.useCompatibilityMode = true;
			attribs.locale = Locale.getDefault();
			
			return loadReferences(refs, attribs);
		}
		
		/** 
		 * 
		 * <pre>
		 * Given a set of paths in the OS, load the files one by one into the Properties. This method
		* relies on merge(), which overwrites if it finds a duplicate. So, suppose you have the following
		* 
		* <application>/WEB-INF/myapp.properties
		* <USER_HOME>/.ext/passwords.properties
		* 
		* Then you would do the following:
		* 
		* List<String> list = new List<String>();
		* list.add("C://MyStuff/tomcat/webapps/mywar/WEB-INF/myapp.properties");
		* list.add("C://Users/dsmith/.ext/passwords.properties");
		* Properties props = Properties.Factory.loadReferences(list,attribs);
		* 
		* props will now have properties from myapp.properties and written on top of those will be 
		* contents of passwords.properties
		* 
		* The loadReferences methods support ResourceBundle style localization via attributes.locale. 
		* Set this to the desired locale. Bracket goes beyond ResourceBundles by supporting 
		* *externalized* localization.
		* 
		* </pre>
		* 
		* */
		
		public synchronized static Properties loadReferences(List<PropertiesReference> refs, Attributes attribs){
			
			LoadList list = new LoadList(attribs);
			for(PropertiesReference ref: refs){
				list.addReference(ref);
			}
			
			list.load();
			
			return list.getProps();
		}
		
	}

}