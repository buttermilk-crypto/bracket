/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Implement a SAXParser handler to consume the SAX events and turn that into a Properties file.
 * It would be cleaner to use Dom4J or something like that, but I wanted to avoid additional dependencies where I could.
 * 
 * @author Dave
 */

class BracketPropertiesSAXHandler extends BracketSaxHandler {

	int count;
	
	StringBuffer keyBuf;
	Stack<String> keyStack;
	List<String> comments;
	String separator;
	List<String> values;
	
	boolean commentOn;
	boolean separatorOn;
	boolean valueOn;
	
	public BracketPropertiesSAXHandler() {
		super();
		keyStack = new Stack<String>();
		comments = new ArrayList<String>();
		values = new ArrayList<String>();
	}
	
	public void characters(char[] buffer, int start, int length) {
        String temp = new String(buffer, start, length);
        if(commentOn){
        	comments.add(temp);
        }else if(separatorOn){
        	separator = temp;
        }else if(valueOn){
        	values.add(temp);
        }
	}

	
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		// test the initial (wrapping) element, which should be "nproperties"
		if(count == 0) {
			if(qName.equals("nproperties")){
				String namespace = attributes.getValue("xmlns");
				if(namespace == null || !namespace.equals("http://github.com/buttermilk-crypto/bracket")){
					throw new RuntimeException("Bad namespace, attribute xmlns must be set to 'http://github.com/buttermilk-crypto/bracket'");
				}
			}else{
				throw new RuntimeException("Bracket properties serialized with xml must have a top level element called 'nproperties'");
			}
		}else{
			
			if(qName.equals("c")){
				commentOn = true; 
				return;
			}
			
			if(qName.equals("s")){
				separatorOn = true; 
				return;
			}
			
			if(qName.equals("v")){
				valueOn = true; 
				return;
			}
			
			
			// ok, must be part of a key - do the work of collecting elements for a key
			
			keyStack.push(qName.substring(1)); // clip the "n" off the front
		}
     		
		count++;
	}

	public void endElement(String uri, String localName, String qName) throws SAXException {
		 
		if(qName.equals("c")){
			commentOn = false; 
			return;
		}
		
		if(qName.equals("s")){
			separatorOn = false; 
			return;
		}
		
		if(qName.equals("v")){
			valueOn = false; 
			
			 keyBuf = new StringBuffer();
			 for(String part: keyStack){
				 if(keyBuf.length() > 0) keyBuf.append(".");
				 keyBuf.append(part);
			 }
			// keyBuf.append(".");
			// keyBuf.append(qName);
			 BasicValueModel model = new BasicValueModel();
			 model.setSeparator(separator.charAt(0));
			 for(String value: values){
				model.addValue(value);
			 }
			 for(String comment: comments){
				model.addComment(comment);
			 }
				
			 props.getPropertyMap().put(keyBuf.toString(), model);
				
			 // now clean up
				
			 keyBuf = null;
			 values.clear();
			 comments.clear();
			 separator = null;
			 
			 // pop the topmost item
			 keyStack.remove(qName);
			 
			 return;
		}
		
		 // pop the topmost item
		 keyStack.remove(qName.substring(1));
			 
	}
}
