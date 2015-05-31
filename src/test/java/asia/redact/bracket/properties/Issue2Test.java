package asia.redact.bracket.properties;

import java.io.InputStream;

import org.junit.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.Properties.Mode;

public class Issue2Test {

	public Issue2Test() {}
	
	// Issue # 2 - properties after multiline property not being recognized/parsed
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/Issue2Resources/test1.properties");
		Assert.assertNotNull(in);
		// tests against PropertiesLexer - this is now fixed
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("test.key1"));
		Assert.assertTrue(props.containsKey("test.key2"));
		Assert.assertTrue(props.containsKey("test.key3"));
		Assert.assertTrue(props.containsKey("test.key4"));
	}
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/Issue2Resources/test1.properties");
		Assert.assertNotNull(in);
		// tests against LineScanner - I think this always worked
		Properties.Factory.mode = Mode.Line;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertTrue(props.containsKey("test.key1"));
		Assert.assertTrue(props.containsKey("test.key2"));
		Assert.assertTrue(props.containsKey("test.key3"));
		Assert.assertTrue(props.containsKey("test.key4"));
	}

}
