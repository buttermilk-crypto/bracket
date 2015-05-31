package asia.redact.bracket.properties;

import java.io.File;
import java.nio.charset.Charset;

import junit.framework.Assert;

import org.junit.Test;

public class Issue10Test {

	@Test
	public void test0() {
		
		Properties props = Properties.Factory.getInstance(
				new File("./src/test/resources/Issue10Resources/myprops.properties"), 
				Charset.forName("UTF-8")
		);
		Assert.assertEquals(2, 
			props.getPropertyMap().get("myprop.multiline").getValues().size());
		
	}

}
