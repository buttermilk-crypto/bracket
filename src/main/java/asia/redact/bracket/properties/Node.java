/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.List;

/**
 * Node Interface 
 * 
 * @author Dave
 *
 */
public interface Node {

	public void addChild(Node child);
	
	public void addChild(String name, String value);

	public void addChild(String name, ValueModel model);

	public Node getChild(String nodeName);

	public Node getDescendant(String nodePath);

	public boolean hasValue();

	public String get();
	
	public String getName();

	public List<Node> getChildren();

	public ValueModel getValue();

}