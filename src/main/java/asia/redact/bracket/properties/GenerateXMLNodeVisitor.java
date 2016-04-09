/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
/**
 * Format properties into xml. The default namespace is http://github.com/buttermilk-crypto/bracket
 * Not all properties may be amenable to this representation.
 *  
 * @author Dave
 *
 */
public class GenerateXMLNodeVisitor implements NodeVisitor {
	
	final static String lineSeparator = System.getProperty("line.separator");
	final String NS = "xmlns=\"http://github.com/buttermilk-crypto/bracket\"";
	final Writer writer;
	int level;
	final HashSet<Node> set = new HashSet<Node>();
	
	public GenerateXMLNodeVisitor(Writer writer) {
		super();
		this.writer = writer;
	}

	public void pre(Node node) {
		if(!set.contains(node)){
			set.add(node);
			level++;
		}
		try {
			writer.append(spaces());
			writer.append("<");
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void action(Node node) {
		String patched = "n"+node.getName();
		if(!validateNodeNameForUseAsXMLElement(patched)){
			throw new RuntimeException("Node name will not make for valid xml: "+node.getName());
		}
		
		try {
			writer.append(patched);
			if(level == 1){
				writer.append(" ");
				writer.append(NS);
			}
			writer.append(">");
			writer.append(lineSeparator);
			if(node.hasValue()){
				writer.append(node.getValue().toXML(level));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void post(Node node) {
		if(set.contains(node)){
			set.add(node);
			level--;
		}
		try {
			writer.append(spaces());
			writer.append("</").append("n");
			writer.append(node.getName()).append(">").append("\n");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void setLevel(int level) {
		this.level = level;
	}

	protected String spaces(){
		StringBuilder sp = new StringBuilder();
		for(int i=0;i<level;i++)sp.append(" ");
		return sp.toString();
	}
	
	protected boolean validateNodeNameForUseAsXMLElement(Node node) {
		return validateNodeNameForUseAsXMLElement(node.getName());
	}
	
	protected boolean validateNodeNameForUseAsXMLElement(String name) {
		 if(name == null) return false;
		if(name.equals("")) return false;
		if(name.startsWith("xml")) return false;
		if(Character.isDigit(name.charAt(0))) return false;
		if(!Character.isJavaIdentifierStart(name.charAt(0))) return false;
		String part = name.substring(1);
		for(int i = 0;i<part.length();i++){
			if(!Character.isJavaIdentifierPart(part.charAt(i))) return false;
		}
		return true;
	}
}
