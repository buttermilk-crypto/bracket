package asia.redact.bracket.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.example.IndentMultilineFormat;

public class Issue3Test {

	public Issue3Test() {}
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/Issue3Resources/test1.properties");
		Assert.assertNotNull(in);
		// tests against PropertiesLexer
		Properties props = Properties.Factory.getInstance(in);
		
		OutputAdapter out = new OutputAdapter(props);
		Writer w = new StringWriter();
		try {
			out.writeTo(w,new IndentMultilineFormat(5,'*'));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.err.println(w.toString());
	}
	


}
