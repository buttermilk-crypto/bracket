/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties;

import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import asia.redact.bracket.properties.Properties.Mode;
/**
 * <pre>
 * Populate a Properties object. Parser is a thread-safe one use object. The list of
 * tokens normally is the output of a PropertiesLexer but can be generated in other ways,
 * for example manually.
 * 
 * </pre>
 * 
 * @author Dave
 * @see PropertiesLexer
 */
public class PropertiesParser {
	
	final Stack<PropertiesToken> tokens;
	final Properties properties;
	private final Lock lock = new ReentrantLock();
	private boolean trimValues;

	public PropertiesParser(List<PropertiesToken> tokens, Properties properties) {
		super();
		this.tokens = new Stack<PropertiesToken>();
		for(int i = tokens.size()-1;i>-1;i--){
			this.tokens.add(tokens.get(i));
		}
		this.properties = properties;
	}
	
	/**
	 * This constructor expects the tokens to be in parse order, it reverses the order as it pushes them onto the stack
	 * (so the stack order is backwards to the parse). 
	 * 
	 * @param tokens
	 */
	public PropertiesParser(List<PropertiesToken> tokens) {
		super();
		this.tokens = new Stack<PropertiesToken>();
		for(int i = tokens.size()-1;i>-1;i--){
			this.tokens.add(tokens.get(i));
		}
		this.properties = new PropertiesImpl();
	}
	
	/**
	 * This constructor expects the tokens to be in reverse parse order, it uses the stack as-is
	 * (the stack order is backwards to the parse order). 
	 * 
	 * @param tokens
	 */
	public PropertiesParser(Stack<PropertiesToken> tokens) {
		super();
		this.tokens = tokens;
		this.properties = new PropertiesImpl();
	}
	
	public void parse(){
		lock.lock();
		try {
			String key = null;
			BasicValueModel value = new BasicValueModel();
			while(tokens.size() > 0){
				
				// end of file
				if(peek().type==PropertiesTokenType.EOF){
					pop(); // remove the last token
					
					break;
				}
				
				// blank line
				if(peek().type==PropertiesTokenType.NATURAL_LINE_BREAK){
					pop(); // remove the line break
					
					if(key != null && value.getValues().size() > 0){
						properties.getPropertyMap().put(key, value);
						value= new BasicValueModel();
						key= null;
					}
					
					continue;
				}
				
				// comment
				if(peek().type==PropertiesTokenType.COMMENT){
					PropertiesToken t = pop(); //comment line value
					value.addComment(t.text);
					pop(); // remove the line break
					continue;
				}
				
				// meta data
				if(peek().type==PropertiesTokenType.META_DATA){
					PropertiesToken t = pop(); //comment line value
				//	analyzeMetaData(t.text);
					pop(); // remove the line break
					continue;
				}
				
				// key
				if(peek().type==PropertiesTokenType.KEY){
					PropertiesToken t = pop(); //comment line value
					
					// trim the key text at this point
					key = t.text.trim();
					
					// key with no separator or value, weird but legal
					if(peek().type==PropertiesTokenType.NATURAL_LINE_BREAK){
						pop();
						value.addValue(""); //set it to an empty string value
						properties.getPropertyMap().put(key, value);
						value= new BasicValueModel();
						key= null;
						continue;
					}else if(peek().type==PropertiesTokenType.SEPARATOR){
						t = pop();
						value.setSeparator(t.text.charAt(0));
					}
					
					
				}
				
				if(peek().type==PropertiesTokenType.VALUE){
					PropertiesToken t = pop();
					String text = t.text;
					// single line property
					if(peek().type == PropertiesTokenType.NATURAL_LINE_BREAK){
						pop();
						if(trimValues) {
							value.addValue(trimEndingWhiteSpace(text));
						}else{
							value.addValue(text);
						}
						properties.getPropertyMap().put(key, value);
						value= new BasicValueModel();
						key = null;
						continue;
					}else if(peek().type == PropertiesTokenType.LOGICAL_LINE_BREAK){
						
						value.addValue(text);
						
						pop();
					}else if(peek().type == PropertiesTokenType.EOF){
						if(trimValues) {
							value.addValue(trimEndingWhiteSpace(text));
						}else{
							value.addValue(text);
						}
						pop();
						
						properties.getPropertyMap().put(key, value);
						value= new BasicValueModel();
						key = null;
						break;
					}
				}
			}
		}finally{
			lock.unlock();
		}
	}
	
	private PropertiesToken peek() {
		try {
			PropertiesToken tok = tokens.peek();
			return tok;
		}catch(EmptyStackException x){
			return PropertiesToken.eof();
		}
	}
	
	private PropertiesToken pop() {
		try {
			PropertiesToken tok = tokens.pop();
			return tok;
		}catch(EmptyStackException x){
			return PropertiesToken.eof();
		}
	}
	
	public Properties getProperties() {
		return properties;
	}

	public boolean isTrimValues() {
		return trimValues;
	}

	public void setTrimValues(boolean trimValues) {
		this.trimValues = trimValues;
	}
	
	private String trimEndingWhiteSpace(String text){
		
		int count = 0;
		for(int i = text.length()-1;i>-1;i--){
			if(Character.isWhitespace(text.charAt(i))){
				count++;
			}else{
				break;
			}
		}
		
		if(count >0) return text.substring(0,text.length()-count);
		else return text;
	}
	
}
