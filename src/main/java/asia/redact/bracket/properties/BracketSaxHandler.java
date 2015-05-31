package asia.redact.bracket.properties;

import org.xml.sax.helpers.DefaultHandler;

public class BracketSaxHandler extends DefaultHandler {

	protected Properties props;
	
	public BracketSaxHandler() {
		this.props = Properties.Factory.getInstance();
	}
	
	public Properties getResult() {
		return props;
	}

}
