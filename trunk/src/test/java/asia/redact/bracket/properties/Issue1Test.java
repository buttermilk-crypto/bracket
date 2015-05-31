package asia.redact.bracket.properties;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.Properties.Mode;

public class Issue1Test {

	public Issue1Test() {}
	
	// Test keys are being trimmed for whitespace
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/Issue1Resources/test1.properties");
		Assert.assertNotNull(in);
		// tests against PropertiesLexer
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("blank.before"));
		Assert.assertFalse(props.containsKey("blank.before.and.after "));
		Assert.assertTrue(props.containsKey("blank.before.and.after"));
		Assert.assertTrue(props.containsKey("org.osgi.framework.bundle.parent"));
	}
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/Issue1Resources/test1.properties");
		Assert.assertNotNull(in);
		// tests against the LineScanner
		Properties.Factory.mode = Properties.Mode.Line;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("blank.before"));
		Assert.assertFalse(props.containsKey("blank.before.and.after "));
		Assert.assertTrue(props.containsKey("blank.before.and.after"));
		Assert.assertTrue(props.containsKey("org.osgi.framework.bundle.parent"));
	}
	
	// test that values do not have dangling whitespace
	
	@Test
	public void test3() {
		InputStream in = getClass().getResourceAsStream("/Issue1Resources/test2.properties");
		Assert.assertNotNull(in);
		// normal mode, not compatibility
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("test.key1"));
		Assert.assertTrue(props.containsKey("test.key2"));
	//	Assert.assertEquals("a value with whitespace at the end     ", props.get("test.key1"));
		Assert.assertEquals("a multiline value with whitespace at the end     ", props.get("test.key2"));
	}
	
	@Test
	public void test3a() {
		InputStream in = getClass().getResourceAsStream("/Issue1Resources/test2.properties");
		Assert.assertNotNull(in);
		// compatibility mode, which trims the values of ending whitespace
		Properties.Factory.mode = Mode.Compatibility;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("test.key1"));
		Assert.assertTrue(props.containsKey("test.key2"));
		Assert.assertEquals("a value with whitespace at the end", props.get("test.key1"));
		Assert.assertEquals("a multiline value with whitespace at the end", props.get("test.key2"));
	}
	
	@Test
	public void test4() {
		InputStream in = getClass().getResourceAsStream("/Issue1Resources/test3.properties");
		java.util.Properties props = new java.util.Properties();
		try {
			props.load(in);
			String val = props.getProperty("a.b.c.d");
			Assert.assertEquals("",val);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
	}

}
