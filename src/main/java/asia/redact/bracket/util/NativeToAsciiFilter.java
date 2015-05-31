/*
 *  This file is part of Bracket Properties
 *  Copyright 2013 David R. Smith
 *
 */
package asia.redact.bracket.util;

import java.io.IOException;

/**
 * <pre>
 * Assuming the output is to be understood as ASCII, encode out internal java bytes above 127 as unicode escapes like \\uXXXX.
 * 
 * Rework of the Sun open source code to use a StringBuffer instead of a stream.
 * 
 * </pre>
 * 
 * @author Dave
 *
 */
public class NativeToAsciiFilter {
	
	final String lineBreak = System.getProperty("line.separator");
	final StringBuffer out;
	
	public NativeToAsciiFilter() {
		out = new StringBuffer();
	}
	
	public NativeToAsciiFilter(String initialInput) {
		out = new StringBuffer();
		write(initialInput);
	}
	
	public NativeToAsciiFilter(StringBuffer buf) {
		out = buf;
	}
	
	public NativeToAsciiFilter write(char [] buf) {
		write(buf,0,buf.length);
		return this;
	}
	
	public  NativeToAsciiFilter write(String str) {
			try {
				write(str,0,str.length());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return this;
	}
	
	  public  NativeToAsciiFilter write(char[] buf, int off, int len) {

		    for (int i = 0; i < len; i++) {
		        if ((buf[i] > '\u007F')) { 
				    out.append('\\');
			        out.append('u');
			        String hex = Integer.toHexString(buf[i]);
			        StringBuilder hex4 = new StringBuilder(hex);
			        hex4.reverse();
			        int length = 4 - hex4.length();
			        for (int j = 0; j < length; j++) hex4.append('0');
			        for (int j = 0; j < 4; j++) out.append(hex4.charAt(3 - j));
		        } else {
		        	out.append(buf[i]);
		        }
		    }
		    
		    return this;
		}
	  
	  /**
	     * Writes a portion of a string.
	     *
	     * @param  str  String to be written
	     * @param  off  Offset from which to start reading characters
	     * @param  len  Number of characters to be written
	     *
	     * @exception  IOException  If an I/O error occurs
	     */
	    public NativeToAsciiFilter write(String str, int off, int len) throws IOException {
	    	write(str.toCharArray(), off, len);
	    	return this;
	    }
	    
	    public String getResult() {
	    	return out.toString();
	    }
}
