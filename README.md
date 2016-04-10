# bracket

bracket properties is a library to work with java .properties files. It has many features missing from the 
core java implementation such as retention of order and UTF-8 support. If you can think of something you
wish java properties files could do better, chances are bracket-properties already has it. 

## Quickstart

For maven, use:

	<dependency>
	    <groupId>asia.redact.bracket.properties</groupId>
	    <artifactId>bracket-properties</artifactId>
	    <version>1.3.6</version>
	</dependency>

For ant, download the jar from [Link] (https://www.cryptoregistry.com/downloads/bracket-properties/1.3.6/bracket-properties-1.3.6.jar)

##Instantiation


	// Get properties from various input sources
	
	Reader reader = new FileReader("file.properties");
	Properties props = Properties.Factory.getInstance(reader);
	
	File file = new File("file.properties");
	Properties props = Properties.Factory.getInstance(file, Charset.forName("UTF-8");
	
	
	InputStream in = getClass().getResourceAsStream("/log4j-example.properties");
	Properties props = Properties.Factory.getInstance(in);
	
	
	URL url = new URL("http://www.somewhere.com/getProps");
	Properties props = Properties.Factory.getInstance(url);
	
More advanced:

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
	
## Underlying Data Model

The data model is a map with ValueModel keys:

	//data 
	protected AbstractMap<String,ValueModel> map;
	
	// in the typical case this is a LinkedHashMap so order is preserved
	protected void initMap() {
		map = new LinkedHashMap<String,ValueModel>();
	}
	
The accessor api has several useful methods:

	Map<String,ValueModel> m = props.getPropertyMap(); // return the underlying map of ValueObjects
	Map<String,String> m = props.getFlattenedMap(); // return a map where multilines have been flattened
	
The ValueModel has a `getValue()` method which concatenates multilines, or you can get them as a list:
	
	ValueModel model = props.getPropertyMap().get(key);
	char sep = model.getSeparator();
	String concatenated =model.getValue();
	List<String> values = model.getValues();
	List<String> comments = model.getComments();
	

## Multiline Support

	Properties props = Properties.Factory.getInstance();
	props.put("key1", "first line ", "\ncontinuation1 ", "\ncontinuation2");
	System.out.println("key1: " + props.get("key1"));
	
Output is

	key1: first line 
	continuation1 
	continuation2

## Comment Support

Comments are retained and re-serialized as expected in most cases. The internal data structure basically sees comments
as something above, rather than below, a key value pair, so don't comment below things and then things will be grouped
as you would probably expect.

	String key2 = "key2";
	props.put(key2, new Comment("# You rock"), "my value");
	System.err.println(props.getPropertyMap().get(key2).asKeyValueRep(key2));
		
Output is

	# You rock
	key2=my value

 All of the Factory object creation methods will parse comments on the input and do the right thing on re-serialization.

## Accessors Support
 
 We have lots of syntactic sugar:
 
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
	
## Friendly Boolean Values

	props.put("myflag", true);
	props.put("myflag0", "enabled");
	props.put("myflag1", "ok");
	props.put("myflag2", "yes");
	props.put("myflag3", "disabled");
	props.put("myflag4", "no");
	
	props.booleanValue("myflag"); // returns true
	props.booleanValue("myflag0"); // returns true
	props.booleanValue("myflag1"); // returns true
	props.booleanValue("myflag2"); // returns true
	props.booleanValue("myflag3"); // returns false
	props.booleanValue("myflag4"); // returns false


## List Support

 Start with a file like this:

	wrapper.java.classpath.1=../lib/wrapper.jar 
	wrapper.java.classpath.2=../lib/myapp.jar 
	wrapper.java.classpath.3=../lib/mysql.jar 
	wrapper.java.classpath.4=../classes 
	wrapper.java.classpath.10=../lib/wrapper2.jar


Get those properties as a list:


	InputStream in = getClass().getResourceAsStream("/ListResources/list.properties"); 
	Properties props = Properties.Factory.getInstance(in); 
	
	// getList method
	List<String> list = props.getList("wrapper.java.classpath"); 
	StringBuffer classpath = new StringBuffer(); 
	int i = 0; 
	for(String s: list){ 
	  classpath.append(s); 
	      if(i<list.size()-1) 
	          classpath.append(File.pathSeparator); 
	  i++; 
	} 
	  System.out.println(classpath.toString());

Which outputs:

	../lib/wrapper.jar;../lib/myapp.jar;../lib/mysql.jar;../classes;../lib/wrapper2.jar

You can also create list properties using putList():

	List<String> fruit = new ArrayList<String>(); 
	fruit.add("apple"); 
	fruit.add("orange"); 
	fruit.add("banana"); 
	props.putList(fruit, "fruit");


This will create keys like

	fruit.0=apple 
	fruit.1=orange 
	fruit.2=banana


## Serialization Done Correctly

	OutputAdapter out = new OutputAdapter(props); 
	Writer w = new StringWriter(); 
	out.writeTo(w); 
  
or

	MyOutputFormat format = new MyOutputFormat(); 
	OutputAdapter out = new OutputAdapter(props); 
	Writer w = new StringWriter(); 
	out.writeTo(w, format); 
 
For the common case of java.util.Properties compatibility in US-ASCII encoding with embedded Unicode escapes, 
AsciiOutputFormat is provided:

	AsciiOutputFormat format = new AsciiOutputFormat(); 
	OutputAdapter out = new OutputAdapter(props); 
	File file = new File("my.properties");
	out.writeAsciiTo(file,format); 

or just use

    out.writeAsciiTo(file);
    
there is also the simple

	OutputAdapter.toString(props);

 
## Easy Externalization 

	// some externalized properties in user.home 
	String home = System.getProperty("user.home"); 
	String adminExternalProps = home+File.separator+"administrator.properties";
	
	// some defaults in a template loaded from an embedded classpath
	String templateProps = "/template.properties";
	
	List<PropertiesReference> refs = new ArrayList<PropertiesReference>();
	refs.add(new PropertiesReference(ReferenceType.CLASSLOADED,templateProps));
	refs.add(new PropertiesReference(ReferenceType.EXTERNAL,adminExternalProps));
	
	// these will load in order and override as expected
	Properties props = Properties.Factory.loadReferences(refs);


## Obfuscation

Version 1.3.6 and above has a nice obfuscator built-in.

    String password1 = "password1";
    String stringBase64 = Obfuscate.FACTORY.encrypt(password1);
    String result = Obfuscate.FACTORY.decrypt(stringBase64);
    
The api for it looks like this:

    Properties props = Properties.Factory.getInstance();
    props.put("key", "value");
		
	// obfuscate value internally
	props.obfuscate("key");
		
	System.out.println(props.get("key")); // prints e.g., nqJxv2ZOfgcLrOvUd9r1aJ0TK5aVTxZmVbgKbJsGzFQ=
		
	// deobfuscate value internally
	props.deobfuscate("key");
		
	System.out.println(props.get("key")); // prints "value"
	Assert.assertEquals(props.get("key"), "value");


The algorithm is good enough that each invocation creates a different obfuscation.

 
## XML Support

Not sure how useful this is but some people seem interested. 

input file:

	a.b.c=value
	a.b.c.d=subvalue1
	a.b.c.e=subvalue2

Code:

	Properties props = Properties.Factory.getInstance();
	
	props.put("a.b.c", "value");
	props.put("a.b.c.d", "subvalue1");
	props.put("a.b.c.e", "subvalue2");
	
	OutputAdapter outAdapter = new OutputAdapter(props);
	StringWriter writer = new StringWriter();
	outAdapter.writeAsXml(writer);

Output:

	<?xml version="1.0" encoding="UTF-8"?>
	 <nproperties xmlns="http://github.com/buttermilk-crypto/bracket">
	  <na>
	   <nb>
	    <nc>
	     <s>=</s>
	     <v><![CDATA[value]]></v>
	     <nd>
	      <s>=</s>
	      <v><![CDATA[subvalue1]]></v>
	    </nd>
	     <ne>
	      <s>=</s>
	      <v><![CDATA[subvalue2]]></v>
	    </ne>
	   </nc>
	  </nb>
	 </na>
	</nproperties>



##Immutable Properties Support

The idea is that once loaded the properties are both thread-safe and unchangable. I think this is prudent and 
actually often what is wanted for a config file.

	props = Properties.Factory.immutableInstance(props);

	props.put("key", "value"); //throws an UnsupportedOperationException
	
	



 



