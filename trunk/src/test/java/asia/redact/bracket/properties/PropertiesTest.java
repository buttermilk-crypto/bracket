/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;
import java.util.Locale;
import java.util.Comparator;

import org.junit.Test;

import asia.redact.bracket.properties.GroupParams;
import asia.redact.bracket.properties.LocaleStringBuilder;
import asia.redact.bracket.properties.Node;
import asia.redact.bracket.properties.OutputAdapter;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.BasicValueModel;
import junit.framework.Assert;

public class PropertiesTest {
	

	@Test
	public void test0(){
		Properties props = Properties.Factory.getInstance();
		props.put("test", "value1", ", ", "value2");
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("value1, value2", props.get("test"));
		OutputAdapter out = new OutputAdapter(props);
		StringWriter sw = new StringWriter();
		try {
			out.writeTo(sw);
			System.out.println(sw.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test0a(){
		InputStream in = getClass().getResourceAsStream("/funnykey.properties");
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(1, props.size());
		Assert.assertEquals("value1", props.get(":="));
	}
	
	@Test
	public void test1(){
		InputStream in = getClass().getResourceAsStream("/log4j-example.properties");
		Properties props = Properties.Factory.getInstance(in);
		Node fileNode = props.getTree(new GroupParams("log4j.appender.FILE"));
		Assert.assertNotNull(fileNode);
		Assert.assertEquals(1,fileNode.getChildren().size());
		Node n = fileNode.getChild("log4j").getChild("appender").getChild("FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
		n = fileNode.getDescendant("log4j.appender.FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
	}
	
	@Test
	public void test1unix(){
		InputStream in = getClass().getResourceAsStream("/log4j-example.unix.properties");
		Properties props = Properties.Factory.getInstance(in);
		Node fileNode = props.getTree(new GroupParams("log4j.appender.FILE"));
		Assert.assertNotNull(fileNode);
		Assert.assertEquals(1,fileNode.getChildren().size());
		Node n = fileNode.getChild("log4j").getChild("appender").getChild("FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
		n = fileNode.getDescendant("log4j.appender.FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
	}
	
	@Test
	public void test1mac(){
		InputStream in = getClass().getResourceAsStream("/log4j-example.mac.properties");
		Properties props = Properties.Factory.getInstance(in);
		Node fileNode = props.getTree(new GroupParams("log4j.appender.FILE"));
		Assert.assertNotNull(fileNode);
		Assert.assertEquals(1,fileNode.getChildren().size());
		Node n = fileNode.getChild("log4j").getChild("appender").getChild("FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
		n = fileNode.getDescendant("log4j.appender.FILE");
		Assert.assertTrue(n.hasValue());
		Assert.assertEquals("org.apache.log4j.FileAppender", n.get());
	}
	
	@Test
	public void test2(){
		LocaleStringBuilder b = new LocaleStringBuilder("StrutsResources", Locale.UK);
		List<String> list = b.getSearchStrings();
		Assert.assertEquals(3,list.size());
	//	Assert.assertEquals("StrutsResources_en_GB.properties",list.get(2));
		
		Properties props = Properties.Factory.getInstance("TestLocaleProps", Locale.ITALY);
		Assert.assertEquals("Albero", props.get("s1"));
		props = Properties.Factory.getInstance("TestLocaleProps", Locale.ITALIAN);
		Assert.assertEquals("Albero", props.get("s1"));
		props = Properties.Factory.getInstance("TestLocaleProps", Locale.getDefault());
		Assert.assertEquals("Tree", props.get("s1"));
		
		OutputAdapter out = new OutputAdapter(props);
		StringWriter sw = new StringWriter();
		try {
			out.writeTo(sw);
			System.out.println(sw.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
		
		@Test
		public void test2a(){
		Properties props = Properties.Factory.getInstance();
		BasicValueModel vm = new BasicValueModel("value line 1", "value line 2");
		vm.addComment("# this is a comment line, it will appear above the key/value pair.");
		props.getPropertyMap().put("key", vm);
		OutputAdapter out = new OutputAdapter(props);
		StringWriter sw = new StringWriter();
		try {
			out.writeTo(sw);
			System.out.println(sw.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	

	
	@Test
	public void test4(){
		InputStream in = getClass().getResourceAsStream("/test3.properties");
		Properties props = Properties.Factory.getInstance(in);
		Node base = props.getTree().getDescendant("a.b.c");
		Assert.assertEquals("this",base.getChild("s1").get());
		OutputAdapter out = new OutputAdapter(props);
		StringWriter sw = new StringWriter();
		try {
			out.writeTo(sw);
			System.out.println(sw.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void test5() {
		InputStream in = getClass().getResourceAsStream("/compare.properties");
		Properties props = Properties.Factory.getInstance(in);
		Properties sorted = Properties.Factory.sortedInstance(props, new Comparator<String>() {

			public int compare(String o1, String o2) {
				return o1.compareTo(o2);
			}
			
		});
		
		Assert.assertEquals("a", sorted.getPropertyMap().keySet().iterator().next());
	}
	
	@Test
	public void test6() {
		InputStream in = getClass().getResourceAsStream("/ListResources/list.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(in);
		List<String> list = props.getList("wrapper.java.classpath");
		Assert.assertEquals(5, list.size());
		Assert.assertEquals("../lib/wrapper2.jar", list.get(4));
		StringBuffer classpath = new StringBuffer();
		int i = 0;
		for(String s: list){
			classpath.append(s);
			if(i<list.size()-1)classpath.append(File.pathSeparator);
			i++;
		}
		
		System.err.println(classpath.toString());
	}
	
	
}
