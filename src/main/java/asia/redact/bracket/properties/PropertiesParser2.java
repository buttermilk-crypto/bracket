/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import asia.redact.bracket.properties.line.Line;
import asia.redact.bracket.properties.line.LineScanner;
/**
 * Use the line scanner to load a Properties implementation.
 * 
 * This class does not use PropertiesToken, it is almost a "streaming" parser
 * which reads the tokens from the line and parses as it reads. 
 * It is intended for low memory situations or for very large properties files, 
 * where it ought to perform a bit better than PropertiesParser.
 * 
 * @author Dave
 *
 */
public class PropertiesParser2 {

	protected final LineScanner scanner;
	protected final Properties props;
	private final Lock lock = new ReentrantLock();
	
	public PropertiesParser2(LineScanner scanner) {
		this.scanner=scanner;
		this.props = new PropertiesImpl();
	}
	
	public PropertiesParser2(LineScanner scanner, Properties props) {
		this.scanner=scanner;
		this.props = props;
	}
	
	public void parse(){
		
		try {
		lock.lock();
			Line line = null;
			String key = null;
			BasicValueModel model = new BasicValueModel();
			boolean hasContinuation=false;
			while((line =scanner.line())!=null){
				if(hasContinuation){ 							// previous line has continuation
					model.addValue(line.logicalLineContents()); // so collect the current line
					if(line.hasContinuation()){ 				// if current line has continuation
						continue;								// then continue our tight collection loop
					}else{										
						hasContinuation=false;					// the continuation has ended
						
						// Issue 8 fix
						props.getPropertyMap().put(key, model);
						key=null;
						model=new BasicValueModel();
						continue;
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
					model.setSeparator(parts[1].charAt(0));
					hasContinuation=line.hasContinuation();
					model.addValue(parts[2]);
					
				}
			}
			// last one
			if(key !=null){
				props.getPropertyMap().put(key, model);
			}
		
		}finally {
			lock.unlock();
		}
	}
	
	public Properties getProperties() {
		return props;
	}

}
