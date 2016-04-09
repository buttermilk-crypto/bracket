/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import asia.redact.bracket.properties.Properties.Mode;
import asia.redact.bracket.util.AsciiToNativeFilterReader;
/**
 * <pre>
 * 
 * Parse a properties file generally conformant to the description at
 *  http://download.oracle.com/javase/6/docs/api/java/util/Properties.html#load(java.io.Reader)
 *  into tokens. 
 *  
 *  The lexer will be slightly more true to the data than java.util. For example, it will preserve
 *  whitespace in the value as valid data, which java.util.Properties would silently strip off. The lexer 
 *  will also happily consume UTF-8 (no need for unicode-style escapes). Use Mode.Compatibility 
 *  for better compatibility to the java.util package and the "spec" above.  
 * 
 * There is one additional extension in the lexer: a comment line which starts with #;; is treated 
 * as transient (not read in as a comment or saved as a comment in the properties results). This can be 
 * used to generate a transient header and footer.
 * 
 * As of version 1.3.2-SNAPSHOT and above, these lines are parsed as META_DATA tokens and are available in 
 * the parse phase. 
 * 
 * This class is an "off-line" (non-streaming) lexer, it is backed by a String as input, which implies it is 
 * limited by memory resources. That's not a problem for all but unusually large properties files on contemporary 
 * hardware.
 * 
 * </pre>
 * 
 * @author Dave
 * 
 * @see PropertiesToken
 * @see PropertiesTokenType
 * @see InputAdapter
 */
public class PropertiesLexer {

	final String input;
	int index;
	final List<PropertiesToken> list = new ArrayList<PropertiesToken>();
	private final Lock lock = new ReentrantLock();

