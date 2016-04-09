/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to represent the properties keys as a tree. This is mainly for use by the XML representation. 
 * Not all properties may effectively use this representation, they must meet the requirements of the XML spec
 * 
 * @author Dave
 *
 * @see OutputAdapter 
 * @see NodeVisitor
 */
public class NodeImpl implements Node, Serializable {
	
	private static final long serialVersionUID = 1L;
	
	final String name;
	final List<Node> children;
	ValueModel value;
	
	NodeImpl(String name) {
		super();
		this.name = name;
		children = new ArrayList<Node>();
	}
	
	NodeImpl(String name, String value){
		this(name);
		this.value = new BasicValueModel(value);
	}
	
	NodeImpl(String name, ValueModel model){
		this(name);
		this.value = model;
	}
	
	public void addChild(Node child){
		children.add(child);
	}
	
	public void addChild(String name, String value){
		NodeImpl n = new NodeImpl(name,value);
		addChild(n);
	}
	
	public void addChild(String name, ValueModel model){
		NodeImpl n = new NodeImpl(name,model);
		addChild(n);
	}
	
	public Node getChild(String nodeName){
		for(Node n : children){
			if(n.getName().equals(nodeName)) return n;
		}
		return null;
	}
	
	public Node getDescendant(String nodePath){
		
		Node node = this;
		String [] names = nodePath.split("\\.");
		if(names.length==0)return null;
		for(String nodeName:names){
			node = node.getChild(nodeName);
			if(node == null) return null;
		}
		return node;
	}
	
	public boolean hasValue(){
		return value !=null;
	}
	
	
	public String get(){
		if(value==null) throw new RuntimeException("Missing value, which should not happen because you would have called hasValue() to check first.");
		return value.getValue();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((children == null) ? 0 : children.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		NodeImpl other = (NodeImpl) obj;
		if (children == null) {
			if (other.children != null)
				return false;
		} else if (!children.equals(other.children))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getName() {
		return name;
	}

	public List<Node> getChildren() {
		return children;
	}

	public ValueModel getValue() {
		return value;
	}

	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(name);
		buf.append("=");
		buf.append(value);
		buf.append("\n");
		for(Node child: children){
			buf.append(child.toString());
		}
		
		return buf.toString();
	}
	
	
}
