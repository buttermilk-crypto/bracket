# bracket

bracket properties is a library to work with java .properties files. It has many features missing from the 
core java implementation such as retention of order and UTF-8 support. If you can think of something you
wish java properties files could do better, chances are bracket-properties already has it. 

## Quickstart

  // Get properties from various input sources

Reader reader = new FileReader("file.properties");
Properties props = Properties.Factory.getInstance(reader);

File file = new File("file.properties");
Properties props = Properties.Factory.getInstance(file, Charset.forName("UTF-8");


InputStream in = getClass().getResourceAsStream("/log4j-example.properties");
Properties props = Properties.Factory.getInstance(in);


URL url = new URL("http://www.somewhere.com/getProps");
Properties props = Properties.Factory.getInstance(url);

java.util.Properties props = new Properties(); 
props.load(somefile); 
Properties bracketProps = Properties.Factory.getInstance(props);
    
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
     classpath.append(File.pathSeparator); i++; 
} 
  System.err.println(classpath.toString());

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
 
 For the common case of java.util.Properties compatibility in US-ASCII encoding with embedded unicode escapes, 
 AsciiOutputFormat is provided:

AsciiOutputFormat format = new AsciiOutputFormat(); 
OutputAdapter out = new OutputAdapter(props); 
File file = new File("my.properties");
out.writeAsciiTo(file,format); 

or just use

out.writeAsciiTo(file);
 
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


