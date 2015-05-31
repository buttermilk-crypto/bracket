/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties;

/**
 * <pre>
 * Write out the nodes from a tree into a bracket Properties object. This
 * operation is destructive to anything currently in the properties file!
 * </pre>
 * 
 * @author Dave
 *
 */
public class TreeSynchronizer {

	Properties props;
	Node rootNode;
	
	TreeSynchronizer(Properties props, Node rootNode) {
		super();
		this.props = props;
		this.rootNode = rootNode;
	}
	
	void synch(){
		props.clear();
		visit(rootNode, null);
	}
	
	void visit(Node node, String path){
		String currentPath = path;
		if(currentPath == null)currentPath=node.getName();
		else currentPath+="."+node.getName();
		if(node.hasValue()){
			props.getPropertyMap().put(currentPath, (BasicValueModel)node.getValue());
		}
		for(Node n:node.getChildren()){
			visit(n,currentPath);
		}
	}
}


