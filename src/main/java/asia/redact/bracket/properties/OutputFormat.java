/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties;

import java.util.List;

/**
 * <pre>
 * You can implement custom output formats using this interface and OutputAdapter.writeTo(Writer, OutputFormat);
 * 
 * </pre>
 * @author Dave
 *
 */
public interface OutputFormat {

	public String formatContentType();
	public String formatHeader();
	public String format(String key, char separator, List<String> values, List<String> comments);
	public String formatFooter();
	
}
