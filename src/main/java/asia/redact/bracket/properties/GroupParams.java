/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith
 *
 */
package asia.redact.bracket.properties;

/**
 * Used by the XML implementation
 * 
 * @author Dave
 *
 */
public class GroupParams {
	
	String separator;
	String partialKey;
	String rootNodeName;
	
	public GroupParams() {
		super();
		this.separator = "\\.";
		this.partialKey = "";
	}
	
	public GroupParams(String partialKey) {
		super();
		this.separator = "\\.";
		this.partialKey = partialKey;
	}
	
	public GroupParams(String separator, String partialKey) {
		super();
		this.separator = separator;
		this.partialKey = partialKey;
	}

	public String getSeparator() {
		return separator;
	}

	public void setSeparator(String separator) {
		this.separator = separator;
	}

	public String getPartialKey() {
		return partialKey;
	}

	public void setPartialKey(String partialKey) {
		this.partialKey = partialKey;
	}

	public String getRootNodeName() {
		return rootNodeName;
	}

	public void setRootNodeName(String rootNodeName) {
		this.rootNodeName = rootNodeName;
	}
	
}
