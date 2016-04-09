/*
 *  This file is part of Bracket Properties
 *  Copyright 2011-2016 David R. Smith, All Rights Reserved
 *
 */
package asia.redact.bracket.properties;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Map.Entry;
import java.util.Set;

import asia.redact.bracket.properties.Properties.Mode;

/**
 * Output the properties to various data sinks and in various charsets and formats.
 * 
 * @author Dave
 *
 */
public class OutputAdapter {

	final Properties properties;
	final static String lineSeparator = System.getProperty("line.separator");

	public OutputAdapter(Properties properties) {
		super();
		this.properties = properties;
	}
	
	/**
	 * Caution, uses FileWriter - defaults to local encoding
	 * 
	 */
	public void writeTo(File file){
		writeTo(file,new BasicOutputFormat());
	}
	
	/**
	 * Caution, uses FileWriter - defaults to local encoding
	 * 
	 * @deprecated
	 */
	public void writeTo(File file, OutputFormat format) {
		FileWriter out = null;
		try {
			out = new FileWriter(file);
			writeTo(out, format);
			out.flush();
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
	
	/**
	 * Use to control the format of the output. Can be used with ExplicitOutputFormat
	 */
	public void writeTo(OutputStream out, OutputFormat format, Charset charset) {
		
		try {
			OutputStreamWriter writer = new OutputStreamWriter(out,charset);
			writeTo(writer, format);
			writer.flush();
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
	
	/**
	 * Use to control the format of the output. Can be used with ExplicitOutputFormat
	 * 
	 */
	public void writeTo(File file, OutputFormat format, Charset charset) {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(file);
			OutputStreamWriter writer = new OutputStreamWriter(out,charset);
			writeTo(writer, format);
			writer.flush();
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
	
	/**
	 * This is specifically intended for compatibility with java.util.Properties, which outputs in ISO-8859-1 (US-ASCII)
	 * 
	 * Use AsciiOutputFormat to get unicode escapes or an output format with similar filtering.
	 * 
	 */
	public void writeAsciiTo(File file, OutputFormat format) {
		writeTo(file, format, Charset.forName("ISO-8859-1"));
	}
	
	/**
	 * This is specifically intended for compatibility with java.util.Properties, which outputs in ISO-8859-1 (US-ASCII)
	 * 
	 */
	public void writeAsciiTo(File file) {
		writeTo(file, new AsciiOutputFormat(), Charset.forName("ISO-8859-1"));
	}
	
	public void writeAsciiTo(Writer writer) {
		try {
			writeTo(writer, new AsciiOutputFormat());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void writeTo(Writer writer, OutputFormat format) throws IOException {
		
		if(properties instanceof ArrayListPropertiesImpl){
			writeToL(writer, format, (ArrayListPropertiesImpl)properties);
			return;
		}
		
		Set<Entry<String,ValueModel>> set = properties.getPropertyMap().entrySet();
		
		writer.append(format.formatContentType());
		writer.append(format.formatHeader());
		
		for(Entry<String,ValueModel> e: set) {
			String key = e.getKey();
			ValueModel model = e.getValue();
			writer.append(format.format(key, model.getSeparator(),model.getValues(),model.getComments()));
		}
		
		writer.append(format.formatFooter());
	}
	
	private void writeToL(Writer writer, OutputFormat format, ArrayListPropertiesImpl impl) throws IOException {
		
		writer.append(format.formatContentType());
		writer.append(format.formatHeader());
		
		for(asia.redact.bracket.properties.Entry e: impl.list) {
			String key = e.getKey();
			ValueModel model = e.getValue();
			writer.append(format.format(key, model.getSeparator(),model.getValues(),model.getComments()));
		}
		
		writer.append(format.formatFooter());
	}
	
	public void writeTo(Writer writer) throws IOException {
		writeTo(writer,new PlainOutputFormat());
	}
	
	/**
	 * <pre>
	 * 
	 * The output from this method is designed to be read back in using 
	 * Properties.Factory.getInstanceFromXML(File).
	 * 
	 * It is expected to fail if any nodes are not suitable for xml elements. For example,
	 * the following property keys will not work in that they end with tokens not usable as xml elements:
	 * 
	 * a.b.c.1=value
	 * a.b.c.xml=value
	 * a.b.c.@$=value
	 * 
	 * If these are identified, the method will fail with a RuntimeException
	 * 
	 * If Properties.Factory.mode == Mode.Compatibility, then we emit ISO-8859-1 as the encoding. 
	 * Otherwise, we emit UTF-8 as the encoding
	 * 
	 * Note, this method does NOT close the writer
	 * 
	 * </pre>
	 * 
	 */
	public void writeAsXml(final Writer out){
		
		try {
			if(Properties.Factory.mode == Mode.Compatibility) {
				out.append("<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>");
			}else{
				out.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
			}
			out.append(lineSeparator);
		} catch (IOException e) {
			e.printStackTrace();
		}
		GroupParams params = new GroupParams();
		// change the root node name
		params.rootNodeName = "properties"; // this actually turns into "nproperties" later
		
		Node node = properties.getTree(params);
		visit(node, 0, new GenerateXMLNodeVisitor(out));
	}
	
	protected void visit(Node node, int level, NodeVisitor func){
		
		func.setLevel(level);
		func.pre(node);
		func.action(node);  
		level++;
		
	    for(Node childNode:node.getChildren()){
	        visit(childNode, level, func);
	    }
	    
	    func.post(node);
	}
	
	public static final String toString(Properties props){
		OutputAdapter out = new OutputAdapter(props);
		StringWriter writer = new StringWriter();
		try {
			out.writeTo(writer);
			return writer.toString();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
