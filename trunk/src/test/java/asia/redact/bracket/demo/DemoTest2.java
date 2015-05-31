/*
 *  This file is part of Bracket Properties
 *  Copyright 2014 David R. Smith
 *
 */
package asia.redact.bracket.demo;

import java.io.InputStream;
import java.io.StringWriter;

import org.junit.Test;

import asia.redact.bracket.properties.OutputAdapter;
import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.Properties.Mode;

public class DemoTest2 {

	@Test
	public void demoUTF(){
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/UTF8Resources/test.utf8.properties");
		Properties props = Properties.Factory.getInstance(in);
		System.out.println(OutputAdapter.toString(props));
	}

	@Test
	public void demoASCII(){
		InputStream in = Thread.currentThread().getClass().getResourceAsStream("/UTF8Resources/test.properties");
		Properties.Factory.mode = Mode.Compatibility;
		Properties props = Properties.Factory.getInstance(in);
		//System.out.println(OutputAdapter.toString(props));
		
		StringWriter writer = new StringWriter();
		OutputAdapter out = new OutputAdapter(props);
		out.writeAsciiTo(writer);
		System.err.println(writer.toString());
	}

}
