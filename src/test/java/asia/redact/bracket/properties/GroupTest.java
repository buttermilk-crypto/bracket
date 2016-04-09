package asia.redact.bracket.properties;

import junit.framework.Assert;

import org.junit.Test;

public class GroupTest {
	
	@Test
	public void test0() {
		
		Properties props = Properties.Factory.getInstance();
		props.put("a.b.c","first level");
		props.put("a.b.c.d","next level");
		props.put("a.b.c.d.e","still deeper level");
		
		Properties slice = props.slice("a.b.c.d");
		Assert.assertEquals(2, slice.size());
		
		slice = props.slice("a.b.c.d.e");
		Assert.assertEquals(1, slice.size());
	
	}
}
