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

import asia.redact.bracket.properties.PropertiesLexer;
import asia.redact.bracket.properties.PropertiesStreamingLexer;
import asia.redact.bracket.properties.PropertiesToken;
import asia.redact.bracket.properties.PropertiesTokenType;

public class PropertiesLexerTest {
	
	@Test
	public void test0b(){
		InputStream in = getClass().getResourceAsStream("/3props.properties");
		PropertiesLexer lexer = new PropertiesLexer(new InputStreamReader(in));
		lexer.lex();
		System.err.println(lexer.getList());
		Assert.assertEquals(13,lexer.getList().size());
	}
	@Test
	public void test0(){
		InputStream in = getClass().getResourceAsStream("/3props.properties");
		PropertiesStreamingLexer lexer = new PropertiesStreamingLexer(new InputStreamReader(in));
		lexer.lex();
		System.err.println(lexer.getList());
		Assert.assertEquals(13,lexer.getList().size());
	}
	
	@Test
	public void test1b(){
		InputStream in = getClass().getResourceAsStream("/test2.properties");
		PropertiesLexer lexer = new PropertiesLexer(new InputStreamReader(in));
		lexer.lex();
		System.err.println(lexer.getList());
		Assert.assertEquals(17,lexer.getList().size());
	}
	
	@Test
	public void test1(){
		InputStream in = getClass().getResourceAsStream("/test2.properties");
		PropertiesStreamingLexer lexer = new PropertiesStreamingLexer(new InputStreamReader(in));
		lexer.lex();
		System.err.println(lexer.getList());
		Assert.assertEquals(17,lexer.getList().size());
	}

	@Test
	public void testLexer0() {
		InputStream in = getClass().getResourceAsStream("/test.properties");
		Assert.assertNotNull(in);
		PropertiesLexer lexer = new PropertiesLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		System.err.println("Basic"+list);
		Assert.assertEquals(PropertiesTokenType.COMMENT, list.get(0).type);
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(1).type.name());
		Assert.assertEquals(PropertiesTokenType.KEY.name(), list.get(2).type.name());
		Assert.assertEquals(PropertiesTokenType.SEPARATOR.name(), list.get(3).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(4).type.name());
		Assert.assertEquals(PropertiesTokenType.LOGICAL_LINE_BREAK.name(), list.get(5).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(6).type.name());
		Assert.assertEquals(PropertiesTokenType.LOGICAL_LINE_BREAK.name(), list.get(7).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(8).type.name());
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(9).type.name());
		Assert.assertTrue(PropertiesTokenType.COMMENT== list.get(10).type);
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(11).type.name());
		Assert.assertEquals(PropertiesTokenType.COMMENT.name(), list.get(12).type.name());
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(13).type.name());
	}
	
	@Test
	public void testLexer0a() {
		InputStream in = getClass().getResourceAsStream("/test.properties");
		Assert.assertNotNull(in);
		PropertiesStreamingLexer lexer = new PropertiesStreamingLexer(in);
		lexer.lex();
		List<PropertiesToken> list = lexer.getList();
		Assert.assertNotNull(list);
		System.err.println("Streaming:"+list);
		Assert.assertEquals(PropertiesTokenType.COMMENT, list.get(0).type);
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(1).type.name());
		Assert.assertEquals(PropertiesTokenType.KEY.name(), list.get(2).type.name());
		Assert.assertEquals(PropertiesTokenType.SEPARATOR.name(), list.get(3).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(4).type.name());
		Assert.assertEquals(PropertiesTokenType.LOGICAL_LINE_BREAK.name(), list.get(5).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(6).type.name());
		Assert.assertEquals(PropertiesTokenType.LOGICAL_LINE_BREAK.name(), list.get(7).type.name());
		Assert.assertEquals(PropertiesTokenType.VALUE.name(), list.get(8).type.name());
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(9).type.name());
		Assert.assertTrue(PropertiesTokenType.COMMENT== list.get(10).type);
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(11).type.name());
		Assert.assertEquals(PropertiesTokenType.COMMENT.name(), list.get(12).type.name());
		Assert.assertEquals(PropertiesTokenType.NATURAL_LINE_BREAK.name(), list.get(13).type.name());
	}
}
