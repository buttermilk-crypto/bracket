/*
 *  This file is part of Bracket Properties
 *  Copyright 2013 David R. Smith
 *
 */
package asia.redact.bracket.util;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public class Encodings {

	public static String getDefaultEncoding() {
		return Charset.defaultCharset().name();
	}
	
	public static CharsetEncoder getDefaultEncoder() {
		return Charset.defaultCharset().newEncoder();
	}
	
	/**
	 * Normally false, default is usually UTF-8?
	 * 
	 * @return
	 */
	public static boolean platformDefaultsToAScii() {
		return getDefaultEncoding().equals("ISO-8859-1");
	}
	
	public static CharsetEncoder getAsciiEncoder(){
		if(Charset.isSupported("ISO-8859-1")) {
			return Charset.forName("ISO-8859-1").newEncoder();
		}else{
			throw new RuntimeException("Your platform dow not support ASCII?");
		}
	}

}
