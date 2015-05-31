/*
 *  This file is part of Bracket Properties
 *  Copyright 2011 David R. Smith
 *
 */

package asia.redact.bracket.properties.alt;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Map.Entry;
import java.util.Set;

import asia.redact.bracket.properties.BasicOutputFormat;
import asia.redact.bracket.properties.OutputFormat;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.ValueModel;

/**
 * Output the properties in dot format
 * 
 * @author Dave
 * 
 * TODO possibly extend OutputAdapter
 *
 */
public class DotOutputAdapter {

	final Properties properties;
	final static String lineSeparator = System.getProperty("line.separator");
	
	/**
	 * This constructor will use a SortedPropertiesImpl
	 * 
	 * @param properties
	 */
	public DotOutputAdapter(Properties properties, boolean sortRequired) {
		super();
		if(sortRequired){
			this.properties = Properties.Factory.sortedInstance(properties);
		}else{
			this.properties = properties;
		}
	}
	
	public void writeTo(File file){
		writeTo(file,new BasicOutputFormat());
	}
	
	public void writeTo(File file, OutputFormat format) {
		FileWriter out = null;
		try {
			out = new FileWriter(file);
			writeTo(out, format);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(out != null) {
				try {
					out.close();
				} catch (IOException e) {}
			}
		}
	}
	
	public void writeTo(Writer writer, OutputFormat format) throws IOException {
		Set<Entry<String,ValueModel>> set = properties.getPropertyMap().entrySet();
		
		writer.append(format.formatHeader());
		String baseKey = null;
		for(Entry<String,ValueModel> e: set) {
			String key = e.getKey();
			if(baseKey == null) baseKey = key;
			else{
				if(key.contains(baseKey)){
					key = key.substring(baseKey.length(), key.length());
				}
			}
			ValueModel model = e.getValue();
			writer.append(format.format(key, model.getSeparator(),model.getValues(),model.getComments()));
		}
		
		writer.append(format.formatFooter());
	}
	
	public void writeTo(Writer writer) throws IOException {
		writeTo(writer,new BasicOutputFormat());
	}
	
}