	/**
	 * Convenience method, swallows the input whole
	 * This method filters for unicode escapes if the mode is Compatibility
	 * 
	 */
	public PropertiesLexer(String input) {
		super();
		//this.input = input;
		if(Properties.Factory.mode == Mode.Compatibility){
			// first filter the entire input for unicode escapes
			AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(new StringReader(input));
			StringBuilder builder = new StringBuilder();
			char [] array = new char[8120];
			int count = 0;
			try {
				while((count = reader.read(array))!= -1) {
					builder.append(array,0,count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			this.input = builder.toString();
		}else{
			// normal, String will be in the default encoding, normally UTF-8
			this.input = input;  
		}
	}
	
	/**
	 * Convenience method, swallows the input whole
	 * This method filters for unicode escapes if the mode is Compatibility
	 * 
	 */
	public PropertiesLexer(Reader in){
		//this(new InputAdapter().asString(in));
		
		if(Properties.Factory.mode == Mode.Compatibility){
			// first filter the entire input for unicode escapes
			AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(in);
			StringBuilder builder = new StringBuilder();
			char [] array = new char[8120];
			int count = 0;
			try {
				while((count = reader.read(array))!= -1) {
					builder.append(array,0,count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			input = builder.toString();
		}else{
			// normal, String will be in the default encoding, normally UTF-8
			input =  new InputAdapter().asString(in);
		}
	}
	
	/**
	 * Convenience method, swallows the input whole
	 * This method filters for unicode escapes if the mode is Compatibility
	 * This method requires we specify the explicit charset of the file
	 * 
	 */
	public PropertiesLexer(File in, Charset charset){
	   String str = new InputAdapter().asString(in,charset);
		if(Properties.Factory.mode == Mode.Compatibility){
			// first filter the entire input for unicode escapes
			AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(new StringReader(str));
			StringBuilder builder = new StringBuilder();
			char [] array = new char[8120];
			int count = 0;
			try {
				while((count = reader.read(array))!= -1) {
					builder.append(array,0,count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			input = builder.toString();
		}else{
			// normal, String will be in the default encoding, normally UTF-8
			input =  str;
		}
	}
	
	/**
	 * Convenience method, swallows the input whole
	 * This method filters for unicode escapes if the mode is Compatibility
	 * 
	 */
	public PropertiesLexer(InputStream in){

		if(Properties.Factory.mode == Mode.Compatibility){
			// first filter the entire input for unicode escapes
			AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(new InputStreamReader(in));
			StringBuilder builder = new StringBuilder();
			char [] array = new char[8120];
			int count = 0;
			try {
				while((count = reader.read(array))!= -1) {
					builder.append(array,0,count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			input = builder.toString();
		}else{
			// normal, String will be in the default encoding, normally UTF-8
			input =  new InputAdapter().asString(in);
		}
	}

	public PropertiesLexer(InputStream in, Charset charset){

		if(Properties.Factory.mode == Mode.Compatibility){
			// first filter the entire input for unicode escapes
			AsciiToNativeFilterReader reader = new AsciiToNativeFilterReader(new InputStreamReader(in,charset));
			StringBuilder builder = new StringBuilder();
			char [] array = new char[8120];
			int count = 0;
			try {
				while((count = reader.read(array))!= -1) {
					builder.append(array,0,count);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			input = builder.toString();
		}else{
			// normal, String will be in the default encoding, normally UTF-8
			input =  new InputAdapter().asString(in);
		}
	}
	
	public void lex() {
		lock.lock();
		long count = 0;
		try {
			if(input==null)return;
			PropertiesToken lastEol = null;
			while(hasNext()){
				StringBuffer buf = new StringBuffer();
				PropertiesToken eol = null;
				while(hasNext() && ((eol=scanLineBreak())==null)){
						buf.append(next());
				}
				analyzeLine(buf.toString(),lastEol, count);
				count++;
				if(eol == null) {
					return;
				}
				lastEol = eol;
				list.add(eol);
				index+=eol.text.length();
			}
		}finally{
			list.add(PropertiesToken.eof());
			lock.unlock();
		}
	}
	
	private void analyzeLine(String buf, PropertiesToken tok,long count){
		lock.lock();
		try {
			//strip off any leading white space
			int countBlank = 0;
			for(int i =0;i<buf.length();i++){
				if(Character.isWhitespace(buf.charAt(i))){
					countBlank++;
					continue;
				}else{
					break;
				}
			}
			if(countBlank>0)buf=buf.substring(countBlank);
			// this should handle blank lines
			if(buf.isEmpty())return;
			
			// do not keep our Last Generated header banner or our End footer banner
			// as of version 1.3.2-SNAPSHOT, parse this as META_DATA if we are in Explicit mode
			if((buf.length() > 3) && (buf.charAt(0)== '#' && buf.charAt(1)== ';' && buf.charAt(2)==';')){
				
				//if(Properties.Factory.mode == Mode.Explicit){
				
					// collect all the contents of the line up to the line break
				//	String meta = buf.substring(3, buf.length());
				//	list.add(new PropertiesToken(PropertiesTokenType.META_DATA,meta));
					
				//}else{
					// skip this line if not explicit mode
					return;
				//}
				
			}
			
			char ch = buf.charAt(0);
			switch(ch){
				case '#':
				case '!': comment(buf); break;
				default: {
					scanKeyValue(buf,tok);
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	private void scanKeyValue(String buf,PropertiesToken eol){
		lock.lock();
		try {
			int sepIndex=-1;
			char previous = '\0';
			char ch = '\0';
			boolean sawEscapedDelimiter=false;
			for(int i=0;i<buf.length();i++){
			    previous=ch;
				ch = buf.charAt(i);
				if((ch == '='||ch==':') && previous == '\\'){
					sawEscapedDelimiter=true;
				}
				if((ch == '='||ch==':') && previous != '\\'){
					//I see an actual delimiter
					sepIndex=i;
					break;
				}
			}
			if(sepIndex == -1){
				//no separator found, test if continuation...
				if(eol !=null && eol.type==PropertiesTokenType.LOGICAL_LINE_BREAK){
					list.add(new PropertiesToken(PropertiesTokenType.VALUE,buf));
				}
			}else{
				// key and value
				
				//first purge escaped delimiters from the key, if needed
				if(sawEscapedDelimiter){
					String key = buf.substring(0, sepIndex);
					previous = '\0';
					ch = '\0';
					StringBuilder b = new StringBuilder();
					for(int i=0;i<key.length();i++){
					    previous=ch;
						ch = buf.charAt(i);
						if((ch == '='||ch==':') && previous == '\\'){
							b.deleteCharAt(b.length()-1);
						}
						b.append(ch);
					}
					// use cleaned up key
					// Issue #1 - need to trim key also of whitespace at end, should be trimmed at front above
					list.add(new PropertiesToken(PropertiesTokenType.KEY,b.toString().trim()));
					
				}else{
					//delimiter not escaped, so just use key as is
					// Issue #1 - but need to trim key of whitespace
					list.add(new PropertiesToken(PropertiesTokenType.KEY,buf.substring(0, sepIndex).trim()));
				}
				
				
				list.add(new PropertiesToken(PropertiesTokenType.SEPARATOR,String.valueOf(buf.charAt(sepIndex))));
				list.add(new PropertiesToken(PropertiesTokenType.VALUE,buf.substring(sepIndex+1, buf.length())));
			}
		}finally{
			lock.unlock();
		}
	}
	
	private void comment(String buf){
		lock.lock();
		try {
			list.add(new PropertiesToken(PropertiesTokenType.COMMENT,buf));
		}finally{
			lock.unlock();
		}
	}
	
	private PropertiesToken scanLineBreak(){
		lock.lock();
		try {
			if(la(0)=='\\' && la(1) == '\r'&&la(2)=='\n'){
				return new PropertiesToken(PropertiesTokenType.LOGICAL_LINE_BREAK,"\\\r\n");
			}else if(la(0)=='\\' && la(1) == '\r'){ 
				return new PropertiesToken(PropertiesTokenType.LOGICAL_LINE_BREAK,"\\\r");
			}else if(la(0)=='\\' && la(1) == '\n'){
				return new PropertiesToken(PropertiesTokenType.LOGICAL_LINE_BREAK,"\\\n");
			}else if(la(0) == '\r'&&la(1)=='\n'){
				return new PropertiesToken(PropertiesTokenType.NATURAL_LINE_BREAK,"\r\n"); 
			}else if(la(0) == '\r'){
				return new PropertiesToken(PropertiesTokenType.NATURAL_LINE_BREAK,"\r"); 
			}else if(la(0) == '\n'){
				return new PropertiesToken(PropertiesTokenType.NATURAL_LINE_BREAK,"\n"); 
			}
			return null;
		}finally{
			lock.unlock();
		}
	}
	

	private boolean hasNext() {
		lock.lock();
		try {
			return index < input.length();
		}finally{
			lock.unlock();
		}
	}
	
	private char next() {
		lock.lock();
		try {
			if(index >= input.length()) {
				throw new RuntimeException("problem, index >= "+input.length());
			}
			char ch = input.charAt(index);
			index++;
			return ch;
		}finally{
			lock.unlock();
		}
	}
	
	private Character la(int count) {
		lock.lock();
		try {
			try {
				return input.charAt(index+count);
			}catch(IndexOutOfBoundsException x){
				return '\0';
			}
		}finally{
			lock.unlock();
		}
	}

	public List<PropertiesToken> getList() {
		return list;
	}
}
