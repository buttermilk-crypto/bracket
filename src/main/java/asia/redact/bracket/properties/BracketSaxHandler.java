/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import org.xml.sax.helpers.DefaultHandler;

/**
 * Base class for SAX Handlers
 * 
 * @author Dave
 * @see BracketCompatibilitySAXHandler
 * @see BracketPropertiesSAXHandler
 */
public class BracketSaxHandler extends DefaultHandler {

	protected Properties props;
	
	public BracketSaxHandler() {
		this.props = Properties.Factory.getInstance();
	}
	
	public Properties getResult() {
		return props;
	}

}
