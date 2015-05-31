/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties;

import java.io.*;
import java.util.List;

import org.junit.Test;

import org.junit.Assert;

import asia.redact.bracket.properties.OutputAdapter;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.PropertiesImpl;
import asia.redact.bracket.properties.PropertiesLexer;
import asia.redact.bracket.properties.PropertiesParser;
import asia.redact.bracket.properties.PropertiesToken;

public class OutputAdapterTest {

	@Test
	public void testWriter0() {
		InputStream in = getClass().getResourceAsStream("/test.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(3, props.size());
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		try {
			out.writeTo(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println(w.toString());
		
	}
	
	@Test
	public void testWriter1() {
		InputStream in = getClass().getResourceAsStream("/ibm-example.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
	
		Assert.assertNotNull(list);
		
		Properties props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		try {
			out.writeTo(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println(w.toString());
		
	}
	

	
	@Test
	public void testWriter2() {
		InputStream in = getClass().getResourceAsStream("/ibm-example2.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
	
		Assert.assertNotNull(list);
		
		Properties props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		try {
			out.writeTo(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//System.err.println(w.toString());
		
		w = new StringWriter();
		out.writeAsXml(w);
		System.out.println(w.toString());
		
	}
	
	@Test
	public void testWriter3() {
		InputStream in = getClass().getResourceAsStream("/log4j-example.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(7, props.size());
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		try {
			out.writeTo(w);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println(w.toString());
		
	}
	
	@Test
	public void testWriter4() {
		InputStream in = getClass().getResourceAsStream("/test3.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(in);
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		out.writeAsXml(w);
		System.err.println(w.toString());
		
	}
	
	
}
