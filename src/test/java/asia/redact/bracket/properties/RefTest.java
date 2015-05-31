package asia.redact.bracket.properties;

import junit.framework.Assert;

import org.junit.Test;

public class RefTest {

	@Test
	public void test0(){
		PropertiesImpl impl = new PropertiesImpl();
		impl.put("1.3.6.1.4.1.311.1.3", "some data");
		impl.putKeyRef("dhcp", "1.3.6.1.4.1.311.1.3");
		Assert.assertEquals(impl.getKeyRef("dhcp"), "some data");
	}
}
