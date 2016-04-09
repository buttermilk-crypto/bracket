/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties.alt;

import asia.redact.bracket.properties.line.*;
import asia.redact.bracket.properties.*;
/**
 * 
 * Experimental. Parse a file in the Dot Properties format.
 * 
 * @author Dave
 *
 */
public class DotPropertiesParser extends PropertiesParser2 {
	
	public DotPropertiesParser(LineScanner scanner) {
		super(scanner);
	}
	
	public DotPropertiesParser(LineScanner scanner, Properties props) {
		super(scanner,props);
	}
	
	public void parse(){
		String fullKey = null;
		Line line = null;
		String key = null;
		BasicValueModel model = new BasicValueModel();
		boolean hasContinuation=false;
		while((line =scanner.line())!=null){
			if(hasContinuation){
				model.addValue(line.logicalLineContents());
				if(line.hasContinuation()){
					continue;
				}else{
					hasContinuation=false;
				}
			}
			if(line.isEmptyLine())continue;
			if(line.isPrivateComment())continue;
			if(line.isCommentLine()){
				if(key !=null){
					props.getPropertyMap().put(key, model);
					key=null;
					model=new BasicValueModel();
				}
				model.addComment(line.commentContents());
				continue;
			}else if(line.isNaturalLine()){
				if(key !=null){
					props.getPropertyMap().put(key, model);
					key=null;
					model=new BasicValueModel();
				}
				String [] parts = line.naturalLineContents();
				key = parts[0];
				if(!key.startsWith(".")){
					fullKey = key;
				}else{
					key = fullKey+key;
				}
				model.setSeparator(parts[1].charAt(0));
				hasContinuation=line.hasContinuation();
				model.addValue(parts[2]);
				
			}
		}
		// last one
		if(key !=null){
			props.getPropertyMap().put(key, model);
		}
	}
	
}
