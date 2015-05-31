/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties.line;

/**
 * Nicer way of representing line breaks
 * 
 * @author Dave
 *
 */
public enum LineEnding {
	CR("\r"),LF("\n"),CRLF("\r\n");
	
	private final String ending;
	
	private LineEnding(String chars){
		this.ending=chars;
	}

	public String getEnding() {
		return ending;
	}
}
