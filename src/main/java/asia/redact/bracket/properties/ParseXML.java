/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;


import java.io.Reader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;

import asia.redact.bracket.properties.Properties.Mode;

/**
 * Package protected wrapper on the SAXParser code
 * 
 * @author Dave
 * @see BracketPropertiesSAXHandler
 */
class ParseXML {
	
	private Properties props;
	
	public ParseXML(){}
	
	/**
	 * <pre>
	 * Parse our xml input.
	 * 
	 * It is expected to use the character encoding of the reader regardless of what is declared in the file; 
	 * but we try to sync these - you may have to sync them manually by paying attention. In essence: if your 
	 * output was generated while in Compatibility mode, then you should use Compatibility mode when deserializing
	 * the files back in.
	 * 
	 * See  Properties.getInstanceFromXML();
	 * 
	 * </pre>
	 * 
	 * @param reader
	 */
	public void parse(Reader reader) {

		BracketSaxHandler handler = null;
		if(Properties.Factory.mode == Mode.Compatibility) {
			handler = new BracketCompatibilitySAXHandler();
		}else{
			handler = new BracketPropertiesSAXHandler();
		}
        SAXParserFactory factory = SAXParserFactory.newInstance();
        SAXParser parser;
		try {
			parser = factory.newSAXParser();
			InputSource source = new InputSource(reader);
		    parser.parse(source, handler);
		    props = handler.getResult();
		} catch (Exception x){
			throw new RuntimeException("Parsing properties failed: "+x.getMessage());
		}
	}

	public Properties getProps() {
		return props;
	}
	
}
