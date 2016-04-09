/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.demo;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Comparator;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.Comment;
import asia.redact.bracket.properties.OutputAdapter;
import asia.redact.bracket.properties.PlainOutputFormat;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.PropertiesImpl;
import asia.redact.bracket.properties.PropertiesLexer;
import asia.redact.bracket.properties.PropertiesParser;
import asia.redact.bracket.properties.PropertiesToken;
import asia.redact.bracket.properties.ValueModel;
import asia.redact.bracket.util.ClassPathBuilder;


public class DemoTest1 {
	
	// Basic Use
	
	// first, about java.util.Properties
	
	@Test
	public void example0() {
		
		java.util.Properties p = new java.util.Properties();
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo.properties");
		try {
			p.load(in);
			p.get((Object) "item0");
			p.getProperty("itemX", "x");
			StringWriter writer = new StringWriter();
			p.store(writer, "comment");
			System.err.println(writer.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// takes a PrintStream
	//	p.list(System.out);
		
		Set<Object> keys = p.keySet();
		Iterator<Object> iter = keys.iterator();
		while(iter.hasNext()){
			String key = (String) iter.next();
			String value = p.getProperty(key);
			System.out.println(key+"="+value);
		}
	}
	
	@Test
	public void canonicalUse0() {
	
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo.properties");
		Properties props = Properties.Factory.getInstance(in);
		if(props.containsKey("item0")){
			Assert.assertTrue(props.containsKey("item0"));
		}
		
		StringWriter writer = new StringWriter();
		OutputAdapter adapter = new OutputAdapter(props);
		try {
			// adapter.writeTo(writer);
			adapter.writeTo(writer, new PlainOutputFormat());
		//	System.out.println(writer.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// syntactic sugar for above
		 System.out.println(OutputAdapter.toString(props));
	}
	
	@Test
	public void instantiation() throws Exception {
		
		Properties props; // interface asia.redact.bracket.properties.Properties
		
		props = Properties.Factory.getInstance();   // return an empty properties object
		props.put("key", "value");

        // Get properties from various input sources

        Reader reader = new FileReader("src/test/resources/demo/template.properties");
        props = Properties.Factory.getInstance(reader);

        File file = new File("src/test/resources/demo/template.properties");
        props = Properties.Factory.getInstance(file, Charset.forName("UTF-8"));

        InputStream in = getClass().getResourceAsStream("/log4j-example.properties");
        props = Properties.Factory.getInstance(in);

        String urlPath = "https://raw.githubusercontent.com/buttermilk-crypto/bracket/master/src/test/resources/10props.properties";
        URL url = new URL(urlPath);
        props = Properties.Factory.getInstance(url);
        
	}
	
	public void intantiations2(){
		
		Properties props; // interface asia.redact.bracket.properties.Properties
		
		props = Properties.Factory.getInstance();   // return an empty properties object
		props.put("key", "value");
		
		// from a map
		Map<String,String> map = new LinkedHashMap<String,String>(); 
		map.put("item0", "value0");
		props = Properties.Factory.getInstance(map);
		
		// from legacy java.util
		java.util.Properties legacy = new java.util.Properties();
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo.properties");
		try {
			legacy.load(in);
		} catch (IOException e) {}
		props = Properties.Factory.getInstance(legacy);
		
		// sorted instances
		props = Properties.Factory.sortedInstance(props);
		props = Properties.Factory.sortedInstance(props, new Comparator<String>() {
			public int compare(String key0, String key1) {
				return key0.compareTo(key1);
			}
		});
		
		// list backed instance
		props = Properties.Factory.listBackedInstance(props);
		
		// an Immutable instance with immutable ImmutableValueModel values
		props = Properties.Factory.immutableInstance(props);
		
	}
	
	@Test
	public void accessorsBasic() {
		
		Properties props = Properties.Factory.getInstance();
		
		props.put("key0", "val");
		props.put("key1", "first line ", "\ncontinuation1 ", "\ncontinuation2");
		System.err.println("key1: " + props.get("key1"));
		
		String key2 = "key2";
		props.put(key2, new Comment("# You rock"), "my value");
		System.err.println(props.getPropertyMap().get(key2).asKeyValueRep(key2));
		
		props.put(key2, ':', new Comment("# You rock"), "my value");
		System.err.println(props.getPropertyMap().get(key2).asKeyValueRep(key2));
		
		props.put("key.int", 10);
		props.put("key.float", 1f);
		props.put("key.long", 1L);
		props.put("key.bool", true);
		props.put("key.date", new Date().getTime());
		props.put("key.biginteger", new BigInteger("1"));
		props.put("key.bigdecimal", new BigDecimal(Math.PI));
		props.put("key.bitset", "0111");
		
		props.intValue("key.int");
		props.floatValue("key.float");
		props.longValue("key.long");
		props.booleanValue("key.bool");
		props.dateValue("key.date");
		props.bigValue("key.biginteger");
		props.bigDecimalValue("key.bigdecimal");
		
		BitSet bs = props.bitsetValue("key.bitset");
		for(int i = 0; i<bs.length(); i++) {
			System.err.println(bs.get(i));
		}
		
	}
	
	@SuppressWarnings("unused")
	@Test
	public void accessorsBasic2() {
		
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo1.properties");
		Properties props = Properties.Factory.getInstance(in);
		
		String val = props.get("key.string1");
		
		String key = "key.string2";
		String val2 = props.get(key);
		
		ValueModel model = props.getPropertyMap().get(key);
		char sep = model.getSeparator();
		String concatenated =model.getValue();
		List<String> values = model.getValues();
		List<String> comments = model.getComments();
		
		System.err.println(model.asKeyValueRep(key));
		
		props.intValue("key.int");
		props.longValue("key.long");
		props.booleanValue("key.bool");
		props.dateValue("key.date.long");
		props.bigValue("key.biginteger");
		
	}
	
	@Test
	public void lists() {
		
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo2.properties");
		Properties props = Properties.Factory.getInstance(in);
		
		String keyBase = "wrapper.java.classpath";
		@SuppressWarnings("unused")
		List<String> cp = props.getList(keyBase); // loop through this list
		
		ClassPathBuilder builder = new ClassPathBuilder(props);
		String classpath = builder.build(keyBase);
		System.out.println(classpath);
	}
	
	@Test
	public void lists2() {
		
		Properties props = Properties.Factory.getInstance();
		
		// building up a list
		String keyBase = "shopping";
		List<String> items = new ArrayList<String>();
		items.add("Peas");
		items.add("Carrots");
		items.add("Onions");
		props.putList(items, new Comment("# the shopping list"), keyBase);
		System.out.println(OutputAdapter.toString(props));
		
	}
	
	
	@Test
	public void underTheCoversUse0() {
	
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/demo.properties");
		PropertiesLexer lexer = new PropertiesLexer(new InputStreamReader(in,Charset.forName("UTF-8")));
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		
		PropertiesImpl props = new PropertiesImpl();
		PropertiesParser parser = new PropertiesParser(list, props);
		parser.setTrimValues(true);
		parser.parse();
		
		System.out.println(OutputAdapter.toString(props));
		
	}
	
}
