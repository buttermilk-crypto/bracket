/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2014 David R. Smith
 *
 */
package asia.redact.bracket.properties.mgmt;

import java.io.Serializable;
import java.util.Locale;

/**
 * Instructions on how to handle the load of the files which apply to the full work flow. 
 * See comments on individual fields below for details.
 * 
 * @author Dave
 *
 */
public class Attributes implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * If this is true, when a path is a directory, it will be scanned for properties files (*.propeties) and
	 * those will be loaded in the order in which they are found using javaio.File.listFiles().
	 */
	public boolean scanForPropertiesFiles;
	
	/**
	 * If set to true, emit a warning if files in the list are not .properties files (ignored if scanForPropertiesFiles is set).
	 */
	public boolean warnOnNoPropertiesFileExtension;
	
	/**
	 * If set to true, will warn on the fact a resource was not found where it was specified. If false, will be ignored
	 */
	public boolean warnOnReferenceNotFound;
	
	/**
	 * Fail cleanly and immediately with a RuntimeException if reference yields no properties
	 */
	public boolean failOnReferenceNotFound;
	
	/**
	 * If true, search for bracket extensions header and load accordingly. This turns on Mode.Explicit
	 */
	public boolean useBracketExtensionHeader;
	
	/**
	 * If true, Compatibility mode is turned on for parsing as cvlose as possible to java.util.Properties
	 */
	public boolean useCompatibilityMode;
	
	/**
	 * If true, use Mode.Line. 
	 */
	public boolean useLineMode;
	
	/**
	 * Use BasicToken Mode 
	 */
	public boolean useBasicTokenMode;
	
	/**
	 * If true, insert all the loaded properties into System using setProperty() on each item; 
	 */
	public boolean insertResultsIntoSystemProperties;
	
	/**
	 * If this is non-null, treat the LoadList like a set of ResourceBundle paths and localize them. Obviously
	 * this only applies to EXTERNAL and CLASSLOADED ReferenceTypes 
	 */
	public Locale locale;

}
