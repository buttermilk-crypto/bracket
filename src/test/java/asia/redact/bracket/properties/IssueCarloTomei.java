package asia.redact.bracket.properties;

import junit.framework.Assert;

import org.junit.Test;

import asia.redact.bracket.exampleclasses.ApplicationProperties;

public class IssueCarloTomei {

	@Test
	public void test0() {
		try {
			ApplicationProperties props = ApplicationProperties.getInstance();
			String val = props.getProperty("test.0");
			Assert.assertEquals("one", val);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
