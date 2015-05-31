/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.junit.Test;

import org.junit.Assert;

import asia.redact.bracket.properties.PropertiesImpl;
import asia.redact.bracket.properties.PropertiesLexer;
import asia.redact.bracket.properties.PropertiesParser;
import asia.redact.bracket.properties.PropertiesParser2;
import asia.redact.bracket.properties.PropertiesToken;
import asia.redact.bracket.properties.line.LineScanner;

public class PropertiesParserTest {

	@Test
	public void testParser0() {
		InputStream in = getClass().getResourceAsStream("/test.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	@Test
	public void testParser0a() {
		InputStream in = getClass().getResourceAsStream("/test.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser2(lexer,props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	@Test
	public void testParser0aunix() {
		InputStream in = getClass().getResourceAsStream("/test.unix.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser2(lexer,props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	@Test
	public void testParser0amac() {
		InputStream in = getClass().getResourceAsStream("/test.mac.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser2(lexer,props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	@Test
	public void testParser0unix() {
		InputStream in = getClass().getResourceAsStream("/test.unix.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	@Test
	public void testParser0mac() {
		InputStream in = getClass().getResourceAsStream("/test.mac.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("Another value    Some more Yet Another", props.get("item1"));
	}
	
	
	@Test
	public void testParser1() {
		InputStream in = getClass().getResourceAsStream("/test2.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser(list, props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("val", props.get("item1"));
	}
	
	@Test
	public void testParser1a() {
		InputStream in = getClass().getResourceAsStream("/test2.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser2(lexer,props).parse();
		Assert.assertEquals(3, props.size());
		Assert.assertEquals("val", props.get("item1"));
	}
	
	@Test
	public void testParser1b() {
		InputStream in = getClass().getResourceAsStream("/TestLocaleProps.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		PropertiesImpl props = new PropertiesImpl();
		new PropertiesParser2(lexer,props).parse();
		Assert.assertEquals(2, props.size());
		Assert.assertEquals("Forest", props.get("s2"));
	}
	
}
