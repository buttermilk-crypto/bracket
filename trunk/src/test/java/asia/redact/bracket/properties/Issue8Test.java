package asia.redact.bracket.properties;


import java.io.InputStream;
import java.io.InputStreamReader;

import junit.framework.Assert;
import org.junit.Test;

import asia.redact.bracket.properties.line.LineScanner;


/**
 * <pre>
 * Issue 8 - multi-line parsing in edge case
 * 
 * </pre>
 * @author Dave
 *
 */
public class Issue8Test {

	@Test
	public void test0() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input0.props");
		Properties.Factory.mode=Properties.Mode.BasicToken;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(3, props.size());
		Assert.assertTrue(props.containsKey("multiLineProperty"));
		Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
	@Test
	public void test1() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input0.props");
		Properties.Factory.mode=Properties.Mode.Line;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(3, props.size());
		Assert.assertTrue(props.containsKey("multiLineProperty"));
		Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
	@Test
	public void test2() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input1.props");
		Properties.Factory.mode=Properties.Mode.BasicToken;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(3, props.size());
		Assert.assertTrue(props.containsKey("multiLineProperty"));
		Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
	@Test
	public void test3() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input1.props");
		Properties.Factory.mode=Properties.Mode.Line;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(3, props.size());
		Assert.assertTrue(props.containsKey("multiLineProperty"));
		Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
	@Test
	public void test4() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input2.props");
		PropertiesParser2 parser = new PropertiesParser2(new LineScanner(new InputStreamReader(in)));
		parser.parse();
		Properties props = parser.getProperties();
		Assert.assertEquals(3, props.size());
		Assert.assertTrue(props.containsKey("multiLineProperty"));
		Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
	// not fixed yet for this lexer, use line as above
	@Test
	public void test5() {
		InputStream in = getClass().getResourceAsStream("/Issue8Resources/input2.props");
		Properties.Factory.mode=Properties.Mode.BasicToken;
		Properties props = Properties.Factory.getInstance(in);
		Assert.assertEquals(3, props.size());
	//	Assert.assertTrue(props.containsKey("multiLineProperty"));
	//	Assert.assertTrue(!props.containsKey("nestedKey"));
	}
	
}
