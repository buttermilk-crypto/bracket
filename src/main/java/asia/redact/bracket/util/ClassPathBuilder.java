/*
 *  This file is part of Bracket Properties
 *  Copyright 2014 David R. Smith
 *
 */
package asia.redact.bracket.util;

import java.io.File;
import java.util.List;

import asia.redact.bracket.properties.Properties;

/**
 * construct a classpath from properties
 * 
 * @author Dave
 *
 */
public class ClassPathBuilder {
	
	Properties props;

	public ClassPathBuilder(Properties props) {
		this.props = props;
	}
	
	public String build(String keyBase) {
		List<String> list = props.getList(keyBase);
		StringBuffer classpath = new StringBuffer();
		int i = 0;
		for(String s: list){
			classpath.append(s);
			if(i<list.size()-1)classpath.append(File.pathSeparator);
			i++;
		}
		
		return classpath.toString();
	}

}
