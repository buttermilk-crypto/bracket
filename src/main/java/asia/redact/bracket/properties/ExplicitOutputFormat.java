/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.List;

import asia.redact.bracket.util.NativeToAsciiFilter;

/**
 * Compatibility format for simulating java.util.Properties output by encoding characters above ASCII 127 with 
 * unicode escapes. Use in conjunction with OutputAdapter.writeAsciiTo(). This is deprecated but retained for documentation

 * 
 * @author Dave
 * @deprecated use AsciiOutputFormat
 *
 */
public class ExplicitOutputFormat implements OutputFormat {
	
	protected final static String lineSeparator = System.getProperty("line.separator");
	
	public ExplicitOutputFormat() {
		super();
	}
	
	public String formatContentType() {
		return "";
	}
	
	public String formatHeader() {
		StringBuffer buf = new StringBuffer("#;; last-generated-on=");
		buf.append(new java.util.Date().toString());
		buf.append(lineSeparator);
		return buf.toString();
	}

	public String format(String key, char separator, List<String> values, List<String> comments) {
		
		if(key == null) throw new RuntimeException("Key cannot be null in a format");
		
		StringBuffer buf = new StringBuffer();
		if(comments != null && comments.size()>0) {
			for(String c: comments){
				buf.append(new NativeToAsciiFilter().write(c).getResult());
				buf.append(lineSeparator);
			}
		}
	    StringBuilder keyBuilder=new StringBuilder();
	    for(int i=0;i<key.length();i++){
	    	char ch = key.charAt(i);
	    	if(ch==':'||ch=='='){
	    		keyBuilder.append('\\');	
	    	}
	    	keyBuilder.append(ch);
	    }
		buf.append(keyBuilder.toString());
		buf.append(separator);
		
		if(values != null && values.size()>0){
			int count = values.size();
			int i = 0;
			for(String s: values){
				buf.append(new NativeToAsciiFilter().write(s).getResult());
				if(i<count-1) {
					buf.append('\\');
				}
				buf.append(lineSeparator);
				i++;
			}
		}
		
		return buf.toString();
	}



	public String formatFooter() {
		StringBuffer buf = new StringBuffer(lineSeparator);
		buf.append("#;; end properties file ");
		buf.append(lineSeparator);
		return buf.toString();
	}



}
