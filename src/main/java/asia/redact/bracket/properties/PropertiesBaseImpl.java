/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */
package asia.redact.bracket.properties;

import java.io.Serializable;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.regex.Pattern;

/**
 * Constants and regex patterns of general applicability
 * 
 * @author Dave
 *
 */
public class PropertiesBaseImpl implements Serializable {
	
	private static final long serialVersionUID = 1L;
	protected Pattern dotIntegerPattern = Pattern.compile("\\.(\\d+)");
	protected Pattern dotIdentifierPattern = Pattern.compile("\\.([a-zA-Z]+[a-zA-Z0-9]+)");
	protected Pattern dotKeyValuePattern = Pattern.compile("\\.\\d+\\.[kv]");
	protected static final String REF_TOKEN = "_$";
	
	protected Pattern antStyleVarPattern = Pattern.compile("\\$\\{(.+)\\}");
	
	protected final Lock lock = new ReentrantLock();

}
