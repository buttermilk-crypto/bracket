/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

/**
 * Tokens used in the PropertiesLexer and PropertiesParser.
 * 
 * @author Dave
 *
 */
public enum PropertiesTokenType {
	META_DATA, NATURAL_LINE_BREAK,LOGICAL_LINE_BREAK,KEY,SEPARATOR,VALUE,COMMENT,EOF;
}
