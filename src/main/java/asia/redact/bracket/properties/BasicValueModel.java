/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */

package asia.redact.bracket.properties;

import java.util.ArrayList;
import java.util.List;

import asia.redact.bracket.properties.Properties.Mode;
import asia.redact.bracket.util.NativeToAsciiFilter;

/**
 * <pre>
 * Representation of properties values which accounts better for the actual complexity of a potential properties value.
 * 
 * We consider comments above the key/value pair to be a part of the value. This is not really correct,
 * and they should probably be associated with the key, but it generally works because there is a one to many 
 * relation between a key/value pair and a set of comments. It is also nice to keep a key as a simple 
 * String data type.
 * 
 * Only comments above the key/value pair are considered part of the comment set. This is an arbitrary determination,
 * but it places a very light burden on a programmer and it seems a reasonable convention. It means comments at the end 
 * (past any key) may potentially be discarded
 * 
 * 
 * </pre>
 * 
 * @author Dave
 *
 */
public class BasicValueModel implements ValueModel {

	private static final long serialVersionUID = 1L;
	final static String lineSeparator = System.getProperty("line.separator");
	protected final List<String> comments;
	protected final List<String> values;
	protected char separator = '=';
	
	public BasicValueModel() {
		comments = new ArrayList<String>();
		values = new ArrayList<String>();
	}
	
	public BasicValueModel(String ... value) {
		this();
		for(String v: value){
			values.add(v);
		}
	}
	
	public BasicValueModel(char sep,String... value) {
		this();
		this.separator=sep;
		for(String v: value){
			values.add(v);
		}
	}
	
	public BasicValueModel(Comment comment,String... value) {
		this();
		comments.add(comment.comment);
		for(String v: value){
			values.add(v);
		}
	}
	
	public BasicValueModel(Comment comment, char sep, String... value) {
		this();
		this.separator=sep;
		comments.add(comment.comment);
		for(String v: value){
			values.add(v);
		}
	}
	
	
    BasicValueModel(List<String> comments, List<String> values) {
		this.comments = comments;
		this.values = values;
	}

	@Override
	public synchronized int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((comments == null) ? 0 : comments.hashCode());
		result = prime * result + separator;
		result = prime * result + ((values == null) ? 0 : values.hashCode());
		return result;
	}

	@Override
	public synchronized boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BasicValueModel other = (BasicValueModel) obj;
		if (comments == null) {
			if (other.comments != null)
				return false;
		} else if (!comments.equals(other.comments))
			return false;
		if (separator != other.separator)
			return false;
		if (values == null) {
			if (other.values != null)
				return false;
		} else if (!values.equals(other.values))
			return false;
		return true;
	}

	/* (non-Javadoc)
	 * @see asia.redact.bracket.properties.ValueModel#getSeparator()
	 */
	public char getSeparator() {
		return separator;
	}

	public void setSeparator(char separator) {
		this.separator = separator;
	}

	/* (non-Javadoc)
	 * @see asia.redact.bracket.properties.ValueModel#getComments()
	 */
	public List<String> getComments() {
		return comments;
	}

	/* (non-Javadoc)
	 * @see asia.redact.bracket.properties.ValueModel#getValues()
	 */
	public List<String> getValues() {
		return values;
	}
	
	public void addValue(String value){
		values.add(value);
	}
	
	public void addValues(List<String> values){
		this.values.addAll(values);
	}
	
	public void addComment(String comment){
		comments.add(comment);
	}
	
	public void addComments(List<String> comments){
		this.comments.addAll(comments);
	}
	
	public void clearComments() {
		comments.clear();
	}
	
	public void clearValues(){
		values.clear();
	}
	
	/* (non-Javadoc)
	 * @see asia.redact.bracket.properties.ValueModel#getValue()
	 */
	public String getValue(){
		StringBuilder b = new StringBuilder();
		for(String value:values) b.append(value);
		return b.toString();
	}
	
	public String toString(){
		return getValue();
	}
	
	public String asKeyValueRep(String key){
		StringBuffer buf = new StringBuffer();
		if(comments.size() > 0){
			for(String com: comments){
				buf.append(com);
				buf.append(lineSeparator);
			}
		}
		buf.append(key);
		buf.append(separator);
		int count = 0;
		if(values.size() == 0){
			// output nothing
		}else if(values.size() == 1){
			for(String val: values){
				buf.append(val);
				buf.append(lineSeparator);
		
			}
		}else {
			for(String val: values){
				buf.append(val);
			    if(count<values.size()-1) buf.append("\\");
				buf.append(lineSeparator);
				count++;
			}
		}
		
		return buf.toString();
	}
	
	/**
	 * As of version 1.3.2, will emit ISO-8859-1 if Compatibility mode is in use
	 * Otherwise, UTF-8
	 * 
	 */
	public String toXML(int spaces) {
		
		if(Properties.Factory.mode == Mode.Compatibility){
			return toXMLCompatibilityMode(spaces);
		}
		
		StringBuilder sp = new StringBuilder();
		for(int i=0;i<=spaces;i++)sp.append(" ");
		
		StringBuilder builder = new StringBuilder();
		
		for(String comment: comments){
			builder.append(sp.toString());
			builder.append("<c><![CDATA[").append(comment).append("]]></c>").append(lineSeparator);
		}
		
		builder.append(sp.toString());
		builder.append("<s>").append(separator).append("</s>").append(lineSeparator);
		
		for(String value: values){
			builder.append(sp.toString());
			if(value != null && (!value.equals(""))) {
				builder.append("<v><![CDATA[").append(value).append("]]></v>").append(lineSeparator);
			}else{
				builder.append("<v/>").append(lineSeparator);
			}
			
		}
		
		return builder.toString();
	}
	
	/**
	 * Write in ISO-8859-1 with unicode escapes
	 * @param spaces
	 * @return
	 */
	
	protected String toXMLCompatibilityMode(int spaces) {
		
		StringBuilder sp = new StringBuilder();
		for(int i=0;i<=spaces;i++)sp.append(" ");
		
		StringBuilder builder = new StringBuilder();
		
		for(String comment: comments){
			builder.append(sp.toString());
			String encoded = new NativeToAsciiFilter(comment).getResult();
			builder.append("<c><![CDATA[").append(encoded).append("]]></c>").append(lineSeparator);
		}
		
		builder.append(sp.toString());
		// TODO is there a possibility of an empty or not set char for the sep?
		builder.append("<s>").append(separator).append("</s>").append(lineSeparator);
		
		for(String value: values){
			builder.append(sp.toString());
			if(value != null && (!value.equals(""))) {
				String encoded = new NativeToAsciiFilter(value).getResult();
				builder.append("<v><![CDATA[").append(encoded).append("]]></v>").append(lineSeparator);
			}else{
				// values can be the empty string, legal
				builder.append("<v/>").append(lineSeparator);
			}
			
		}
		
		return builder.toString();
	}
	
	public ValueModel cloneImmutable() {
		return new ImmutableValueModel(comments, separator, values);
	}
	
}
