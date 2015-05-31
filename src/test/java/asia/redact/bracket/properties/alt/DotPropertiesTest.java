package asia.redact.bracket.properties.alt;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.Properties;
import asia.redact.bracket.properties.line.LineScanner;

public class DotPropertiesTest {

	@Test
	public void test0() {
		InputStream in = getClass().getResourceAsStream("/dot-properties/test0.properties");
		Assert.assertNotNull(in);
		LineScanner lexer = new LineScanner(new InputStreamReader(in));
		DotPropertiesParser p = new DotPropertiesParser(lexer);
		p.parse();
		Properties result = p.getProperties();
		System.err.println(result);
		Assert.assertTrue(result.size()==7);
		Assert.assertEquals(result.get("a.b.c.d.e.f"), "a value");
	}
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/dot-properties/test1.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(in);
		DotOutputAdapter adapter = new DotOutputAdapter(props, false);
		StringWriter writer = new StringWriter();
		try {
			adapter.writeTo(writer);
		} catch (IOException e) {
			Assert.fail();
		}
		System.err.println(writer.toString());
		StringReader reader = new StringReader(writer.toString());
		Properties result = Properties.Factory.getDotInstance(reader);
		Assert.assertNotNull(result);
		Assert.assertEquals("another", result.get("a.b.c.d"));
	}
	
	/**
	 * Sorting is needed for this scenario
	 * 
	 */
	@Test
	public void test3() {
		InputStream in = getClass().getResourceAsStream("/dot-properties/test2.properties");
		Assert.assertNotNull(in);
		Properties props = Properties.Factory.getInstance(in);
		DotOutputAdapter adapter = new DotOutputAdapter(props, true);
		StringWriter writer = new StringWriter();
		try {
			adapter.writeTo(writer);
		} catch (IOException e) {
			Assert.fail();
		}
		System.err.println(writer.toString());
		StringReader reader = new StringReader(writer.toString());
		Properties result = Properties.Factory.getDotInstance(reader);
		Assert.assertNotNull(result);
		Assert.assertEquals("another", result.get("a.b.c.d"));
	}
	
	/**
	 * <pre>
node.0=asia.redact.bracket.util.Dimension
.width=100
.height=200

another.class.0=asia.redact.bracket.util.Window
.dimension=@node.0
       </pre>
	*/
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/ExtendedAccessors/test1.properties");
		Assert.assertNotNull(in);
		Properties result = Properties.Factory.getDotInstance(new InputStreamReader(in, Charset.forName("UTF-8")));
		Assert.assertNotNull(result);
		Assert.assertEquals("100", result.get("node.0.width"));
		Assert.assertEquals(100, result.intValue("node.0.width"));
	}

}
