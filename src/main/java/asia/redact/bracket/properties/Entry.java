/*
 *  This file is part of Bracket Properties
 *  Copyright 2014-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.List;

/**
 * In certain cases we need a container for both the key and the ValueModel
 * 
 * @author Dave
 *
 */
public class Entry {

	String key;
	ValueModel model;
	
	public Entry(String key, ValueModel model) {
		super();
		this.key = key;
		this.model = model;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public ValueModel getModel() {
		return model;
	}

	public void setModel(ValueModel model) {
		this.model = model;
	}
	
	/**
	 * Alternative
	 * 
	 * @return
	 */
	public ValueModel getValue() {
		return model;
	}
	
	public String lineTerminatedValues() {
		StringBuffer buf = new StringBuffer();
		List<String> l = model.getValues();
		for(String s: l){
			buf.append(s);
			buf.append("\n");
		}
		if(buf.length()>0){
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
	
	public String commentValues() {
		StringBuffer buf = new StringBuffer();
		List<String> l = model.getComments();
		for(String s: l){
			buf.append(s);
			buf.append("\n");
		}
		if(buf.length()>0){
			buf.deleteCharAt(buf.length()-1);
		}
		return buf.toString();
	}
}
