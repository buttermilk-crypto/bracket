/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.Serializable;

/**
 * Container for token type and text
 * 
 * @author Dave
 *
 */
public class PropertiesToken implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public final PropertiesTokenType type;
	String text;
	
	public PropertiesToken(PropertiesTokenType type, String text) {
		super();
		this.type = type;
		this.text = text;
	}
	
	public String toString(){
		return new StringBuilder().append(type).append(":").append(text).toString();
	}
	
	public static PropertiesToken eof(){
		return new PropertiesToken(PropertiesTokenType.EOF,"");
	}
}
