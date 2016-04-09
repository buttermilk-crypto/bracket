/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties;

/**
 * package protected class for creating trees - internal
 * 
 * @author Dave
 *
 */
class TreeBuilder {

	final Node root;
	final Properties props;

	TreeBuilder(Properties props) {
		super();
		root = new NodeImpl("");
		this.props = props;
	}
	
	TreeBuilder(Properties props, String rootNodeName) {
		super();
		if(rootNodeName!=null&&(!rootNodeName.equals("")))	root = new NodeImpl(rootNodeName);
		else root = new NodeImpl("");
		this.props = props;
	}
	
	void createNode(String key, ValueModel value, String regex){
		String [] parts = key.split(regex);
		this.descend(root, key, parts, value, 0);
	}
	
	void descend(Node node, String path, String [] parts, ValueModel value, int depth){
		
		if(parts.length == depth) return;
		
		String key = parts[depth];
		Node target = null;
	
		for(Node n: node.getChildren()){
			if(n.getName().equals(key)){
				target = n;
				break;
			}
		}
		
		if(target == null) {
			target = new NodeImpl(key);
			node.addChild(target);
		}
		
		if(depth == parts.length-1){
			((NodeImpl)target).value = value;
		}
		depth++;
		descend(target, path, parts, value, depth);
	}

	Node tree() {
		return root;
	}
	
}
