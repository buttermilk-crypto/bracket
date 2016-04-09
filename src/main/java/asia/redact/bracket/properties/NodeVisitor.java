/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

public interface NodeVisitor {
	void pre(Node node);
	void action(Node node);
	void post(Node node);
	void setLevel(int level);
}
