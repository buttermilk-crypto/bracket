package asia.redact.bracket.properties;

import junit.framework.Assert;

import org.junit.Test;

public class ResolveTest {
	
	@Test
	public void test0() {
		Properties props = Properties.Factory.getInstance();
		props.put("user.home", "${user.home}");
		props.put("user.homenot", "user.home");
		props.put("PATH", "${PATH}");
		Assert.assertTrue(!props.resolve("user.home").contains("$"));
		Assert.assertEquals("user.home", props.resolve("user.homenot"));
		Assert.assertTrue(props.resolve("PATH").length()>10);
	}

}
